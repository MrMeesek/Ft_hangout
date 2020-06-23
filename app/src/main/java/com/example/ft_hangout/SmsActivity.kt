package com.example.ft_hangout

import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class SmsActivity : BaseActivity(), SmsBroadcastListener {
    private var contactIndex: Int = -1
    private val SMS_PERMISSIONS = 21
    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms)
        contactIndex = intent.extras!!.get("contactIndex") as Int
        supportActionBar!!.title = (application as FtHangout).contacts[contactIndex].name
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.RECEIVE_SMS,
                    android.Manifest.permission.READ_SMS,
                    android.Manifest.permission.SEND_SMS
                ),
                SMS_PERMISSIONS
            )
        } else {
            findViewById<ListView>(R.id.smsChat).adapter =
                SmsAdapter(fetchContactConversation(), this)

            val editText = findViewById<EditText>(R.id.text)
            val sendButton = findViewById<Button>(R.id.message_send)
            sendButton.isEnabled = false
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    sendButton.isEnabled = editText.text.toString().isNotEmpty()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //unused
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //unused
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        smsBroadcastReceiver = SmsBroadcastReceiver(this)
        registerReceiver(
            smsBroadcastReceiver,
            IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        )

    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
    }


    private fun fetchContactConversation(): ArrayList<Message> {
        val contact = (application as FtHangout).contacts[contactIndex]
        val phoneNumber = contact.phoneNumber?.replace("\\s".toRegex(), "")
        val uriList = arrayOf(
            Pair(Telephony.Sms.Inbox.CONTENT_URI, Receiver.Me),
            Pair(Telephony.Sms.Sent.CONTENT_URI, Receiver.Contact)
        )
        val projection = arrayOf(Telephony.Sms.DATE, Telephony.Sms.BODY)
        val selection = "${Telephony.Sms.ADDRESS}=?"
        val selectionArgs = arrayOf(phoneNumber)
        val conversation = ArrayList<Message>()
        for (uri in uriList) {
            val cursor =
                contentResolver.query(uri.first, projection, selection, selectionArgs, null)
            while (cursor!!.moveToNext()) {
                val receiver =
                    uri.second
                val date = cursor.getLong(
                    cursor.getColumnIndex(projection[0])
                )
                val body = cursor.getString(cursor.getColumnIndex(projection[1]))
                conversation.add(Message(receiver, date, body))
            }
            cursor.close()
        }
        conversation.sortBy { it.date }
        conversation.forEach { Log.d("DEBUG", "${it.receiver}, ${it.date}, ${it.body}") }
        return conversation
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            SMS_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findViewById<ListView>(R.id.smsChat).adapter =
                        SmsAdapter(fetchContactConversation(), this)
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.toast_sms_permissions_required),
                        Toast.LENGTH_LONG
                    )
                        .show()
                    finish()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun hasPermission(): Boolean {
        val permissions = arrayOf(
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.SEND_SMS
        )
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_DENIED
            )
                return false
        }
        return true
    }

    override fun updateUi(address: String, messageBody: String, timestamp: Long) {
        val contact = (application as FtHangout).contacts[contactIndex]
        Log.d("DEBUG", "$address == ${contact.phoneNumber}")
        if (address == contact.phoneNumber?.replace("\\s".toRegex(), "")) {
            val newMessage =
                Message(Receiver.Me, timestamp, messageBody)
            val adapter = findViewById<ListView>(R.id.smsChat).adapter as SmsAdapter
            adapter.add(newMessage)
        }
    }

    fun sendMessage(view: View) {
        val editText = findViewById<EditText>(R.id.text)
        val newMessage =
            Message(Receiver.Contact, System.currentTimeMillis(), editText.text.toString())
        val destinationAddress =
            (application as FtHangout).contacts[contactIndex].phoneNumber
        SmsManager.getDefault()
            .sendTextMessage(destinationAddress, null, newMessage.body, null, null)
        editText.text.clear()
        val adapter = findViewById<ListView>(R.id.smsChat).adapter as SmsAdapter
        adapter.add(newMessage)
    }

}