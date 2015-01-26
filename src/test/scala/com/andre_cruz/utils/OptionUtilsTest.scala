package com.andre_cruz.utils

import org.scalatest.{Matchers, WordSpecLike}


class OptionUtilsTest extends WordSpecLike with Matchers {

  import OptionUtils._

  "optionIf" when {
    "its predicate is true" should {
      "yield an Option" in {
        optionIf(true) { 123 } shouldBe Some(123)
      }
    }

    "its predicate is false" should {
      "yield None" in {
        optionIf(false) { 123 } shouldBe None
      }

      "never evaluate its 'result' block" should {
        optionIf(false) {
          fail("'result' should not have evaluated")
        } shouldBe None
      }
    }
  }

}
