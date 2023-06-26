package com.example.contactsapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.HeaderViewListAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandongogetap.stickyheaders.exposed.StickyHeader
import com.example.contactsapp.R
import com.example.contactsapp.databinding.ItemviewContactsBinding
import com.example.contactsapp.interfaces.ItemClickListener
import com.example.contactsapp.model.ContactsDto

class ContactsAdapter(private var listener: ItemClickListener): RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    private var ContactList = ArrayList<ContactsDto>()
    private var FullList = ArrayList<ContactsDto>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemviewContactsBinding>(
        LayoutInflater.from(parent.context), R.layout.itemview_contacts, parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ContactList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.sendData(ContactList[position])
        holder.binding.btnShare.setOnClickListener {
            listener.onShare(ContactList[position])
        }
        holder.binding.btnDelete.setOnClickListener {
            listener.onDelete(ContactList[position])
        }
        holder.binding.cardLayout.setOnClickListener {
            listener.onClick(ContactList[position])
        }


    }

    fun updateList(newList: List<ContactsDto>){
        FullList.clear()
        FullList.addAll(newList)

        ContactList.clear()
        ContactList.addAll(FullList)
        Log.e("MainTAGLog", "updateList: $newList")

        notifyDataSetChanged()
    }

    fun filterList(search: String){

        ContactList.clear()

        for (item in FullList){
            if (item.contacts?.lowercase()?.contains(search.lowercase()) == true ||
                item.number?.lowercase()?.contains(search.lowercase()) == true){

                ContactList.add(item)

            }
        }
        notifyDataSetChanged()

    }

    class ViewHolder (val binding: ItemviewContactsBinding):RecyclerView.ViewHolder(binding.root)  {
        fun sendData(data: ContactsDto){
            binding.mydata = data
        }
    }
}