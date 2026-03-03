package org.delcom.entities

import kotlinx.datetime.Clock // Tambahkan import ini
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual // Tambahkan import ini
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Sword(
    var id: String = UUID.randomUUID().toString(),
    var nama: String,
    var pathGambar: String,
    var sejarah: String,
    var kelebihan: String,
    var faktaUnik: String,

    @Contextual
    val createdAt: Instant = Clock.System.now(), // Tambahkan default value ini
    @Contextual
    var updatedAt: Instant = Clock.System.now()  // Tambahkan default value ini
)