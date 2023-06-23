package com.example.contactsapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.contactsapp.model.ContactsDto

@Dao
interface ContactsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: ContactsDto)

    @Delete
    suspend fun delete(contact: ContactsDto)

    @Query("SELECT * from contacts_table order by id ASC")
    fun getAllContacts(): LiveData<List<ContactsDto>>

    @Query("DELETE from contacts_table")
    suspend fun deleteAllData()

    @Query("UPDATE contacts_table SET contacts= :contact, number= :number WHERE id= :id")
    suspend fun update(id: Int?, contact: String?, number: String?)
}