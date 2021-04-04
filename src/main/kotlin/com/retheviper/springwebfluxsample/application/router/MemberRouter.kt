package com.retheviper.springwebfluxsample.application.router

import com.retheviper.springwebfluxsample.application.handler.MemberHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.path
import org.springframework.web.reactive.function.server.RouterFunctions.nest
import org.springframework.web.reactive.function.server.router

@Configuration
class MemberRouter(private val handler: MemberHandler) {

    @Bean
    fun routeMember() = nest(path("/api/v1/web/members"),
        router {
            listOf(
                GET("", handler::listMember),
                GET("/{id}", handler::getMember),
                POST("", handler::createMember),
                PUT("/{id}", handler::updateMember),
                DELETE("/{id}", handler::deleteMember)
            )
        }
    )
}