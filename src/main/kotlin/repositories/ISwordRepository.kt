package org.delcom.repositories

import org.delcom.entities.Sword

interface ISwordRepository {
    suspend fun getSwords(search: String): List<Sword>
    suspend fun getSwordById(id: String): Sword?
    suspend fun getSwordByName(name: String): Sword?
    suspend fun addSword(sword: Sword): String
    suspend fun updateSword(id: String, newSword: Sword): Boolean
    suspend fun removeSword(id: String): Boolean
}