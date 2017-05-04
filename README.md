# scorm-parser
A Scala library to parse and validate SCORM 1.2 packages.

### Configuration
Scala 2.11 is required.

```
groupId: io.github.psyanite
artifactId: scorm-parser
version: 0.1.0
```

### Usage

#### Using the parser
```scala
import com.psyanite.scorm.parser.PackageParser
import com.psyanite.scorm.parser.ManifestParser


val zip = new File("my-archive.zip")
val metadata = try {
  PackageParser.parseZip(zip)
} catch {
  case e: Exception => println(e.message)
}

val directory = new File("/my-directory")
val metadata = try {
  PackageParser.parseDirectory(zip)
} catch {
  case e: Exception => println(e.message)
}

val manifest = new File("/my-directory/imsmanifest.xml")
val metadata = try {
  ManifestParser().parse(manifest)
} catch {
  case e: Exception => println(e.message)
}


metadata.item.head.masteryScore match {
  case Some(score) => println("Mastery score is %d".format(score))
  case None        => println("No mastery score")
}

metadata.resources.head.href match {
  case Some(href) => println("First entry point is %s".format(href))
  case None       => println("First resources has no entry point")
}

```

#### Metadata class
```scala
class Metadata(
    var schema: Schema,
    var items: Seq[Item],
    var resources: Seq[Resource]
)
```
