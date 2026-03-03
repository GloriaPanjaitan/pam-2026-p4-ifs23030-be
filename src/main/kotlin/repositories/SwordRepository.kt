package org.delcom.repositories

import org.delcom.dao.SwordDAO
import org.delcom.entities.Sword
import org.delcom.helpers.daoToSwordModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.SwordTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class SwordRepository : ISwordRepository {

    // Mengambil daftar semua pedang (dengan fitur pencarian nama)
    override suspend fun getSwords(search: String): List<Sword> = suspendTransaction {
        if (search.isBlank()) {
            SwordDAO.all()
                .orderBy(SwordTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToSwordModel)
        } else {
            val keyword = "%${search.lowercase()}%"
            SwordDAO.find {
                SwordTable.nama.lowerCase() like keyword
            }
                .orderBy(SwordTable.nama to SortOrder.ASC)
                .limit(20)
                .map(::daoToSwordModel)
        }
    }

    // Mengambil satu pedang berdasarkan ID
    override suspend fun getSwordById(id: String): Sword? = suspendTransaction {
        SwordDAO.find { (SwordTable.id eq UUID.fromString(id)) }
            .limit(1)
            .map(::daoToSwordModel)
            .firstOrNull()
    }

    // Mengambil data berdasarkan nama (untuk validasi agar tidak ada nama kembar)
    override suspend fun getSwordByName(name: String): Sword? = suspendTransaction {
        SwordDAO.find { (SwordTable.nama eq name) }
            .limit(1)
            .map(::daoToSwordModel)
            .firstOrNull()
    }

    // Menambah data pedang baru
    override suspend fun addSword(sword: Sword): String = suspendTransaction {
        val swordDAO = SwordDAO.new {
            nama = sword.nama
            pathGambar = sword.pathGambar
            sejarah = sword.sejarah
            kelebihan = sword.kelebihan
            faktaUnik = sword.faktaUnik
            createdAt = sword.createdAt
            updatedAt = sword.updatedAt
        }

        swordDAO.id.value.toString()
    }

    // Mengubah data pedang
    override suspend fun updateSword(id: String, newSword: Sword): Boolean = suspendTransaction {
        val swordDAO = SwordDAO
            .find { SwordTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (swordDAO != null) {
            swordDAO.nama = newSword.nama
            swordDAO.pathGambar = newSword.pathGambar
            swordDAO.sejarah = newSword.sejarah
            swordDAO.kelebihan = newSword.kelebihan
            swordDAO.faktaUnik = newSword.faktaUnik
            swordDAO.updatedAt = newSword.updatedAt
            true
        } else {
            false
        }
    }

    // Menghapus data pedang
    override suspend fun removeSword(id: String): Boolean = suspendTransaction {
        val rowsDeleted = SwordTable.deleteWhere {
            SwordTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}