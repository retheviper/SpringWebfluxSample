package com.retheviper.springwebfluxsample.springwebfluxsample.testbase.data

import com.retheviper.springwebfluxsample.domain.model.dto.MemberDto
import com.retheviper.springwebfluxsample.domain.model.entity.Member
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import java.time.LocalDateTime

object TestDataCreator {

    private val passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    fun createMemberDto(): MemberDto =
        MemberDto(
            userId = "member1",
            name = "memberName1"
        )

    fun createMemberDtoList(): List<MemberDto> =
        (0..10).map {
            MemberDto(
                userId = "member$it",
                name = "memberName$it"
            )
        }

    fun createMember(): Member =
        Member(
            id = 1,
            userId = "member1",
            name = "memberName1",
            password = passwordEncoder.encode("1234"),
            createdBy = "member1",
            createdDate = LocalDateTime.of(2021, 8, 15, 9, 30),
            lastModifiedBy = "member1",
            lastModifiedDate = LocalDateTime.of(2021, 8, 15, 9, 30),
            accountNonExpired = true,
            accountNonLocked = true,
            credentialsNonExpired = true,
            enabled = true,
            memberInformationId = null
        )

    fun createMemberList(): List<Member> =
        (0..10).map {
            Member(
                id = it.toLong(),
                userId = "member$it",
                name = "memberName$it",
                password = passwordEncoder.encode("${it}234"),
                createdBy = "member$it",
                createdDate = LocalDateTime.of(2021, 8, 15 + it, 9, 30),
                lastModifiedBy = "member$it",
                lastModifiedDate = LocalDateTime.of(2021, 8, 15 + it, 9, 30),
                accountNonExpired = true,
                accountNonLocked = true,
                credentialsNonExpired = true,
                enabled = true,
                memberInformationId = null
            )
        }
}