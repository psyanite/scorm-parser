# scorm-parser
A Scala library to parse and validate SCORM 1.2 packages.

### Configuration
Scala 2.12.3 is required.

```
groupId: io.github.psyanite
artifactId: scorm-parser_2.12
version: 0.2.6
```

### Usage

#### Using the parser
```scala
import com.psyanite.scorm.PackageParser
import com.psyanite.scorm.validator.ManifestValidator


val zip = new File("my-scorm-package.zip")
try {
  val parser = PackageParser(zip)
  val manifest = parser.parse()
  val errors = ManifestValidator().validate(manifest)
} catch {
  case e: Exception => println(e.message)
}

val directory = new File("/my-unzipped-scorm-package")
try {
  val parser = PackageParser(directory)
  val manifest = parser.parse()
  val errors = ManifestValidator().validate(manifest)
} catch {
  case e: Exception => println(e.message)
}

manifest.item.head.masteryScore match {
  case Some(score) => println("Mastery score is %d".format(score))
  case None        => println("Mastery score not found")
}

manifest.resources.head.href match {
  case Some(href) => println("Entry point on first resource is %s".format(href))
  case None       => println("Entry point on first resource not found")
}

manifest.resources(1).head.href match {
  case Some(href) => println("Entry point on second resource is %s".format(href))
  case None       => println("Entry point on second resource not found")
}

```

#### Manifest class
```scala
class Manifest (
  var metadata:  Metadata,
  var items:     Seq[Item],
  var resources: Seq[Resource]
)
```

#### Metadata class
```scala
case class Metadata (
  var schema:        Option[String],
  var schemaVersion: Option[String],
  var scheme:        Option[String]
)
```

#### Item class
```scala
case class Item (
  var identifier:   String,
  var title:        String,
  var masteryScore: Option[Int]
)
```
