package com.example.myapplication.db

import android.app.Application
import android.content.ContentValues
import android.content.Context

class RepositoryClass(context: Context) {

    companion object {
        @Volatile
        private var instance: RepositoryClass? = null

        fun getInstance(application: Application): RepositoryClass {
            return instance ?: synchronized(this) {
                instance ?: RepositoryClass(application).also { instance = it }
            }
        }
    }

    private val dbHelper = DatabaseHelper(context)

    fun getAllHistory(): List<HistoryDB>{
    val db = dbHelper.readableDatabase
    val projection = arrayOf(
        ContractClass.HistoryEntry._ID,
        ContractClass.HistoryEntry.COLUMN_NAME_LABEL,
        ContractClass.HistoryEntry.COLUMN_NAME_SCORE,
        ContractClass.HistoryEntry.COLUMN_NAME_IMAGE
    )

    val cursor = db.query(
        ContractClass.HistoryEntry.TABLE_NAME,
        projection,
        null,
        null,
        null,
        null,
        null,
        null
    )

    val historyList = mutableListOf<HistoryDB>()

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(ContractClass.HistoryEntry._ID))
                val label = getString(getColumnIndexOrThrow(ContractClass.HistoryEntry.COLUMN_NAME_LABEL))
                val score = getFloat(getColumnIndexOrThrow(ContractClass.HistoryEntry.COLUMN_NAME_SCORE))
                val image = getString(getColumnIndexOrThrow(ContractClass.HistoryEntry.COLUMN_NAME_IMAGE))
                historyList.add(HistoryDB(id, label, score, image))
            }
        }
        cursor.close()
        return historyList
    }
    fun deleteHistory(id: Long): Int {
        val db = dbHelper.writableDatabase
        val selection = "${ContractClass.HistoryEntry._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        return db.delete(ContractClass.HistoryEntry.TABLE_NAME, selection, selectionArgs)
    }

    fun insertHistory(label: String, score: Float, image: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ContractClass.HistoryEntry.COLUMN_NAME_LABEL, label)
            put(ContractClass.HistoryEntry.COLUMN_NAME_SCORE, score)
            put(ContractClass.HistoryEntry.COLUMN_NAME_IMAGE, image)
        }
        return db.insert(ContractClass.HistoryEntry.TABLE_NAME, null, values)
    }

    fun updateHistoryLabel(id: Long, newLabel: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ContractClass.HistoryEntry.COLUMN_NAME_LABEL, newLabel)
        }
        val selection = "${ContractClass.HistoryEntry._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        return db.update(ContractClass.HistoryEntry.TABLE_NAME, values, selection, selectionArgs)
    }
}