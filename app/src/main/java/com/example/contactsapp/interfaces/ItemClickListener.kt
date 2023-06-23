package com.example.contactsapp.interfaces

import com.example.contactsapp.model.ContactsDto

interface ItemClickListener {

    fun onShare(contact: ContactsDto)
    fun onDelete(contact: ContactsDto)
    fun onClick(contact: ContactsDto)

}