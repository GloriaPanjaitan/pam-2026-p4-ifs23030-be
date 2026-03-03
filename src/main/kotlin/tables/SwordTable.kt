package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object SwordTable : UUIDTable("swords") {
    val nama = varchar("nama", 100)
    val pathGambar = varchar("path_gambar", 255)
    val sejarah = text("sejarah")
    val kelebihan = text("kelebihan")
    val faktaUnik = text("fakta_unik")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}