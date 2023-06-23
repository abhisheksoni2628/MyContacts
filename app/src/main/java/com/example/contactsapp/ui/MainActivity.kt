package com.example.contactsapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.R
import com.example.contactsapp.adapter.ContactsAdapter
import com.example.contactsapp.database.ContactsDatabase
import com.example.contactsapp.databinding.ActivityMainBinding
import com.example.contactsapp.interfaces.ItemClickListener
import com.example.contactsapp.model.ContactsDto
import com.example.contactsapp.repository.ContactsRepo
import com.example.contactsapp.viewmodel.ContactsViewModel
import com.example.contactsapp.viewmodel.ContactsViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), ItemClickListener {

    val TAG = "MainTAG"

    private val permissionRequestForReadContacts =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // PERMISSION GRANTED
                Log.e(TAG, "onRequestPermissionsResult: All permission Granted")
                CoroutineScope(Dispatchers.Main).launch {
                    fetchContacts()
                }

            } else {
                // PERMISSION NOT GRANTED
                if (Build.VERSION.SDK_INT < 33) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.READ_CONTACTS
                        )
                    ) {
                        //todo check permission
                        checkForContactsPermission { isGranted ->
                            if (isGranted) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    fetchContacts()
                                }
                            }
                        }

                    } else {
                        //todo open setting
                        openPhoneSettings("Contacts")

                    }
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.READ_CONTACTS
                        )
                    ) {
                        //todo check permission
                        checkForContactsPermission { isGranted ->
                            if (isGranted) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    fetchContacts()
                                }
                            }
                        }
                    } else {
                        //todo check permission
                        openPhoneSettings("Contacts")

                    }
                }
            }
        }

    val viewModel: ContactsViewModel by lazy {
        ViewModelProvider(
            this,
            ContactsViewModelFactory(
                ContactsRepo(
                    ContactsDatabase.getDatabase(this).getContactsDao()
                )
            )
        )[ContactsViewModel::class.java]
    }
    lateinit var adapter: ContactsAdapter
    lateinit var database: ContactsDatabase
    //lateinit var currentContact: ContactsDto

   /* val permission = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.CALL_PHONE
    )*/
    val permissionCode = 1000

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
        viewModel.allContacts.observe(this) {
           /* if (allPermissionGranted()) {
                Log.e(TAG, "onCreate: observe${it.size} -> $it")
                if (it.isEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        fetchContacts()
                    }
                } else {
                    binding.progressBar.visibility = GONE
                    adapter.updateList(it)
                }
            } else {*/
                checkForContactsPermission{ isGranted ->
                    if (isGranted) {
                        if (it.isEmpty()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                fetchContacts()
                            }
                        } else {
                            adapter.updateList(it)
                            binding.progressBar.visibility = GONE
                        }
                    }
                }
            }

        binding.ivRefresh.setOnClickListener {
            binding.progressBar.visibility = VISIBLE
            //binding.recyclerView.visibility = GONE
            viewModel.deleteAll()
            //binding.progressBar.visibility = GONE
        }

    }

/*    private fun dataFectchCall() {
        CoroutineScope(Dispatchers.Main).launch {
            fetchContacts()
        }
    }*/

    private fun initialize() {
        adapter = ContactsAdapter(this)
        binding.recyclerView.adapter = adapter

    }


    @SuppressLint("Range")
    private fun fetchContacts() {
        binding.progressBar.visibility = VISIBLE
        val cr = contentResolver
        val cursor: Cursor? =
            cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        if (cursor != null && cursor.count > 0) {

            while (cursor != null && cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNum =
                    cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                if (hasPhoneNum > 0) {
                    val pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        ""
                    )

                    if (pCur != null && pCur.count > 0) {

                        while (pCur != null && pCur.moveToNext()) {
                            val phoneNum =
                                pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                            if (name.isNotEmpty() || phoneNum.isNotEmpty()) {
                                Log.e(TAG, "fetchContacts: $name and $phoneNum")
                                //currentContact = ContactsDto(null, name, phoneNum)
                                viewModel.insert(ContactsDto(null, name, phoneNum))
                            }
                        }
                        pCur.close()
                    }
                }
            }
            cursor.close()
        }
    }

    private fun askForPermission() {
//        ActivityCompat.requestPermissions(this, permission, permissionCode)
    }

    private fun allPermissionGranted(): Boolean {
//        for (item in permission) {
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    item
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return false
//            }
//        }
        return true
    }

