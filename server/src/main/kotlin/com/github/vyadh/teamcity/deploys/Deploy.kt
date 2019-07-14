package com.github.vyadh.teamcity.deploys

import java.time.ZonedDateTime

data class Deploy(
      val project: String,
      val version: String,
      val environment: String,
      val time: ZonedDateTime,
      val status: String,
      val running: Boolean,
      val hanging: Boolean,
      val link: String
) {

  fun toJson(): String {
    return """
      {
        name: "$project",
        version: "$version",
        environment: "$environment",
        time: "$time",
        status: "$status",
        running: $running,
        hanging: $hanging,
        link: "$link"
      }
    """.trimIndent()
  }

}
