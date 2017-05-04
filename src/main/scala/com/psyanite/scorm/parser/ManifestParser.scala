package com.psyanite.scorm.parser

import java.io._

import com.psyanite.scorm.exception.InvalidManifestException
import com.psyanite.scorm.metadata.{Item, Metadata, Resource, Schema}

import scala.xml.{SAXParseException, XML}

class ManifestParser() {

    @throws(classOf[NullPointerException])
    @throws(classOf[IOException])
    @throws(classOf[InvalidManifestException])
    def parse(file: File): Metadata = {
        try {
            val xml = XML.loadFile(file)
            val schema = Schema(xml \\ "schema", xml \\ "schemaversion", xml \\ "metadatascheme")
            val resources = (xml \\ "resource").map(Resource(_))
            val items = (xml \\ "item").map(Item(_))
            Metadata(schema, items, resources)
        }
        catch {
            case _: SAXParseException => throw new Exception("Manifest file was not a well-formed XML file")
        }
    }
}

object ManifestParser {
    def apply() = new ManifestParser
}