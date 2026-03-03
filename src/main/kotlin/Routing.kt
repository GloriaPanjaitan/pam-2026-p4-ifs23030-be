package org.delcom

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.data.AppException
import org.delcom.data.ErrorResponse
import org.delcom.helpers.parseMessageToMap
import org.delcom.services.PlantService
import org.delcom.services.SwordService
import org.delcom.services.ProfileService
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val plantService: PlantService by inject()
    val swordService: SwordService by inject()
    val profileService: ProfileService by inject()

    install(StatusPages) {
        // Tangkap AppException (Validasi gagal, dsb)
        exception<AppException> { call, cause ->
            val dataMap: Map<String, List<String>> = parseMessageToMap(cause.message)
            call.respond(
                status = HttpStatusCode.fromValue(cause.code),
                message = ErrorResponse(
                    status = "fail",
                    message = if (dataMap.isEmpty()) cause.message else "Data yang dikirimkan tidak valid!",
                    data = if (dataMap.isEmpty()) null else dataMap.toString()
                )
            )
        }

        // Tangkap semua Throwable lainnya (Error server 500)
        exception<Throwable> { call, cause ->
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse(
                    status = "error",
                    message = cause.message ?: "Unknown error",
                    data = ""
                )
            )
        }
    }

    routing {
        // Halaman depan (Root)
        get("/") {
            call.respondText("API telah berjalan. Dibuat oleh Gloria Panjaitan.")
        }

        // --- SEMUA API DIBUNGKUS DALAM /api AGAR TIDAK 404 ---
        route("/api") {

            // 1. Route Plants
            route("/plants") {
                get { plantService.getAllPlants(call) }
                post { plantService.createPlant(call) }
                get("/{id}") { plantService.getPlantById(call) }
                put("/{id}") { plantService.updatePlant(call) }
                delete("/{id}") { plantService.deletePlant(call) }
                get("/{id}/image") { plantService.getPlantImage(call) }
            }

            // 2. Route Swords (Milik Gloria)
            route("/swords") {
                get { swordService.getAllSwords(call) }
                post { swordService.createSword(call) }
                get("/{id}") { swordService.getSwordById(call) }
                delete("/{id}") { swordService.deleteSword(call) }
                get("/{id}/image") { swordService.getSwordImage(call) }
            }

            // 3. Route Profile
            route("/profile") {
                get { profileService.getProfile(call) }
                get("/photo") { profileService.getProfilePhoto(call) }
            }
        }
    }
}