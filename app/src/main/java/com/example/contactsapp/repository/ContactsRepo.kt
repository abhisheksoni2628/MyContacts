package com.example.contactsapp.repository

import androidx.lifecycle.LiveData
import com.example.contactsapp.database.ContactsDao
import com.example.contactsapp.model.ContactsDto

class ContactsRepo(var contactDao: ContactsDao) {

    val allContacts: LiveData<List<ContactsDto>> =contactDao.getAllContacts()

    //val deleteFlag : Long = contactDao.deleteAllData()

    suspend fun insert(contact: ContactsDto){
        contactDao.insert(contact)
    }

    suspend fun delete(contact: ContactsDto){
        contactDao.delete(contact)
    }

    suspend fun update(contact: ContactsDto){
        contactDao.update(contact.id, contact.contacts, contact.number)
    }

    suspend fun deleteAll(){
        contactDao.deleteAllData()
    }


}