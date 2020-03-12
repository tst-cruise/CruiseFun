package com.tst

import com.tst.service.CruiseDataStore._
import com.tst.service.CruiseService

object CruiseMain {

  def main(args: Array[String]): Unit = {
    println("Input - Rates:")
    allRates foreach println
    println
    println("Input - Cabin Prices:")
    allCabinPrices foreach println
    println
    println("Expected Output - Best Cabin Prices:")
    CruiseService.getBestGroupPrices(allRates, allCabinPrices) foreach println

    println
    println("Input - Promotions:")
    allPromotions foreach println
    println
    println("Expected Output for All Promotion Combinations:")
    CruiseService.allCombinablePromotions(allPromotions) foreach println

    val combinablePromotionsCurried = CruiseService.combinablePromotions(_: String, allPromotions)
    Seq("P1", "P3") foreach { code =>
      println
      println(s"Expected Output for Promotion Combinations for promotionCode=”$code”:")
      combinablePromotionsCurried(code) foreach println

    }
  }
}
