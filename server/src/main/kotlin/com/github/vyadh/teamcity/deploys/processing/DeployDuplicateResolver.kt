package com.github.vyadh.teamcity.deploys.processing

import com.github.vyadh.teamcity.deploys.Deploy
import java.util.*
import java.util.stream.Collector
import java.util.stream.Collectors
import java.util.stream.Stream

object DeployDuplicateResolver {

  fun resolve(deploys: Stream<Deploy>): List<Deploy> {
    return deploys.collect(groupByUniqueOfMaxTime())
          .values.stream()
          .filter { it.isPresent }        // More concise with flatMap and
          .map { it.get() }               // Java 9's Optional.stream()
          .collect(Collectors.toList())
  }

  private fun groupByUniqueOfMaxTime(): Collector<Deploy, *, MutableMap<Unique, Optional<Deploy>>> {
    return Collectors.groupingBy({ Unique(it.project, it.environment) }, maxTime())
  }

  data class Unique(val project: String, val env: String)

  private fun maxTime(): Collector<Deploy, *, Optional<Deploy>> {
    return Collectors.maxBy(Comparator.comparing(Deploy::time))
  }

}
