package com.example.contactsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.model.ContactsDto
import com.example.contactsapp.repository.ContactsRepo
import java.lang.IllegalArgumentException

class ContactsViewModelFactory(private val repository: ContactsRepo) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)){
            return ContactsViewModel(repository) as T
        } else{
            throw IllegalArgumentException("Not Assigned")
        }
    }

}