package com.tst.service

import com.tst.model.CruiseModels._

object CruiseService extends ICruiseService {

  private val rateCodeGroupLookup: Map[String, String] = CruiseDataStore.DefaultRateCodeGroups.toMap

  override def getBestGroupPrices(rates: Seq[Rate],
                                  prices: Seq[CabinPrice]): Seq[BestGroupPrice] = {
    CruiseServiceUtil.getBestGroupPrices(rates, prices)(rateCodeGroupLookup.get)
  }

  override def allCombinablePromotions(allPromotions: Seq[Promotion]): Seq[PromotionCombo] = {
    import CruiseServiceUtil._
    allPromotions.zipWithIndex.foldLeft(Seq[PromotionCombo]()) { case (combinations, (currentPromotion, index)) =>
      val validPromotions: Seq[Promotion] = allPromotions.drop(index).findCombinablePromotions(currentPromotion)
      validPromotions.removeOverlappingPromotions(combinations) { combinations =>
        combinations ++ validPromotions.tail.searchCombos(currentPromotion)
      }
    }
  }


  override def combinablePromotions(promotionCode: String,
                                    allPromotions: Seq[Promotion]): Seq[PromotionCombo] = {
    import CruiseServiceUtil._
    val (matched, unmatched) = allPromotions.partition(_.code == promotionCode)
    matched flatMap { promotion =>
      unmatched.findCombinablePromotions(promotion).searchCombos(promotion)
    }
  }

}
