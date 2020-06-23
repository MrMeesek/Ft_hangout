package com.example.ft_hangout

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast


class ShowContactInfoActivity : BaseActivity() {

    //private var contact = Contact()
    private var contactIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DEBUG", "showContactInfo onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_contact_info)
        contactIndex = intent.extras!!.get("contactIndex") as Int
        val app = application as FtHangout
        val contact = app.contacts[contactIndex]
        //val contactsView = findViewById<LinearLayout>(R.id.contactExtendedInfo)
        //val contactsAdapter = ContactInfoAdapter(contact, this)


        findViewById<Button>(R.id.modifyContact).setOnClickListener {
            contact.onModifyContact(this, contactIndex)
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        updateContactView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            Log.d("DEBUG", "FINISH")
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ContactAction.DELETED_CONTACT.value) {
            setResult(ContactAction.DELETED_CONTACT.value)
            finish()
        }
        val app = application as FtHangout
        val resultMessage: String = when (resultCode) {
            ContactAction.NEW_CONTACT.value -> "New contact created"
            ContactAction.MODIFIED_CONTACT.value -> "Contact modified"
            else -> ""
        }
        if (resultMessage.isNotEmpty()) Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT)
            .show()
    }

    private fun updateContactView() {
        val app = application as FtHangout
        Log.d("DEBUG", "\nposition = ${contactIndex}\ncontacts.size = ${app.contacts.size}\n")
        val selectedContact = app.contacts[contactIndex]
        findViewById<TextView>(R.id.contactInfo).text = selectedContact.name
        Log.d("DEBUG", "picture uri = ${selectedContact.picture.toString()}")
        if (selectedContact.picture != Uri.EMPTY) findViewById<ImageView>(R.id.contactPicture).setImageURI(
            selectedContact.picture
        )
        else findViewById<ImageView>(R.id.contactPicture).setImageResource(R.mipmap.default_contact_picture)
        findViewById<TextView>(R.id.contactPhoneNumber).text = selectedContact.phoneNumber
        findViewById<TextView>(R.id.contactAddress).text = selectedContact.address
        findViewById<TextView>(R.id.contactEmail).text = selectedContact.email
    }
}
