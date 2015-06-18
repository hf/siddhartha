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

import akka.util.Timeout
import me.stojan.siddhartha.keyspace.{Key, Keyspace}
import me.stojan.siddhartha.system.Dharma
import me.stojan.siddhartha.test.UnitSpec
import me.stojan.siddhartha.util.Bytes
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.duration._

class SiddharthaMapMapSpec extends UnitSpec with BeforeAndAfterEach {
  var dharma: Dharma = null

  implicit val timeout = Timeout(10 seconds)
  import scala.concurrent.ExecutionContext.Implicits.global

  override def beforeEach(): Unit = {
    dharma = Dharma("SiddharthaMapMapSpec")
  }

  override def afterEach(): Unit = {
    dharma.shutdown()
    dharma = null
  }

  "SiddharthaMap.toMap" should "not allow iteration" in {
    val sdh = dharma.createSiddhartha((Keyspace.min, Keyspace.max))
    val map = SiddharthaMap(sdh)

    intercept[UnsupportedOperationException] {
      map.toMap.iterator
    }
  }

  it should "put a value in a map" in {
    val sdh = dharma.createSiddhartha((Keyspace.min, Keyspace.max))
    val map = SiddharthaMap(sdh).toMap

    map + ((Key(Bytes(0, 1, 2, 3)), Bytes(0, 1, 2, 3))) should be (map)
  }

  it should "remove a value from a map" in {
    val sdh = dharma.createSiddhartha((Keyspace.min, Keyspace.max))
    val map = SiddharthaMap(sdh).toMap

    map - (Key(Bytes(0, 1, 2, 3))) should be (map)
  }

  it should "get values from a map" in {
    val sdh = dharma.createSiddhartha((Keyspace.min, Keyspace.max))
    val map = SiddharthaMap(sdh).toMap

    map get (Key(Bytes(0, 1, 2, 3))) should be (None)

    map + ((Key(Bytes(0, 1, 2, 3)), Bytes(0, 1, 2, 3)))

    map get (Key(Bytes(0, 1, 2, 3))) should be (Some(Bytes(0, 1, 2, 3)))

    map - (Key(Bytes(0, 1, 2, 3)))

    map get (Key(Bytes(0, 1, 2, 3))) should be (None)
  }
}
