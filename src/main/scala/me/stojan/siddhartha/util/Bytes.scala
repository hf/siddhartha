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

package me.stojan.siddhartha.util

import java.util
import java.util.Comparator

object Bytes {
  val hashableBytes = 16

  val comparator: Comparator[Bytes] = new Comparator[Bytes] {
    override def compare(x: Bytes, y: Bytes): Int = {
      require(x != null)
      require(y != null)

      val (invert, a, b) = if (x.data.length >= y.data.length) {
        (1, x.data, y.data)
      } else {
        (-1, y.data, x.data)
      }

      for (i <- 0 until (a.length - b.length)) {
        if (a(i) != 0) { return (a(i) & 0xFF) * invert }
      }

      for (i <- (a.length - b.length) until a.length) {
        val comparison = (a(i) & 0xFF) - (b(i - (a.length - b.length)) & 0xFF)
        if (comparison != 0) { return comparison * invert }
      }

      0
    }
  }

  def apply(): Bytes = new Bytes(Array[Byte](0))

  def apply(xs: Byte*) = new Bytes(xs.toArray[Byte])
}

case class Bytes(data: Array[Byte]) extends Equals with Ordered[Bytes] {

  override def compare(that: Bytes): Int = Bytes.comparator.compare(this, that)

  override def canEqual(that: Any): Boolean = that match {
    case None => false
    case null => false
    case _ => true
  }

  override def equals(that: Any): Boolean = that match {
    case Some(x: Any) => this `equals` x
    case array: Array[Byte] => array.asInstanceOf[AnyRef] == data.asInstanceOf[AnyRef] || compare(array) == 0
    case bytes: Bytes => bytes.data.asInstanceOf[AnyRef] == data.asInstanceOf[AnyRef] || compare(bytes.data) == 0
    case _ => false
  }

  override lazy val hashCode: Int = {
    var hashArray = Array.ofDim[Byte](Bytes.hashableBytes)

    for (i <- 0 until Math.min(Bytes.hashableBytes, data.length)) {
      hashArray(i) = data(data.length - i - 1)
    }

    util.Arrays.hashCode(hashArray)
  }
}
