package com.retheviper.springwebfluxsample.application.domain.model.entity

import org.springframework.data.annotation.*
import java.time.LocalDateTime

data class Member(
    @Id var id: Long?,

    val uid: String,

    val name: String,

    val password: String,

    @CreatedBy
    val createdBy: String,

    @CreatedDate
    val createdDate: LocalDateTime,

    @LastModifiedBy
    val lastModifiedBy: String,

    @LastModifiedDate
    val lastModifiedDate: LocalDateTime,

    val accountNonExpired: Boolean,

    val accountNonLocked: Boolean,

    val credentialsNonExpired: Boolean,

    val enabled: Boolean,

    val memberInformationId: Long?
)

data class MemberForm(var uid: String, var name: String, var password: String)