package leon.homework.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import leon.homework.Utils.DataUtils


/*
 * Created by BC on 2017/2/14 0014.
 */

class ChatSQLiteHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?=null, version: Int=1) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("StuChatSQlite","建立数据库")
        db.execSQL("create table if not exists maiye ("
                + "ChatId integer primary key AUTOINCREMENT,"
                + "Chat_Time varchar(16),"
                + "UserType varchar(1),"
                + "Chat_Content varchar2(500));")
        db.execSQL("create table if not exists messagelist (" +
                   "title varchar(10)," +
                   "alia varchar(20)," +
                   "imgpath  varchar(100)," +
                   "lastcontent varchar(500)," +
                   "time varchar(5)," +
                   "unreadnum int default 0);")
        val time = DataUtils.getTodayDateTime().substring(11,16)
        val sql = "Insert into maiye (Chat_Time,UserType,Chat_Content) " +
                "values ('$time','0','感谢使用艾叶。');"
        db.execSQL(sql)
        val sql2 = "Insert into messagelist (title,alia,imgpath,lastcontent,time,unreadnum) " +
                "values ('系统消息','aiye','aiye','感谢使用艾叶。','$time',1);"
        db.execSQL(sql2)
    }
    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {

    }
}
