package org.delcom.module

import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.PlantRepository
import org.delcom.repositories.ISwordRepository    // Import Interface Sword
import org.delcom.repositories.SwordRepository     // Import Repo Sword
import org.delcom.services.PlantService
import org.delcom.services.SwordService             // Import Service Sword
import org.delcom.services.ProfileService
import org.koin.dsl.module

val appModule = module {
    // --- PLANT SECTION (DIPERTAHANKAN) ---
    single<IPlantRepository> {
        PlantRepository()
    }

    single {
        PlantService(get())
    }

    // --- SWORD SECTION (TAMBAHAN KAMU) ---
    // Mendaftarkan Repository Sword
    single<ISwordRepository> {
        SwordRepository()
    }

    // Mendaftarkan Service Sword
    single {
        SwordService(get())
    }

    // --- PROFILE SECTION ---
    single {
        ProfileService()
    }
}