package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.utils.io.jvm.javaio.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.SwordRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.ISwordRepository
import java.io.File
import java.util.*

class SwordService(private val swordRepository: ISwordRepository) {

    // 1. Mengambil semua data pedang (dengan fitur search)
    suspend fun getAllSwords(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val swords = swordRepository.getSwords(search)
        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar pedang",
            mapOf("swords" to swords)
        )
        call.respond(response)
    }

    // 2. Mengambil detail satu pedang berdasarkan ID
    suspend fun getSwordById(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID pedang tidak boleh kosong!")
        val sword = swordRepository.getSwordById(id) ?: throw AppException(404, "Data pedang tidak ditemukan!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data pedang",
            mapOf("sword" to sword)
        )
        call.respond(response)
    }

    // 3. Helper untuk Parsing Multipart Form Data
    private suspend fun getSwordRequest(call: ApplicationCall): SwordRequest {
        val swordReq = SwordRequest()
        val multipartData = call.receiveMultipart()

        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    // Mapping data teks dari Android
                    when (part.name) {
                        "nama" -> swordReq.nama = part.value.trim()
                        "sejarah" -> swordReq.sejarah = part.value.trim()
                        "kelebihan" -> swordReq.kelebihan = part.value.trim()
                        "faktaUnik" -> swordReq.faktaUnik = part.value.trim()
                    }
                }
                is PartData.FileItem -> {
                    // Cek nama part "file" (harus sama dengan ToolsHelper di Android)
                    if (part.name == "file") {
                        val ext = part.originalFileName?.substringAfterLast('.', "")?.let {
                            if (it.isNotEmpty()) ".$it" else ""
                        } ?: ".jpg"

                        val fileName = UUID.randomUUID().toString() + ext
                        val folderPath = "uploads/swords"
                        val filePath = "$folderPath/$fileName"

                        // Membuat folder jika belum ada
                        val folder = File(folderPath)
                        if (!folder.exists()) folder.mkdirs()

                        // Proses penyimpanan file
                        val file = File(filePath)
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                        swordReq.pathGambar = filePath
                    }
                }
                else -> {}
            }
            part.dispose()
        }
        return swordReq
    }

    // 4. Membuat Data Pedang Baru
    suspend fun createSword(call: ApplicationCall) {
        val swordReq = getSwordRequest(call)

        // --- Validasi Input ---
        val validator = ValidatorHelper(swordReq.toMap())
        validator.required("nama", "Nama pedang wajib diisi!")
        validator.required("sejarah", "Sejarah pedang tidak boleh kosong!")
        validator.minLength("sejarah", 10, "Sejarah minimal harus 10 karakter!")
        validator.required("kelebihan", "Kelebihan pedang wajib diisi!")
        validator.required("pathGambar", "Gambar pedang wajib diunggah!")
        validator.validate()

        // Cek Duplikasi Nama
        if (swordRepository.getSwordByName(swordReq.nama) != null) {
            if (swordReq.pathGambar.isNotEmpty()) File(swordReq.pathGambar).delete()
            throw AppException(409, "nama: Pedang dengan nama ini sudah ada!")
        }

        // Simpan ke Database
        val swordId = swordRepository.addSword(swordReq.toEntity())

        val response = DataResponse(
            "success",
            "Berhasil menambah pedang",
            mapOf("swordId" to swordId)
        )
        call.respond(HttpStatusCode.Created, response)
    }

    // 5. Menghapus Data Pedang
    suspend fun deleteSword(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID tidak boleh kosong")
        val sword = swordRepository.getSwordById(id) ?: throw AppException(404, "Data tidak ditemukan")

        if (swordRepository.removeSword(id)) {
            val file = File(sword.pathGambar)
            if (file.exists()) file.delete()
            call.respond(DataResponse("success", "Berhasil menghapus pedang", null))
        } else {
            throw AppException(500, "Gagal menghapus data dari database")
        }
    }

    // 6. Menyajikan File Gambar
    suspend fun getSwordImage(call: ApplicationCall) {
        val id = call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest)
        val sword = swordRepository.getSwordById(id) ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(sword.pathGambar)
        if (!file.exists()) {
            call.respond(HttpStatusCode.NotFound, "File gambar tidak ditemukan")
            return
        }
        call.respondFile(file)
    }
}