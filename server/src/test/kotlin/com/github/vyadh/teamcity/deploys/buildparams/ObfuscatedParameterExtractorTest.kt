package com.github.vyadh.teamcity.deploys.buildparams

import com.github.vyadh.teamcity.deploys.processing.BuildMocks.buildTypeWith
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.buildWith
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import jetbrains.buildServer.serverSide.parameters.types.PasswordsSearcher
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ObfuscatedParameterExtractorTest {

  @Test
  internal fun extractingNullValue() {
    val extractor = ObfuscatedParameterExtractor(passwordsSearcher())
    val build = buildWith(emptyMap())

    val result = extractor.extract(build, "key") { null }

    assertThat(result).isNull()
  }

  @Test
  internal fun extractingDefaultValueWhenKeyBlank() {
    val extractor = ObfuscatedParameterExtractor(passwordsSearcher())
    val build = buildWith(emptyMap())

    val result = extractor.extract(build, "") { "default" }

    assertThat(result).isEqualTo("default")
  }

  @Test
  internal fun noObfuscationRequired() {
    val extractor = ObfuscatedParameterExtractor(passwordsSearcher("pass"))
    val build = buildWith(mapOf(Pair("key", "value")))

    val result = extractor.extract(build, "key") { null }

    assertThat(result).isEqualTo("value")
  }

  @Test
  internal fun obfuscationOfSecretRequired() {
    val extractor = ObfuscatedParameterExtractor(passwordsSearcher("pass"))
    val build = buildWith(mapOf(Pair("secret", "! mypasss !")))

    val result = extractor.extract(build, "secret") { null }

    assertThat(result).isEqualTo("! my******s !")
  }

  @Test
  internal fun obfuscationOfSecretInMultiplePlaces() {
    val extractor = ObfuscatedParameterExtractor(passwordsSearcher("pass"))
    val build = buildWith(mapOf(Pair("secret", "! passpassopass !")))

    val result = extractor.extract(build, "secret") { null }

    assertThat(result).isEqualTo("! ************o****** !")
  }

  @Test
  internal fun obfuscationOfDifferentSecrets() {
    val extractor = ObfuscatedParameterExtractor(passwordsSearcher("pass", "word"))
    val build = buildWith(mapOf(Pair("secret", "! password is my word pass !")))

    val result = extractor.extract(build, "secret") { null }

    assertThat(result).isEqualTo("! ************ is my ****** ****** !")
  }


  private fun buildWith(params: Map<String, String>) =
        buildWith(buildTypeWith(emptyList()), "any", params)

  private fun passwordsSearcher(vararg passwords: String): PasswordsSearcher {
    return mock {
      on { collectPasswords(any()) } doReturn setOf(*passwords)
    }
  }

}
