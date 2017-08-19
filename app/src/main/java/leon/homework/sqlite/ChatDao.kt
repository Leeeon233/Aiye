package leon.homework.sqlite

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import leon.homework.app.AppContext
import leon.homework.data.SaveData
import leon.homework.javaBean.Msg
import leon.homework.javaBean.MsgObject
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Created by BC on 2017/2/21 0021.
 */
class ChatDao:AnkoLogger {
    companion object{
        private var mInstance:ChatDao ?= null
        fun getInstance():ChatDao{
            if (mInstance == null) {
                mInstance = ChatDao()
            }else if(mInstance!!.chathelper!!.databaseName != (SaveData.phonenum+"ChatLog.db")){
                mInstance = ChatDao()
                Log.d("CHATDAO","变更数据库名"+mInstance!!.chathelper!!.databaseName)
            }
            return mInstance!!
        }
    }
    private val TAG = "ChatDao"
    private var chathelper: ChatSQLiteHelper? = null
    init {
        val dbname = SaveData.phonenum+"ChatLog.db"
        chathelper = ChatSQLiteHelper(AppContext.instance!!, dbname)
        info("当前聊天信息数据库名"+chathelper!!.databaseName)
    }
    fun execSQL(sql: String) {
        val db: SQLiteDatabase = chathelper!!.writableDatabase      //获得数据库
        try {
            db.beginTransaction()
            db.execSQL(sql)
            db.setTransactionSuccessful()
            Log.d(TAG,"execSQL 成功执行")
        } catch (e: Exception) {
            Log.d(TAG,"execSQL SQL错误")
            Log.e(TAG, e.message)
        } finally {
            db.endTransaction()
            db.close()
        }
    }
    fun CreateTable(ChatUser:String){
        val sql = "create table if not exists m"+ChatUser+ "("+
                "ChatId integer primary key AUTOINCREMENT,"+
                "Chat_Time varchar(16),"+
                "UserType varchar(1),"+
                "Chat_Content varchar(500));"
        execSQL(sql)
    }

    fun updateChat(alia: String,lastcontent:String,time:String){
        val sql = "update messagelist set lastcontent = '$lastcontent',time = '$time' where alia = '$alia'"
        execSQL(sql)
    }
    fun unreadnumadd(alia: String){
        val sql = "update messagelist set unreadnum = unreadnum+1 where alia = '$alia'"
        execSQL(sql)
    }
    fun unreadnumclear(alia: String){
        val sql = "update messagelist set unreadnum = 0 where alia = '$alia'"
        execSQL(sql)
    }
    fun selectunread():Int{
        val db = chathelper!!.readableDatabase
        var unreadnum = 0
        val cursor = db.query("messagelist", arrayOf("unreadnum"),null,null,null, null, null)
        while (cursor.moveToNext()) {
            val num = cursor.getInt(cursor.getColumnIndex("unreadnum"))
            unreadnum += num
        }
        cursor.close()
        db.close()
        return unreadnum
    }

