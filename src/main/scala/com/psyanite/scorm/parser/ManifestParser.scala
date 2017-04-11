package com.psyanite.scorm.parser

import java.io._

import scala.collection.mutable.{Set => MSet}
import scala.io.Source
import scala.util.Try
import scala.util.control.Breaks._
import scala.xml.Attribute
import scala.xml.parsing.FatalError
import scala.xml.pull._

object EventType extends Enumeration {
   val Schema, Version, Score, Scheme = Value
}

object ManifestValidator {
    val ValidSchemaValue  = "ADL SCORM"
    val ValidVersionValue = "1.2"
    val ValidSchemeValue  = "ADL SCORM 1.2"
    val ManifestFile      = "imsmanifest.xml"
    val MinimumScore      = 0
    val MaximumScore      = 100
}

case class ParseResult(
    var success: Boolean,
    var errors: Set[String],
    var entryPoint: Option[String],
    var score: Option[Int]
)

class Metadata() {
    var schema: Option[String]     = None
    var version: Option[String]    = None
    var scheme: Option[String]     = None
    var entryPoint: Option[String] = None
    var score : Option[String]     = None
}

case class ManifestParser() {
    var eventType: Option[EventType.Value] = None
    val metadata: Metadata                 = new Metadata
    val errors: MSet[String]               = MSet[String]()

    def parse(file: File): ParseResult = {
        try {
            val reader = new XMLEventReader(Source.fromFile(file))
            breakable { for (event <- reader) {
                if (metadata.entryPoint.isEmpty || metadata.score.isEmpty) {
                    event match {
                        case EvElemStart(_, "schema", _, _)             => eventType = Some(EventType.Schema)
                        case EvElemStart(_, "schemaversion", _, _)      => eventType = Some(EventType.Version)
                        case EvElemStart(_, "metadatascheme", _, _)     => eventType = Some(EventType.Scheme)
                        case EvElemStart("adlcp", "masteryscore", _, _) => eventType = Some(EventType.Score)
                        case EvElemStart(_, "resource", _, _)           => setEntryPoint(event)
                        case EvText(text)                               => eventTextMatcher(text)
                        case _                                          =>
                    }
                }
            } }
            validateMetadata()
        }
        catch {
            case _: FatalError => ParseResult(success = false, Set("Manifest file was not a well-formed XML file"), None, None)
            case _: Exception => ParseResult(success = false, Set("Error parsing manifest file"), None, None)
        }
    }

    def validateMetadata(): ParseResult = {
        validateSchema()

        if (metadata.entryPoint.isEmpty) {
            errors += "SCO entry point not found"
        }

        val masteryScore = metadata.score.flatMap(score => Try(score.toInt).toOption)
        masteryScore.foreach { score =>
            if (score < ManifestValidator.MinimumScore || score > ManifestValidator.MaximumScore) {
                errors += "Mastery score value '%d' is not between 0 and 100".format(score)
            }
        }

        if (errors.isEmpty) {
            ParseResult(success = true, errors.toSet, metadata.entryPoint, masteryScore)
        }
        else {
            ParseResult(success = false, errors.toSet, metadata.entryPoint, masteryScore)
        }
    }

    private def validateSchema() =  {
        if ((metadata.schema, metadata.version, metadata.scheme) == (None, None, None)) {
            errors += "Metadata schema and scheme not found"
        }
        else {
            if (metadata.scheme.isEmpty) {
                metadata.schema match {
                    case None => errors += "Schema value not found"
                    case Some(schema) => if (schema != ManifestValidator.ValidSchemaValue) {
                        errors += "Invalid schema value found; expected '%s', but found '%s'".format(ManifestValidator.ValidSchemaValue, schema)
                    }
                }
                metadata.version match {
                    case None => errors += "Schema version not found"
                    case Some(version) => if (version != ManifestValidator.ValidVersionValue) {
                        errors += "Invalid schema version value found; expected '%s', but found '%s'".format(ManifestValidator.ValidVersionValue, version)
                    }
                }
            }
            else {
                metadata.scheme match {
                    case None => errors += "Scheme value not found"
                    case Some(version) => if (version != ManifestValidator.ValidSchemeValue) {
                        errors += "Invalid scheme value found; expected '%s', but found '%s'".format(ManifestValidator.ValidSchemeValue, version)
                    }
                }
            }
        }
    }

    private def eventTextMatcher(text: String) = {
        eventType match {
            case Some(EventType.Schema)  => metadata.schema = Some(text)
            case Some(EventType.Version) => metadata.version = Some(text)
            case Some(EventType.Scheme)  => metadata.scheme = Some(text)
            case Some(EventType.Score)   => metadata.score = Some(text)
            case _ =>
        }
        eventType = None
    }

    private def setEntryPoint(event: XMLEvent) = {
        if (metadata.entryPoint.isEmpty) {
            getAttributePrefixedValue(event, "adlcp:scormtype").foreach { scormType =>
                if (scormType == "sco") {
                    getAttributeValue(event, "href").foreach { entry =>
                        if (entry != "") {
                            metadata.entryPoint = Some(entry)
                        }
                    }
                }
            }
        }
    }

    private def getAttributePrefixedValue(event: XMLEvent, prefixedKey: String): Option[String] = {
        val attributes = event.asInstanceOf[EvElemStart].attrs
        attributes.filter(_.prefixedKey == prefixedKey) match {
            case attribute: Attribute => Some(attribute.value.text)
            case _ => None
        }
    }

    private def getAttributeValue(event: XMLEvent, label: String): Option[String] = {
        event.asInstanceOf[EvElemStart].attrs.get(label) match {
            case Some(value) => Some(value.toString)
            case None => None
        }
    }
}

object ManifestParser {
    def get = new ManifestParser
}
