package com.example.contactsapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "contacts_table")
data class ContactsDto (

    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "contacts")
    val contacts: String?,

    @ColumnInfo(name = "number")
    val number: String?
) : Serializable