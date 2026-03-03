package org.delcom.dao

import org.delcom.tables.SwordTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID

class SwordDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, SwordDAO>(SwordTable)

    var nama by SwordTable.nama
    var pathGambar by SwordTable.pathGambar
    var sejarah by SwordTable.sejarah
    var kelebihan by SwordTable.kelebihan
    var faktaUnik by SwordTable.faktaUnik
    var createdAt by SwordTable.createdAt
    var updatedAt by SwordTable.updatedAt
}