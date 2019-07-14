package com.github.vyadh.teamcity.deploys

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class DeployTest {

  @Test
  fun jsonRepresentation() {
    val deploy = Deploy(
          project = "project",
          version = "1.1.0",
          environment = "DEV",
          time = ZonedDateTime.parse("2019-05-19T09:41:30+01:00"),
          status = "SUCCESS",
          running = true,
          hanging = true,
          link = "http://tc/build/2"
    )

    val json = deploy.toJson()

    assertThat(json).isEqualTo("""
      {
        name: "project",
        version: "1.1.0",
        environment: "DEV",
        time: "2019-05-19T09:41:30+01:00",
        status: "SUCCESS",
        running: true,
        hanging: true,
        link: "http://tc/build/2"
      }
    """.trimIndent())
  }

}
