package com.example.ft_hangout

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import java.io.*
import java.util.*

class ModifyContact : BaseActivity() {
    //private var contact = Contact()
    private var position: Int = -1


    private object ContactViewHolder {
        internal var contactName: EditText? = null
        internal var contactPicture: ImageView? = null
        internal var contactPhoneNumber: EditText? = null
        internal var contactAddress: EditText? = null
        internal var contactEmail: EditText? = null

        private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
            this.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    afterTextChanged.invoke(s.toString())
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        fun EditText.validate(message: String, validator: (String) -> Boolean): Boolean {
            this.afterTextChanged {
                this.error = if (validator(it)) null else message
            }
            this.error = if (validator(this.text.toString())) null else message
            return this.error == null
        }

        fun String.isValidEmail(): Boolean =
            if (this.isEmpty()) true else Patterns.EMAIL_ADDRESS.matcher(this).matches()

        fun String.isValidPhoneNumber(): Boolean =
            if (this.isEmpty()) true else Patterns.PHONE.matcher(this).matches()

        fun String.isValidName(): Boolean = this.isNotEmpty()

        fun isValidForm(): Boolean {
            val phoneNumber =
                contactPhoneNumber?.validate("Invalid phone number") { s -> s.isValidPhoneNumber() }
            val name = contactName?.validate("Contact name is required") { s -> s.isValidName() }
            val email = contactEmail?.validate("Invalid email address") { s -> s.isValidEmail() }
            if (phoneNumber == true && name == true && email == true)
                return true
            return false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_contact)
        // Default
        setResult(ContactAction.UNCHANGED_CONTACT.value)
        position = intent.extras!!.get("contactIndex") as Int
        val app = application as FtHangout
        //val contact = app.dbHelper.getAllContacts()[position]
        val contact = app.contacts[position]
        ContactViewHolder.contactName = findViewById(R.id.contactInfo)
        ContactViewHolder.contactPicture = findViewById(R.id.contactPicture)
        ContactViewHolder.contactPhoneNumber = findViewById(R.id.contactPhoneNumber)
        ContactViewHolder.contactAddress = findViewById(R.id.contactAddress)
        ContactViewHolder.contactEmail = findViewById(R.id.contactEmail)

        findViewById<ImageButton>(R.id.confirmChangesButton).setOnClickListener {
            if (ContactViewHolder.isValidForm()) {
                val app = application as FtHangout
                updateContact()
                if (contact.id > 0) {
                    Log.d("DEBUG", "update contact ${contact.id}")
                    contact.updateContact(app.dbHelper)
                    setResult(ContactAction.MODIFIED_CONTACT.value)
                } else {
                    Log.d("DEBUG", "save new contact ${contact.id}")
                    contact.saveNewContact(app.dbHelper)
                    setResult(ContactAction.NEW_CONTACT.value)
                }
                finish()
            }
        }
        findViewById<ImageButton>(R.id.deleteContact).setOnClickListener {
            setResult(ContactAction.DELETED_CONTACT.value)
            File(contact.picture.toString()).delete()
            app.contacts[position].deleteContact(app.dbHelper)
            app.contacts.removeAt(position)
            finish()
        }

        findViewById<ImageView>(R.id.contactPicture).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                app.PICK_IMAGE_REQUEST_CODE
            )

        }

        ContactViewHolder.contactName?.setText(contact.name)
        if (contact.picture != Uri.EMPTY) {
            ContactViewHolder.contactPicture?.setImageURI(contact.picture)
        } else
            ContactViewHolder.contactPicture?.setImageResource(R.mipmap.default_contact_picture)
        ContactViewHolder.contactPhoneNumber?.setText(contact.phoneNumber)
        ContactViewHolder.contactAddress?.setText(contact.address)
        ContactViewHolder.contactEmail?.setText(contact.email)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val app = application as FtHangout
        if (requestCode == app.PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val inputStream: InputStream? = data?.data?.let { contentResolver.openInputStream(it) }
            val bpm = BitmapFactory.decodeStream(inputStream)

            val wrapper = ContextWrapper(applicationContext)
            var file = wrapper.getDir("images", Context.MODE_PRIVATE)

            file = File(file, "${UUID.randomUUID()}.jpg")
            try {
                val stream: OutputStream = FileOutputStream(file)
                bpm.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val app = application as FtHangout
            val contact = app.contacts[position]
            contact.picture = Uri.parse(file.absolutePath)
            ContactViewHolder.contactPicture?.setImageURI(contact.picture)
            ContactViewHolder.contactPicture?.invalidate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("DEBUG", "REMOVE UNCHANGED CONTACT")
    }

    private fun updateContact() {
        val app = application as FtHangout
        val contact = app.contacts[position]
        contact.name = ContactViewHolder.contactName?.text.toString()
        contact.phoneNumber = ContactViewHolder.contactPhoneNumber?.text.toString()
        contact.address = ContactViewHolder.contactAddress?.text.toString()
        contact.email = ContactViewHolder.contactEmail?.text.toString()
    }
}
