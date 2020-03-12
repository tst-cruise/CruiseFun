package com.tst.service

import com.tst.model.CruiseModels.{CabinPrice, Promotion, Rate}

object CruiseDataStore {
  val DefaultRateCodeGroups = Seq(
    ("M1", "Military"),
    ("M2", "Military"),
    ("S1", "Senior"),
    ("S2" -> "Senior")
  )

  val allRates: Seq[Rate] = DefaultRateCodeGroups map Rate.tupled
  val allCabinPrices: Seq[CabinPrice] = Seq(
    CabinPrice("CA", "M1", 200.00),
    CabinPrice("CA", "M2", 250.00),
    CabinPrice("CA", "S1", 225.00),
    CabinPrice("CA", "S2", 260.00),
    CabinPrice("CB", "M1", 230.00),
    CabinPrice("CB", "M2", 260.00),
    CabinPrice("CB", "S1", 245.00),
    CabinPrice("CB", "S2", 270.00)
  )


  val allPromotions: Seq[Promotion] = Seq(
    Promotion("P1", Seq("P3")), // P1 is not combinable with P3
    Promotion("P2", Seq("P4", "P5")), // P2 is not combinable with P4 and P5
    Promotion("P3", Seq("P1")), // P3 is not combinable with P1
    Promotion("P4", Seq("P2")), // P4 is not combinable with P2
    Promotion("P5", Seq("P2")) // P5 is not combinable with P2
  )

}