/*
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionCode) {

            if (allPermissionGranted()) {
                Log.e(TAG, "onRequestPermissionsResult: All permission Granted")
                CoroutineScope(Dispatchers.Main).launch {
                    fetchContacts()
                }
            } else {
                askForPermission()
            }
        }
    }*/

    private fun checkForContactsPermission(isPermissionGranted: (isGranted: Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT < 33) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isPermissionGranted(true)
            }
            else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CONTACTS
                )
            ) {
                MaterialAlertDialogBuilder(
                    this,
                ).setTitle("Read Contacts")
                    .setMessage("To get Contacts we require this permission.")
                    .setCancelable(false)
                    .setPositiveButton("Continue") { d: DialogInterface?, w: Int ->
                        permissionRequestForReadContacts.launch(Manifest.permission.READ_CONTACTS)
                    }.setNegativeButton("Cancel") { d: DialogInterface, w: Int ->
                        d.dismiss()
                        openPhoneSettings("Contacts")
                    }
                    .show()
            }
            else {
                permissionRequestForReadContacts.launch(Manifest.permission.READ_CONTACTS)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isPermissionGranted(true)
            }
            else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CONTACTS
                )
            ) {
                MaterialAlertDialogBuilder(
                    this,
                ).setTitle("Read Contacts")
                    .setMessage("To get Contacts we require this permission.")
                    .setCancelable(false)
                    .setPositiveButton("Continue") { d: DialogInterface?, w: Int ->
                        permissionRequestForReadContacts.launch(Manifest.permission.READ_CONTACTS)
                    }.setNegativeButton("Cancel") { d: DialogInterface, w: Int ->
                        d.dismiss()
                        openPhoneSettings("Contacts")
                    }
                    .show()
            }
            else {
                permissionRequestForReadContacts.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun openPhoneSettings(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permission required")
            .setMessage("Goto app's setting and grant following permission -> $message")
            .setPositiveButton("Open Settings"
            ) { p0, p1 ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri = Uri.fromParts(
                    "package", packageName, null
                )
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel"
            ) { p0, p1 ->

                p0?.dismiss()
                openPhoneSettings("Contacts")
            }.show()




    }

    override fun onShare(contact: ContactsDto) {
        val text = "Name - ${contact.contacts}" + "\nNumber - ${contact.number}"
        contact.contacts?.let {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("OCR Text", text)
            clipboard.setPrimaryClip(clip)

            contact.contacts.let {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, text)
                if (shareIntent.resolveActivity(packageManager) != null) {
                    startActivity(Intent.createChooser(shareIntent, "Share via"))
                } else {
                    Toast.makeText(this, "No apps available to share", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDelete(contact: ContactsDto) {

        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Delete this Contact")
        builder.setMessage("Are you sure to delete this contact ?")
        builder.setIcon(R.drawable.baseline_delete_forever_24)

        builder.setPositiveButton("Yes") { dialog, id ->
            binding.progressBar.visibility = VISIBLE
            viewModel.delete(contact)
            adapter.notifyDataSetChanged()
        }

        builder.setNegativeButton("No") { dialog, id ->
            dialog.cancel()
        }

        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.black))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(R.color.black))


    }

    override fun onClick(contact: ContactsDto) {
        contact.number?.let {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$it")
            startActivity(intent)
        }

    }
}