package com.retheviper.springwebfluxsample.springwebfluxsample.testbase.basespec

import io.kotest.core.spec.style.FreeSpec
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest
open class RouterTestSpec : FreeSpec() {

    internal lateinit var webClient: WebTestClient
}