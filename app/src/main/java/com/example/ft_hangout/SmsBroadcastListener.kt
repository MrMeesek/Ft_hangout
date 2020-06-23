package com.example.ft_hangout

interface SmsBroadcastListener {
    fun updateUi(address: String, messageBody: String, timestamp: Long)
}