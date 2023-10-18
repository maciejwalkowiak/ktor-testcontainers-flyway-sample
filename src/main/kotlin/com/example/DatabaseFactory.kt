package com.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import java.io.File

object DatabaseFactory {
    lateinit var dataSource: HikariDataSource

    fun init(config: ApplicationConfig) {
        val appConfig = config.config("ktor")
        val driverClassName = appConfig.property("storage.driverClassName").getString()
        val username = appConfig.property("storage.username").getString()
        val password = appConfig.property("storage.password").getString()
        val jdbcURL = appConfig.property("storage.jdbcURL").getString()
        this.dataSource = createHikariDataSource(url = jdbcURL, driver = driverClassName, username = username, password = password)
    }

    fun connection() = dataSource.connection

    private fun createHikariDataSource(
        url: String,
        driver: String,
        username: String,
        password: String
    ): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = driver
            jdbcUrl = url
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        config.username = username
        config.password = password
        return HikariDataSource(config)
    }
}
