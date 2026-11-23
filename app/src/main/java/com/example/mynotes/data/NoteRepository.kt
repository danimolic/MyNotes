package com.example.mynotes.data

import kotlinx.coroutines.flow.Flow

/** Punto único de acceso al DAO. */
class NoteRepository(private val dao: NoteDao) {
    val notes: Flow<List<Note>> = dao.observeAll()

    suspend fun findById(id: Long): Note? = dao.findById(id)

    suspend fun save(note: Note) {
        if (note.id == 0L) dao.insert(note) else dao.update(note)
    }

    suspend fun delete(note: Note) = dao.delete(note)

    suspend fun setCompleted(note: Note, completed: Boolean) =
        dao.update(note.copy(completed = completed))
}
