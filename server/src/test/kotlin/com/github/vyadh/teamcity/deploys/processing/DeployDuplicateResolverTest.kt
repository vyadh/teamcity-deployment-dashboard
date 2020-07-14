package com.github.vyadh.teamcity.deploys.processing

import com.github.vyadh.teamcity.deploys.Deploy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.util.stream.Stream

class DeployDuplicateResolverTest {

  @Test
  internal fun emptyDeploysReturnsEmptyList() {
    val result = DeployDuplicateResolver.resolve(Stream.empty())

    assertThat(result).isEmpty()
  }

  @Test
  internal fun uniqueProjectsAreReturnedFully() {
    val deploys = Stream.of(
          deploy(project = "A"),
          deploy(project = "B"),
          deploy(project = "C")
    )

    val result = DeployDuplicateResolver.resolve(deploys)

    assertThat(result).containsExactlyInAnyOrder(
          deploy(project = "A"),
          deploy(project = "B"),
          deploy(project = "C")
    )
  }


  @Test
  internal fun uniqueEnvironmentsAreReturnedFully() {
    val deploys = Stream.of(
          deploy(env = "DEV"),
          deploy(env = "UAT"),
          deploy(env = "PRD")
    )

    val result = DeployDuplicateResolver.resolve(deploys)

    assertThat(result).containsExactlyInAnyOrder(
          deploy(env = "DEV"),
          deploy(env = "UAT"),
          deploy(env = "PRD")
    )
  }

  @Test
  internal fun uniqueProjectsAndEnvironmentsAreReturnedFully() {
    val deploys = Stream.of(
          deploy(project = "A", env = "DEV"),
          deploy(project = "A", env = "UAT"),
          deploy(project = "B", env = "DEV"),
          deploy(project = "B", env = "UAT")
    )

    val result = DeployDuplicateResolver.resolve(deploys)

    assertThat(result).containsExactlyInAnyOrder(
          deploy(project = "A", env = "DEV"),
          deploy(project = "A", env = "UAT"),
          deploy(project = "B", env = "DEV"),
          deploy(project = "B", env = "UAT")
    )
  }

  @Test
  internal fun resolveUseLatestByTimeWhenOldestFirstWhenDuplicates() {
    val deploys = Stream.of(
          deploy(project = "Project", env = "DEV", time = "08:30:00"),
          deploy(project = "Project", env = "DEV", time = "08:30:10")
    )

    val result = DeployDuplicateResolver.resolve(deploys)

    assertThat(result).containsExactly(
          deploy(project = "Project", env = "DEV", time = "08:30:10")
    )
  }

  @Test
  internal fun resolveUseLatestByTimeWhenNewestFirstWhenDuplicates() {
    val deploys = Stream.of(
          deploy(project = "Project", env = "DEV", time = "08:30:10"),
          deploy(project = "Project", env = "DEV", time = "08:30:00")
    )

    val result = DeployDuplicateResolver.resolve(deploys)

    assertThat(result).containsExactly(
          deploy(project = "Project", env = "DEV", time = "08:30:10")
    )
  }

  @Test
  internal fun resolveUseLatestByTimeWhenManyDuplicates() {
    val deploys = Stream.of(
          deploy(project = "Project", env = "DEV", time = "08:30:10"),
          deploy(project = "Project", env = "DEV", time = "08:30:30"),
          deploy(project = "Project", env = "DEV", time = "08:30:40"),
          deploy(project = "Project", env = "DEV", time = "08:30:20")
    )

    val result = DeployDuplicateResolver.resolve(deploys)

    assertThat(result).containsExactly(
          deploy(project = "Project", env = "DEV", time = "08:30:40")
    )
  }

  @Test
  internal fun duplicatesAreResolvedOnlyWithinProjectEnvironmentPair() {
    val deploys = Stream.of(
          deploy(project = "A", env = "DEV", time = "10:00:00"),
          deploy(project = "A", env = "DEV", time = "10:00:01"),
          deploy(project = "A", env = "PRD", time = "11:00:01"),
          deploy(project = "A", env = "PRD", time = "11:00:00"),
          deploy(project = "B", env = "DEV", time = "12:00:01"),
          deploy(project = "B", env = "DEV", time = "12:00:00"),
          deploy(project = "B", env = "PRD", time = "13:00:00"),
          deploy(project = "B", env = "PRD", time = "13:00:01")
    )

    val result = DeployDuplicateResolver.resolve(deploys)

    assertThat(result).containsExactlyInAnyOrder(
          deploy(project = "A", env = "DEV", time = "10:00:01"),
          deploy(project = "A", env = "PRD", time = "11:00:01"),
          deploy(project = "B", env = "DEV", time = "12:00:01"),
          deploy(project = "B", env = "PRD", time = "13:00:01")
    )
  }


  private fun deploy(
        project: String = "project",
        env: String = "env",
        time: String = "21:33:00"): Deploy {

    val datetime = ZonedDateTime.parse("2019-07-02T${time}Z")
    return Deploy(
          project, "", env, datetime, "", false, false,"", "")
  }

}
