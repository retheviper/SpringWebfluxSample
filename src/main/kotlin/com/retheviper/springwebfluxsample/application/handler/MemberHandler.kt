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
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MemberHandler(private val repository: MemberRepository, private val passwordEncoder: PasswordEncoder) {

    private val id: String = "id"

    fun listMember(request: ServerRequest): Mono<ServerResponse> =
        ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Flux.fromIterable(repository.findAll())
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map { MemberDto(it.uid, it.name) })
            .switchIfEmpty(notFound().build())

    fun getMember(request: ServerRequest): Mono<ServerResponse> =
        ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(repository.findById(request.pathVariable(id).toLong()))
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map { record -> record.map { MemberDto(it.uid, it.name) }.orElseThrow() })
            .switchIfEmpty(notFound().build())

    fun createMember(request: ServerRequest): Mono<ServerResponse> =
        accepted()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request.bodyToMono(MemberForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .filter { member -> !repository.existsByUid(member.uid) }
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
        val original = repository.findById(request.pathVariable(id).toLong())
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request.bodyToMono(MemberForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .filter { passwordEncoder.matches(it.password, original.password) }
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap { member ->
                    Mono.fromCallable {
                        repository.save(
                            Member(
                                id = original.id,
                                uid = request.pathVariable(id),
                                name = member.name,
                                password = passwordEncoder.encode(member.password)
                            )
                        )
                    }
                        .then(Mono.just(MemberDto(request.pathVariable(id), member.name)))
                })
    }

    fun deleteMember(request: ServerRequest): Mono<ServerResponse> =
        ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.justOrEmpty(repository.findByUid(request.pathVariable(id)))
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map { member -> repository.delete(member) })
}