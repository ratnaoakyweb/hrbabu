package com.hrbabu.tracking.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hrbabu.tracking.database.AppDatabase
import com.hrbabu.tracking.database.PunchEvent
import com.hrbabu.tracking.database.PunchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PunchViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: PunchRepository

    init {
        val dao = AppDatabase.getDatabase(application).punchEventDao()
        repo = PunchRepository(dao)
    }

    fun saveEvent(event: PunchEvent, onDone: (Long) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repo.insertEvent(event)
            onDone(id)
        }
    }

    fun getUnsynced(onResult: (List<PunchEvent>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repo.getUnsynced()
            onResult(list)
        }
    }

    fun markSynced(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.markSynced(id)
        }
    }
}