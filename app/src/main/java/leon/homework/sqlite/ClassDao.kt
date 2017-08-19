package leon.homework.sqlite

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import leon.homework.app.AppContext
import leon.homework.Utils.DataUtils
import leon.homework.data.SaveData
import leon.homework.javaBean.ClassObject
import leon.homework.javaBean.MsgObject
import leon.homework.javaBean.StudentObject
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/*
 * Created by Administrator on 2017/4/2.
 */
class ClassDao :AnkoLogger{
    companion object {
        private var mInstance: ClassDao? = null
        fun getInstance(): ClassDao {
            if (mInstance == null) {
                mInstance = ClassDao()
            }else if(mInstance!!.classhelper!!.databaseName != (SaveData.phonenum+"Class.db")){
                mInstance = ClassDao()
            }
            return mInstance!!
        }
    }
    private var classhelper: ClassSQLiteHelper? = null
    init {
        val dbname = SaveData.phonenum+"Class.db"
        classhelper = ClassSQLiteHelper(AppContext.instance!!,dbname)
    }

    fun execSQL(sql: String) {
        val db: SQLiteDatabase = classhelper!!.writableDatabase      //获得数据库
        try {
            db.beginTransaction()
            db.execSQL(sql)
            db.setTransactionSuccessful()
            info("execSQL 成功执行")
        } catch (e: Exception) {
            Log.d("ClassDao","execSQL SQL错误")
            Log.e("ClassDao", e.message)
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun createClass(Class_id: String,Class_name:String){
        //val db = classhelper!!.writableDatabase
        val sql = "insert into classlist (Class_id,Class_name) " +
                        "values ('m$Class_id','$Class_name');"
        execSQL(sql)
        val sql2 = "create table if not exists 'm$Class_id' ( " +
                "Stu_alia VARCHAR(16) PRIMARY KEY," +
                "Stu_name VARCHAR(10)," +
                "Stu_img VARCHAR(30) );"
        execSQL(sql2)
        /*try {
            db.beginTransaction()
            //val sql = "insert into classlist (Class_id,Class_name) " +
            //        "values ('$Class_id','$Class_name');"
            //info(sql)
            //db.execSQL(sql)
            info("创建班级 $Class_name     ${classhelper!!.databaseName}")
            val sql2 = "create table if not exists 'm$Class_id' ( " +
                    "Stu_alia VARCHAR(16) PRIMARY KEY," +
                    "Stu_name VARCHAR(10)," +
                    "Stu_img VARCHAR(30) );"
            info(sql2)
            db.execSQL(sql2)
            info("添加班级表 m$Class_id")
            db.endTransaction()

            info("createClass---成功执行")
        }catch (e: Exception){
            Log.e("ClassDao", "", e.cause)
            info("createClass---sql错误")
        }finally {
            info("加学生")
            //insertAstudent(Class_id,"1491187919621207","陈刚")
            //db.close()
        }*/
    }

    fun selectClasses():ArrayList<ClassObject>{
        val db = classhelper!!.writableDatabase
        val classes = ArrayList<ClassObject>()
        val cursor = db.query("classlist", arrayOf("Class_id","Class_name"), null, null, null, null, null)
        while (cursor.moveToNext()) {
            val classid = cursor.getString(cursor.getColumnIndex("Class_id"))
            val classname = cursor.getString(cursor.getColumnIndex("Class_name"))
            val mclass = ClassObject(classname,classid)
            classes.add(mclass)
        }
        cursor.close()
        return classes
    }

    fun selectClassname(Class_id: String):String{
        val db = classhelper!!.readableDatabase      //获得数据库
        var Classname:String?=null
        val cursor = db.query("classlist", arrayOf("Class_name"), "Class_id=?", arrayOf(Class_id), null, null, null)
        while (cursor.moveToNext()) {
            Classname = cursor.getString(cursor.getColumnIndex("Class_name"))
        }
        cursor.close()
        //db.close()
        return Classname!!
    }

    fun insertAstudent(Class_id:String,Stu_alia:String,Stu_name:String){
        val db = classhelper!!.writableDatabase
        try {
            db.beginTransaction()
            val sql = "Insert into 'm$Class_id' (Stu_alia,Stu_name,Stu_img) " +
                    "values ('$Stu_alia','$Stu_name','img');"
            info(sql)
            db.execSQL(sql)
            db.setTransactionSuccessful()
            info("insertAstudent---成功执行")
        }catch (e: Exception){
            info(e.message)
            info("insertAstudent---sql错误")
        }finally {
            db.endTransaction()
            info("添加学生完成，创建对话")
            val stuchat = MsgObject(Stu_name,Stu_alia,/*TODO*/"","我是$Stu_name", DataUtils.getTodayDateTime().substring(11,16))
            if(!ChatDao.getInstance().chatIsExist(Stu_alia))
            ChatDao.getInstance().insertChat(stuchat)
            //db.close()
        }
    }

    fun selectStudents(Class_id:String):ArrayList<StudentObject>{
        val db = classhelper!!.writableDatabase
        val students = ArrayList<StudentObject>()
        val cursor = db.query(Class_id, arrayOf("Stu_alia","Stu_name","Stu_img"), null, null, null, null, null)
        while (cursor.moveToNext()) {
            val Stu_alia = cursor.getString(cursor.getColumnIndex("Stu_alia"))
            val Stu_name = cursor.getString(cursor.getColumnIndex("Stu_name"))
            info(Stu_name)
            val Stu_img = cursor.getString(cursor.getColumnIndex("Stu_img"))
            val student = StudentObject(Stu_alia,Stu_name,Stu_img)
            students.add(student)
        }
        cursor.close()
        return students
    }
}