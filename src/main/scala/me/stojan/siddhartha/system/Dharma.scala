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

package me.stojan.siddhartha.system

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.Config
import me.stojan.siddhartha.actor.{Buddha, Siddhartha}
import me.stojan.siddhartha.keyspace.Key
import me.stojan.siddhartha.message.{IncarnateTopLevel, IncarnateWithParent, Status}

import scala.concurrent.Future
import scala.concurrent.duration._

object Dharma {
  def apply(name: String = "dharma", config: Option[Config] = None): Dharma = Dharma(ActorSystem(name, config))
}

case class Dharma(system: ActorSystem) {
  val buddha: ActorRef = system.actorOf(Props[Buddha], "buddha")

  def createSiddhartha(parent: ActorRef): ActorRef = {
    val siddhartha = system.actorOf(Props[Siddhartha])

    buddha ! IncarnateWithParent(siddhartha, parent)

    siddhartha
  }

  def createSiddhartha(keyspace: (Key, Key)): ActorRef = {
    val siddhartha = system.actorOf(Props[Siddhartha])

    buddha ! IncarnateTopLevel(siddhartha, keyspace)

    siddhartha
  }

  def siddharthas(implicit timeout: Timeout = Timeout(1 second)): Future[Option[Seq[ActorRef]]] = ask(buddha, Status("siddhartha")).mapTo[Option[Seq[ActorRef]]]
}