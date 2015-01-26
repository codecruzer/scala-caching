package com.andre_cruz.caching

import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.duration._


class ExpirableTest extends WordSpecLike with Matchers {

  /** Sequential expirable for deterministic non-time bound testing */
  private[caching] class MockExpirable(var counter: Long) extends Expirable {
    protected def getCurrentTimeMillis = {
      counter += 1L
      counter
    }
  }

  // Init time is offset by 1 because class construction will cause the counter to increment
  def create(initTime: Long = 0L) = new MockExpirable(initTime - 1L)

  "An Expirable" when {

    "constructed" should {
      "contain its creation time and last access time" in {
        val initTime = 1L
        val expirable = create(initTime)
        expirable.creationTimeMillis should be (initTime)
        expirable.lastAccessTimeMillis should be (initTime)
        expirable.creationTimeMillis shouldBe expirable.lastAccessTimeMillis
      }
    }

    "refreshing" should {
      "update its last access time" in {
        val initTime = 1L
        val expirable = create(initTime)
        expirable.lastAccessTimeMillis should be (initTime)
        expirable.refresh()
        expirable.lastAccessTimeMillis should be (initTime + 1L)
      }
    }

    "checking expiration" should {
      "not be expired with creation time < TTL" in {
        create().isExpired(1L, 2.millis, Duration.Inf) shouldBe false
      }

      "be expired with creation time >= TTL" in {
        val ttl = 2.millis
        create().isExpired(2L, ttl, Duration.Inf) shouldBe true
        create().isExpired(3L, ttl, Duration.Inf) shouldBe true
      }

      "not be expired with last access time time < TTI" in {
        create().isExpired(1L, Duration.Inf, 2.millis) shouldBe false
      }

      "be expired with last access time >= TTI" in {
        val tti = 2.millis
        create().isExpired(2L, Duration.Inf, tti) shouldBe true
        create().isExpired(3L, Duration.Inf, tti) shouldBe true
      }
    }
  }

}
