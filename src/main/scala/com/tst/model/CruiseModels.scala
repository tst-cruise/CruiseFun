package com.tst.model

object CruiseModels {

  case class Rate(rateCode: String, rateGroup: String)

  case class CabinPrice(cabinCode: String,
                        rateCode: String,
                        price: BigDecimal)

  case class BestGroupPrice(cabinCode: String,
                            rateCode: String,
                            price: BigDecimal,
                            rateGroup: String)

  case class Promotion(code: String, notCombinableWith: Seq[String])
  case class PromotionCombo(promotionCodes: Seq[String])
}
