package com.example.myapplication.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_HISTORY_TABLE = """
            CREATE TABLE ${ContractClass.HistoryEntry.TABLE_NAME} (
                ${ContractClass.HistoryEntry._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${ContractClass.HistoryEntry.COLUMN_NAME_LABEL} TEXT NOT NULL,
                ${ContractClass.HistoryEntry.COLUMN_NAME_SCORE} REAL NOT NULL,
                ${ContractClass.HistoryEntry.COLUMN_NAME_IMAGE} BLOB NOT NULL
            );
        """.trimIndent()
        db.execSQL(SQL_CREATE_HISTORY_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Kode peningkatan versi database di sini jika diperlukan
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "History.db"
    }
}
