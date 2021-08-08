package com.retheviper.springwebfluxsample.springwebfluxsample.testbase

import com.retheviper.springwebfluxsample.application.domain.model.dto.MemberDto

object TestBase {

    fun createMemberDtoList() =
        (0..10).map {
            MemberDto(
                name = "member$it",
                uid = "memberUid$it"
            )
        }
}