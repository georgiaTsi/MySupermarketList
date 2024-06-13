package com.example.mysupermarketlist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class SupermarketDatabaseHelper(private val mainActivity: MainActivity) {

    val dbHelper = SupermarketDbHelper(mainActivity)

    fun addToDatabase(title: String){
        //Gets the data repository in write mode
        val db = dbHelper.writableDatabase

        //Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(SupermarketList.SupermarketEntry.COLUMMN_NAME_TITLE, title)
            put(SupermarketList.SupermarketEntry.COLUMN_NAME_ISCHECKED, false)
        }

        //Insert the new row, returning the primary key value of the new row
        db?.insert(SupermarketList.SupermarketEntry.TABLE_NAME, null, values)
    }

    fun readFromDatabase() : MutableList<ListAdapter.ListItem> {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM " + SupermarketList.SupermarketEntry.TABLE_NAME, null)

        val items = mutableListOf<ListAdapter.ListItem>()
        while(cursor.moveToNext()){
            val title: String = cursor.getString(0)
            val isChecked: Boolean = java.lang.Boolean.valueOf(cursor.getString(1))

            items.add(ListAdapter.ListItem(title, isChecked))
        }
        cursor.close()

        return items
    }

    fun deleteFromDatabase(){
        val query = java.lang.String.format(
            "DELETE FROM ${SupermarketList.SupermarketEntry.TABLE_NAME} WHERE ${SupermarketList.SupermarketEntry.COLUMN_NAME_ISCHECKED} = '" + true + "'"
        )

        dbHelper.readableDatabase.execSQL(query)
    }

    fun updateDatabase(item: ListAdapter.ListItem){
        val query = java.lang.String.format(
            "UPDATE ${SupermarketList.SupermarketEntry.TABLE_NAME} SET ${SupermarketList.SupermarketEntry.COLUMN_NAME_ISCHECKED} = '%b' WHERE ${SupermarketList.SupermarketEntry.COLUMMN_NAME_TITLE} = '${item.title}'",
            item.isChecked
        )

        dbHelper.writableDatabase.execSQL(query)
    }

    fun dropDatabase(){
        SQLiteDatabase.deleteDatabase(mainActivity.getDatabasePath(SupermarketDbHelper.DATABASE_NAME))
    }

    object SupermarketList {
        object SupermarketEntry : BaseColumns {
            const val TABLE_NAME = "supermarket"
            const val COLUMMN_NAME_TITLE = "title"
            const val COLUMN_NAME_ISCHECKED = "ischecked"
        }
    }

    class SupermarketDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
        override fun onCreate(db: SQLiteDatabase){
            val sqlCreateEntries = "CREATE TABLE ${SupermarketList.SupermarketEntry.TABLE_NAME} (" +
                    "${SupermarketList.SupermarketEntry.COLUMMN_NAME_TITLE} TEXT," +
                    "${SupermarketList.SupermarketEntry.COLUMN_NAME_ISCHECKED} BOOLEAN)"

            db.execSQL(sqlCreateEntries)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            val sqlDeleteEntries = "DROP TABLE IF EXISTS ${SupermarketList.SupermarketEntry.TABLE_NAME}"

            db.execSQL(sqlDeleteEntries)
            onCreate(db)
        }

        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            onUpgrade(db, oldVersion, newVersion)
        }

        companion object {
            const val DATABASE_VERSION = 1
            const val DATABASE_NAME = "Supermarket.db"
        }
    }
}