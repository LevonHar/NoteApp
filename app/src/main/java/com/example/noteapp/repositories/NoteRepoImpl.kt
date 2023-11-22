package com.example.noteapp.repositories

import com.example.noteapp.database.NoteDatabase
import com.example.noteapp.database.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepoImpl(private val database: NoteDatabase): NoteRepo {
    private val dao = database.noteDao()

    override suspend fun addNote(note: NoteEntity) = dao.addNote(note)
    override suspend fun deleteNote(note: NoteEntity) = dao.deleteNote(note)
    override suspend fun getNotes(): Flow<List<NoteEntity>> = dao.getNotes()
}