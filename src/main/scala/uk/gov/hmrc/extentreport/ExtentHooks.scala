/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.extentreport

import java.io.File

import org.junit.{AfterClass, BeforeClass}
import org.openqa.selenium.WebDriver

trait ExtentHooks {

  val webDriver: WebDriver

  @BeforeClass
  def setup(): Unit = {
    val dirName = "target/test-reports/html-report"
    val extentProperties = ExtentProperties.INSTANCE
    val dir = new File(dirName)
    val successful = dir.mkdir()
    ExtentProperties.create(webDriver, dirName + "/index.html")
  }

  @AfterClass
  def writeExtentReport(): Unit = {
    Reporter.loadXMLConfig("src/test/resources/extent-config.xml")
  }
}
