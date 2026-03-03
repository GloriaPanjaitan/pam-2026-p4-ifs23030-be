package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Sword

@Serializable
data class SwordRequest(
    var nama: String = "",
    var sejarah: String = "",     // Pengganti deskripsi
    var kelebihan: String = "",   // Pengganti manfaat
    var faktaUnik: String = "",   // Pengganti efekSamping
    var pathGambar: String = "",
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "sejarah" to sejarah,
            "kelebihan" to kelebihan,
            "faktaUnik" to faktaUnik,
            "pathGambar" to pathGambar
        )
    }

    fun toEntity(): Sword {
        return Sword(
            nama = nama,
            sejarah = sejarah,
            kelebihan = kelebihan,
            faktaUnik = faktaUnik,
            pathGambar = pathGambar
        )
    }
}