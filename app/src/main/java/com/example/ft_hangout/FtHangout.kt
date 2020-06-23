package com.example.ft_hangout

import android.app.Application
import java.util.*

class FtHangout : Application() {
    var dbHelper = DBHelper(this)
    val PICK_IMAGE_REQUEST_CODE = 42
    val NEW_CONTACT_REQUEST_CODE = 4242

    lateinit var contacts: ArrayList<Contact>
    var time: Long = 0

    private var mActivityTransitionTimer: Timer? = null
    var wasInBackground = false
    private val MAX_ACTIVITY_TRANSITION_TIME_MS: Long = 2000

    fun startActivityTransitionTimer() {
        time = System.currentTimeMillis()
        mActivityTransitionTimer = Timer()
        mActivityTransitionTimer?.schedule(object : TimerTask() {
            override fun run() {
                wasInBackground = true
            }
        }, MAX_ACTIVITY_TRANSITION_TIME_MS)
    }

    fun stopActivityTransitionTimer() {
        if (this.mActivityTransitionTimer != null) {
            this.mActivityTransitionTimer!!.cancel()
        }
        this.wasInBackground = false
        time = 0
    }

    override fun onCreate() {
        contacts = dbHelper.getAllContacts()
        super.onCreate()
    }
}
