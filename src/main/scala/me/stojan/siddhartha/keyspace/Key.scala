package me.stojan.siddhartha.keyspace

import java.util

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

case class Key(data: Array[Byte]) extends Equals with Ordered[Array[Byte]] with Comparable[Array[Byte]] {
  override def compare(that: Array[Byte]): Int = {
    require(that != null)

    val (invert, a, b) = if (data.length >= that.length) { (1, data, that) }
      else { (-1, that, data) }

    for (i <- 0 until (a.length - b.length)) {
      if (a(i) != 0) { return invert }
    }

    for (i <- (a.length - b.length) until a.length) {
      val comparison = a(i) `compare` b(i - (a.length - b.length))
      if (comparison != 0) { return comparison * invert }
    }

    0
  }

  override def hashCode(): Int = util.Arrays.hashCode(data)

  override def canEqual(that: Any): Boolean = that match {
    case _: Key => true
    case _: Array[Byte] => true
    case _ => false
  }

  override def equals(that: Any): Boolean = that match {
    case t: Key => data == t.data || compare(t.data) == 0
    case t: Array[Byte] => data == t.data || compare(t) == 0
    case _ => false
  }
}