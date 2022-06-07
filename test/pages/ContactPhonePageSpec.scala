package pages

import pages.behaviours.PageBehaviours


class ContactPhonePageSpec extends PageBehaviours {

  "ContactPhonePage" - {

    beRetrievable[String](ContactPhonePage)

    beSettable[String](ContactPhonePage)

    beRemovable[String](ContactPhonePage)
  }
}
