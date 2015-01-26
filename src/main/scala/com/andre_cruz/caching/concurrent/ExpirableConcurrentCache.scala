package com.andre_cruz.caching.concurrent

import com.andre_cruz.caching.{ExpirableEntry, Caching}

import scala.collection.concurrent.TrieMap
import scala.concurrent.{Promise, ExecutionContext, Future}
import scala.util.Failure


trait ExpirableConcurrentCache[K, V] extends Caching[K, V] {

  private[caching] type Entry = ExpirableEntry[Future[V]]

  private[caching] val entries = TrieMap.empty[K, Entry]

  def get(key: K): Option[Future[V]] = entries.get(key) map { entry =>
    entry.refresh()
    entry.value
  }

  def put(key: K, futureValue: => Future[V])(implicit ec: ExecutionContext): Future[V] = {
    val promise = Promise[V]()
    val entry = new ExpirableEntry(promise.future)

    entries.putIfAbsent(key, entry)
      .map(_.value)
      .getOrElse {
        promise.completeWith(futureValue)
          .future
          .andThen { case Failure(_) => entries.remove(key, entry) }
      }
  }

  def remove(key: K): Option[Future[V]] = entries.remove(key).map(_.value)

  def clear(): Unit = entries.clear()

}
