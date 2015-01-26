package com.andre_cruz.caching

import scala.concurrent.duration.Duration


trait Expirable {

  protected def getCurrentTimeMillis: Long

  val creationTimeMillis: Long = getCurrentTimeMillis

  private var _lastAccessTimeMillis = creationTimeMillis

  def lastAccessTimeMillis = _lastAccessTimeMillis

  def refresh() = _lastAccessTimeMillis = getCurrentTimeMillis

  def isExpired(atTimeMillis: Long, timeToLive: Duration, timeToIdle: Duration) =
    (timeToLive.isFinite && (atTimeMillis >= creationTimeMillis + timeToLive.toMillis)) ||
      (timeToIdle.isFinite && (atTimeMillis >= lastAccessTimeMillis + timeToIdle.toMillis))

}
