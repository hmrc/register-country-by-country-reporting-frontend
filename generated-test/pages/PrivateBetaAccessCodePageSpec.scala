package pages

import pages.behaviours.PageBehaviours

class PrivateBetaAccessCodePageSpec extends PageBehaviours {

  "PrivateBetaAccessCodePage" - {

    beRetrievable[String](PrivateBetaAccessCodePage)

    beSettable[String](PrivateBetaAccessCodePage)

    beRemovable[String](PrivateBetaAccessCodePage)
  }
}
