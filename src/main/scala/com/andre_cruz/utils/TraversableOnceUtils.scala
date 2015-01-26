package com.andre_cruz.utils

import com.andre_cruz.utils.OptionUtils.optionIf


object TraversableOnceUtils {

  implicit class RichTraversableOnce[+A](underlying: TraversableOnce[A]) {

    def minOption[B >: A](implicit cmp: Ordering[B]): Option[A] = optionIf(underlying.nonEmpty) {
      underlying.min(cmp)
    }

    def maxOption[B >: A](implicit cmp: Ordering[B]): Option[A] = optionIf(underlying.nonEmpty) {
      underlying.max(cmp)
    }

    def minByOption[B](f: A => B)(implicit cmp: Ordering[B]): Option[A] = optionIf(underlying.nonEmpty) {
      underlying.minBy(f)
    }

    def maxByOption[B](f: A => B)(implicit cmp: Ordering[B]): Option[A] = optionIf(underlying.nonEmpty) {
      underlying.maxBy(f)
    }

  }

}
