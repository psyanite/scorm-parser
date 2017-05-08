package com.psyanite.scorm.node

class Manifest (
    var metadata: Metadata,
    var items: Seq[Item],
    var resources: Seq[Resource]
)

object Manifest {
    def apply(metadata: Metadata, items: Seq[Item], resources: Seq[Resource]): Manifest = {
        new Manifest(metadata, items, resources)
    }
}
