package com.retheviper.springwebfluxsample.domain.configuration

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions.*
import io.r2dbc.spi.Option
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator


@Configuration
@EnableR2dbcRepositories
class ServerR2dbcConfiguration : AbstractR2dbcConfiguration() {

    @Bean
    @ConditionalOnProperty(value = ["spring.profiles.active"], havingValue = "dev")
    override fun connectionFactory(): ConnectionFactory =
        ConnectionFactories.get(
            builder()
                .option(DRIVER, "h2")
                .option(PROTOCOL, "mem")
                .option(HOST, "localhost")
                .option(USER, "sa")
                .option(PASSWORD, "password")
                .option(DATABASE, "testdb")
                .option(Option.valueOf("DB_CLOSE_DELAY"), "-1")
                .build()
        )

    @Bean
    fun connectionFactoryInitializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer =
        ConnectionFactoryInitializer().apply {
            setConnectionFactory(connectionFactory)
            setDatabasePopulator(
                CompositeDatabasePopulator().apply {
                    addPopulators(
                        ResourceDatabasePopulator(ClassPathResource("schema.sql")),
                        ResourceDatabasePopulator(ClassPathResource("data.sql"))
                    )
                }
            )
        }
}