package com.retheviper.springwebfluxsample.application.domain.entity

import javax.persistence.*

@Entity
data class Member(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long?,
    @Column(nullable = false, unique = true, length = 16) var uid: String,
    @Column(nullable = false, length = 16) var name: String,
    @Column(nullable = false) var password: String
)

data class MemberDto(var uid: String, var name: String)

data class MemberForm(var uid: String, var name: String, var password: String)