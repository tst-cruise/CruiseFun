package com.tst.service

import com.tst.model.CruiseModels._

trait ICruiseService {
  def getBestGroupPrices(rates: Seq[Rate], prices: Seq[CabinPrice]): Seq[BestGroupPrice]

  def allCombinablePromotions(allPromotions: Seq[Promotion]): Seq[PromotionCombo]

  def combinablePromotions(
                            promotionCode: String,
                            allPromotions: Seq[Promotion]): Seq[PromotionCombo]
}