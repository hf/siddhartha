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

import java.util

import akka.actor.{Actor, ActorRef}
import me.stojan.siddhartha.keyspace.{Key, Keyspace}
import me.stojan.siddhartha.message._
import me.stojan.siddhartha.util.Bytes

class Siddhartha extends Actor {
  val map: util.SortedMap[Key, Bytes] = new util.TreeMap[Key, Bytes]()

  override def receive: Receive = {
    case Status(ofWhat: String) => sender ! (ofWhat match {
      case "active" => Some(false)
      case _ => None
    })

    case AskToJoin(siddhartha: ActorRef) => siddhartha ! Join()

    case Child(keyspace: (Key, Key), data: util.Map[Key, Bytes]) =>
      map.putAll(data)

      becomeActive(keyspace, Some(sender), Seq())

    case _ =>
  }

  def active(keyspace: (Key, Key), parent: Option[ActorRef], children: Seq[(Key, ActorRef)]): Receive = {
    case Status(ofWhat: String) => sender ! (ofWhat match {
      case "active" => Some(true)
      case "keyspace" => Some(keyspace)
      case "parent" => Some(parent)
      case "children" => Some(children)
      case _ => sender ! None
    })

    case _: Join =>
      val halvedKeyspace = Keyspace.halve(keyspace)
      val parentKeyspace = (halvedKeyspace._1, halvedKeyspace._2)
      val childKeyspace = (halvedKeyspace._2, halvedKeyspace._3)

      sender ! Child(childKeyspace, (map.subMap _).tupled(childKeyspace))

      // TODO: Refresh map without childData values once in a while

      becomeActive(parentKeyspace, parent, children :+ (halvedKeyspace._2, sender))

    case dht: DHTMessage =>
      if (dht.key within keyspace) {
        dht match {
          case Put(key, value) => store(key, value)
          case Get(key) => sender ! Value(key, Option(map.get(key)))
        }
      } else {
        if (dht.key < keyspace._1) {
          parent.foreach(_ forward dht)
        } else {
          responsibleChild(dht.key, children) forward dht
        }
      }

    case _ =>
  }

  def becomeActive(keyspace: (Key, Key), parent: Option[ActorRef], children: Seq[(Key, ActorRef)]): Unit = {
    context.become(active(keyspace, parent, children), true)
  }

  protected def store(key: Key, value: Option[Bytes]): Unit = if (value.isEmpty) {
    map.remove(key)
  } else {
    map.put(key, value.get)
  }

  protected def responsibleChild(key: Key, children: Seq[(Key, ActorRef)]): ActorRef = {
    children.find(key >= _._1).map(_._2).orNull
  } ensuring(_ != null)
}
