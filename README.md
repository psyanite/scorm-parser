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
val result = PackageParser.parseZip(zip)

val directory = new File("/my-directory")
result = PackageParser.parseDirectory(zip)

val manifest = new File("/my-directory/imsmanifest.xml")
result = ManifestParser().parse(manifest)

if (result.success) {
  println("Valid SCORM 1.2 package detected")
  println("Entry point is %s".format(result.entryPoint.get))
  println("Mastery score is %d".format(result.score.get))
}
else {
  result.errors.foreach(println)
}
```

#### ParseResult class
```
case class ParseResult(
    var success: Boolean,
    var errors: Set[String],
    var entryPoint: Option[String],
    var score: Option[Int]
)
```
