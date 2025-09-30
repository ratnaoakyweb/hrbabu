package com.hrbabu.tracking.database
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PunchEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: PunchEvent): Long

    @Query("SELECT * FROM punch_events WHERE isSynced = 0 ORDER BY createdAt ASC")
    suspend fun getUnsyncedEvents(): List<PunchEvent>

    @Query("UPDATE punch_events SET isSynced = 1 WHERE id = :eventId")
    suspend fun markAsSynced(eventId: Long)

    @Query("SELECT * FROM punch_events ORDER BY createdAt DESC")
    suspend fun getAllEvents(): List<PunchEvent>
}
