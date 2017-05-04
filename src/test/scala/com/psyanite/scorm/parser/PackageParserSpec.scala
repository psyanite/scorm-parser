package com.psyanite.scorm.parser

import java.io.File
import java.nio.file.Path

import com.psyanite.scorm.exception.InvalidManifestException
import net.lingala.zip4j.exception.ZipException
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

    describe(PackageParser().getClass.getName) {

        describe("parseZip()") {

            it("should return correct metadata when parsing a valid zip") {
                val zip = buildValidZipFile("valid-zip")
                val metadata = PackageParser().parseZip(zip)
                metadata.items.head.masteryScore should be (None)
                metadata.resources.head.href should be (Some("SCORM.htm"))
            }

            it("should throw %s when parsing a non-existing zip".format(classOf[NullPointerException].getCanonicalName)) {
                val zip = new File("non-existing.zip").toPath
                assertThrows[NullPointerException] {
                    PackageParser().parseZip(zip)
                }
            }

            it("should throw %s when parsing a corrupt zip".format(classOf[ZipException].getCanonicalName)) {
                val zip = buildInvalidZipFile("invalid-zip-corrupt")
                assertThrows[ZipException] {
                    PackageParser().parseZip(zip)
                }
            }

            it("should throw %s when parsing a zip with no manifest file".format(classOf[InvalidManifestException].getCanonicalName)) {
                val zip = buildInvalidZipFile("invalid-zip-no-manifest")
                val exception = intercept[InvalidManifestException] {
                    PackageParser().parseZip(zip)
                }
                exception.getMessage should be ("Manifest file 'imsmanifest.xml' not found")
            }

            it("should throw %s when parsing a zip with a manifest file with no schema".format(classOf[InvalidManifestException].getCanonicalName)) {
                val zip = buildInvalidZipFile("invalid-zip-no-schema")
                val exception = intercept[InvalidManifestException] {
                    PackageParser().parseZip(zip)
                }
                exception.getMessage should be ("Metadata schema and scheme not found")
            }
        }

        describe("parseDirectory()") {

            it("should throw %s when parsing a non-existing directory".format(classOf[NullPointerException].getCanonicalName)) {
                val directory = new File("non-existing-directory").toPath
                assertThrows[NullPointerException] {
                    PackageParser().parseZip(directory)
                }
            }

            it("should throw %s when the manifest file is empty".format(classOf[Exception].getCanonicalName)) {
                val directory = buildInvalidSubDir("invalid-empty")
                val exception = intercept[Exception] {
                    PackageParser().parseDirectory(directory)
                }
                exception.getMessage should be ("Manifest file was not a well-formed XML file")
            }

            it("should throw %s when the manifest file is a random string".format(classOf[Exception].getCanonicalName)) {
                val directory = buildInvalidSubDir("invalid-random-string")
                val exception = intercept[Exception] {
                    PackageParser().parseDirectory(directory)
                }
                exception.getMessage should be ("Manifest file was not a well-formed XML file")
            }

            it("should throw %s when the manifest file is not well-formed".format(classOf[Exception].getCanonicalName)) {
                val directory = buildInvalidSubDir("invalid-not-well-formed")
                val exception = intercept[Exception] {
                    PackageParser().parseDirectory(directory)
                }
                exception.getMessage should be ("Manifest file was not a well-formed XML file")
            }

            it("should throw %s when parsing a directory with no 'imsmanifest.xml' file".format(classOf[InvalidManifestException].getCanonicalName)) {
                val directory = buildInvalidSubDir("invalid-no-manifest-file")
                val exception = intercept[InvalidManifestException] {
                    PackageParser().parseDirectory(directory)
                }
                exception.getMessage should be ("Manifest file 'imsmanifest.xml' not found")
            }

            it("should throw %s when the manifest file has no schema and scheme".format(classOf[InvalidManifestException].getCanonicalName)) {
                val directory = buildInvalidSubDir("invalid-no-schema-no-scheme")
                val exception = intercept[InvalidManifestException] {
                    PackageParser().parseDirectory(directory)
                }
                exception.getMessage should be ("Metadata schema and scheme not found")
            }

            it("should throw %s when the manifest file has no schema value".format(classOf[InvalidManifestException].getCanonicalName)) {
                val directory = buildInvalidSubDir("invalid-no-schema-value")
                val exception = intercept[InvalidManifestException] {
                    PackageParser().parseDirectory(directory)
                }
                exception.getMessage should be ("Metadata schema not found")
            }

            it("should throw %s when the manifest file has no schema version".format(classOf[InvalidManifestException].getCanonicalName)) {
                val directory = buildInvalidSubDir("invalid-no-schema-version")
                val exception = intercept[InvalidManifestException] {
                    PackageParser().parseDirectory(directory)
                }
                exception.getMessage should be ("Metadata schema version not found")
            }

            it("should throw %s when the manifest file has an invalid schema value".format(classOf[InvalidManifestException].getCanonicalName)) {
                val directory = buildInvalidSubDir("invalid-schema-value")
                val exception = intercept[InvalidManifestException] {
                    PackageParser().parseDirectory(directory)
                }
                exception.getMessage should be ("Invalid metadata schema value found; expected 'ADL SCORM', but found 'ADL SCORM1'")
            }

            it("should throw %s when the manifest file has an invalid schema version".format(classOf[InvalidManifestException].getCanonicalName)) {
                val directory = buildInvalidSubDir("invalid-schema-version")
                val exception = intercept[InvalidManifestException] {
                    PackageParser().parseDirectory(directory)
                }
                exception.getMessage should be ("Invalid metadata schema version value found; expected '1.2', but found '1.21'")
            }

            it("should throw %s when the manifest file has an invalid scheme value".format(classOf[InvalidManifestException].getCanonicalName)) {
                val directory = buildInvalidSubDir("invalid-scheme-value")
                val exception = intercept[InvalidManifestException] {
                    PackageParser().parseDirectory(directory)
                }
                exception.getMessage should be ("Invalid metadata scheme value found; expected 'ADL SCORM 1.2', but found 'ADL SCORM 1.21'")
            }

            it("should throw %s when the manifest file has a mastery score smaller than 0".format(classOf[InvalidManifestException].getCanonicalName)) {
                val directory = buildInvalidSubDir("invalid-mastery-score-too-small")
                val exception = intercept[InvalidManifestException] {
                    PackageParser().parseDirectory(directory)
                }
                exception.getMessage should be ("Invalid mastery score '-20' found; expected value between 0 and 100")
            }

            it("should throw %s when the manifest file has a mastery score larger than 100".format(classOf[InvalidManifestException].getCanonicalName)) {
                val directory = buildInvalidSubDir("invalid-mastery-score-too-large")
                val exception = intercept[InvalidManifestException] {
                    PackageParser().parseDirectory(directory)
                }
                exception.getMessage should be ("Invalid mastery score '200' found; expected value between 0 and 100")
            }

            describe("should return correct metadata when parsing a directory with a valid manifest file") {

                it("when the manifest file has both valid metadata schema and scheme") {
                    val directory = buildValidSubDir("valid-1")
                    val metadata = PackageParser().parseDirectory(directory)
                    metadata.items.head.masteryScore should be (Some(99))
                    metadata.resources.head.href should be (Some("heyo.html"))
                }

                it("when the manifest file only has a valid metadata schema") {
                    val directory = buildValidSubDir("valid-noodle-box")
                    val metadata = PackageParser().parseDirectory(directory)
                    metadata.items.head.masteryScore should be (Some(80))
                    metadata.resources.head.href should be (Some("index_lms.html"))
                }

                it("when the manifest file only has a valid metadata scheme") {
                    val directory = buildValidSubDir("valid-blood-and-body")
                    val metadata = PackageParser().parseDirectory(directory)
                    metadata.items.head.masteryScore should be (Some(0))
                    metadata.resources.head.href should be (Some("Blood and Body Fluid Exposure Protocol.html"))
                }

                it("when the manifest file has no mastery score") {
                    val directory = buildValidSubDir("valid-content-packaging-single-sco-scorm-1-2")
                    val metadata = PackageParser().parseDirectory(directory)
                    metadata.items.head.masteryScore should be (None)
                    metadata.resources.head.href should be (Some("shared/launchpage.html"))
                }

                it("when the manifest file has the first SCO resource with an empty href, but a valid second SCO resource") {
                    val directory = buildValidSubDir("valid-2")
                    val metadata = PackageParser().parseDirectory(directory)
                    metadata.items.head.masteryScore should be (Some(69))
                    metadata.resources.head.href should be (Some(""))
                    metadata.resources(1).href should be (Some("hello.html"))
                }

                it("when the manifest file has a single SCO") {
                    val directory = buildValidSubDir("valid-scorm-test-1")
                    val metadata = PackageParser().parseDirectory(directory)
                    metadata.items.head.masteryScore should be (None)
                    metadata.resources.head.href should be (Some("SCORM.htm"))
                }

                it("when the manifest file has multiple SCOs") {
                    val directory = buildValidSubDir("valid-run-time-minimum-calls")
                    val metadata = PackageParser().parseDirectory(directory)
                    metadata.items.head.masteryScore should be (None)
                    metadata.resources.head.href should be (Some("Playing/Playing.html"))
                }
            }
        }
    }
}

