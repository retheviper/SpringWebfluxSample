package com.retheviper.springwebfluxsample.application.router

import com.retheviper.springwebfluxsample.application.common.constant.Constant
import com.retheviper.springwebfluxsample.application.handler.MemberHandler
import com.retheviper.springwebfluxsample.domain.model.dto.MemberDto
import com.retheviper.springwebfluxsample.domain.repository.MemberRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates.path
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.nest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class MemberRouter(
    private val handler: MemberHandler
) {

    @Bean
    fun routeMember(): RouterFunction<ServerResponse> =
        nest(path("/api/v1/web/members"),
            coRouter {
                GET("") { handler.listMember() }
                GET("/{id}", handler::getMember)
                POST("", handler::createMember)
                PUT("/{id}", handler::updateMember)
                DELETE("/{id}", handler::deleteMember)
            }
        )
}