package com.example.plugins

import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import javax.sql.DataSource

fun Application.flywayMigrations(dataSource: DataSource) {
    val cleanOnValidationError = environment.config
        .config("ktor")
        .property("storage.flyway.cleanOnValidationError")
        .getString()
        .toBoolean()

    Flyway.configure()
        .dataSource(dataSource)
        .cleanDisabled(!cleanOnValidationError)
        .cleanOnValidationError(cleanOnValidationError)
        .locations("classpath:/migrations/")
        .apply {
            Flyway(this).apply {
                migrate()
            }
        }
}
