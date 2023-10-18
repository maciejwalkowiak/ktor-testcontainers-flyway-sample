package com.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import java.io.File

object DatabaseFactory {
    private lateinit var dataSource: HikariDataSource

    fun init(config: ApplicationConfig) {
        val appConfig = config.config("ktor")
        val driverClassName = appConfig.property("storage.driverClassName").getString()
        val jdbcURL = appConfig.property("storage.jdbcURL").getString() +
                (appConfig.propertyOrNull("storage.dbFilePath")?.getString()?.let {
                    File(it).canonicalFile.absolutePath
                } ?: "")
        this.dataSource = createHikariDataSource(url = jdbcURL, driver = driverClassName)
    }

    fun connection() = dataSource.connection

    private fun createHikariDataSource(
        url: String,
        driver: String
    ) = HikariDataSource(HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })
}
