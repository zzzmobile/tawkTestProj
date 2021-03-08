package com.test.tawktest.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.test.tawktest.model.UserNoteTableModel

@Database(entities = arrayOf(UserNoteTableModel::class), version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao() : DAOAccess

    companion object {

        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabaseClient(context: Context) : NoteDatabase {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, NoteDatabase::class.java, "NOTE_DATABASE")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }

    }

}