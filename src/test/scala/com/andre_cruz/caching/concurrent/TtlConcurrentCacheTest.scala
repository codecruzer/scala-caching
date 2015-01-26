package com.andre_cruz.caching.concurrent

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class TtlConcurrentCacheTest extends WordSpecLike with Matchers with ScalaFutures {

  // TODO: Refactor to inject an ExpirableEntry so it can be mocked with a
  // sequential time unit to allow for deterministic non-time related tests

  def emptyCache(lifeDuration: Duration, idleDuration: Duration) = new TtlConcurrentCache[String, Int] {
    val timeToLive = lifeDuration
    val timeToIdle = idleDuration
  }

  "An TtlConcurrentCache" when {
    "its TTL or TTI are finite" should {
      "allow expiration" in {
        val infinite = Duration.Inf
        val finite = 1.millis
        emptyCache(infinite, finite).isExpirationAllowed shouldBe true
        emptyCache(finite, infinite).isExpirationAllowed shouldBe true
        emptyCache(finite, finite).isExpirationAllowed shouldBe true
      }
    }

    "its TTL and TTI are infinite" should {
      "not allow expiration" in {
        emptyCache(Duration.Inf, Duration.Inf).isExpirationAllowed shouldBe false
      }
    }

    "attempting to remove expired entries with expiration is enabled" should {
      "remove all expired entries" in {
        // TODO: Replace with non-time based deterministic test
        val ttl = 10.millis
        val cache = emptyCache(ttl, Duration.Inf)
        cache.put("key-1", Future { 1 })
        cache.put("key-2", Future { 2 })

        // Sleep longer than TTL then force expiration
        Thread.sleep(ttl.toMillis)
        cache.tryRemoveExpiredEntries()
        cache.entries should be (empty)
      }
    }

    "attempting to remove expired entries with expiration is disabled" should {
      "do nothing" in {
        val cache = emptyCache(Duration.Inf, Duration.Inf)
        cache.put("key-1", Future { 1 })
        cache.put("key-2", Future { 2 })
        cache.tryRemoveExpiredEntries()
        cache.entries shouldNot be (empty)
      }
    }

    "putting an entry with expiration enabled" should {
      "remove expired entries and put the new entry" in {
        // TODO: Replace with non-time based deterministic test
        val (key1, value1) = "key-1" -> 1
        val (key2, value2) = "key-2" -> 2
        val ttl = 10.millis
        val cache = emptyCache(ttl, Duration.Inf)

        cache.put(key1, Future { value1 })
        Thread.sleep(ttl.toMillis) // Sleep longer than TTL then force expiration
        cache.put(key2, Future { value2 })

        cache.entries.contains(key1) shouldBe false
        cache.entries.contains(key2) shouldBe true
      }
    }
  }

}
