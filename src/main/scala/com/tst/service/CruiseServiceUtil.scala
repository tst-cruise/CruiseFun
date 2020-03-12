package com.tst.service

import com.tst.model.CruiseModels._

import scala.annotation.tailrec

private[service] object CruiseServiceUtil {

  implicit class PromotionsDecorator(allPromotions: Seq[Promotion]) {
    lazy val promotionCodes = allPromotions.map(_.code)

    def findCombinablePromotions(promotionCode: String): Seq[Promotion] = {
      allPromotions filterNot { p =>
        p.notCombinableWith.contains(promotionCode)
      }
    }

    def findCombinablePromotions(promotion: Promotion): Seq[Promotion] = {
      findCombinablePromotions(promotion.code)
    }

    // Search for any promotion combo which is part of another promotion combo.
    // For example, [P4, P5] is a part of [P1, P4, P5] and therefore considered overlapping with the latter.
    def overlappingPromotions(combinations: Seq[PromotionCombo]): Option[PromotionCombo] = {
      combinations find { combination =>
        promotionCodes forall combination.promotionCodes.contains
      }
    }

    def removeOverlappingPromotions(combinations: Seq[PromotionCombo])(handler: Seq[PromotionCombo] => Seq[PromotionCombo]): Seq[PromotionCombo] = {
      overlappingPromotions(combinations) match {
        case Some(_) => combinations
        case _ => handler(combinations)
      }
    }

    // breathFirstSearch
    def searchCombos(currentPromotion: Promotion): Seq[PromotionCombo] = {
      (0 until allPromotions.size).foldLeft(Seq[PromotionCombo]()) { (combinations, index) =>
        val currentCombination = Seq(currentPromotion)
        val remainPromotions = allPromotions.drop(index)
        val promotionCodes = depthFirstSearch(currentCombination, remainPromotions).promotionCodes
        val combo: PromotionCombo = PromotionCombo(promotionCodes)
        remainPromotions.removeOverlappingPromotions(combinations) { combinations =>
          combinations :+ combo
        }
      }
    }

    @tailrec
    private def depthFirstSearch(currentCombination: Seq[Promotion], promotions: Seq[Promotion]): Seq[Promotion] = {
      if (promotions.isEmpty) {
        currentCombination
      }
      else {
        val currentPromotion = promotions.head
        val newBreadcrumb = currentCombination :+ currentPromotion
        val validPromotions: Seq[Promotion] = promotions.tail.findCombinablePromotions(currentPromotion)
        depthFirstSearch(newBreadcrumb, validPromotions)
      }
    }
  }

  def getBestGroupPrices(rates: Seq[Rate], prices: Seq[CabinPrice])(lookup: String => Option[String]): Seq[BestGroupPrice] = {
    val tempBestGroupPrices: Seq[BestGroupPrice] = for {
      price <- prices
      rateGroup <- lookup(price.rateCode)
    } yield BestGroupPrice(price.cabinCode, price.rateCode, price.price, rateGroup)

    val tempAggregatedBestGroupPrices: Iterable[Seq[BestGroupPrice]] = tempBestGroupPrices groupBy {
      case BestGroupPrice(cabinCode, _, _, rateGroup) =>
        (cabinCode, rateGroup)
    } values

    val output: Seq[BestGroupPrice] = tempAggregatedBestGroupPrices.toSeq map { aggregatedGroupPrices =>
      aggregatedGroupPrices.sortBy(_.price).head
    }
    output sortBy { priceEntry => (priceEntry.cabinCode, priceEntry.rateCode) }
  }
}