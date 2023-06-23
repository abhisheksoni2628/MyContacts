package com.example.contactsapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.contactsapp.model.ContactsDto

const val DATABASE_NAME = "contacts_database"

@Database(entities = [ContactsDto::class], version = 1, exportSchema = false)
abstract class ContactsDatabase: RoomDatabase() {


    abstract fun getContactsDao(): ContactsDao

    companion object {

        @Volatile
        private var INSTANCE: ContactsDatabase? = null

        fun getDatabase(context: Context): ContactsDatabase {

            return ((INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactsDatabase::class.java,
                    DATABASE_NAME
                ).build()

                INSTANCE = instance

                instance

            }))

        }
    }
}