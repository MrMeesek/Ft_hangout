package com.example.ft_hangout

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.core.content.ContextCompat

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Log.d("DEBUG", "Building ")
        if (
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }

        val app = application as (FtHangout)
        val contactList = app.contacts //app.dbHelper.getAllContacts()
        val contactsView = findViewById<ListView>(R.id.contacts)

        val contactsAdapter = ContactsAdapter(contactList, this)
        contactsView.adapter = contactsAdapter

        val addContactButton = findViewById<ImageButton>(R.id.add_contact)
        addContactButton.setOnClickListener {
            app.contacts.add(Contact())
            app.contacts.last().onModifyContact(this, app.contacts.size - 1)
        }
    }

    override fun onResume() {
        super.onResume()

        //val app = application as (FtHangout)
        //val contactList = app.contacts
        val adapter = findViewById<ListView>(R.id.contacts).adapter as ContactsAdapter
        adapter.notifyDataSetChanged()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    Log.d("DEBUG", "permission denied")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val app = application as FtHangout
        val resultMessage: String = when (resultCode) {
            ContactAction.NEW_CONTACT.value -> getString(R.string.toast_new_contact_created)
            ContactAction.MODIFIED_CONTACT.value -> getString(R.string.toast_contact_modified)
            ContactAction.DELETED_CONTACT.value -> getString(R.string.toast_contact_deleted)
            else -> ""
        }
        if (resultMessage.isNotEmpty()) Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT)
            .show()
        if (resultCode == ContactAction.UNCHANGED_CONTACT.value) {
            app.contacts.removeAt(app.contacts.lastIndex)
            val adapter = findViewById<ListView>(R.id.contacts).adapter as ContactsAdapter
            adapter.notifyDataSetChanged()
        }
        getString(R.string.toast_new_contact_created)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


}
