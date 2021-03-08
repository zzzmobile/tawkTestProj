package com.test.tawktest.model

import android.content.Context
import androidx.lifecycle.LiveData
import com.test.tawktest.room.NoteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteRepository {
    companion object {
        var noteDatabase: NoteDatabase? = null
        var noteTableModel: LiveData<UserNoteTableModel>? = null

        fun initializeDB(context: Context) : NoteDatabase {
            return NoteDatabase.getDatabaseClient(context)
        }

        // insert user note into room database
        fun insertData(context: Context, login: String, note: String) {
            noteDatabase = initializeDB(context)

            CoroutineScope(Dispatchers.IO).launch {
                val loginDetails = UserNoteTableModel(login, note)
                noteDatabase!!.noteDao().InsertData(loginDetails)
            }
        }

        // get user note from room database
        fun getUserNote(context: Context, login: String) : LiveData<UserNoteTableModel>? {
            noteDatabase = initializeDB(context)
            noteTableModel = noteDatabase!!.noteDao().getUserNote(login)
            return noteTableModel
        }
    }
}