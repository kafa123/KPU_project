package com.example.kpu

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "formData.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "UserDetails"
        private const val COL_ID = "id"
        private const val COL_NAME = "nama"
        private const val COL_NIK = "nik"
        private const val COL_PHONE = "noHP"
        private const val COL_GENDER = "gender"
        private const val COL_DATE = "date"
        private const val COL_ADDRESS = "alamat"
        private const val COL_IMAGE = "image"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("info","udah msuk")
        val createTable = ("CREATE TABLE $TABLE_NAME ("
                + "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COL_NAME TEXT,"
                + "$COL_NIK TEXT,"
                + "$COL_PHONE TEXT,"
                + "$COL_GENDER TEXT,"
                + "$COL_DATE TEXT,"
                + "$COL_ADDRESS TEXT,"
                + "$COL_IMAGE TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Method to insert data
    fun insertData(nama: String, nik: String, noHP: String, gender: String, date: String, alamat: String, image: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_NAME, nama)
        contentValues.put(COL_NIK, nik)
        contentValues.put(COL_PHONE, noHP)
        contentValues.put(COL_GENDER, gender)
        contentValues.put(COL_DATE, date)
        contentValues.put(COL_ADDRESS, alamat)
        contentValues.put(COL_IMAGE, image)

        val result = db.insert(TABLE_NAME, null, contentValues)
        return result != -1L
    }

    fun getAllUsers(): List<data> {
        val userList = ArrayList<data>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM UserDetails", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("nama"))
                val nik = cursor.getString(cursor.getColumnIndexOrThrow("nik"))
                val imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image"))

                val data = data(id,imagePath, name, nik)
                userList.add(data)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return userList
    }

    fun getDetailsUser(id: Int): dataDetail? {
        val db = readableDatabase
        var user: dataDetail? = null
        val cursor = db.rawQuery("SELECT * FROM UserDetails WHERE id = ?", arrayOf(id.toString()))

        if (cursor.moveToFirst()) {
            do{
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("nama"))
                val nik = cursor.getString(cursor.getColumnIndexOrThrow("nik"))
                val imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image"))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow("noHP"))
                val gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val alamat = cursor.getString(cursor.getColumnIndexOrThrow("alamat"))

                user = dataDetail(userId,imagePath, name, nik, phone, alamat, date,gender)
            } while (cursor.moveToNext())

        }
        cursor.close()
        return user
    }
}
