package com.psyanite.scorm

import java.io._
import java.nio.file.{Files, Path, Paths}

import com.psyanite.scorm.exception.ParseException
import com.psyanite.scorm.node._
import com.psyanite.scorm.validator.ManifestValidator
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException

import scala.xml.{SAXParseException, XML}

class PackageParser(val path: Path) {

    @throws(classOf[IOException])
    @throws(classOf[ParseException])
    @throws(classOf[ZipException])
    def this(zip: File) = {
        this(PackageParser.unzip(zip))
    }

    @throws(classOf[NullPointerException])
    @throws(classOf[IOException])
    @throws(classOf[ParseException])
    def parse: Manifest = {
        val directory = path.toFile
        if (!directory.exists) {
            throw new NullPointerException("Directory does not exist")
        }
        val manifest = path.resolve(PackageParser.ManifestFile).toFile
        if (!manifest.exists) {
            throw new ParseException("Manifest file '%s' not found".format(PackageParser.ManifestFile))
        }
        parseManifest(manifest.toPath)
    }

    @throws(classOf[NullPointerException])
    @throws(classOf[IOException])
    @throws(classOf[ParseException])
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

    /**
      * Returns a sequence of errors found upon validation
      * @param manifest manifest file to validate
      * @return
      */
    def validate(manifest: Manifest): Seq[String] = {
        ManifestValidator().validate(manifest)
    }
}

object PackageParser {
    final val ManifestFile = "imsmanifest.xml"

    def apply(path: Path): PackageParser = new PackageParser(path)

    @throws(classOf[NullPointerException])
    @throws(classOf[IOException])
    @throws(classOf[ParseException])
    @throws(classOf[ZipException])
    def apply(zip: File): PackageParser = {
        val path: Path = unzip(zip)
        apply(path)
    }

    @throws(classOf[NullPointerException])
    @throws(classOf[ZipException])
    private def unzip(zip: File): Path = {
        if (!zip.exists()) {
            throw new NullPointerException("Zip file does not exist")
        }
        val zipFile = new ZipFile(zip)
        val directory = Files.createTempDirectory(Paths.get(zip.getParent), zip.getName + System.nanoTime.toString)
        zipFile.extractAll(directory.toString)
        directory
    }

}
