package com.example

import com.example.plugins.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.test.*

class ApplicationTest {
    companion object {
        val postgres = PostgreSQLContainer("postgres:latest")

        init {
            postgres.withReuse(true)
            postgres.start()
        }
    }
    @Test
    fun testRoot() = testApplication {
        environment {
            config = config.mergeWith(
                MapApplicationConfig(
                    "ktor.storage.jdbcURL" to postgres.getJdbcUrl(),
                    "ktor.storage.driverClassName" to "org.postgresql.Driver",
                    "ktor.storage.username" to postgres.username,
                    "ktor.storage.password" to postgres.password,
                    "ktor.storage.flyway.cleanOnValidationError" to "true",
                )
            )
        }
        application {
            DatabaseFactory.init(environment.config)
            flywayMigrations(DatabaseFactory.dataSource)
            configureSerialization()
            configureDatabases()
            configureRouting()
        }
        val response = client.post("/cities") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Berlin", "population": 4000000}""")
        }
        client.get("/cities/${response.bodyAsText()}").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("""{"name":"Berlin","population":4000000}""", bodyAsText())
        }
    }
}
