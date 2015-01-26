package com.andre_cruz.caching

import scala.concurrent.{ExecutionContext, Future}


trait Caching[K, V] {
  def get(key: K): Option[Future[V]]
  def put(key: K, futureValue: => Future[V])(implicit ec: ExecutionContext): Future[V]
  def remove(key: K): Option[Future[V]]
  def clear(): Unit
}
