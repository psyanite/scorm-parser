package com.psyanite.scorm.parser

import java.io._
import java.nio.file.{Files, Paths}

import net.lingala.zip4j.core.ZipFile
import org.apache.commons.io.FileUtils

case class PackageParser() {
    def parseZip(file: File): ParseResult = {
        if (!file.exists) {
            return ParseResult(success = false, Set("File does not exist, " + file.toString), None, None)
        }
        var directory = new File("")
        try {
            val zip = new ZipFile(file)
            directory = Files.createTempDirectory(Paths.get(file.getParent), file.getName + System.nanoTime.toString).toFile
            zip.extractAll(directory.toString)
            parseDirectory(directory)
        }
        catch {
            case _: Exception => ParseResult(success = false, Set("Error unzipping zip file, " + file.toString), None, None)
        }
        finally {
            FileUtils.deleteDirectory(directory)
        }
    }

    def parseDirectory(dir: File): ParseResult = {
        if (!dir.exists) {
            return ParseResult(success = false, Set("Directory does not exist, " + dir.toString), None, None)
        }
        val manifest = new File(dir, ManifestValidator.ManifestFile)
        if (!manifest.exists) {
            return ParseResult(success = false, Set("Manifest file 'imsmanifest.xml' not found"), None, None)
        }
        ManifestParser.get.parse(manifest)
    }
}

object PackageParser {
    def get = new PackageParser
}
