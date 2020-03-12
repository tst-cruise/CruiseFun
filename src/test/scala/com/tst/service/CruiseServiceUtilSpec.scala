package com.tst.service

import com.tst.model.CruiseModels.{BestGroupPrice, CabinPrice, Rate}

class CruiseServiceUtilSpec extends CruiseSpecBase {


  test("CruiseServiceUtil#getBestGroupPrices should find the lowest price for each distinct pair of cabin code and rate group ") {
    val uniqueCabinCodes = distinctCabinCodes(CruiseDataStore.allCabinPrices)
    val uniqueRateGroups = distinctRateGroups(CruiseDataStore.allRates)
    val bestGroupPrinces = CruiseServiceUtil.getBestGroupPrices(CruiseDataStore.allRates, CruiseDataStore.allCabinPrices)(CruiseDataStore.DefaultRateCodeGroups.toMap.get)

    val expectedPermutations = uniqueCabinCodes.length * uniqueRateGroups.length
    val actualPermutations = bestGroupPrinces.length

    // assertion based on invariant properties
    assert(expectedPermutations == actualPermutations)

    // Assert every best group price will not be higher than any price for the corresponding cabin code and rate code.
    val asserts: Iterable[Boolean] = for {
      cabinCode <- uniqueCabinCodes
      rateCode <- CruiseDataStore.allRates.map(_.rateCode)
      allMatchedPrices = findCabinPrices(CruiseDataStore.allCabinPrices, cabinCode, rateCode).map(_.price)
      bestMatchedGroupPrices = findCabinPrices(toCabinPrices(bestGroupPrinces), cabinCode, rateCode).map(_.price)
    } yield allMatchedPrices forall { price => bestMatchedGroupPrices.forall(groupPrice => groupPrice <= price) }


    assert(asserts.forall(identity))
  }

  private def distinctRateGroups(rates: Seq[Rate]): Seq[String] = {
    distinctAttributes(rates)(_.rateGroup)
  }

  private def distinctCabinCodes(prices: Seq[CabinPrice]): Seq[String] = {
    distinctAttributes(prices)(_.cabinCode)
  }

  // This method is built on top of Scala core libraries and therefore will not be test.
  private def distinctAttributes[A](entries: Seq[A])(extractor: A => String): Seq[String] = {
    (entries map extractor).distinct
  }

  private def toCabinPrices(bestGroupPrinces: Seq[BestGroupPrice]): Seq[CabinPrice] = {
    bestGroupPrinces map { groupPrice => CabinPrice(groupPrice.cabinCode, groupPrice.rateCode, groupPrice.price) }
  }

  private def findCabinPrices(allCabinPrices: Seq[CabinPrice], cabinCode: String, rateCode: String): Seq[CabinPrice] = {
    allCabinPrices.filter(princeEntry => princeEntry.cabinCode == cabinCode && princeEntry.rateCode == rateCode)
  }

}
