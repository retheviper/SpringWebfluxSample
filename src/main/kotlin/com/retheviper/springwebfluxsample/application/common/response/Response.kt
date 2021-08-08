package com.retheviper.springwebfluxsample.application.common.response

import org.reactivestreams.Publisher
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body

object Response {

    fun ok(publisher: Publisher<Any>) =
        ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(publisher)

    fun accepted(publisher: Publisher<Any>) =
        ServerResponse
            .accepted()
            .contentType(MediaType.APPLICATION_JSON)
            .body(publisher)

    fun created(publisher: Publisher<Any>) =
        ServerResponse
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(publisher)
}