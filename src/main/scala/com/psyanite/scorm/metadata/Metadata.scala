package com.psyanite.scorm.metadata

class Metadata(
    var schema: Schema,
    var items: Seq[Item],
    var resources: Seq[Resource]
)

object Metadata {
    def apply(schema: Schema, items: Seq[Item], resources: Seq[Resource]): Metadata = {
        new Metadata(schema, items, resources)
    }
}
