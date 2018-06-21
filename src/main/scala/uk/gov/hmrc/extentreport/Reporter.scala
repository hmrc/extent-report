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
import java.util

import com.aventstack.extentreports.reporter.ExtentHtmlReporter
import com.aventstack.extentreports.{ExtentReports, ExtentTest}


object Reporter extends ExtentCucumberFormatter {

  private val systemInfoKeyMap = new util.HashMap[String, Boolean]

  override def getExtentHtmlReport: ExtentHtmlReporter = ExtentCucumberFormatter.getExtentHtmlReport

  override def getExtentReport: ExtentReports = ExtentCucumberFormatter.getExtentReport

  private def getCurrentScenario: ExtentTest = ExtentCucumberFormatter.scenarioThreadLocal.get



  def loadXMLConfig(xmlPath: String): Unit = {
    getExtentHtmlReport.loadXMLConfig(xmlPath)
  }

  def loadXMLConfig(file: File): Unit = {
    getExtentHtmlReport.loadXMLConfig(file)
  }



  def loadConfig(xmlPath: String): Unit = {
    getExtentHtmlReport.loadConfig(xmlPath)
  }

  def setSystemInfo(key: String, value: String): Unit = {
    if (systemInfoKeyMap.isEmpty || !systemInfoKeyMap.containsKey(key)) systemInfoKeyMap.put(key, false)
    if (systemInfoKeyMap.get(key)) return
    getExtentReport.setSystemInfo(key, value)
    systemInfoKeyMap.put(key, true)
  }

  def setTestRunnerOutput(log: util.List[String]): Unit = {
    getExtentReport.setTestRunnerOutput(log)
  }


  def setTestRunnerOutput(outputMessage: String): Unit = {
    getExtentReport.setTestRunnerOutput(outputMessage)
  }


}
