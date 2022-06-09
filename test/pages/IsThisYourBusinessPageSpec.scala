package pages

import pages.behaviours.PageBehaviours

class IsThisYourBusinessPageSpec extends PageBehaviours {

  "IsThisYourBusinessPage" - {

    beRetrievable[Boolean](IsThisYourBusinessPage)

    beSettable[Boolean](IsThisYourBusinessPage)

    beRemovable[Boolean](IsThisYourBusinessPage)
  }
}
