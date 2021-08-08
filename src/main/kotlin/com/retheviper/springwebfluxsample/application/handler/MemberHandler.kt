package com.retheviper.springwebfluxsample.application.handler

import com.retheviper.springwebfluxsample.application.common.constant.Constant
import com.retheviper.springwebfluxsample.application.common.response.Response
import com.retheviper.springwebfluxsample.application.domain.entity.Member
import com.retheviper.springwebfluxsample.application.domain.entity.MemberDto
import com.retheviper.springwebfluxsample.application.domain.entity.MemberForm
import com.retheviper.springwebfluxsample.application.domain.repository.MemberRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MemberHandler(private val repository: MemberRepository, private val passwordEncoder: PasswordEncoder) {

    fun listMember(request: ServerRequest): Mono<ServerResponse> =
        Response.ok(
            Flux.fromIterable(repository.findAll())
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map { MemberDto(it.uid, it.name) }
        ).switchIfEmpty(notFound().build())

    fun getMember(request: ServerRequest): Mono<ServerResponse> =
        Response.ok(
            Mono.just(repository.findById(request.pathVariable(Constant.ID).toLong()))
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map { record -> record.map { MemberDto(it.uid, it.name) }.orElseThrow() })
            .switchIfEmpty(notFound().build())

    fun createMember(request: ServerRequest): Mono<ServerResponse> =
        Response.created(
            request.bodyToMono(MemberForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .filter { !repository.existsByUid(it.uid) }
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.CONFLICT)))
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
                    }
                        .then(Mono.just(MemberDto(member.uid, member.name)))
                })

    fun updateMember(request: ServerRequest): Mono<ServerResponse> {
        val original = repository.findById(request.pathVariable(Constant.ID).toLong())
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        return Response.ok(
            request.bodyToMono(MemberForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .filter { passwordEncoder.matches(it.password, original.password) }
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap { member ->
                    Mono.fromCallable {
                        repository.save(
                            Member(
                                id = original.id,
                                uid = request.pathVariable(Constant.ID),
                                name = member.name,
                                password = passwordEncoder.encode(member.password)
                            )
                        )
                    }
                        .then(Mono.just(MemberDto(request.pathVariable(Constant.ID), member.name)))
                })
    }

    fun deleteMember(request: ServerRequest): Mono<ServerResponse> =
        Response.ok(
            Mono.justOrEmpty(repository.findByUid(request.pathVariable(Constant.ID)))
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map { repository.delete(it!!) }
        )
}