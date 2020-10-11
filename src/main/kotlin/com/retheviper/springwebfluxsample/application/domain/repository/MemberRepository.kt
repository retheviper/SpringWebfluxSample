package com.retheviper.springwebfluxsample.application.domain.repository

import com.retheviper.springwebfluxsample.application.domain.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<Member, Long> {

    fun findByUid(uid: String): Member?

    fun existsByUid(uid: String): Boolean
}