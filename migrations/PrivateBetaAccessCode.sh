#!/bin/bash

echo ""
echo "Applying migration PrivateBetaAccessCode"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /privateBetaAccessCode                        controllers.PrivateBetaAccessCodeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /privateBetaAccessCode                        controllers.PrivateBetaAccessCodeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePrivateBetaAccessCode                  controllers.PrivateBetaAccessCodeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePrivateBetaAccessCode                  controllers.PrivateBetaAccessCodeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "privateBetaAccessCode.title = privateBetaAccessCode" >> ../conf/messages.en
echo "privateBetaAccessCode.heading = privateBetaAccessCode" >> ../conf/messages.en
echo "privateBetaAccessCode.checkYourAnswersLabel = privateBetaAccessCode" >> ../conf/messages.en
echo "privateBetaAccessCode.error.required = Enter privateBetaAccessCode" >> ../conf/messages.en
echo "privateBetaAccessCode.error.length = PrivateBetaAccessCode must be 100 characters or less" >> ../conf/messages.en
echo "privateBetaAccessCode.change.hidden = PrivateBetaAccessCode" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPrivateBetaAccessCodeUserAnswersEntry: Arbitrary[(PrivateBetaAccessCodePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[PrivateBetaAccessCodePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPrivateBetaAccessCodePage: Arbitrary[PrivateBetaAccessCodePage.type] =";\
    print "    Arbitrary(PrivateBetaAccessCodePage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(PrivateBetaAccessCodePage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration PrivateBetaAccessCode completed"
