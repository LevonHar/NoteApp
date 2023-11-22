package com.example.noteapp

import android.app.Application
import androidx.room.Room
import com.example.noteapp.database.NoteDatabase
import com.example.noteapp.repositories.NoteRepo
import com.example.noteapp.repositories.NoteRepoImpl
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class KotlinApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(module {
                single {
                    Room
                        .databaseBuilder(this@KotlinApp,NoteDatabase::class.java, "db")
                        .build()
                }
                single {
                    NoteRepoImpl(database = get())
                } bind NoteRepo::class
            })
        }
    }
}