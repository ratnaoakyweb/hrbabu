package com.hrbabu.tracking.database

class PunchRepository(private val dao: PunchEventDao) {

    suspend fun insertEvent(event: PunchEvent): Long = dao.insert(event)

    suspend fun getUnsynced(): List<PunchEvent> = dao.getUnsyncedEvents()

    suspend fun markSynced(id: Long) = dao.markAsSynced(id)

    suspend fun getAll() = dao.getAllEvents()
}
