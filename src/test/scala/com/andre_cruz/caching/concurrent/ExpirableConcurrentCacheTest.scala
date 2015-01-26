package com.andre_cruz.caching.concurrent

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class ExpirableConcurrentCacheTest extends WordSpecLike with Matchers with ScalaFutures {

  // TODO: Refactor to inject an ExpirableEntry so it can be mocked with a
  // sequential time unit to allow for deterministic non-time related tests

  def emptyCache = new ExpirableConcurrentCache[String, Int] {}

  def createCache(entries: (String, Int)*) = {
    val cache = emptyCache
    entries foreach { case (k, v) =>
      cache.put(k, Future { v })
    }
    cache
  }

  "A ExpirableConcurrentCache" when {

    /** 'get' tests */
    "getting an existing entry by key" should {
      "return the value Future for that key" in {
        val (key, value) = "key-1" -> 1
        createCache(key -> value).get(key).get.futureValue should be (value)
      }

      "refresh the entry's last access time" in {
        // TODO: Replace with non-time based deterministic test
        val key = "key-1"
        val cache = createCache(key -> 1)
        def getLastAccessTime = cache.entries.get(key).get.lastAccessTimeMillis
        val initialAccessTime = getLastAccessTime

        Thread.sleep(10L)
        cache.get(key)

        val updatedAccessTime = getLastAccessTime
        updatedAccessTime should be > initialAccessTime
      }
    }

    "getting a non-existing entry" should {
      "return None" in {
        emptyCache.get("absent") should be (None)
      }
    }

    "caching a new value" should {
      "return the value Future" in {
        emptyCache.put("key-1", Future { 1 }).futureValue should be (1)
      }

      "store the entry by the provided key" in {
        val cache = emptyCache
        val (key, value) = "key-1" -> 1
        cache.put(key, Future { value })
        cache.get(key).get.futureValue should be (value)
      }
    }

    /** 'set' tests */
    "caching a value with a key that already exists" should {
      "return the value Future of the original entry" in {
        val key = "key-1"
        val firstValue = 1
        val secondValue = 2
        val cache = createCache(key -> firstValue)
        cache.put(key, Future { secondValue }).futureValue should be (firstValue)
      }

      "not update the cache with the new value" in {
        val key = "key-1"
        val firstValue = 1
        val secondValue = 2
        val cache = createCache(key -> firstValue)
        cache.put(key, Future { secondValue }).futureValue should be (firstValue)
        cache.get(key).get.futureValue should be (firstValue)
      }
    }

    /** 'remove' tests */
    "removing an existing entry" should {
      "remove the entry and return the value Future" in {
        val (key, value) = "key-1" -> 1
        val cache = createCache(key -> value)
        val removedValue = cache.remove(key)
        removedValue.get.futureValue should be (value)
        cache.entries.contains(key) shouldBe false
      }
    }

    "removing a non-existing entry" should {
      "return None" in {
        emptyCache.remove("absent") should be (None)
      }
    }

    /** 'clear' tests */
    "clearing the cache" should {
      "remove all entries" in {
        val cache = createCache(
          "key-1" -> 1,
          "key-2" -> 2,
          "key-3" -> 3
        )
        cache.clear()
        cache.entries shouldBe empty
      }
    }
  }

}
