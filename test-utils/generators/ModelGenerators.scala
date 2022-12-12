/*
 * Copyright 2022 HM Revenue & Customs
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
import models.subscription.request._
import models.{Address, BusinessType, Country}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryCountry: Arbitrary[Country] =
    Arbitrary {
      for {
        state <- Gen.oneOf(Seq("Valid", "Invalid"))
        code  <- Gen.pick(2, 'A' to 'Z')
        name  <- arbitrary[String]
      } yield Country(state, code.mkString, name)
    }

  implicit lazy val arbitraryBusinessWithoutIdAddress: Arbitrary[Address] =
    Arbitrary {
      for {
        addressLine1 <- arbitrary[String]
        addressLine2 <- arbitrary[Option[String]]
        addressLine3 <- arbitrary[String]
        addressLine4 <- arbitrary[Option[String]]
        postCode     <- arbitrary[Option[String]]
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
        name <- arbitrary[String]
      } yield OrganisationDetails(organisationName = name)
    }

  implicit val arbitraryContactInformation: Arbitrary[ContactInformation] =
    Arbitrary {
      for {
        orgDetails <- arbitrary[OrganisationDetails]
        email      <- arbitrary[String]
        phone      <- Gen.option(arbitrary[String])
        mobile     <- Gen.option(arbitrary[String])
      } yield ContactInformation(orgDetails, email, phone, mobile)
    }

  implicit val arbitraryRequestDetail: Arbitrary[RequestDetail] = Arbitrary {
    for {
      idType           <- arbitrary[String]
      idNumber         <- arbitrary[String]
      tradingName      <- arbitrary[String]
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
        receiptDate        <- arbitrary[String]
        acknowledgementRef <- arbitrary[String]
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

  implicit val arbitraryEmailRequest: Arbitrary[EmailRequest] = Arbitrary {
    for {
      to          <- arbitrary[List[String]]
      id          <- arbitrary[String]
      contactName <- arbitrary[Map[String, String]]

    } yield EmailRequest(to, id, contactName)
  }
}
