package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.PlantDAO
import org.delcom.dao.SwordDAO      // Tambahkan import untuk SwordDAO
import org.delcom.entities.Plant
import org.delcom.entities.Sword      // Tambahkan import untuk Sword
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

// --- KODE PLANT (DIPERTAHANKAN) ---
fun daoToModel(dao: PlantDAO) = Plant(
    dao.id.value.toString(),
    dao.nama,
    dao.pathGambar,
    dao.deskripsi,
    dao.manfaat,
    dao.efekSamping,
    dao.createdAt,
    dao.updatedAt
)

// --- KODE SWORD (DITAMBAHKAN) ---
fun daoToSwordModel(dao: SwordDAO) = Sword(
    id = dao.id.value.toString(),
    nama = dao.nama,
    pathGambar = dao.pathGambar,
    sejarah = dao.sejarah,        // Sesuai field yang kamu inginkan
    kelebihan = dao.kelebihan,    // Sesuai field yang kamu inginkan
    faktaUnik = dao.faktaUnik,    // Gunakan camelCase (tanpa spasi)
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt
)