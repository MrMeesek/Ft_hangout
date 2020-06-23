package com.example.ft_hangout


import android.content.Context
import android.os.Bundle
import android.util.Log

import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import java.time.format.DateTimeFormatter
import java.util.*

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val preferences = getSharedPreferences("ft_hangout", Context.MODE_PRIVATE)
        val theme = preferences.getString("apptheme", "blue")
        Log.d("DEBUG", "theme = $theme")
        when (theme) {
            "blue" -> setTheme(R.style.AppTheme)
            "red" -> setTheme(R.style.AppTheme_Red)
            "green" -> {
                Log.d("DEBUG", "setTheme(green)")
                setTheme(R.style.AppTheme_Green)
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        val app = application as FtHangout
        if (app.wasInBackground) {
            val time = Date((application as FtHangout).time)
            if (time != Date(0)) {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                val formatted = time.toString().format(formatter)
                Toast.makeText(this, formatted, Toast.LENGTH_SHORT).show()
            }
        }
        app.stopActivityTransitionTimer()
        super.onResume()

    }

    override fun onPause() {
        (application as FtHangout).startActivityTransitionTimer()
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId != android.R.id.home) {
            val preferences = getSharedPreferences("ft_hangout", Context.MODE_PRIVATE)
            Log.d(
                "DEBUG",
                "itemid = ${item.itemId} , ${R.id.themeDefault}, ${R.id.themeGreen} ${R.id.themeRed}"
            )
            val theme = when (item.itemId) {
                R.id.themeDefault -> "blue"
                R.id.themeGreen -> "green"
                R.id.themeRed -> "red"
                else -> "blue"
            }
            preferences.edit().putString("apptheme", theme).apply()
            finish()
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}
