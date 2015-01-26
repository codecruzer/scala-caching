package com.andre_cruz.caching


private[caching] class ExpirableEntry[T](val value: T) extends Expirable {
  protected def getCurrentTimeMillis: Long = System.currentTimeMillis
}
