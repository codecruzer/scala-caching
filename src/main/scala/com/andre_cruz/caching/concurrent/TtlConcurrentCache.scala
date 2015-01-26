package com.andre_cruz.caching.concurrent

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.Duration

trait TtlConcurrentCache[K, V] extends ExpirableConcurrentCache[K, V] {

  val timeToLive: Duration
  val timeToIdle: Duration

  lazy val isExpirationAllowed = timeToLive.isFinite || timeToIdle.isFinite

  private[caching] def tryRemoveExpiredEntries() = {
    if (isExpirationAllowed) {
      val currentTimeMillis = System.currentTimeMillis
      entries.readOnlySnapshot() foreach { case (key, entry) =>
        if (entry.isExpired(currentTimeMillis, timeToLive, timeToIdle))
          entries.remove(key)
      }
    }
  }

  override def put(key: K, futureValue: => Future[V])(implicit ec: ExecutionContext): Future[V] = {
    tryRemoveExpiredEntries()
    super.put(key, futureValue)
  }

}
