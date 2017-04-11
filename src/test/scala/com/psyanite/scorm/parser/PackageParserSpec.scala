package com.psyanite.scorm.parser

import java.io.File

import org.scalatest.{FunSpec, Matchers}

class PackageParserSpec extends FunSpec with Matchers {

    private val BasePath       = "src/test/resources/"
    private val ValidDirPath   = BasePath + "valid/directory/"
    private val InvalidDirPath = BasePath + "invalid/directory/"
    private val ValidZipPath   = BasePath + "valid/zip/"
    private val InvalidZipPath = BasePath + "invalid/zip/"

    private def buildValidSubDir(dir: String): File = {
        new File(ValidDirPath + dir)
    }

    private def buildInvalidSubDir(dir: String): File = {
        new File(InvalidDirPath + dir)
    }

    private def buildValidZipFile(zip: String): File = {
        new File(ValidZipPath + zip + ".zip")
    }

    private def buildInvalidZipFile(zip: String): File = {
        new File(InvalidZipPath + zip + ".zip")
    }

    describe(PackageParser.getClass.getName) {

        describe("parseZip()") {

            it("should return false when parsing a non-existing zip") {
                val zip = new File("non-existing.zip")
                val result = PackageParser.get.parseZip(zip)
                result.success should be (false)
                result.errors should be (Set("File does not exist, " + zip.toString))
            }

            it("should return true when parsing a valid zip") {
                val zip = buildValidZipFile("valid-zip")
                val result = PackageParser.get.parseZip(zip)
                result.success should be (true)
                result.errors should be (Set())
                result.entryPoint should be (Some("SCORM.htm"))
                result.score should be (None)
            }

            it("should return false when parsing a corrupt zip") {
                val zip = buildInvalidZipFile("invalid-zip-corrupt")
                val result = PackageParser.get.parseZip(zip)
                result.success should be (false)
                result.errors should contain ("Error unzipping zip file, " + zip.toString)
                result.entryPoint should be (None)
                result.score should be (None)
            }

            it("should return false when parsing a zip with no manifest file") {
                val zip = buildInvalidZipFile("invalid-zip-no-manifest")
                val result = PackageParser.get.parseZip(zip)
                result.success should be (false)
                result.errors should contain ("Manifest file 'imsmanifest.xml' not found")
                result.entryPoint should be (None)
                result.score should be (None)
            }

            it("should return false when parsing a zip with a manifest file with no schema") {
                val zip = buildInvalidZipFile("invalid-zip-no-schema")
                val result = PackageParser.get.parseZip(zip)
                result.success should be (false)
                result.errors should contain ("Metadata schema and scheme not found")
            }
        }

        describe("parseDirectory()") {

            it("should return false when parsing a non-existing directory") {
                val directory = new File("non-existing-directory")
                val result = PackageParser.get.parseDirectory(directory)
                result.success should be (false)
                result.errors should contain ("Directory does not exist, " + directory.toString)
                result.entryPoint should be (None)
                result.score should be (None)
            }

            it("should return false when parsing a directory with no 'imsmanifest.xml' file") {
                val directory = buildInvalidSubDir("invalid-no-manifest-file")
                val result = PackageParser.get.parseDirectory(directory)
                result.success should be (false)
                result.errors should contain ("Manifest file 'imsmanifest.xml' not found")
                result.entryPoint should be (None)
                result.score should be (None)
            }

            describe("should return false when parsing a directory with an invalid manifest file") {

                it("when the manifest file is empty") {
                    val result = PackageParser.get.parseDirectory(buildInvalidSubDir("invalid-empty"))
                    result.success should be (false)
                    result.errors should contain ("SCO entry point not found")
                    result.errors should contain ("Metadata schema and scheme not found")
                }

                it("when the manifest file is a random string") {
                    val result = PackageParser.get.parseDirectory(buildInvalidSubDir("invalid-random-string"))
                    result.success should be (false)
                    result.errors should contain ("SCO entry point not found")
                    result.errors should contain ("Metadata schema and scheme not found")
                    result.entryPoint should be (None)
                    result.score should be (None)
                }

                it("when the manifest file is not well-formed") {
                    val result = PackageParser.get.parseDirectory(buildInvalidSubDir("invalid-not-well-formed"))
                    result.success should be (false)
                    result.errors should be (Set("Manifest file was not a well-formed XML file"))
                }

                it("when the manifest file has no schema and scheme") {
                    val result = PackageParser.get.parseDirectory(buildInvalidSubDir("invalid-no-schema-no-scheme"))
                    result.success should be (false)
                    result.errors should be (Set("Metadata schema and scheme not found"))
                }

                it("when the manifest file has no schema value") {
                    val result = PackageParser.get.parseDirectory(buildInvalidSubDir("invalid-no-schema-value"))
                    result.success should be (false)
                    result.errors should be (Set("Schema value not found"))
                }

                it("when the manifest file has no schema version") {
                    val result = PackageParser.get.parseDirectory(buildInvalidSubDir("invalid-no-schema-version"))
                    result.success should be (false)
                    result.errors should be (Set("Schema version not found"))
                }

                it("when the manifest file has an invalid schema value") {
                    val result = PackageParser.get.parseDirectory(buildInvalidSubDir("invalid-schema-value"))
                    result.success should be (false)
                    result.errors should be (Set("Invalid schema value found; expected 'ADL SCORM', but found 'ADL SCORM1'"))
                }

                it("when the manifest file has an invalid schema version") {
                    val result = PackageParser.get.parseDirectory(buildInvalidSubDir("invalid-schema-version"))
                    result.success should be (false)
                    result.errors should be (Set("Invalid schema version value found; expected '1.2', but found '1.21'"))
                }

                it("when the manifest file has an invalid scheme value") {
                    val result = PackageParser.get.parseDirectory(buildInvalidSubDir("invalid-scheme-value"))
                    result.success should be (false)
                    result.errors should be (Set("Invalid scheme value found; expected 'ADL SCORM 1.2', but found 'ADL SCORM 1.21'"))
                }

                it("when the manifest file has a mastery score smaller than 0") {
                    val result = PackageParser.get.parseDirectory(buildInvalidSubDir("invalid-mastery-score-too-small"))
                    result.success should be (false)
                    result.errors should be (Set("Mastery score value '-20' is not between 0 and 100"))
                }

                it("when the manifest file has a mastery score larger than 100") {
                    val result = PackageParser.get.parseDirectory(buildInvalidSubDir("invalid-mastery-score-too-large"))
                    result.success should be (false)
                    result.errors should be (Set("Mastery score value '200' is not between 0 and 100"))
                }

                it("when the manifest file has a valid SCO but the href is empty") {
                    val result = PackageParser.get.parseDirectory(buildInvalidSubDir("invalid-no-schema-version"))
                    result.success should be (false)
                    result.errors should be (Set("Schema version not found"))
                }
            }

            describe("should return success when parsing a directory with a valid manifest file") {

                it("when the manifest file has both valid metadata schema and scheme") {
                    val result = PackageParser.get.parseDirectory(buildValidSubDir("valid-1"))
                    result.success should be (true)
                    result.entryPoint should be (Some("heyo.html"))
                    result.score should be (Some(99))
                }

                it("when the manifest file only has a valid metadata schema") {
                    val result = PackageParser.get.parseDirectory(buildValidSubDir("valid-noodle-box"))
                    result.success should be (true)
                    result.entryPoint should be (Some("index_lms.html"))
                    result.score should be (Some(80))
                }

                it("when the manifest file only has a valid metadata scheme") {
                    val result = PackageParser.get.parseDirectory(buildValidSubDir("valid-blood-and-body"))
                    result.success should be (true)
                    result.entryPoint should be (Some("Blood and Body Fluid Exposure Protocol.html"))
                    result.score should be (Some(0))
                }

                it("when the manifest file has no mastery score") {
                    val result = PackageParser.get.parseDirectory(buildValidSubDir("valid-content-packaging-single-sco-scorm-1-2"))
                    result.success should be (true)
                    result.entryPoint should be (Some("shared/launchpage.html"))
                    result.score should be (None)
                }

                it("when the manifest file has the first SCO resource with an empty href, but a valid second SCO resource") {
                    val result = PackageParser.get.parseDirectory(buildValidSubDir("valid-2"))
                    result.success should be (true)
                    result.entryPoint should be (Some("hello.html"))
                    result.score should be (Some(69))
                }

                it("when the manifest file has a single SCO") {
                    val result = PackageParser.get.parseDirectory(buildValidSubDir("valid-scorm-test-1"))
                    result.success should be (true)
                    result.entryPoint should be (Some("SCORM.htm"))
                    result.score should be (None)
                }

                it("when the manifest file has multiple SCOs") {
                    val result = PackageParser.get.parseDirectory(buildValidSubDir("valid-run-time-minimum-calls"))
                    result.success should be (true)
                    result.entryPoint should be (Some("Playing/Playing.html"))
                    result.score should be (None)
                }
            }
        }
    }
}

