package com.example.mountain

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "events.db" // 데이터베이스 이름
        private const val DATABASE_VERSION = 1 // 데이터베이스 버전
        private const val TABLE_EVENTS = "events" // 테이블 이름
    }

    // 데이터베이스 생성 시 호출되는 메서드
    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_EVENTS (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date TEXT NOT NULL," +
                "title TEXT NOT NULL)")
        db.execSQL(createTableQuery)
    }

    // 데이터베이스 버전이 변경될 때 호출되는 메서드
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EVENTS") // 기존 테이블 삭제
        onCreate(db) // 새 테이블 생성
    }
}
