package com.andre_cruz.caching.concurrent

import com.andre_cruz.caching.Caching

import scala.concurrent.duration.Duration


class LruTtlConcurrentCache[K, V](
  val maxEntryLimit: Int,
  val timeToLive: Duration,
  val timeToIdle: Duration
) extends Caching[K, V]
  with LruConcurrentCache[K, V]
  with TtlConcurrentCache[K, V] {}
