package com.example.ft_hangout

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log

class SmsBroadcastReceiver(private val smsBroadcastListener: SmsBroadcastListener) :
    BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        val format = bundle?.getString("format")
        val pdus = bundle?.get("pdus") as Array<Any>
        for (i in pdus.indices) {
            val smsMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
            val sender = smsMessage.displayOriginatingAddress
            val message = smsMessage.messageBody
            val timestamp = smsMessage.timestampMillis
            Log.d("DEBUG", "Message From $sender: $message")
            smsBroadcastListener.updateUi(sender, message, timestamp)
        }
    }
}

