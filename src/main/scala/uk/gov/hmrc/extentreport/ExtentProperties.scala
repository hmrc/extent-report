/*
 * Copyright 2018 HM Revenue & Customs
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

import org.openqa.selenium.WebDriver

object ExtentProperties extends Enumeration {
  type ExtentProperties = Value
  val INSTANCE = Value
  var reportPath: String = _
  var extentXServerUrl: String = _
  var projectName: String = _
  var webDriver: WebDriver = _





  def create(wd: WebDriver, path: String): ExtentProperties = {
    reportPath = "output" + File.separator + "Run_" + System.currentTimeMillis + File.separator + "report.html"
    projectName = "default"
    webDriver = wd
    reportPath = path
    INSTANCE
  }


}