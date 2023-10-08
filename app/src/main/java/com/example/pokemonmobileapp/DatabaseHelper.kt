package com.example.pokemonmobileapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val TABLE_NAME = "users"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create the table
        val CREATE_USERS_TABLE = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY," +
                "$COLUMN_USERNAME TEXT," +
                "$COLUMN_PASSWORD TEXT" +
                ")"
        db.execSQL(CREATE_USERS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")

        // Create tables again
        onCreate(db)
    }

    fun addUser(username: String, password: String): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_USERNAME, username)
        contentValues.put(COLUMN_PASSWORD, password)
        val success = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return success != -1L
    }

    @SuppressLint("Range")
    fun getUser(username: String): User? {
        val db = readableDatabase
        val columns = arrayOf(COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD)
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf(username)
        val cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null)
        val result = if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            val username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME))
            val password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD))
            User(id, username, password)
        } else {
            null
        }
        cursor.close()
        db.close()
        return result
    }

    data class User(val id: Int, val username: String, val password: String)
}

