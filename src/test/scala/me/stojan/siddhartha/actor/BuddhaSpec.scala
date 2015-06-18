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

import akka.actor.{ActorRef, PoisonPill, Props}
import akka.testkit.TestProbe
import me.stojan.siddhartha.keyspace.Keyspace
import me.stojan.siddhartha.message._
import me.stojan.siddhartha.test.ActorSystemSpec

import scala.concurrent.duration._

class BuddhaSpec extends ActorSystemSpec("BuddhaSpec") {

  "Buddha" should "incarnate top level Siddharthas" in {
    val buddha = system.actorOf(Props[Buddha])
    val sdhProbe = TestProbe()

    buddha ! IncarnateTopLevel(sdhProbe.ref, (Keyspace.min, Keyspace.max))

    sdhProbe.expectMsg(Duration(50, MILLISECONDS), Child((Keyspace.min, Keyspace.max)))

    buddha ! Status("siddhartha")

    val siddharthas = expectMsgType[Option[Seq[ActorRef]]]

    siddharthas.nonEmpty should be (true)

    siddharthas.get should contain (sdhProbe.ref)

    buddha ! Status("siddhartha:parented")

    val parentedSiddharthas = expectMsgType[Option[Seq[ActorRef]]]

    parentedSiddharthas.nonEmpty should be (true)

    parentedSiddharthas.get.isEmpty should be (true)

    buddha ! Status("siddhartha:toplevel")

    val topLevelSiddharthas = expectMsgType[Option[Seq[ActorRef]]]

    topLevelSiddharthas.nonEmpty should be (true)

    topLevelSiddharthas.get should contain (sdhProbe.ref)

    buddha ! PoisonPill
  }

  it should "incarnate parented Siddharthas" in {
    val buddha = system.actorOf(Props[Buddha])
    val parentProbe = TestProbe()
    val sdhProbe = TestProbe()

    buddha ! IncarnateWithParent(sdhProbe.ref, parentProbe.ref)

    sdhProbe.expectMsg(Duration(50, MILLISECONDS), AskToJoin(parentProbe.ref))

    buddha ! Status("siddhartha")

    val siddharthas = expectMsgType[Option[Seq[ActorRef]]]

    siddharthas.nonEmpty should be (true)

    siddharthas.get should contain (sdhProbe.ref)

    buddha ! Status("siddhartha:parented")

    val parentedSiddharthas = expectMsgType[Option[Seq[ActorRef]]]

    parentedSiddharthas.nonEmpty should be (true)

    siddharthas.get should contain (sdhProbe.ref)

    buddha ! Status("siddhartha:toplevel")

    val topLevelSiddharthas = expectMsgType[Option[Seq[ActorRef]]]

    topLevelSiddharthas.nonEmpty should be (true)

    topLevelSiddharthas.get.isEmpty should be (true)

    buddha ! PoisonPill
  }
}
