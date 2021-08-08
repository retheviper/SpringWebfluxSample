package com.retheviper.springwebfluxsample.springwebfluxsample.application.router

import com.retheviper.springwebfluxsample.application.domain.model.dto.MemberDto
import com.retheviper.springwebfluxsample.application.handler.MemberHandler
import com.retheviper.springwebfluxsample.application.router.MemberRouter
import com.retheviper.springwebfluxsample.springwebfluxsample.testbase.data.TestDataCreator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.reactive.awaitSingle
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Flux

@WebFluxTest
class MemberRouterTest {

    companion object {

        private lateinit var webClient: WebTestClient

        private lateinit var handler: MemberHandler

        @JvmStatic
        @BeforeAll
        fun init() {
            handler = mockk()
            webClient = WebTestClient
                .bindToRouterFunction(MemberRouter(handler).routeMember())
                .build()
        }
    }

    @Test
    fun listMember_OK() {
        val expected = TestDataCreator.createMemberDtoList()

        coEvery {
            handler.listMember()
        } coAnswers {
            ServerResponse.ok().body(Flux.fromIterable(TestDataCreator.createMemberDtoList())).awaitSingle()
        }

        webClient.get()
            .uri("/api/v1/web/members")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBodyList<MemberDto>()
            .value<Nothing> {
                Assertions.assertEquals(expected, it)
            }

        coVerify { handler.listMember() }
    }
}