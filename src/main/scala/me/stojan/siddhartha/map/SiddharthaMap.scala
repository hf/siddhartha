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

package me.stojan.siddhartha.map


import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import me.stojan.siddhartha.keyspace.Key
import me.stojan.siddhartha.message.{Get, Put, Value}
import me.stojan.siddhartha.util.Bytes

import scala.concurrent.{ExecutionContext, Future}

case class SiddharthaMap(siddhartha: ActorRef) {

  def get(key: Key)(implicit timeout: Timeout, ec: ExecutionContext): Future[Value] = ask(siddhartha, Get(key)).mapTo[Value]

  def put(key: Key, value: Option[Bytes])(implicit timeout: Timeout, ec: ExecutionContext): Future[Value] = ask(siddhartha, Put(key, value)).mapTo[Value]

  def remove(key: Key)(implicit timeout: Timeout, ec: ExecutionContext): Future[Value] = put(key, None)

}