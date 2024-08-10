package com.example.myapplication.db
import android.provider.BaseColumns

object ContractClass {

    object HistoryEntry : BaseColumns {
        const val TABLE_NAME = "history"
        const val COLUMN_NAME_LABEL = "label" // Properti untuk menampung label
        const val COLUMN_NAME_SCORE = "score" // Properti untuk menampung skor
        const val COLUMN_NAME_IMAGE = "image" // Properti untuk menampung gambar

        const val _ID = BaseColumns._ID

        const val SQL_CREATE_TABLE =
            "CREATE TABLE $TABLE_NAME (" +
                    "$_ID INTEGER PRIMARY KEY," +
                    "$COLUMN_NAME_LABEL TEXT," +
                    "$COLUMN_NAME_SCORE REAL," +
                    "$COLUMN_NAME_IMAGE BLOB)" // Menggunakan BLOB untuk menampung gambar

        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}
