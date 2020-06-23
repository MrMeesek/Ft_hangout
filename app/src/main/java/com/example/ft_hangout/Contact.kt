package com.example.ft_hangout

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast

class Contact {
    var id: Long = -1
    var name: String? = null
    var picture: Uri? = null
    var phoneNumber: String? = null
    var address: String? = null
    var email: String? = null


    constructor(
        id: Long,
        name: String?,
        phoneNumber: String?,
        picture: Uri?,
        address: String?,
        email: String?
    ) {
        this.id = id
        this.name = name
        this.picture = picture
        this.phoneNumber = phoneNumber
        this.address = address
        this.email = email
    }

    constructor(
        name: String?,
        phoneNumber: String?,
        picture: Uri?,
        address: String?,
        email: String?
    ) {
        this.name = name
        this.phoneNumber = phoneNumber
        this.picture = picture
        this.address = address
        this.email = email
    }

    constructor() {
        picture = Uri.EMPTY
    }

    fun onContactCall(context: Context) {

        Log.d("DEBUG", "send call")
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:" + this.phoneNumber)
        context.startActivity(intent)
    }

    fun sendMessage(context: Context, position: Int) {
        if (phoneNumber != null && phoneNumber!!.isNotEmpty()) {
            val intent = Intent(context, SmsActivity::class.java)
            intent.putExtra("contactIndex", position)
            context.startActivity(intent)
        } else {
            Toast.makeText(context, R.string.toast_invalid_phone_number, Toast.LENGTH_SHORT).show()
        }
    }

    fun onShowAllInfo(context: Context, position: Int) {
        val intent = Intent(context, ShowContactInfoActivity::class.java)
        intent.putExtra("contactIndex", position)
        (context as Activity).startActivityForResult(
            intent,
            (context.application as FtHangout).NEW_CONTACT_REQUEST_CODE
        )
        Log.d("Debug", "adapter item click")
    }

    fun onModifyContact(context: Context, position: Int) {
        val intent = Intent(context, ModifyContact::class.java)
        intent.putExtra("contactIndex", position)
        (context as Activity).startActivityForResult(
            intent,
            (context.application as FtHangout).NEW_CONTACT_REQUEST_CODE
        )
    }

    fun saveNewContact(dbHelper: DBHelper): Boolean {
        if (id.toInt() < 0) {
            return dbHelper.insertContact(this)
        }
        return false
    }

    fun deleteContact(dbHelper: DBHelper): Boolean {
        if (id.toInt() > 0) {
            return dbHelper.deleteContact(this)
        }
        return false
    }

    fun updateContact(dbHelper: DBHelper): Boolean {
        if (id.toInt() > 0) {
            return dbHelper.updateContact(this)
        }
        return false
    }
}