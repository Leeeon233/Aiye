package leon.homework.sqlite

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import leon.homework.app.AppContext
import leon.homework.javaBean.ChoiceExercise
import leon.homework.javaBean.ShortExercise
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


/*
 * Created by BC on 2017/2/19 0019.
 */
class WorkQuesDao :AnkoLogger{
    companion object{
        var mInstance:WorkQuesDao ?= null
        fun getInstance():WorkQuesDao{
            if (mInstance == null) {
                mInstance = WorkQuesDao()
            }
            return mInstance!!
        }
    }
    private val TAG = "WorkQuesDao"
    private var workQuesHelper: WorkQuesSQLiteHelper? = null
    init {
        workQuesHelper = WorkQuesSQLiteHelper(AppContext.instance!!)
    }

    fun execSQL(sql: String) {
        val db: SQLiteDatabase = workQuesHelper!!.writableDatabase      //获得数据库
        val sqlArray = sql.split(";".toRegex()).dropLastWhile(String::isEmpty).toTypedArray() //使用;分隔多项sql语句
        try {
            db.beginTransaction()                                  //开启事务
            for (i in 0..sqlArray.size-1) {                        //最后一条分隔为空
                db.execSQL(sqlArray[i] + ";")
                Log.d("WorkQuesDAO", sqlArray[i] + ";")
            }
            db.setTransactionSuccessful()
            Log.d("WorQueskDAO ","成功执行")
        } catch (e: Exception) {
            Log.d("WorkQuesDAO","SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {
            db.endTransaction()
            db.close()
        }
    }
    fun selectRankbyId(quesid: String):Int{
        val db = workQuesHelper!!.readableDatabase
        var rank:Int?=null
        try {
            /*var tablename = ""
            var idname = ""
            var answername = ""
            when(questype){
                "1"->{
                    tablename = "choicques"
                    idname = "choic_id"
                    answername = "choic_ans"
                }
                "2"->{
                    tablename = "fillques"
                    idname = "fill_id"
                    answername = "fill_ans"
                }
            }*/
            val cursor = db.query("exercise", arrayOf("rank"), "id=?", arrayOf(quesid), null, null, null)
            while (cursor.moveToNext()) {
                rank = cursor.getInt(cursor.getColumnIndex("rank"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "", e.cause)
        } finally {
            db.close()
            return rank!!
        }
    }
    fun selectAnswerbyId(quesid:String):String{     //未测试
        val db = workQuesHelper!!.readableDatabase
        var answer:String?=null
        try {
            /*var tablename = ""
            var idname = ""
            var answername = ""
            when(questype){
                "1"->{
                    tablename = "choicques"
                    idname = "choic_id"
                    answername = "choic_ans"
                }
                "2"->{
                    tablename = "fillques"
                    idname = "fill_id"
                    answername = "fill_ans"
                }
            }*/
            val cursor = db.query("exercise", arrayOf("answer"), "id=?", arrayOf(quesid), null, null, null)
            while (cursor.moveToNext()) {
                answer = cursor.getString(cursor.getColumnIndex("answer"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "", e.cause)
            Log.e(TAG, "selectAnswerbyId--错误")
        } finally {
            //db.close()
            Log.e(TAG, "selectAnswerbyId--成功")
        }
        return answer!!
    }

    fun selectChoice(choId:String):ChoiceExercise{
        val db = workQuesHelper!!.readableDatabase
        val img =arrayOfNulls<String>(5)
        val cursor = db.query("exercise", arrayOf("title", "cho_a", "cho_b","cho_c","cho_d","answer","img1","img2","img3","img4","img5"/*,"rank"*/), "id=?", arrayOf(choId), null, null, null)
        var ChoiceEX:ChoiceExercise? = null
        while (cursor.moveToNext()) {
            info(1)
            val content = cursor.getString(cursor.getColumnIndex("title"))
            info(2)
            val A = cursor.getString(cursor.getColumnIndex("cho_a"))
            val B = cursor.getString(cursor.getColumnIndex("cho_b"))
            val C = cursor.getString(cursor.getColumnIndex("cho_c"))
            val D = cursor.getString(cursor.getColumnIndex("cho_d"))
            val cs = arrayOf(A,B,C,D)
            for(i in 1..5){
                info(3)
                if(!(cursor.getString(cursor.getColumnIndex("img$i")).isNullOrEmpty())){
                    info(4)
                    img[i]=cursor.getString(cursor.getColumnIndex("img$i"))
                }
            }
            //val rank = cursor.getInt(cursor.getColumnIndex("rank"))
            val answer = cursor.getString(cursor.getColumnIndex("answer"))
            info(5)
            ChoiceEX = ChoiceExercise(choId,content,cs,img,answer,/*rank*/6)
        }
        db.close()
        return ChoiceEX!!
    }

    fun selectSho(shoId:String): ShortExercise {
        val db = workQuesHelper!!.readableDatabase
        val img =arrayOfNulls<String>(5)
        val cursor = db.query("exercise", arrayOf("title","answer","img1","img2","img3","img4","img5"/*"rank"*/), "id=?", arrayOf(shoId), null, null, null)
        var ShoEX:ShortExercise? = null
        while (cursor.moveToNext()) {
            val fill_sbj = cursor.getString(cursor.getColumnIndex("title"))
            val fill_ans = cursor.getString(cursor.getColumnIndex("answer"))
            for(i in 1..5){
                if(!(cursor.getString(cursor.getColumnIndex("img$i")).isNullOrEmpty())){
                    img.set(i,cursor.getString(cursor.getColumnIndex("img$i")))
                }
            }
            //val rank = cursor.getInt(cursor.getColumnIndex("rank"))
            ShoEX = ShortExercise(shoId,fill_sbj,fill_ans,img,/*rank*/5)
        }
        db.close()
        return ShoEX!!
    }
}