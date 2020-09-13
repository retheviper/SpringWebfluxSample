package com.retheviper.springwebfluxsample.application.handler

import com.retheviper.springwebfluxsample.application.domain.entity.Member
import com.retheviper.springwebfluxsample.application.domain.entity.MemberDto
import com.retheviper.springwebfluxsample.application.domain.entity.MemberForm
import com.retheviper.springwebfluxsample.application.domain.repository.MemberRepository
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class MemberHandler(private val repository: MemberRepository, private val passwordEncoder: PasswordEncoder) {

    private val userId: String = "uid"

    fun listMember(request: ServerRequest): Mono<ServerResponse> =
        ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Flux.fromIterable(repository.findAll()).map { MemberDto(it.uid, it.name) })
            .switchIfEmpty(notFound().build())

    fun getMember(request: ServerRequest): Mono<ServerResponse> =
        ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(repository.findByUid(request.pathVariable(userId))).map { MemberDto(it.uid, it.name) })
            .switchIfEmpty(notFound().build())

    fun createMember(request: ServerRequest): Mono<ServerResponse> =
        accepted()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request.bodyToMono(MemberForm::class.java)
                .switchIfEmpty(Mono.empty())
                .filter(Objects::nonNull)
                .filter { member -> !repository.existsByUid(member.uid) }
                .flatMap { member ->
                    Mono.fromCallable {
                        repository.save(
                            Member(
                                id = null,
                                uid = member.uid,
                                name = member.name,
                                password = passwordEncoder.encode(member.password)
                            )
                        )
                    }.then(Mono.just(MemberDto(member.uid, member.name)))
                })
            .switchIfEmpty(status(HttpStatus.CONFLICT).build())


    fun updateMember(request: ServerRequest): Mono<ServerResponse> =
        ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request.bodyToMono(MemberForm::class.java)
                .filter { member ->
                    val original = repository.findByUid(request.pathVariable(userId))
                    passwordEncoder.matches(member.password, original.password)
                }
                .switchIfEmpty(Mono.empty())
                .filter(Objects::nonNull)
                .flatMap { member ->
                    Mono.fromCallable {
                        repository.save(
                            Member(
                                id = repository.findByUid(request.pathVariable(userId)).id,
                                uid = request.pathVariable(userId),
                                name = member.name,
                                password = passwordEncoder.encode(member.password)
                            )
                        )
                    }.then(Mono.just(MemberDto(request.pathVariable(userId), member.name)))
                })
            .switchIfEmpty(notFound().build())

    fun deleteMember(request: ServerRequest): Mono<ServerResponse> =
        ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.justOrEmpty(repository.findByUid(request.pathVariable(userId)))
                .switchIfEmpty(Mono.empty())
                .filter(Objects::nonNull)
                .map { member -> repository.delete(member) })
            .switchIfEmpty(notFound().build())
}