package com.example.contactsapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactsapp.database.ContactsDatabase
import com.example.contactsapp.model.ContactsDto
import com.example.contactsapp.repository.ContactsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactsViewModel(private val repository: ContactsRepo): ViewModel() {

    val allContacts: LiveData<List<ContactsDto>>

    init {
        allContacts = repository.allContacts
    }

    fun delete(contact: ContactsDto){
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(contact)
        }
    }

    fun insert(contact: ContactsDto){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(contact)
        }
    }

    fun update(contact: ContactsDto){
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(contact)
        }
    }

    fun deleteAll(){
        viewModelScope.launch(Dispatchers.Main){
            repository.deleteAll()
        }
    }

}