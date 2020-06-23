package com.example.ft_hangout

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.provider.BaseColumns._ID
import android.util.Log
import androidx.core.net.toUri
import java.util.logging.Logger

class DBHelper(
    context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertContact(newContact: Contact): Boolean {
        val db = this.writableDatabase
        val contactValues = ContentValues().apply {
            put(COLUMN_CONTACT_NAME, newContact.name)
            put(COLUMN_CONTACT_PICTURE, newContact.picture.toString())
            put(COLUMN_CONTACT_PHONE_NUMBER, newContact.phoneNumber)
            put(COLUMN_CONTACT_ADDRESS, newContact.address)
            put(COLUMN_CONTACT_EMAIL, newContact.email)
        }
        val newRowId = db?.insert(TABLE_CONTACT, null, contactValues)
        if (newRowId != null) newContact.id = newRowId
        db.close()
        return newContact.id > 0
    }

    @Throws(SQLiteConstraintException::class)
    fun updateContact(contact: Contact): Boolean {
        val db = this.writableDatabase
        val contactValues = ContentValues().apply {
            put(COLUMN_CONTACT_NAME, contact.name)
            put(COLUMN_CONTACT_PICTURE, contact.picture.toString())
            put(COLUMN_CONTACT_PHONE_NUMBER, contact.phoneNumber)
            put(COLUMN_CONTACT_ADDRESS, contact.address)
            put(COLUMN_CONTACT_EMAIL, contact.email)
        }
        val selection = "$_ID LIKE ${contact.id}"
        val ret = db?.update(TABLE_CONTACT, contactValues, selection, emptyArray())
        return ret == 1
    }


    @Throws(SQLiteConstraintException::class)
    fun deleteContact(contact: Contact): Boolean {
        val db = this.writableDatabase
        val selection = "$_ID LIKE ?"
        val selectionArg = arrayOf(contact.id.toString())
        val totalRowDeleted = db.delete(TABLE_CONTACT, selection, selectionArg)
        db.close()
        return totalRowDeleted == 1
    }

    @Throws(SQLiteConstraintException::class)
    fun getAllContacts(): ArrayList<Contact> {
        val contacts = ArrayList<Contact>()
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM $TABLE_CONTACT", null)
        } catch (e: SQLiteException) {
            Logger.getLogger(MainActivity::class.java.name).warning("$e")
            return ArrayList()
        }
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val contact = Contact()
                contact.id = cursor.getLong(cursor.getColumnIndex(_ID))
                contact.name = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_NAME))
                contact.picture =
                    cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_PICTURE)).toUri()
                Log.d("DEBUG", "URI = ${contact.picture.toString()}")
                contact.phoneNumber =
                    cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_PHONE_NUMBER))
                contact.address = cursor.getString(
                    cursor.getColumnIndex(
                        COLUMN_CONTACT_ADDRESS
                    )
                )
                contact.email = cursor.getString((cursor.getColumnIndex(COLUMN_CONTACT_EMAIL)))
                contacts.add(contact)
                cursor.moveToNext()
            }
        }
        cursor.close()
        db.close()
        return contacts
    }

    companion object : BaseColumns {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "FtHangoutsDB.db"

        private const val TABLE_CONTACT = "contact"

        /*    private val COLUMN_CONTACT_ID = "_id" */
        private const val COLUMN_CONTACT_NAME = "name"
        private const val COLUMN_CONTACT_PICTURE = "picture"
        private const val COLUMN_CONTACT_PHONE_NUMBER = "phone_number"
        private const val COLUMN_CONTACT_ADDRESS = "address"
        private const val COLUMN_CONTACT_EMAIL = "email"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${this.TABLE_CONTACT} (" +
                    "$_ID INTEGER PRIMARY KEY," +
                    "${this.COLUMN_CONTACT_NAME} TEXT," +
                    "${this.COLUMN_CONTACT_PICTURE} TEXT," +
                    "${this.COLUMN_CONTACT_PHONE_NUMBER} TEXT," +
                    "${this.COLUMN_CONTACT_ADDRESS} TEXT," +
                    "${this.COLUMN_CONTACT_EMAIL} TEXT)"
        private const val SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS ${this.TABLE_CONTACT}"
    }
}