package com.retheviper.springwebfluxsample.springwebfluxsample.application.router

import com.retheviper.springwebfluxsample.application.handler.MemberHandler
import com.retheviper.springwebfluxsample.application.model.request.MemberUpsertForm
import com.retheviper.springwebfluxsample.application.router.MemberRouter
import com.retheviper.springwebfluxsample.domain.model.dto.MemberDto
import com.retheviper.springwebfluxsample.domain.repository.MemberRepository
import com.retheviper.springwebfluxsample.springwebfluxsample.testbase.basespec.RouterTestSpec
import com.retheviper.springwebfluxsample.springwebfluxsample.testbase.data.TestDataCreator
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class MemberRouterTest : RouterTestSpec() {

    private lateinit var repository: MemberRepository

    init {

        beforeSpec {
            repository = mockk()
            val handler = MemberHandler(repository, PasswordEncoderFactories.createDelegatingPasswordEncoder())
            webClient = WebTestClient
                .bindToRouterFunction(MemberRouter(handler).routeMember())
                .build()
        }

        "listMember" {
            val expected = TestDataCreator.createMemberDtoList()

            coEvery {
                repository.findAll()
            } coAnswers {
                Flux.fromIterable(TestDataCreator.createMemberList())
            }

            webClient.get()
                .uri("/api/v1/web/members")
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectBodyList<MemberDto>()
                .value<Nothing> {
                    it shouldBe expected
                }

            coVerify { repository.findAll() }
        }

        "getMember" {
            val expected = TestDataCreator.createMemberDto()

            coEvery {
                repository.findById(1)
            } coAnswers {
                Mono.just(TestDataCreator.createMember())
            }

            val actual = webClient.get()
                .uri("/api/v1/web/members/1")
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectBody<MemberDto>()
                .returnResult()
                .responseBody

            actual shouldBe expected

            coVerify { repository.findById(1) }
        }

        "createMember" {
            val expected = TestDataCreator.createMemberDto()

            coEvery {
                repository.existsByUid(expected.userId)
            } coAnswers {
                Mono.just(false)
            }

            coEvery {
                repository.save(any())
            } coAnswers {
                Mono.just(TestDataCreator.createMember())
            }

            val actual = webClient.post()
                .uri("/api/v1/web/members")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(
                    Mono.just(
                        MemberUpsertForm(
                            userId = expected.userId,
                            name = expected.name,
                            password = "1234"
                        )
                    ),
                    MemberUpsertForm::class.java
                )
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectBody<MemberDto>()
                .returnResult()
                .responseBody

            actual shouldBe expected

            coVerify {
                repository.existsByUid(expected.userId)
                repository.save(any())
            }
        }

        "updateMember" {
            val expected = TestDataCreator.createMemberDto()

            coEvery {
                repository.findById(1)
            } coAnswers {
                Mono.just(TestDataCreator.createMember())
            }

            coEvery {
                repository.save(any())
            } coAnswers {
                Mono.just(TestDataCreator.createMember())
            }

            val actual = webClient.put()
                .uri("/api/v1/web/members/1")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(
                    Mono.just(
                        MemberUpsertForm(
                            userId = expected.userId,
                            name = expected.name,
                            password = "1234"
                        )
                    ),
                    MemberUpsertForm::class.java
                )
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectBody<MemberDto>()
                .returnResult()
                .responseBody

            actual shouldBe expected

            coVerify {
                repository.findById(1)
                repository.save(any())
            }
        }

        "deleteMember" {
            coEvery {
                repository.findById(1)
            } coAnswers {
                Mono.just(TestDataCreator.createMember())
            }

            coEvery {
                repository.deleteById(1)
            } coAnswers {
                Mono.empty()
            }

            webClient.delete()
                .uri("/api/v1/web/members/1")
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectBody<Unit>()

            coVerify {
                repository.findById(1)
                repository.deleteById(1)
            }
        }
    }
}