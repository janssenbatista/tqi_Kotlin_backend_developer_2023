package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023

import org.springframework.boot.fromApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.boot.with
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer

@TestConfiguration(proxyBeanMethods = false)
class Application {

    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer("postgres:14")
    }

}

fun main(args: Array<String>) {
    fromApplication<TqiKotlinBackednDeveloper2023Application>().with(Application::class).run(*args)
}
