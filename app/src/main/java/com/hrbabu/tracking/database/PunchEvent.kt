package com.hrbabu.tracking.database
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "punch_events")
data class PunchEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val imagePath: String,       // path in cacheDir (no read permission required)
    val eventType: String,       // "PunchIn" / "PunchOut"
    val createdAt: Long,         // System.currentTimeMillis()
    val isSynced: Boolean = false
)
