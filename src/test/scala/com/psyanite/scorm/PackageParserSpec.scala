package com.psyanite.scorm

import java.io.File
import java.nio.file.Path

import com.psyanite.scorm.PackageParser
import com.psyanite.scorm.exception.ParseException
import com.psyanite.scorm.validator.ManifestValidator
import org.apache.commons.io.FileUtils
import org.scalatest.{FunSpec, Matchers}

class PackageParserSpec extends FunSpec with Matchers {

    private val BasePath       = "src/test/resources/"
    private val ValidDirPath   = BasePath + "valid/directory/"
    private val InvalidDirPath = BasePath + "invalid/directory/"
    private val ValidZipPath   = BasePath + "valid/zip/"
    private val InvalidZipPath = BasePath + "invalid/zip/"

    private def buildValidSubDir(dir: String): Path = {
        new File(ValidDirPath + dir).toPath
    }

    private def buildInvalidSubDir(dir: String): Path = {
        new File(InvalidDirPath + dir).toPath
    }

    private def buildValidZipFile(zip: String): Path = {
        new File(ValidZipPath + zip + ".zip").toPath
    }

    private def buildInvalidZipFile(zip: String): Path = {
        new File(InvalidZipPath + zip + ".zip").toPath
    }

    describe(PackageParser.getClass.getCanonicalName) {

        describe("parseZip()") {

            it("should return correct metadata when parsing a valid zip") {
                val parser = PackageParser(buildValidZipFile("valid-zip").toFile)
                val manifest = parser.parse
                manifest.items.head.masteryScore should be (None)
                manifest.resources.head.href should be (Some("SCORM.htm"))
                val result = ManifestValidator().validate(manifest)
                result should be (Seq())
                FileUtils.r
            }

            it("should throw %s when parsing a non-existing zip".format(classOf[NullPointerException].getCanonicalName)) {
                val parser = PackageParser(new File("non-existing.zip"))
                val exception = intercept[NullPointerException] {
                    parser.parse
                }
                exception.getMessage should be ("Zip file does not exist")
            }
//
//            it("should throw %s when parsing a corrupt zip".format(classOf[ZipException].getCanonicalName)) {
//                val util = new PackageParser
//                util.zip = Some(buildInvalidZipFile("invalid-zip-corrupt"))
//                assertThrows[ZipException] {
//                    util.parseZip
//                }
//            }
//
//            it("should throw %s when parsing a zip with no manifest file".format(classOf[ParseException].getCanonicalName)) {
//                val util = new PackageParser
//                util.zip = Some(buildInvalidZipFile("invalid-zip-no-manifest"))
//                val exception = intercept[ParseException] {
//                    util.parseZip
//                }
//                exception.getMessage should be ("Manifest file 'imsmanifest.xml' not found")
//            }
//
//            it("should throw %s when parsing a zip with a manifest file with no schema".format(classOf[ParseException].getCanonicalName)) {
//                val util = new PackageParser
//                util.zip = Some(buildInvalidZipFile("invalid-zip-no-schema"))
//                val manifest = util.parseZip
//                val errors = util.validate(manifest)
//                errors should contain ("Metadata schema and scheme not found")
//            }
//        }
//
//        describe("parseDirectory()") {
//
//            it("should throw %s when parsing a non-existing directory".format(classOf[ParseException].getCanonicalName)) {
//                val util = new PackageParser
//                util.directory = Some(new File("non-existing-directory").toPath)
//                val exception = intercept[ParseException] {
//                    util.parse
//                }
//                exception.getMessage should be ("Directory does not exist")
//            }
//
//            it("should throw %s when the manifest file is empty".format(classOf[Exception].getCanonicalName)) {
//                val util = new PackageParser
//                util.directory = Some(buildInvalidSubDir("invalid-empty"))
//                val exception = intercept[ParseException] {
//                    util.parse
//                }
//                exception.getMessage should be ("Manifest file was not a well-formed XML file")
//            }
//
//            it("should throw %s when the manifest file is a random string".format(classOf[Exception].getCanonicalName)) {
//                val util = new PackageParser
//                util.directory = Some(buildInvalidSubDir("invalid-random-string"))
//                val exception = intercept[ParseException] {
//                    util.parse
//                }
//                exception.getMessage should be ("Manifest file was not a well-formed XML file")
//            }
//
//            it("should throw %s when the manifest file is not well-formed".format(classOf[Exception].getCanonicalName)) {
//                val util = new PackageParser
//                util.directory = Some(buildInvalidSubDir("invalid-not-well-formed"))
//                val exception = intercept[ParseException] {
//                    util.parse
//                }
//                exception.getMessage should be ("Manifest file was not a well-formed XML file")
//            }
//
//            it("should throw %s when parsing a directory with no 'imsmanifest.xml' file".format(classOf[ParseException].getCanonicalName)) {
//                val util = new PackageParser
//                util.directory = Some(buildInvalidSubDir("invalid-no-manifest-file"))
//                val exception = intercept[ParseException] {
//                    util.parse
//                }
//                exception.getMessage should be ("Manifest file 'imsmanifest.xml' not found")
//            }
//
//            it("should throw %s when the manifest file has no schema and scheme".format(classOf[ParseException].getCanonicalName)) {
//                val util = new PackageParser
//                util.directory = Some(buildInvalidSubDir("invalid-no-schema-no-scheme"))
//                val manifest = util.parse
//                val errors = util.validate(manifest)
//                errors should contain ("Metadata schema and scheme not found")
//            }
//
//            it("should throw %s when the manifest file has no schema value".format(classOf[ParseException].getCanonicalName)) {
//                val util = new PackageParser
//                util.directory = Some(buildInvalidSubDir("invalid-no-schema-value"))
//                val manifest = util.parse
//                val errors = util.validate(manifest)
//                errors should contain ("Metadata schema value not found")
//            }
//
//            it("should throw %s when the manifest file has no schema version".format(classOf[ParseException].getCanonicalName)) {
//                val util = new PackageParser
//                util.directory = Some(buildInvalidSubDir("invalid-no-schema-version"))
//                val manifest = util.parse
//                val errors = util.validate(manifest)
//                errors should contain ("Metadata schema version value not found")
//            }
//
//            it("should throw %s when the manifest file has an invalid schema value".format(classOf[ParseException].getCanonicalName)) {
//                val util = new PackageParser
//                util.directory = Some(buildInvalidSubDir("invalid-schema-value"))
//                val manifest = util.parse
//                val errors = util.validate(manifest)
//                errors should contain ("Invalid metadata schema value found; expected 'ADL SCORM', but found 'ADL SCORM1'")
//            }
//
//            it("should throw %s when the manifest file has an invalid schema version".format(classOf[ParseException].getCanonicalName)) {
//                val util = new PackageParser
//                util.directory = Some(buildInvalidSubDir("invalid-schema-version"))
//                val manifest = util.parse
//                val errors = util.validate(manifest)
//                errors should contain ("Invalid metadata schema version value found; expected '1.2', but found '1.21'")
//            }
//
//            it("should throw %s when the manifest file has an invalid scheme value".format(classOf[ParseException].getCanonicalName)) {
//                val util = new PackageParser
//                util.directory = Some(buildInvalidSubDir("invalid-scheme-value"))
//                val manifest = util.parse
//                val errors = util.validate(manifest)
//                errors should contain ("Invalid metadata scheme value found; expected 'ADL SCORM 1.2', but found 'ADL SCORM 1.21'")
//            }
//
//            it("should throw %s when the manifest file has a mastery score smaller than 0".format(classOf[ParseException].getCanonicalName)) {
//                val util = new PackageParser
//                util.directory = Some(buildInvalidSubDir("invalid-mastery-score-too-small"))
//                val manifest = util.parse
//                val errors = util.validate(manifest)
//                errors should contain ("Invalid mastery score '-20' found; expected value between 0 and 100")
//            }
//
//            it("should throw %s when the manifest file has a mastery score larger than 100".format(classOf[ParseException].getCanonicalName)) {
//                val util = new PackageParser
//                util.directory = Some(buildInvalidSubDir("invalid-mastery-score-too-large"))
//                val manifest = util.parse
//                val errors = util.validate(manifest)
//                errors should contain ("Invalid mastery score '200' found; expected value between 0 and 100")
//            }
//
//            describe("should return correct metadata when parsing a directory with a valid manifest file") {
//
//                it("when the manifest file has both valid metadata schema and scheme") {
//                    val util = new PackageParser
//                    util.directory = Some(buildValidSubDir("valid-1"))
//                    val manifest = util.parse
//                    manifest.items.head.masteryScore should be (Some(99))
//                    manifest.resources.head.href should be (Some("heyo.html"))
//                    val result = util.validate(manifest)
//                    result should be (Seq())
//                }
//
//                it("when the manifest file only has a valid metadata schema") {
//                    val util = new PackageParser
//                    util.directory = Some(buildValidSubDir("valid-noodle-box"))
//                    val manifest = util.parse
//                    manifest.items.head.masteryScore should be (Some(80))
//                    manifest.resources.head.href should be (Some("index_lms.html"))
//                    val result = util.validate(manifest)
//                    result should be (Seq())
//                }
//
//                it("when the manifest file only has a valid metadata scheme") {
//                    val util = new PackageParser
//                    util.directory = Some(buildValidSubDir("valid-blood-and-body"))
//                    val manifest = util.parse
//                    manifest.items.head.masteryScore should be (Some(0))
//                    manifest.resources.head.href should be (Some("Blood and Body Fluid Exposure Protocol.html"))
//                    val result = util.validate(manifest)
//                    result should be (Seq())
//                }
//
//                it("when the manifest file has no mastery score") {
//                    val util = new PackageParser
//                    util.directory = Some(buildValidSubDir("valid-content-packaging-single-sco-scorm-1-2"))
//                    val manifest = util.parse
//                    manifest.items.head.masteryScore should be (None)
//                    manifest.resources.head.href should be (Some("shared/launchpage.html"))
//                    val result = util.validate(manifest)
//                    result should be (Seq())
//                }
//
//                it("when the manifest file has the first SCO resource with an empty href, but a valid second SCO resource") {
//                    val util = new PackageParser
//                    util.directory = Some(buildValidSubDir("valid-2"))
//                    val manifest = util.parse
//                    manifest.items.head.masteryScore should be (Some(69))
//                    manifest.resources.head.href should be (Some(""))
//                    manifest.resources(1).href should be (Some("hello.html"))
//                    val result = util.validate(manifest)
//                    result should be (Seq())
//                }
//
//                it("when the manifest file has a single SCO") {
//                    val util = new PackageParser
//                    util.directory = Some(buildValidSubDir("valid-scorm-test-1"))
//                    val manifest = util.parse
//                    manifest.items.head.masteryScore should be (None)
//                    manifest.resources.head.href should be (Some("SCORM.htm"))
//                    val result = util.validate(manifest)
//                    result should be (Seq())
//                }
//
//                it("when the manifest file has multiple SCOs") {
//                    val util = new PackageParser
//                    util.directory = Some(buildValidSubDir("valid-run-time-minimum-calls"))
//                    val manifest = util.parse
//                    manifest.items.head.masteryScore should be (None)
//                    manifest.resources.head.href should be (Some("Playing/Playing.html"))
//                    val result = util.validate(manifest)
//                    result should be (Seq())
//                }
//            }
        }
    }
}

