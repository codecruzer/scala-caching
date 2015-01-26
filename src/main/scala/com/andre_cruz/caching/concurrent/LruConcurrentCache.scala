package com.andre_cruz.caching.concurrent

import com.andre_cruz.utils.TraversableOnceUtils.RichTraversableOnce

import scala.concurrent.{ExecutionContext, Future}


trait LruConcurrentCache[K, V] extends ExpirableConcurrentCache[K, V] {

  val maxEntryLimit: Int

  require(maxEntryLimit > 0, "maxEntryLimit must be greater than zero")

  private[caching] def removeLeastRecentlyUsed() = for {
    (oldestEntryKey, _) <- entries.minByOption { case (_, entry) => entry.creationTimeMillis }
    oldestEntry <- entries.remove(oldestEntryKey)
  } yield oldestEntry.value

  override def put(key: K, futureValue: => Future[V])(implicit ec: ExecutionContext): Future[V] = {
    if (entries.size >= maxEntryLimit)
      removeLeastRecentlyUsed()
    super.put(key, futureValue)
  }

}
