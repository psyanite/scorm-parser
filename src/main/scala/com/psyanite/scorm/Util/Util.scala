package com.psyanite.scorm.Util

import java.io._
import java.nio.file.{Files, Path, Paths}

import com.psyanite.scorm.exception.ParseException
import com.psyanite.scorm.node._
import com.psyanite.scorm.validator.ManifestValidator
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import org.apache.commons.io.FileUtils

import scala.xml.{SAXParseException, XML}
import scalaz.{NonEmptyList, Validation}

class Util (
    var zip: Option[Path]       = None,
    var directory: Option[Path] = None,
    var manifest: Option[Path]  = None
) {

    @throws(classOf[NullPointerException])
    @throws(classOf[IOException])
    @throws(classOf[ParseException])
    @throws(classOf[ZipException])
    def parseZip: Manifest = {
        zip.map(parseZip).getOrElse(throw new ParseException("Zip file is not defined in parser"))
    }

    @throws(classOf[NullPointerException])
    @throws(classOf[IOException])
    @throws(classOf[ParseException])
    def parseDirectory: Manifest = {
        directory.map(parseDirectory).getOrElse(throw new ParseException("Directory is not defined in parser"))
    }

    @throws(classOf[NullPointerException])
    @throws(classOf[IOException])
    @throws(classOf[ParseException])
    def parseManifest: Manifest = {
        manifest.map(parseManifest).getOrElse(throw new ParseException("Manifest file is not defined in parser"))
    }

    def validate(manifest: Manifest): Validation[NonEmptyList[String], Manifest] = {
        ManifestValidator().validate(manifest)
    }

    private def parseZip(path: Path): Manifest = {
        val file = path.toFile
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

    private def parseDirectory(path: Path): Manifest = {
        val manifest = path.resolve(Util.ManifestFile).toFile
        if (!manifest.exists()) {
            throw new ParseException("Manifest file '%s' not found".format(Util.ManifestFile))
        }
        parseManifest(manifest.toPath)
    }

    private def parseManifest(path: Path): Manifest = {
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
}

object Util {
    def ManifestFile = "imsmanifest.xml"
}
