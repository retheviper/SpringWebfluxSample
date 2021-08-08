package com.retheviper.springwebfluxsample.application.handler

import com.retheviper.springwebfluxsample.application.common.constant.Constant
import com.retheviper.springwebfluxsample.application.common.response.Response
import com.retheviper.springwebfluxsample.application.domain.model.dto.MemberDto
import com.retheviper.springwebfluxsample.application.domain.model.entity.Member
import com.retheviper.springwebfluxsample.application.domain.model.entity.MemberForm
import com.retheviper.springwebfluxsample.application.domain.repository.MemberRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.kotlin.extra.bool.not
import java.time.LocalDateTime

@Component
class MemberHandler(private val repository: MemberRepository, private val passwordEncoder: PasswordEncoder) {

    suspend fun listMember(): ServerResponse =
        Response.ok(
            repository.findAll()
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map { MemberDto(it.uid, it.name) }
        ).switchIfEmpty(notFound().build()).awaitSingle()

    suspend fun getMember(request: ServerRequest): ServerResponse =
        Response.ok(
            repository.findById(request.pathVariable(Constant.ID).toLong())
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map { record -> record.let { MemberDto(it.uid, it.name) } })
            .switchIfEmpty(notFound().build()).awaitSingle()

    suspend fun createMember(request: ServerRequest): ServerResponse =
        Response.created(
            request.bodyToMono(MemberForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .filterWhen { repository.existsByUid(it.uid).not() }
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.CONFLICT)))
                .flatMap { member ->
                    repository.save(
                        Member(
                            id = null,
                            uid = member.uid,
                            name = member.name,
                            password = passwordEncoder.encode(member.password),
                            accountNonExpired = true,
                            accountNonLocked = true,
                            enabled = true,
                            credentialsNonExpired = true,
                            createdBy = member.uid,
                            createdDate = LocalDateTime.now(),
                            lastModifiedBy = member.uid,
                            lastModifiedDate = LocalDateTime.now(),
                            memberInformationId = null
                        )
                    ).map { MemberDto(it.uid, it.name) }
                }).awaitSingle()

    suspend fun updateMember(request: ServerRequest): ServerResponse {
        val original = repository.findById(request.pathVariable(Constant.ID).toLong())
            .awaitSingle()

        return Response.ok(
            request.bodyToMono(MemberForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .filter { passwordEncoder.matches(it.password, original.password) }
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap { member ->
                    Mono.fromCallable {
                        repository.save(
                            original.copy(
                                id = original.id,
                                uid = request.pathVariable(Constant.ID),
                                name = member.name,
                                password = passwordEncoder.encode(member.password)
                            )
                        ).map { MemberDto(it.uid, it.name) }
                    }
                }
        ).awaitSingle()
    }

    suspend fun deleteMember(request: ServerRequest): ServerResponse =
        Response.ok(
            repository.findByUid(request.pathVariable(Constant.ID))
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map { repository.delete(it) }
        ).awaitSingle()
}