package com.retheviper.springwebfluxsample.springwebfluxsample.testbase.data

import com.retheviper.springwebfluxsample.application.domain.model.dto.MemberDto

object TestDataCreator {

    fun createMemberDtoList() =
        (0..10).map {
            MemberDto(
                name = "member$it",
                uid = "memberUid$it"
            )
        }
}