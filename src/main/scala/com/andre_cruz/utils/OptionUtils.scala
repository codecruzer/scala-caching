package com.andre_cruz.utils


object OptionUtils {

  def optionIf[T](predicate: => Boolean)(result: => T): Option[T] = {
    if (predicate)
      Option(result)
    else
      None
  }

}
