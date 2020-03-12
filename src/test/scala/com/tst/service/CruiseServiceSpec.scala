package com.tst.service

import com.tst.model.CruiseModels.{Promotion, PromotionCombo}
import org.scalacheck.{Gen, Prop}
import org.scalatest.prop.Checkers

import scala.util.Random

class CruiseServiceSpec extends CruiseSpecBase with Checkers {
  private val allPromotions = CruiseDataStore.allPromotions

  test("CruiseService.allCombinablePromotions should find all promotions that can be combined") {
    import CruiseServiceUtil._
    val allCombinablePromotions = CruiseService.allCombinablePromotions(allPromotions)
    assert(noViolations(allCombinablePromotions) && allPromotions.overlappingPromotions(allCombinablePromotions).isEmpty)
  }


  private val mockPromotionCodeCount = 10
  private val mockPromotionCodeWidth = 2
  private val strGen = (n: Int) => Gen.listOfN(n, Gen.alphaChar).map(_.mkString)
  private val promotionCodeGen: Gen[Seq[String]] = Gen.listOfN(mockPromotionCodeCount, strGen(mockPromotionCodeWidth))

  test("CruiseService.allCombinablePromotions should pass pro") {
    import CruiseServiceUtil._
    check(Prop.forAll(promotionCodeGen) { promotionCodes =>
      val mockPromotions: Seq[Promotion] = promotionCodes map { promotionCode =>
        val otherPromotionCodes = promotionCodes filterNot (_ == promotionCode)
        val notCombinableWithSamples: Seq[String] = otherPromotionCodes.take(Random.nextInt(mockPromotionCodeCount - 1))
        Promotion(promotionCode, notCombinableWithSamples)
      }

      val allCombinablePromotions = CruiseService.allCombinablePromotions(mockPromotions)
      allCombinablePromotions foreach println

      val assertResult = noViolations(allCombinablePromotions) && mockPromotions.overlappingPromotions(allCombinablePromotions).isEmpty
      println(s"Success $assertResult")
      assertResult
    })
  }

  test("CruiseService.combinablePromotions should find all promotions for a given promotion code") {
    import CruiseServiceUtil._

    allPromotions foreach println
    assert {
      Seq("P1", "P3") forall { promotionCode =>
        val combinablePromotions: Seq[PromotionCombo] = CruiseService.combinablePromotions(promotionCode, allPromotions)
        noViolations(combinablePromotions) && allPromotions.overlappingPromotions(combinablePromotions).isEmpty
      }
    }
  }


  // check for an invariant property - no promotion combo can contain promotions on the notCombinableWith list.
  private def noViolations(allCombinablePromotions: Seq[PromotionCombo]): Boolean = {
    allCombinablePromotions forall { combo =>
      combo.promotionCodes forall { promotionCode =>
        val otherPromotionCodes: Seq[String] = combo.promotionCodes.filter(_ != promotionCode)
        val forbiddenPromotionCodes: Option[Seq[String]] = allPromotions collectFirst { case promotion if promotion.code == promotionCode =>
          promotion.notCombinableWith
        }
        val invalidPromotionCodesFound: Boolean = forbiddenPromotionCodes match {
          case Some(forbiddenCodes) => val violations = forbiddenCodes find otherPromotionCodes.contains
            violations.isDefined
          case None => false
        }
        !invalidPromotionCodesFound
      }
    }
  }
}