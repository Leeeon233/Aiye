package leon.homework.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/*
 * Created by Administrator on 2017/4/2.
 */
class ClassSQLiteHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?=null, version: Int=1) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("ClassSQLiteHelper","建立班级数据库")
        db!!.execSQL("create table if not exists classlist ( " +
                "Class_id VARCHAR(20) PRIMARY KEY," +
                "Class_name VARCHAR(20) );")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}