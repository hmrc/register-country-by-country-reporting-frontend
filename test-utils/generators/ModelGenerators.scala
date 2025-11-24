/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package generators

import models.email.EmailRequest
import models.matching.RegistrationInfo
import models.register.response.details.AddressResponse
import models.subscription.request._
import models.{Address, BusinessType, Country, SafeId, UniqueTaxpayerReference}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  def nonEmptyString: Gen[String] =
    Gen.nonEmptyListOf(Gen.alphaChar).map(_.mkString)

  implicit lazy val arbitraryCountry: Arbitrary[Country] =
    Arbitrary {
      for {
        state <- Gen.oneOf(Seq("Valid", "Invalid"))
        code  <- Gen.pick(2, 'A' to 'Z')
        name  <- nonEmptyString
      } yield Country(state, code.mkString, name)
    }

  implicit lazy val arbitraryPostCode: Gen[Option[String]] =
    Gen.option(Gen.stringOfN(6, Gen.alphaNumChar))

  implicit lazy val arbitraryBusinessWithoutIdAddress: Arbitrary[Address] =
    Arbitrary {
      for {
        addressLine1 <- nonEmptyString
        addressLine2 <- arbitrary[Option[String]]
        addressLine3 <- nonEmptyString
        addressLine4 <- arbitrary[Option[String]]
        postCode     <- arbitraryPostCode
        country      <- arbitrary[Country]
      } yield Address(addressLine1, addressLine2, addressLine3, addressLine4, postCode, country)
    }

  implicit lazy val arbitraryBusinessType: Arbitrary[BusinessType] =
    Arbitrary {
      Gen.oneOf(BusinessType.values.toSeq)
    }

  implicit val arbitraryOrganisationDetails: Arbitrary[OrganisationDetails] =
    Arbitrary {
      for {
        name <- nonEmptyString
      } yield OrganisationDetails(organisationName = name)
    }

  implicit val arbitraryContactInformation: Arbitrary[ContactInformation] =
    Arbitrary {
      for {
        orgDetails <- arbitrary[OrganisationDetails]
        email      <- nonEmptyString
        phone      <- Gen.option(nonEmptyString)
        mobile     <- Gen.option(nonEmptyString)
      } yield ContactInformation(orgDetails, email, phone, mobile)
    }

  implicit val arbitraryRequestDetail: Arbitrary[RequestDetail] = Arbitrary {
    for {
      idType           <- nonEmptyString
      idNumber         <- nonEmptyString
      tradingName      <- nonEmptyString
      isGBUser         <- arbitrary[Boolean]
      primaryContact   <- arbitrary[ContactInformation]
      secondaryContact <- Gen.option(arbitrary[ContactInformation])
    } yield RequestDetail(
      IDType = idType,
      IDNumber = idNumber,
      tradingName = Some(tradingName),
      isGBUser = isGBUser,
      primaryContact = primaryContact,
      secondaryContact = secondaryContact
    )
  }

  implicit val arbitraryRequestCommonForSubscription: Arbitrary[RequestCommonForSubscription] =
    Arbitrary {
      for {
        receiptDate        <- nonEmptyString
        acknowledgementRef <- nonEmptyString
      } yield RequestCommonForSubscription(
        regime = "CBC",
        conversationID = None,
        receiptDate = receiptDate,
        acknowledgementReference = acknowledgementRef,
        originatingSystem = "MDTP",
        None
      )
    }

  implicit val arbitraryCreateSubscriptionForCBCRequest: Arbitrary[CreateSubscriptionForCBCRequest] =
    Arbitrary {
      for {
        requestCommon <- arbitrary[RequestCommonForSubscription]
        requestDetail <- arbitrary[RequestDetail]
      } yield CreateSubscriptionForCBCRequest(
        SubscriptionRequest(requestCommon, requestDetail)
      )
    }

  implicit lazy val arbitraryUniqueTaxpayerReference: Arbitrary[UniqueTaxpayerReference] = Arbitrary {
    for {
      utr <- nonEmptyString
    } yield UniqueTaxpayerReference(utr)
  }

  implicit val arbitraryEmailRequest: Arbitrary[EmailRequest] = Arbitrary {
    for {
      to          <- arbitrary[List[String]]
      id          <- nonEmptyString
      contactName <- arbitrary[Map[String, String]]

    } yield EmailRequest(to, id, contactName)
  }

  implicit lazy val arbitrarySafeId: Arbitrary[SafeId] = Arbitrary {
    for {
      value <- nonEmptyString
    } yield SafeId(value)
  }

  implicit lazy val arbitraryAddressResponse: Arbitrary[AddressResponse] =
    Arbitrary {
      for {
        addressLine1 <- nonEmptyString
        addressLine2 <- arbitrary[Option[String]]
        addressLine3 <- arbitrary[Option[String]]
        addressLine4 <- arbitrary[Option[String]]
        postCode     <- arbitraryPostCode
        countryCode  <- Gen.numStr
      } yield AddressResponse(addressLine1, addressLine2, addressLine3, addressLine4, postCode, countryCode)
    }

  implicit val arbitraryRegistrationInfo: Arbitrary[RegistrationInfo] = Arbitrary {
    for {
      safeId  <- arbitrarySafeId.arbitrary
      name    <- nonEmptyString
      address <- arbitraryAddressResponse.arbitrary

    } yield RegistrationInfo(safeId, name, address)
  }
}
