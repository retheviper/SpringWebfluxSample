package com.retheviper.springwebfluxsample.application.model.request

data class MemberUpsertForm(
    val userId: String,
    val name: String,
    val password: String
)