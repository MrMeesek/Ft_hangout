package com.example.ft_hangout

import java.text.SimpleDateFormat
import java.util.*

data class Message(val receiver: Receiver, val date: Long, val body: String) {
    fun formattedDate(): String =
        SimpleDateFormat("yyyy.MM.dd HH:mm").format(Date(date))

}