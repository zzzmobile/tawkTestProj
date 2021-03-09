package com.test.tawktest.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.test.tawktest.model.UserNoteTableModel

@Dao
interface DAOAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun InsertData(userNoteTableModel: UserNoteTableModel)

    @Query("SELECT * FROM Note WHERE login =:login")
    fun getUserNote(login: String?) : LiveData<UserNoteTableModel>

    @Query("SELECT * FROM Note")
    fun existUserNote() : LiveData<List<UserNoteTableModel>>

}