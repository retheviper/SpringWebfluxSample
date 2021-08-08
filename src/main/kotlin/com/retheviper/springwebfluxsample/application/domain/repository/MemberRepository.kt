package com.retheviper.springwebfluxsample.application.domain.repository

import com.retheviper.springwebfluxsample.application.domain.model.entity.Member
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface MemberRepository : R2dbcRepository<Member, Long> {

    @Query("SELECT * FROM member WHERE uid = :uid ")
    fun findByUid(uid: String): Mono<Member?>

    @Query("SELECT CASE WHEN count(m.uid)> 0 THEN true ELSE false END FROM member AS m WHERE m.uid = :uid")
    fun existsByUid(uid: String): Mono<Boolean>
}