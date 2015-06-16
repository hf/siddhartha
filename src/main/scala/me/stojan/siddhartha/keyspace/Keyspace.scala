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

package me.stojan.siddhartha.keyspace

import me.stojan.siddhartha.util._

object Keyspace {
  val bytes = 512 / 8

  lazy val min: Key = Bytes()
  lazy val max: Key = {
    val data = Array.ofDim[Byte](bytes)

    for (i <- 0 until data.length) {
      data(i) = 0xFF.toByte
    }

    Bytes(data)
  }

  def halve(a: Key, b: Key): (Key, Key, Key) = (a, a + (b - a) / 2, b)

  def halve(keyspace: (Key, Key)): (Key, Key, Key) = halve(keyspace._1, keyspace._2)

  def within(key: Key, keyspace: (Key, Key)) = key >= keyspace._1 && key < keyspace._2
}
