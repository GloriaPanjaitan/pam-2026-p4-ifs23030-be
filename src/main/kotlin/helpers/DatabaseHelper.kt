package org.delcom.helpers

import io.ktor.server.application.*
import org.delcom.tables.PlantTable    // Import tabel Plant milik dosen
import org.delcom.tables.SwordTable    // Import tabel Sword milik kamu
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val dbHost = environment.config.property("ktor.database.host").getString()
    val dbPort = environment.config.property("ktor.database.port").getString()
    val dbName = environment.config.property("ktor.database.name").getString()
    val dbUser = environment.config.property("ktor.database.user").getString()
    val dbPassword = environment.config.property("ktor.database.password").getString()

    val database = Database.connect(
        url = "jdbc:postgresql://$dbHost:$dbPort/$dbName",
        driver = "org.postgresql.Driver",
        user = dbUser,
        password = dbPassword
    )

    // BAGIAN INI YANG PENTING:
    // Menjalankan perintah pembuatan tabel secara otomatis
    transaction(database) {
        SchemaUtils.create(PlantTable, SwordTable)
    }
}