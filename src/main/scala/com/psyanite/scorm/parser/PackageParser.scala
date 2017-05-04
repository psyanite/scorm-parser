package com.psyanite.scorm.parser

import java.io._
import java.nio.file.{Files, Path, Paths}

import com.psyanite.scorm.exception.InvalidManifestException
import com.psyanite.scorm.metadata.Metadata
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import org.apache.commons.io.FileUtils

class PackageParser {

    @throws(classOf[NullPointerException])
    @throws(classOf[IOException])
    @throws(classOf[InvalidManifestException])
    @throws(classOf[ZipException])
    def parseZip(path: Path): Metadata = {
        val file = path.toFile
        val zip = new ZipFile(file)
        val directory = Files.createTempDirectory(Paths.get(file.getParent), file.getName + System.nanoTime.toString)
        zip.extractAll(directory.toString)
        val metadata = parseDirectory(directory)
        FileUtils.deleteDirectory(directory.toFile)
        metadata
    }

    @throws(classOf[NullPointerException])
    @throws(classOf[IOException])
    @throws(classOf[InvalidManifestException])
    def parseDirectory(path: Path): Metadata = {
        val manifest = new File(path.toFile, PackageParser.ManifestFile)
        if (!manifest.exists()) {
            throw new InvalidManifestException("Manifest file 'imsmanifest.xml' not found")
        }
        ManifestParser().parse(manifest)
    }
}

object PackageParser {
    def ManifestFile = "imsmanifest.xml"
    def apply() = new PackageParser
}
