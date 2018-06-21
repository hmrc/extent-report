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

import java.io.{BufferedOutputStream, File, FileOutputStream}
import java.util
import com.aventstack.extentreports.markuputils.MarkupHelper
import com.aventstack.extentreports.reporter.ExtentHtmlReporter
import com.aventstack.extentreports.{ExtentReports, ExtentTest, GherkinKeyword}
import gherkin.formatter.model._
import gherkin.formatter.{Formatter, Reporter}
import org.openqa.selenium.{OutputType, TakesScreenshot, WebDriver}

class ExtentCucumberFormatter(file: File) extends Reporter with Formatter {

  private var htmlReporter: ExtentHtmlReporter = new ExtentHtmlReporter(new File(ExtentProperties.getReportPath))
  private var extentReports: ExtentReports = new ExtentReports
  val featureTestThreadLocal = new InheritableThreadLocal[ExtentTest]
  private val scenarioOutlineThreadLocal = new InheritableThreadLocal[ExtentTest]
  private val stepListThreadLocal = new InheritableThreadLocal[util.LinkedList[Step]]
  val scenarioThreadLocal = new InheritableThreadLocal[ExtentTest]
  val stepTestThreadLocal = new InheritableThreadLocal[ExtentTest]
  private var scenarioOutlineFlag = false
  var scenarioName: String = null
  var webDriver : WebDriver = null

  private def setExtentReport(): Unit = {
    extentReports.attachReporter(htmlReporter)
  }

  def this() = {
    this(file = new File(ExtentProperties.getReportPath))
    setExtentReport()
    stepListThreadLocal.set(new util.LinkedList[Step])
    scenarioOutlineFlag = false
  }

  def getExtentHtmlReport: ExtentHtmlReporter = {
    htmlReporter
  }


  def getExtentReport: ExtentReports = {
    extentReports
  }

  override def before(`match`: Match, result: Result): Unit = {}

  override def result(result: Result): Unit = {
    if (scenarioOutlineFlag) return

    if (Result.PASSED == result.getStatus) stepTestThreadLocal.get.pass(Result.PASSED)
    else if (Result.FAILED == result.getStatus) {
      stepTestThreadLocal.get.fail(result.getError)
      val destinationPath = screenShot(result)
      stepTestThreadLocal.get.addScreenCaptureFromPath(destinationPath.get.getName)
    }
    else if (Result.SKIPPED == result) stepTestThreadLocal.get.skip(Result.SKIPPED.getStatus)
    else if (Result.UNDEFINED == result) stepTestThreadLocal.get.skip(Result.UNDEFINED.getStatus)
  }

  override def after(`match`: Match, result: Result): Unit = {}

  override def `match`(`match`: Match): Unit = {
    val step = stepListThreadLocal.get.poll
    var data: Array[Array[String]] = null
    if (step.getRows != null) {
      val rows = step.getRows
      val rowSize = rows.size
      var i = 0
      while ( {
        i < rowSize
      }) {
        val dataTableRow = rows.get(i)
        val cells = dataTableRow.getCells
        val cellSize = cells.size
        if (data == null) data = Array.ofDim[String](rowSize, cellSize)
        var j = 0
        while ( {
          j < cellSize
        }) {
          data(i)(j) = cells.get(j)

          {
            j += 1;
            j - 1
          }
        }

        {
          i += 1;
          i - 1
        }
      }
    }

    val scenarioTest = scenarioThreadLocal.get
    var stepTest: ExtentTest = null

    try
      stepTest = scenarioTest.createNode(new GherkinKeyword(step.getKeyword), step.getKeyword + step.getName)
    catch {
      case e: ClassNotFoundException =>
        e.printStackTrace()
    }

    if (data != null) {
      val table = MarkupHelper.createTable(data)
      stepTest.info(table)
    }

    stepTestThreadLocal.set(stepTest)
  }

  override def embedding(mimeType: String, data: Array[Byte]): Unit = {}

  override def write(text: String): Unit = {}

  override def syntaxError(state: String, event: String, legalEvents: util.List[String], uri: String, line: Integer): Unit = {}

  override def uri(uri: String): Unit = {}

  override def feature(feature: Feature): Unit = {
    setExtentReport()
    stepListThreadLocal.set(new util.LinkedList[Step])
    scenarioOutlineFlag = false

    featureTestThreadLocal.set(extentReports.createTest(classOf[com.aventstack.extentreports.gherkin.model.Feature], feature.getName))
    val test = featureTestThreadLocal.get

    import scala.collection.JavaConversions._
    for (tag <- feature.getTags) {
      test.assignCategory(tag.getName)
    }
  }

  override def scenarioOutline(scenarioOutline: ScenarioOutline): Unit = {
    scenarioOutlineFlag = true
    val node = featureTestThreadLocal.get.createNode(
      classOf[com.aventstack.extentreports.gherkin.model.ScenarioOutline], scenarioOutline.getName)
    scenarioOutlineThreadLocal.set(node)
  }

  override def examples(examples: Examples): Unit = {
    val test = scenarioOutlineThreadLocal.get

    var data: Array[Array[String]] = null
    val rows = examples.getRows
    val rowSize = rows.size
    var i = 0
    while ( {
      i < rowSize
    }) {
      val examplesTableRow = rows.get(i)
      val cells = examplesTableRow.getCells
      val cellSize = cells.size
      if (data == null) data = Array.ofDim[String](rowSize, cellSize)
      var j = 0
      while ( {
        j < cellSize
      }) {
        data(i)(j) = cells.get(j)

        {
          j += 1;
          j - 1
        }
      }

      {
        i += 1;
        i - 1
      }
    }
    test.info(MarkupHelper.createTable(data))
  }

  override def startOfScenarioLifeCycle(scenario: Scenario): Unit = {
    if (scenarioOutlineFlag) scenarioOutlineFlag = false

    var scenarioNode: ExtentTest = null
    if (scenarioOutlineThreadLocal.get != null && scenario.getKeyword.trim.equalsIgnoreCase("Scenario Outline")) scenarioNode = scenarioOutlineThreadLocal.get.createNode(classOf[com.aventstack.extentreports.gherkin.model.Scenario], scenario.getName)
    else scenarioNode = featureTestThreadLocal.get.createNode(classOf[com.aventstack.extentreports.gherkin.model.Scenario], scenario.getName)

    import scala.collection.JavaConversions._
    for (tag <- scenario.getTags) {
      scenarioNode.assignCategory(tag.getName)
    }
    scenarioThreadLocal.set(scenarioNode)
    scenarioName = scenario.getName
  }

  override def background(background: Background): Unit = {}

  override def scenario(scenario: Scenario): Unit = {}

  override def step(step: Step): Unit = {
    if (scenarioOutlineFlag) return
    stepListThreadLocal.get.add(step)
  }

  override def endOfScenarioLifeCycle(scenario: Scenario): Unit = {}

  override def done(): Unit = {
    getExtentReport.flush()
  }

  override def close(): Unit = {}

  override def eof(): Unit = {}

  def screenShot(result: Result): Option[File] = {
    webDriver = ExtentProperties.getWebDriver
    webDriver match {
      case screenshot1: TakesScreenshot =>
        val screenshot: Array[Byte] = screenshot1.getScreenshotAs(OutputType.BYTES)
        val screenshotName = scenarioName
        val bos = new BufferedOutputStream(new FileOutputStream("target/" + screenshotName + ".png"))
        bos.write(screenshot)
        bos.close()
        val destinationPath: File = new File("target/" + screenshotName + ".png")
        Some(destinationPath)

    }
  }
}

object ExtentCucumberFormatter extends ExtentCucumberFormatter {

}
