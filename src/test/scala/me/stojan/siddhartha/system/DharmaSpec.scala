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

import me.stojan.siddhartha.keyspace.Keyspace
import me.stojan.siddhartha.test.UnitSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures

class DharmaSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures {

  var dharma: Dharma = null

  override def beforeEach() = {
    dharma = Dharma("DharmaSpec")
  }

  override def afterEach() {
    dharma.shutdown()
    dharma = null
  }

  "Dharma" should "create a local Buddha" in {
    dharma.buddha.path should be (dharma / "buddha")
  }

  it should "create top level Siddharthas" in {
    val topLevelSDH = dharma.createSiddhartha((Keyspace.min, Keyspace.max))

    whenReady(dharma.siddharthas) { result =>
      result.nonEmpty should be (true)
      result.get should contain (topLevelSDH)
    }
  }

  it should "create parented Siddharthas" in {
    val parentSDH = dharma.createSiddhartha((Keyspace.min, Keyspace.max))
    val sdh = dharma.createSiddhartha(parentSDH)

    whenReady(dharma.siddharthas) { result =>
      result.nonEmpty should be (true)
      result.get should contain (sdh)
    }
  }


}
