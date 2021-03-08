package com.test.tawktest.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Note")
data class UserNoteTableModel (
    @ColumnInfo(name = "login")
    var login: String,

    @ColumnInfo(name = "note")
    var note: String

) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var Id: Int? = null

}