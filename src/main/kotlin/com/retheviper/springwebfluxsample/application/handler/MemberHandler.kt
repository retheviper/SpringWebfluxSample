package com.retheviper.springwebfluxsample.application.handler

import com.retheviper.springwebfluxsample.application.domain.entity.Member
import com.retheviper.springwebfluxsample.application.domain.repository.MemberRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono
import java.util.*

@Component
class MemberHandler(private val repository: MemberRepository) {

    private val userId: String = "uid"

    @Transactional(readOnly = true)
    fun listMember(request: ServerRequest): Mono<ServerResponse> = ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(repository.findAll()))
        .switchIfEmpty(notFound().build())

    @Transactional(readOnly = true)
    fun getMember(request: ServerRequest): Mono<ServerResponse> = ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(repository.findByUid(request.pathVariable(userId))))
        .switchIfEmpty(notFound().build())

    @Transactional
    fun createMember(request: ServerRequest): Mono<ServerResponse> = accepted()
        .contentType(MediaType.APPLICATION_JSON)
        .body(request.bodyToMono(Member::class.java)
            .switchIfEmpty(Mono.empty())
            .filter(Objects::nonNull)
            .flatMap { member ->
                Mono.fromCallable {
                    repository.save(member)
                }.then(Mono.just(member))
            })
        .switchIfEmpty(notFound().build())

    @Transactional
    fun updateMember(request: ServerRequest): Mono<ServerResponse> = ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.justOrEmpty(repository.findByUid(request.pathVariable(userId)))
            .switchIfEmpty(Mono.empty())
            .filter(Objects::nonNull)
            .flatMap { member ->
                Mono.fromCallable {
                    repository.save(member)
                }.then(Mono.just(member))
            })
        .switchIfEmpty(notFound().build())

    @Transactional
    fun deleteMember(request: ServerRequest): Mono<ServerResponse> = ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.justOrEmpty(repository.findByUid(request.pathVariable(userId)))
            .switchIfEmpty(Mono.empty())
            .filter(Objects::nonNull)
            .flatMap { member ->
                Mono.fromCallable {
                    repository.delete(member)
                }.then(Mono.just(member))
            })
        .switchIfEmpty(notFound().build())
}