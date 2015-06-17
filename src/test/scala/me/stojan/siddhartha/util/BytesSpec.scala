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

import me.stojan.siddhartha.test.UnitSpec

class BytesSpec extends UnitSpec {

  "Bytes" should "compare correctly" in {
    Bytes.comparator.compare(Bytes(), Bytes(0, 0, 0)) should be (0)
    Bytes.comparator.compare(Bytes(0, 0, 1), Bytes(0, 1)) should be (0)
    Bytes.comparator.compare(Bytes(0, 2), Bytes(0, 1)) shouldBe > (0)
    Bytes.comparator.compare(Bytes(3, 4, 5), Bytes(3, 4, 5, 6)) shouldBe < (0)
  }

  it should "canEqual and equal correctly" in {
    Bytes() `canEqual` None should be (false)
    Bytes() `canEqual` null should be (false)
    Bytes() `canEqual` Some(Bytes()) should be (true)
    Bytes() `canEqual` Array[Byte]() should be (true)
    Bytes() `canEqual` Bytes() should be (true)

    Bytes() `equals` Bytes(0, 0, 0) should be (true)
    Bytes(0, 1, 2, 3) `equals` Bytes(1, 2, 3) should be (true)
    Bytes(2, 4, 5) `equals` Bytes(2, 4, 6) should be (false)
    Bytes(1, 2, 3) `equals` Some(Bytes(0, 1, 2, 3)) should be (true)
    Bytes(3, 4, 5) `equals` Array[Byte](3, 4, 5) should be (true)
  }

  it should "have the same hashCode for 'same' content" in {
    Bytes().hashCode should be (Bytes(0, 0, 0).hashCode)
    Bytes(0, 0, 1, 2, 3).hashCode should be (Bytes(0, 0, 0, 1, 2, 3).hashCode)
  }

  it should "return a relative comparison value" in {
    Bytes() `compare` Bytes(0, 1) should be (-1)
    Bytes(0, 2) `compare` Bytes(0, 1) should be (1)

    Bytes(0, 200.toByte) `compare` Bytes(0, 0, 202.toByte) should be (-2)
    Bytes(130.toByte, 0, 200.toByte) `compare` Bytes(0, 0, 202.toByte) should be (130)
  }
}