    fun insertChat(msgObject:MsgObject){
        val title = msgObject.title
        val alia = msgObject.alia
        val imgpath = msgObject.bmpath
        val lastcontent = msgObject.lastcontent
        val time = msgObject.time
        val uneadnum = msgObject.unreadnum
        val sql = "Insert into messagelist (title,alia,imgpath,lastcontent,time,unreadnum) " +
                "values ('$title','$alia','$imgpath','$lastcontent','$time',$uneadnum);"
        execSQL(sql)
    }
    fun selectChat():ArrayList<MsgObject>{
        val db = chathelper!!.readableDatabase
        val messagelist = ArrayList<MsgObject>()
        val cursor = db.query("messagelist", arrayOf("title", "alia", "imgpath","lastcontent","time","unreadnum"),null,null,null, null, null)
        while (cursor.moveToNext()) {
            val title = cursor.getString(cursor.getColumnIndex("title"))
            val alia = cursor.getString(cursor.getColumnIndex("alia"))
            val imgpath = cursor.getString(cursor.getColumnIndex("imgpath"))
            val lastcontent = cursor.getString(cursor.getColumnIndex("lastcontent"))
            val time = cursor.getString(cursor.getColumnIndex("time"))
            val unreadnum = cursor.getInt(cursor.getColumnIndex("unreadnum"))
            val msgo = MsgObject(title,alia,imgpath,lastcontent,time)
            msgo.unreadnum = unreadnum
            messagelist.add(msgo)
        }
        db.close()
        return messagelist
    }
    fun chatIsExist(alia:String):Boolean{
        var isexist = false
        val db = chathelper!!.readableDatabase    //获得数据库
        try {
            db.beginTransaction()
            val sql = "select count(*) from messagelist where alia ='$alia';"
            val cursor = db.rawQuery(sql, null)
            if (cursor.moveToNext()) {
                val count = cursor.getInt(0)
                if (count > 0) {
                    isexist = true
                }
            }
            db.setTransactionSuccessful()
            Log.d(TAG,"tableisexist--成功执行")
        } catch (e: Exception) {
            Log.d(TAG,"tableisexist--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {
            db.endTransaction()
            db.close()
        }
        return isexist
    }

    fun insertMsg(ChatUser:String,msg:Msg){
        val Chat_Time = msg.msgtime
        val UserType = msg.msgType
        val Chat_Content = msg.content
        val sql = "Insert into m$ChatUser (Chat_Time,UserType,Chat_Content) " +
                    "values ('$Chat_Time',$UserType,'$Chat_Content');"
        execSQL(sql)
    }
    fun selectMsg(ChatUser:String):ArrayList<Msg>{
        val db = chathelper!!.readableDatabase
        val msglist = ArrayList<Msg>()
        val cursor = db.query("m"+ChatUser, arrayOf("Chat_Time", "UserType", "Chat_Content"),null,null,null, null, null)
        /*val num = cursor.count
        if(num<20){
            while (cursor.moveToNext()) {
                val Chat_Time = cursor.getString(cursor.getColumnIndex("Chat_Time"))
                val UserType = cursor.getInt(cursor.getColumnIndex("UserType"))
                val Chat_Content = cursor.getString(cursor.getColumnIndex("Chat_Content"))
                msglist.add(Msg(Chat_Content,UserType,Chat_Time))
            }
        }else{
            cursor.move(num-20)
            while (cursor.moveToNext()) {
                val Chat_Time = cursor.getString(cursor.getColumnIndex("Chat_Time"))
                val UserType = cursor.getInt(cursor.getColumnIndex("UserType"))
                val Chat_Content = cursor.getString(cursor.getColumnIndex("Chat_Content"))
                msglist.add(Msg(Chat_Content,UserType,Chat_Time))
            }
        }*/
        while (cursor.moveToNext()) {
            val Chat_Time = cursor.getString(cursor.getColumnIndex("Chat_Time"))
            val UserType = cursor.getInt(cursor.getColumnIndex("UserType"))
            val Chat_Content = cursor.getString(cursor.getColumnIndex("Chat_Content"))
            msglist.add(Msg(Chat_Content,UserType,Chat_Time))
        }
        db.close()
        return msglist
    }
    fun tableisexist(tablename: String):Boolean{
        var isexist = false
        val db = chathelper!!.readableDatabase    //获得数据库
        try {
            db.beginTransaction()
            val sql = "select count(*) from sqlite_master where type ='table' and name ='m$tablename';"
            val cursor = db.rawQuery(sql, null)
            if (cursor.moveToNext()) {
                val count = cursor.getInt(0)
                if (count > 0) {
                    isexist = true
                }
            }
            db.setTransactionSuccessful()
            Log.d(TAG,"tableisexist--成功执行")
        } catch (e: Exception) {
            Log.d(TAG,"tableisexist--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {
            db.endTransaction()
            db.close()
        }
        return isexist
    }
}