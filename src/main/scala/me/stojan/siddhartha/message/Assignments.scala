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

package me.stojan.siddhartha.message

import java.util
import java.util.Collections

import akka.actor.ActorRef
import me.stojan.siddhartha.keyspace.Key
import me.stojan.siddhartha.util.Bytes

case class Join()
case class AskToJoin(siddhartha: ActorRef)

case class Child(keyspace: (Key, Key), data: util.Map[Key, Bytes] = Collections.emptyMap())

sealed trait Incarnate {
  def siddhartha: ActorRef
}

case class IncarnateTopLevel(siddhartha: ActorRef, keyspace: (Key, Key)) extends Incarnate
case class IncarnateWithParent(siddhartha: ActorRef, parent: ActorRef) extends Incarnate