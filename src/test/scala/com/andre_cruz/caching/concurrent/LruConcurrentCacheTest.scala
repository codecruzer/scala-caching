package com.andre_cruz.caching.concurrent

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class LruConcurrentCacheTest extends WordSpecLike with Matchers with ScalaFutures {

  // TODO: Refactor to inject an ExpirableEntry so it can be mocked with a
  // sequential time unit to allow for deterministic non-time related tests

  private[caching] class TestLruConcurrentCache(val maxEntryLimit: Int) extends LruConcurrentCache[String, Int] {}

  def emptyCache(entryLimit: Int) = new TestLruConcurrentCache(entryLimit)

  /** Sleeps for a few millis per put, so entry times are unique */
  def createCache(entryLimit: Int, entries: (String, Int)*) = {
    val cache = emptyCache(entryLimit)
    entries foreach { case (k, v) =>
      cache.put(k, Future { v })
      Thread.sleep(5L)
    }
    cache
  }

  "An LruConcurrentCache" when {

    "creating a cache with a maxEntryLimit <= 1" should {
      "throw an IllegalArgumentException" in {
        an [IllegalArgumentException] should be thrownBy emptyCache(0)
        an [IllegalArgumentException] should be thrownBy emptyCache(-1)
      }
    }

    "removing the least recently used entry while containing 0 entries" should {
      "do nothing and return None" in {
        val cache = emptyCache(1)
        cache.removeLeastRecentlyUsed() should be (None)
        cache.entries shouldBe empty
      }
    }

    "removing the least recently used entry while containing 1 entry" should {
      "remove and return the only entry" in {
        val value = 1
        val cache = createCache(1, "key-1" -> value)
        cache.removeLeastRecentlyUsed().get.futureValue should be (value)
        cache.entries shouldBe empty
      }
    }

    "removing the least recently used entry while containing >1 entries" should {
      "remove and return the oldest entry" in {
        val (key, value) = "key-1" -> 1
        val cache = createCache(
          3,
          key -> value,
          "key-2" -> 2,
          "key-3" -> 3
        )
        cache.removeLeastRecentlyUsed().get.futureValue should be (value)
        cache.entries.contains(key) shouldBe false
      }
    }

    "putting a value while at capacity" should {
      "evict the oldest entry and add the newest" in {
        val (oldestKey, oldestValue) = "key-1" -> 1
        val (newestKey, newestValue) = "key-3" -> 3
        val cache = createCache(
          2,
          oldestKey -> oldestValue,
          "key-2" -> 2
        )
        cache.put(newestKey, Future { newestValue })
        cache.entries.contains(oldestKey) shouldBe false
        cache.entries.contains(newestKey) shouldBe true
      }
    }
  }

}
