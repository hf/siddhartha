/*
 * Copyright (c) 2015 Stojan Dimitrovski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.stojan.siddhartha.actor

import akka.actor.{Actor, ActorRef}
import me.stojan.siddhartha.keyspace.{Key, Keyspace}
import me.stojan.siddhartha.message._
import me.stojan.siddhartha.util.Bytes

import scala.collection.mutable

class Siddhartha extends Actor {
  val map = new mutable.HashMap[Key, Bytes]()

  var isActive = false

  override def receive: Receive = {
    case AskToJoin(siddhartha: ActorRef) => siddhartha ! Join()
    case Child(keyspace: (Key, Key)) => becomeActive(keyspace, Some(sender), Seq())
    case _ =>
  }

  def active(keyspace: (Key, Key), parent: Option[ActorRef], children: Seq[ActorRef]): Receive = {
    case _: Join =>
      val halved = Keyspace.halve(keyspace._1, keyspace._2)

      sender ! Child((halved._2, halved._3))

      becomeActive((halved._1, halved._2), parent, children :+ sender)

    case dht: DHTMessage =>
      if (!Keyspace.within(dht.key, keyspace)) {
        if (dht.key < keyspace._1) {
          parent.foreach(_ forward dht)
        } else {
          children.foreach(_ forward dht)
        }
      } else {
        dht match {
          case Put(key, value) => if (value.isEmpty) { map.remove(key) } else { map.put(key, value.get) }
          case Get(key) => sender ! Value(key, map.get(key))
        }
      }

    case _ =>
  }

  def becomeActive(keyspace: (Key, Key), parent: Option[ActorRef], children: Seq[ActorRef]): Unit = {
    context.become(active(keyspace, parent, children), true)
    isActive = true
  }
}
