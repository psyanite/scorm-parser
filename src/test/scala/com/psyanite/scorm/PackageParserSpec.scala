package com.psyanite.scorm

import java.io.File
import java.nio.file.Path

import com.psyanite.scorm.exception.ParseException
import com.psyanite.scorm.validator.ManifestValidator
import net.lingala.zip4j.exception.ZipException
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

    private def buildInvalidDir(dir: String): Path = {
        new File(InvalidDirPath + dir).toPath
    }

    private def buildValidZipFile(zip: String): File = {
        new File(ValidZipPath + zip + ".zip")
    }

    private def buildInvalidZipFile(zip: String): File = {
        new File(InvalidZipPath + zip + ".zip")
    }

    describe(PackageParser.getClass.getCanonicalName) {

        describe("parsing a zip") {

            it("should return correct metadata when parsing a valid zip") {
                val parser = PackageParser(buildValidZipFile("valid-zip"))
                val manifest = parser.parse
                manifest.items.head.masteryScore should be (None)
                manifest.resources.head.href should be (Some("SCORM.htm"))
                val result = ManifestValidator().validate(manifest)
                result should be (Seq())
                FileUtils.deleteDirectory(parser.path.toFile)
            }

            it("should throw %s when parsing a non-existing zip".format(classOf[NullPointerException].getCanonicalName)) {
                val exception = intercept[NullPointerException] {
                    PackageParser(new File("non-existing.zip"))
                }
                exception.getMessage should be ("Zip file does not exist")
            }

            it("should throw %s when parsing a corrupt zip".format(classOf[ZipException].getCanonicalName)) {
                val exception = intercept[ZipException] {
                    PackageParser(buildInvalidZipFile("invalid-zip-corrupt"))
                }
                exception.getMessage should be ("zip headers not found. probably not a zip file")
            }

            it("should throw %s when parsing a zip with no manifest file".format(classOf[ParseException].getCanonicalName)) {
                val parser = PackageParser(buildInvalidZipFile("invalid-zip-no-manifest"))
                val exception = intercept[ParseException] {
                    parser.parse
                }
                exception.getMessage should be ("Manifest file 'imsmanifest.xml' not found")
                FileUtils.deleteDirectory(parser.path.toFile)
            }

            it("should throw %s when parsing a zip with a manifest file with no schema".format(classOf[ParseException].getCanonicalName)) {
                val parser = PackageParser(buildInvalidZipFile("invalid-zip-no-schema"))
                val manifest = parser.parse
                val errors = ManifestValidator().validate(manifest)
                errors should contain ("Metadata schema and scheme not found")
                FileUtils.deleteDirectory(parser.path.toFile)
            }
        }

        describe("parsing a directory") {

            it("should throw %s when parsing a non-existing directory".format(classOf[ParseException].getCanonicalName)) {
                val parser = PackageParser(new File("non-existing-directory").toPath)
                val exception = intercept[NullPointerException] {
                    parser.parse
                }
                exception.getMessage should be ("Directory does not exist")
            }

            it("should throw %s when the manifest file is empty".format(classOf[Exception].getCanonicalName)) {
                val parser = PackageParser(buildInvalidDir("invalid-empty"))
                val exception = intercept[ParseException] {
                    parser.parse
                }
                exception.getMessage should be ("Manifest file was not a well-formed XML file")
            }

            it("should throw %s when the manifest file is a random string".format(classOf[Exception].getCanonicalName)) {
                val parser = PackageParser(buildInvalidDir("invalid-random-string"))
                val exception = intercept[ParseException] {
                    parser.parse
                }
                exception.getMessage should be ("Manifest file was not a well-formed XML file")
            }

            it("should throw %s when the manifest file is not well-formed".format(classOf[Exception].getCanonicalName)) {
                val parser = PackageParser(buildInvalidDir("invalid-not-well-formed"))
                val exception = intercept[ParseException] {
                    parser.parse
                }
                exception.getMessage should be ("Manifest file was not a well-formed XML file")
            }

            it("should throw %s when parsing a directory with no 'imsmanifest.xml' file".format(classOf[ParseException].getCanonicalName)) {
                val parser = PackageParser(buildInvalidDir("invalid-no-manifest-file"))
                val exception = intercept[ParseException] {
                    parser.parse
                }
                exception.getMessage should be ("Manifest file 'imsmanifest.xml' not found")
            }

            it("should throw %s when the manifest file has no schema and scheme".format(classOf[ParseException].getCanonicalName)) {
                val parser = PackageParser(buildInvalidDir("invalid-no-schema-no-scheme"))
                val manifest = parser.parse
                val errors = ManifestValidator().validate(manifest)
                errors should contain ("Metadata schema and scheme not found")
            }

            it("should throw %s when the manifest file has an invalid schema value".format(classOf[ParseException].getCanonicalName)) {
                val parser = PackageParser(buildInvalidDir("invalid-schema-value"))
                val manifest = parser.parse
                val errors = ManifestValidator().validate(manifest)
                errors should contain ("Invalid metadata schema value found; expected 'ADL SCORM', but found 'ADL SCORM1'")
            }

            it("should throw %s when the manifest file has an invalid schema version value".format(classOf[ParseException].getCanonicalName)) {
                val parser = PackageParser(buildInvalidDir("invalid-schema-version-value"))
                val manifest = parser.parse
                val errors = ManifestValidator().validate(manifest)
                errors should contain ("Invalid metadata schema version value found; expected '1.2', but found '1.21'")
            }

            it("should throw %s when the manifest file has an invalid scheme value".format(classOf[ParseException].getCanonicalName)) {
                val parser = PackageParser(buildInvalidDir("invalid-scheme-value"))
                val manifest = parser.parse
                val errors = ManifestValidator().validate(manifest)
                errors should contain ("Invalid metadata scheme value found; expected 'ADL SCORM 1.2', but found 'ADL SCORM 1.21'")
            }

            it("should throw %s when the manifest file has a mastery score smaller than 0".format(classOf[ParseException].getCanonicalName)) {
                val parser = PackageParser(buildInvalidDir("invalid-mastery-score-too-small"))
                val manifest = parser.parse
                val errors = ManifestValidator().validate(manifest)
                errors should contain ("Invalid mastery score '-20' found; expected value between 0 and 100")
            }

            it("should throw %s when the manifest file has a mastery score larger than 100".format(classOf[ParseException].getCanonicalName)) {
                val parser = PackageParser(buildInvalidDir("invalid-mastery-score-too-large"))
                val manifest = parser.parse
                val errors = ManifestValidator().validate(manifest)
                errors should contain ("Invalid mastery score '200' found; expected value between 0 and 100")
            }

            describe("should return correct metadata when parsing a directory with a valid manifest file") {

                it("when the manifest file has a valid metadata schema, version and scheme") {
                    val parser = PackageParser(buildValidSubDir("valid-schema-version-and-scheme"))
                    val manifest = parser.parse
                    manifest.items.head.masteryScore should be (Some(99))
                    manifest.resources.head.href should be (Some("heyo.html"))
                    val result = ManifestValidator().validate(manifest)
                    result should be (Seq())
                }

                it("when the manifest file only has a valid metadata schema and version") {
                    val parser = PackageParser(buildValidSubDir("valid-schema-and-version"))
                    val manifest = parser.parse
                    manifest.items.head.masteryScore should be (Some(80))
                    manifest.resources.head.href should be (Some("index_lms.html"))
                    val result = ManifestValidator().validate(manifest)
                    result should be (Seq())
                }

                it("when the manifest file only has a valid metadata schema version") {
                    val parser = PackageParser(buildValidSubDir("valid-only-schema-version"))
                    val manifest = parser.parse
                    manifest.items.head.masteryScore should be (None)
                    manifest.resources.head.href should be (Some("mod1.htm"))
                    val result = ManifestValidator().validate(manifest)
                    result should be (Seq())
                }

                it("when the manifest file only has a valid metadata scheme") {
                    val parser = PackageParser(buildValidSubDir("valid-only-scheme"))
                    val manifest = parser.parse
                    manifest.items.head.masteryScore should be (Some(0))
                    manifest.resources.head.href should be (Some("Blood and Body Fluid Exposure Protocol.html"))
                    val result = ManifestValidator().validate(manifest)
                    result should be (Seq())
                }

                it("when the manifest file has no mastery score") {
                    val parser = PackageParser(buildValidSubDir("valid-no-mastery-score"))
                    val manifest = parser.parse
                    manifest.items.head.masteryScore should be (None)
                    manifest.resources.head.href should be (Some("shared/launchpage.html"))
                    val result = ManifestValidator().validate(manifest)
                    result should be (Seq())
                }

                it("when the manifest file has the first SCO resource with an empty href, but a valid second SCO resource") {
                    val parser = PackageParser(buildValidSubDir("valid-two-resources"))
                    val manifest = parser.parse
                    manifest.items.head.masteryScore should be (Some(69))
                    manifest.resources.head.href should be (Some(""))
                    manifest.resources(1).href should be (Some("hello.html"))
                    val result = ManifestValidator().validate(manifest)
                    result should be (Seq())
                }

                it("when the manifest file has a single SCO") {
                    val parser = PackageParser(buildValidSubDir("valid-single-sco"))
                    val manifest = parser.parse
                    manifest.items.head.masteryScore should be (None)
                    manifest.resources.head.href should be (Some("SCORM.htm"))
                    val result = ManifestValidator().validate(manifest)
                    result should be (Seq())
                }

                it("when the manifest file has multiple SCOs") {
                    val parser = PackageParser(buildValidSubDir("valid-multi-sco"))
                    val manifest = parser.parse
                    manifest.items.head.masteryScore should be (None)
                    manifest.resources.head.href should be (Some("Playing/Playing.html"))
                    val result = ManifestValidator().validate(manifest)
                    result should be (Seq())
                }
            }
        }
    }
}

