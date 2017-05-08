package com.psyanite.scorm.util

import java.io._
import java.nio.file.{Files, Path, Paths}

import com.psyanite.scorm.exception.ParseException
import com.psyanite.scorm.node._
import com.psyanite.scorm.validator.ManifestValidator
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import org.apache.commons.io.FileUtils

import scala.xml.{SAXParseException, XML}

class Util {

    @throws(classOf[NullPointerException])
    @throws(classOf[IOException])
    @throws(classOf[ParseException])
    @throws(classOf[ZipException])
    def parseZip(path: Path): Manifest = {
        val file = path.toFile
        if (!file.exists) {
            throw new ParseException("Zip file does not exist")
        }
        val zipFile = new ZipFile(file)
        val directory = Files.createTempDirectory(Paths.get(file.getParent), file.getName + System.nanoTime.toString)
        try {
            zipFile.extractAll(directory.toString)
            parseDirectory(directory)
        }
        finally {
            FileUtils.deleteDirectory(directory.toFile)
        }
    }

    @throws(classOf[NullPointerException])
    @throws(classOf[IOException])
    @throws(classOf[ParseException])
    def parseDirectory(path: Path): Manifest = {
        val directory = path.toFile
        if (!directory.exists) {
            throw new ParseException("Directory does not exist")
        }
        val manifest = path.resolve(Util.ManifestFile).toFile
        if (!manifest.exists) {
            throw new ParseException("Manifest file '%s' not found".format(Util.ManifestFile))
        }
        parseManifest(manifest.toPath)
    }

    @throws(classOf[NullPointerException])
    @throws(classOf[IOException])
    @throws(classOf[ParseException])
    def parseManifest(path: Path): Manifest = {
        val file = path.toFile
        try {
            val xml       = XML.loadFile(file)
            val metadata  = Metadata(xml \\ "schema", xml \\ "schemaversion", xml \\ "metadatascheme")
            val resources = (xml \\ "resource").map(Resource(_))
            val items     = (xml \\ "item").map(Item(_))
            new Manifest(metadata, items, resources)
        }
        catch {
            case _: SAXParseException => throw new ParseException("Manifest file was not a well-formed XML file")
        }
    }

    /**
      * Returns a sequence of errors found upon validation
      * @param manifest
      * @return
      */
    def validate(manifest: Manifest): Seq[String] = {
        ManifestValidator().validate(manifest)
    }
}

object Util {
    def ManifestFile = "imsmanifest.xml"
}
