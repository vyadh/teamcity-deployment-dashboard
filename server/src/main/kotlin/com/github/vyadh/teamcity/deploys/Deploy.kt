package com.github.vyadh.teamcity.deploys

import java.time.ZonedDateTime

data class Deploy(
      val project: String,
      val version: String,
      val environment: String,
      val time: ZonedDateTime,
      var status: String,
      var link: String
) {

  fun toJson(): String {
    return """
      {
        name: "$project",
        version: "$version",
        environment: "$environment",
        time: "$time",
        status: "$status",
        link: "$link"
      }
    """.trimIndent()
  }

}
