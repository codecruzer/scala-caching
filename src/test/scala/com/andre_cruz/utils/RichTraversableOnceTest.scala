package com.andre_cruz.utils

import org.scalatest.{Matchers, WordSpecLike}


class RichTraversableOnceTest extends WordSpecLike with Matchers {

  import TraversableOnceUtils.RichTraversableOnce

  "A RichTraversableOnce" when {

    /** minOption tests */
    "using minOption while empty" should {
      "yield None" in {
        List.empty[Int].minOption should be (None)
      }
    }

    "using minOption with a single element" should {
      "yield the single element" in {
        List(1).minOption shouldBe Some(1)
      }
    }

    "using minOption with >1 elements" should {
      "yield the minimum element that satisfies the predicate" in {
        List(1, 2, 3).minOption shouldBe Some(1)
      }
    }


    /** maxOption tests */
    "using maxOption while empty" should {
      "yield None" in {
        List.empty[Int].maxOption should be (None)
      }
    }

    "using maxOption with a single element" should {
      "yield the single element" in {
        List(1).maxOption shouldBe Some(1)
      }
    }

    "using maxOption with >1 elements" should {
      "yield the maximum element that satisfies the predicate" in {
        List(1, 2, 3).maxOption shouldBe Some(3)
      }
    }


    /** minByOption tests */
    "using minByOption while empty" should {
      "yield None" in {
        Map.empty[String, Int].minByOption(_._2) should be (None)
      }
    }

    "using minByOption with a single element" should {
      "yield the single element" in {
        val pair = "a" -> 1
        Map(pair).minByOption(_._2) shouldBe Some(pair)
      }
    }

    "using minByOption with >1 elements" should {
      "yield the minimum element that satisfies the predicate" in {
        val minPair = "a" -> 1
        Map(
          minPair,
          "b" -> 2,
          "c" -> 3
        ).minByOption(_._2) shouldBe Some(minPair)
      }
    }


    /** maxByOption tests */
    "using maxByOption while empty" should {
      "yield None" in {
        Map.empty[String, Int].maxByOption(_._2) should be (None)
      }
    }

    "using maxByOption with a single element" should {
      "yield the single element" in {
        val pair = "a" -> 1
        Map(pair).maxByOption(_._2) shouldBe Some(pair)
      }
    }

    "using maxByOption with >1 elements" should {
      "yield the maximum element that satisfies the predicate" in {
        val maxPair = "c" -> 3
        Map(
          "a" -> 1,
          "b" -> 2,
          maxPair
        ).maxByOption(_._2) shouldBe Some(maxPair)
      }
    }

  }

}
