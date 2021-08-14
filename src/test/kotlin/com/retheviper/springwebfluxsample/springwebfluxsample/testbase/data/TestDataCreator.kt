package com.retheviper.springwebfluxsample.springwebfluxsample.testbase.data

import com.retheviper.springwebfluxsample.domain.model.dto.MemberDto

object TestDataCreator {

    fun createMemberDto(): MemberDto =
        MemberDto(
            name = "member1",
            userId = "memberUid1"
        )

    fun createMemberDtoList(): List<MemberDto> =
        (0..10).map {
            MemberDto(
                name = "member$it",
                userId = "memberUid$it"
            )
        }
}