package com.example.mynotes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.Note
import com.example.mynotes.data.NoteDatabase
import com.example.mynotes.data.NoteRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NoteRepository(NoteDatabase.getInstance(application).noteDao())


    val notes: StateFlow<List<Note>?> = repository.notes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    suspend fun findById(id: Long): Note? = repository.findById(id)

    fun save(note: Note): Job = viewModelScope.launch { repository.save(note) }

    fun delete(note: Note): Job = viewModelScope.launch { repository.delete(note) }

    fun toggleCompleted(note: Note): Job =
        viewModelScope.launch { repository.setCompleted(note, !note.completed) }
}
