package com.retheviper.springwebfluxsample.domain.model.entity

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Column
import java.time.LocalDateTime

data class Member(

    @Id
    var id: Long?,

    val userId: String,

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