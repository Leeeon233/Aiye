package leon.homework.sqlite

import android.util.Log
import leon.homework.app.AppContext
import leon.homework.Utils.ImgHelper
import leon.homework.Utils.Utils
import leon.homework.data.Const
import leon.homework.data.SaveData
import leon.homework.javaBean.TodayWork
import leon.homework.javaBean.WorkResult
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/*
 * Created by BC on 2017/2/20 0020.
 * 作业数据管理
 */
class WorkDao : AnkoLogger {
    companion object{
        private var mInstance:WorkDao ?= null
        fun getInstance():WorkDao{
            if (mInstance == null) {
                mInstance = WorkDao()
            }else if(mInstance!!.workhelper!!.databaseName != (SaveData.phonenum+"Work.db")){
                mInstance = WorkDao()
            }
            return mInstance!!
        }
    }
    private val TAG = "WorkDao"
    private val RelationalTable = "RelationalTable"
    private var workhelper: WorkSQLiteHelper? = null
    init {
        val dbname = SaveData.phonenum+"Work.db"
        workhelper = WorkSQLiteHelper(AppContext.instance!!,dbname)
    }

    fun execSQL(sql: String) {
        val db = workhelper!!.writableDatabase      //获得数据库
        try {
            db.beginTransaction()
            db.execSQL(sql)
            db.setTransactionSuccessful()
            Log.d(TAG,"execSQL--成功执行")
        } catch (e: Exception) {
            Log.d(TAG,"execSQL--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun selectWorkResult(workid: String,chonum:Int,shonum:Int):WorkResult{
        val db = workhelper!!.readableDatabase      //获得数据库
        val stuid:String = AppContext.User.alia!!
        var timeused:String?= null
        val result = JSONObject()
        val cho = JSONObject()
        val sho = JSONObject()
        cho.put("num",chonum)
        sho.put("num",shonum)
        try {
            val cursor = db.query("HistoryWork", arrayOf("timeused"), "WorkId=?", arrayOf(workid), null, null, null)
            while (cursor.moveToNext()) {
                timeused = cursor.getString(cursor.getColumnIndex("timeused"))
            }
            cursor.close()
            cho.put("content",getchoresult(workid))
            sho.put("content",getshoresult(workid))
            result.put("cho",cho)
            result.put("sho",sho)
            Log.d(TAG,"selectWorkResult--成功执行")
        }catch (e:Exception){
            Log.d(TAG,"selectWorkResult--SQL错误")
            Log.e(TAG,e.message)
        }finally {
            //db.close()
        }
        return WorkResult(workid,stuid,timeused!!,result)
    }

    private fun getchoresult(workid: String):JSONObject{
        val db = workhelper!!.readableDatabase      //获得数据库
        val result = JSONObject()
        var quesnum = 1
        try {
            val cursor = db.query(workid, arrayOf("quesid","result"), "questype=?", arrayOf("1"), null, null, null)
            while (cursor.moveToNext()){
                val cjs = JSONObject()
                val quesid = cursor.getString(cursor.getColumnIndex("quesid"))
                val qresult = cursor.getString(cursor.getColumnIndex("result"))
                cjs.put("quesid",quesid)
                cjs.put("result",qresult)
                result.put(quesnum.toString(),cjs)
                quesnum++
            }
            cursor.close()
            Log.d(TAG,"getchoresult--成功执行")
        }catch (e:Exception){
            Log.d(TAG,"getchoresult--SQL错误")
            Log.e(TAG,e.message)
        }finally {
            //db.close()
        }
        return result
    }

    private fun getshoresult(workid: String):JSONObject{
        val db = workhelper!!.readableDatabase      //获得数据库
        val result = JSONObject()
        var quesnum = 1
        try {
            val cursor = db.query(workid, arrayOf("quesid","result"), "questype=?", arrayOf("2"), null, null, null)
            while (cursor.moveToNext()){
                val sjs = JSONObject()
                val quesid = cursor.getString(cursor.getColumnIndex("quesid"))
                val qresult = cursor.getString(cursor.getColumnIndex("result"))
                sjs.put("quesid",quesid)
                if(qresult!=null){
                    sjs.put("result",ImgHelper.encodeImage(qresult))
                }else{
                    sjs.put("result","")
                }
                result.put(quesnum.toString(),sjs)
                quesnum++
            }
            cursor.close()
            Log.d(TAG,"getshoresult--成功执行")
        }catch (e:Exception){
            Log.d(TAG,"getshoresult--SQL错误")
            Log.e(TAG,e.message)
        }finally {
            //db.close()
        }
        return result
    }

    fun selectTime(tablename: String):Long{
        val db = workhelper!!.readableDatabase      //获得数据库
        var timeused:Long?=null
        try {
            val cursor = db.query("HistoryWork", arrayOf("timeused"), "WorkId=?", arrayOf(tablename), null, null, null)
            while (cursor.moveToNext()) {
                timeused = cursor.getLong(cursor.getColumnIndex("timeused"))
            }
            cursor.close()
            Log.d(TAG,"selectTime--成功执行")
        }catch (e:Exception){
            Log.d(TAG,"selectTime--SQL错误")
            Log.e(TAG,e.message)
        }finally {
            //db.close()
        }
        return timeused!!
    }
    fun updateTime(workid: String,time:Long){
        val db = workhelper!!.writableDatabase      //获得数据库
        try {
            val sql = "update HistoryWork set timeused = '$time' where WorkId = '$workid';"
            db.execSQL(sql)
            Log.d(TAG,"updateTime--成功执行")
        } catch (e: Exception) {
            Log.d(TAG,"updateTime--SQL错误")
            Log.e(TAG,e.message)
        } finally {
           //db.close()
        }
    }

    fun selectWorkIsfinished(workid: String):Boolean{
        val db = workhelper!!.readableDatabase      //获得数据库
        var isfinished:Boolean?=null
        val cursor = db.query("HistoryWork", arrayOf("IsFinished"), "WorkId=?", arrayOf(workid), null, null, null)
        while (cursor.moveToNext()) {
            isfinished = when(cursor.getInt(cursor.getColumnIndex("IsFinished"))){
                1 -> true
                0 -> false
                else -> {
                    false
                }
            }
        }
        cursor.close()
        //db.close()
        return isfinished!!
    }
    /*
     * 更新学生总分
     */
    fun updateStuTolPoint(workid: String,totalpoint:Int){
        val db = workhelper!!.writableDatabase      //获得数据库
        try {
            val sql = "update HistoryWork set totalpoint = $totalpoint,iscorrect = 1 where WorkId = '$workid';"
            db.execSQL(sql)
            Log.d(TAG,"updateIsfinished--成功执行")
        } catch (e: Exception) {
            Log.d(TAG,"updateIsfinished--SQL错误")
            Log.e(TAG,e.message)
        } finally {
            //db.close()
        }
    }

    /*
     * 更新学生分数
     */
    fun updateStuPoint(workid: String,quesid: String,point: Int){
        val db = workhelper!!.writableDatabase      //获得数据库
        try {
            val sql = "update $workid set point = $point where quesid = '$quesid';"
            db.execSQL(sql)
            Log.d(TAG,"updateIsfinished--成功执行")
        } catch (e: Exception) {
            Log.d(TAG,"updateIsfinished--SQL错误")
            Log.e(TAG,e.message)
        } finally {
            //db.close()
        }
    }

    fun updateIsfinished(workid: String){
        val db = workhelper!!.writableDatabase      //获得数据库
        try {
            val sql = "update HistoryWork set IsFinished = 1 where WorkId = '$workid';"
            db.execSQL(sql)
            Log.d(TAG,"updateIsfinished--成功执行")
        } catch (e: Exception) {
            Log.d(TAG,"updateIsfinished--SQL错误")
            Log.e(TAG,e.message)
        } finally {
            //db.close()
        }
    }

    /*
     * 查询教师批改结果
     */
    fun selectCorrectResult(mid: String, workid: String):JSONObject{
        val db = workhelper!!.readableDatabase      //获得数据库
        val js = JSONObject()
        val res = JSONObject()
        js.put("MId",mid)
        js.put("workid",workid)
        js.put("ACTION",Const.ACTION_GET_POINT)
        var point = 0
        var mpoint = 0
        var num = 1
        try {
            val cursor = db.query(mid, arrayOf("quesid","point"), null, null, null, null, null)
            while (cursor.moveToNext()) {
                val jss = JSONObject()
                jss.put("quesid",cursor.getString(cursor.getColumnIndex("quesid")))
                mpoint = cursor.getInt(cursor.getColumnIndex("point"))
                point+=mpoint
                jss.put("point",mpoint)
                res.put("$num",jss)
                num++
            }
            js.put("result",res)
            js.put("totalpoint",point)
            cursor.close()
            Log.d(TAG,"selectCorrectResult--成功执行")
        }catch (e:Exception){
            Log.d(TAG,"selectCorrectResult--SQL错误")
            Log.e(TAG,e.message)
        }finally {
            updateRelational(mid,point)
        }
        return js
    }
    /*
     * 更新mid表
     */
    private fun updateRelational(mid: String,point: Int){
        val db = workhelper!!.writableDatabase      //获得数据库
        try {
            val sql = "update RelationalTable set IsFinished = 1,TotalPoint = $point where MId = '$mid';"
            db.execSQL(sql)
            Log.d(TAG,"updateTime--成功执行")
        } catch (e: Exception) {
            Log.d(TAG,"updateTime--SQL错误")
            Log.e(TAG,e.message)
        } finally {
            //db.close()
        }
    }
    /*
     *查询教师一条作业是否批改完成
     */
    fun selectTeaIsFinished(mid:String):Boolean{
        val db = workhelper!!.readableDatabase      //获得数据库
        var isfinished = 0
        val cursor = db.query(RelationalTable, arrayOf("IsFinished"), "MId=?", arrayOf(mid), null, null, null)
        while (cursor.moveToNext()) {
            isfinished = cursor.getInt(cursor.getColumnIndex("IsFinished"))
        }
        cursor.close()
        //db.close()
        return isfinished == 1
    }
    /*
     *查询教师此题是否批改完成
     */
    fun selectTeaCorIsfinished(mid: String,qnum:Int):Int{
        val db = workhelper!!.readableDatabase      //获得数据库
        var isfinished:Int?=null
        val cursor = db.query(mid, arrayOf("iscorrect"), "quesnum=?", arrayOf(qnum.toString()), null, null, null)
        while (cursor.moveToNext()) {
            isfinished = cursor.getInt(cursor.getColumnIndex("iscorrect"))
        }
        cursor.close()
        //db.close()
        return isfinished!!
    }
    /*
     * 查询学生此题是否完成
     */
    fun selectIsfinished(tablename: String,qnum:Int):Int{
        val db = workhelper!!.readableDatabase      //获得数据库
        var isfinished:Int?=null
        val cursor = db.query(tablename, arrayOf("isfinished"), "quesnum=?", arrayOf(qnum.toString()), null, null, null)
        while (cursor.moveToNext()) {
            isfinished = cursor.getInt(cursor.getColumnIndex("isfinished"))
        }
        cursor.close()
        //db.close()
        return isfinished!!
    }

    fun selectResult(tablename: String,qnum:Int):String{
        val db = workhelper!!.readableDatabase      //获得数据库
        var result:String?=null
        val cursor = db.query(tablename, arrayOf("result"), "quesnum=?", arrayOf(qnum.toString()), null, null, null)
        while (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex("result"))
        }
        cursor.close()
        //db.close()
        return result!!
    }

    fun tableisexist(tablename: String):Boolean{
        var isexist = false
        val db = workhelper!!.readableDatabase    //获得数据库
        try {
            db.beginTransaction()
            val sql = "select count(*) from sqlite_master where type ='table' and name ='$tablename';"
            val cursor = db.rawQuery(sql, null)
            if (cursor.moveToNext()) {
                val count = cursor.getInt(0)
                if (count > 0) {
                    isexist = true
                }
            }
            cursor.close()
            db.setTransactionSuccessful()
            Log.d(TAG,"tableisexist--成功执行")
        } catch (e: Exception) {
            Log.d(TAG,"tableisexist--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {
            db.endTransaction()
            //db.close()
        }
        return isexist
    }

    fun updateResult(tablename:String,tolnum:Int,result: JSONObject){
        val db = workhelper!!.writableDatabase      //获得数据库

        try {
            db.beginTransaction()
            for(i in 1..tolnum){
                if(result.has(i.toString())){
                    val mresult = result.get(i.toString())
                    val sql = "update $tablename set result = '$mresult',isfinished = 1 where quesnum = $i;"
                    db.execSQL(sql)
                }
            }
            db.setTransactionSuccessful()
            Log.d(TAG,"updateResult--成功执行")
        } catch (e: Exception) {
            Log.d(TAG,"updateResult--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {
            db.endTransaction()
            //db.close()
        }
    }

    /*fun updatePoint(workid: String,points:JSONObject){
        val db = workhelper!!.writableDatabase      //获得数据库

        try {
            db.beginTransaction()
            for(i in 1..tolnum){
                if(result.has(i.toString())){
                    val mresult = result.get(i.toString())
                    val sql = "update $tablename set result = '$mresult',isfinished = 1 where quesnum = $i;"
                    db.execSQL(sql)
                }
            }
            db.setTransactionSuccessful()
            Log.d(TAG,"updateResult--成功执行")
        } catch (e: Exception) {
            Log.d(TAG,"updateResult--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {
            db.endTransaction()
            //db.close()
        }
    }*/


    /*
     * 教师收到一条学生作业
     */

    fun insertRelationalTable(js:JSONObject){
        info("insertRelationalTable>>>>>>>>>>>>>>>>>>>>>>>>")
        val workid = js.getString("workid")
        val stuid = js.getString("stuid")
        val mid = js.getString("id")
        val time = 312321//js.getString("timeused")
        if(js.has("pwork")){
            val pwork = js.getJSONObject("pwork").getString("base64")
            info("pwork>>>>>>>>>>>>>$pwork")
            if((pwork != "") && pwork != null){
                val ppath = Const.DATA_PATH+"$mid/pwork.png"
                Utils.decoderBase64File(pwork,ppath)
            }
        }
        if(js.has("rwork")){
            val rwork = js.getJSONObject("rwork").getString("base64")
            if((rwork != "")&&(rwork!=null)) {
                val rpath = Const.DATA_PATH + "$mid/rwork.amr"
                Utils.decoderBase64File(rwork, rpath)
            }
        }
        val ework = js.getJSONObject("ework")

        val db = workhelper!!.writableDatabase

        try {
            val sql = "insert into RelationalTable (" +
                    "WorkId,StuId,MId,TimeUsed,IsFinished,TotalPoint) " +
                    "values ('$workid','$stuid','$mid','$time',0,0);"
            info(sql)
            execSQL(sql)
            Log.d(TAG,"insertRelationalTable--成功执行")
        }catch (e: Exception){
            Log.e(TAG, "", e.cause)
            Log.d(TAG,"insertRelationalTable--SQL错误")
        }finally {
            createMIdTable(mid,ework)
        }
    }
    private fun createMIdTable(mid:String,js:JSONObject){
        val db = workhelper!!.writableDatabase
        val cho = js.getJSONObject("cho")
        val sho = js.getJSONObject("sho")
        val chocontent = cho.getJSONObject("content")
        val shocontent = sho.getJSONObject("content")
        val chonum = cho.getInt("num")
        val shonum = sho.getInt("num")
        var quesnum = 1
        try {
            db.beginTransaction()
            val createtable = "create table if not exists $mid ( " +
                    "quesnum  VARCHAR( 20 ) ," +
                    "quesid VARCHAR( 30 ) primary key," +
                    "answer VARCHAR( 500 )," +
                    "result VARCHAR( 500 )," +
                    "point INT," +
                    "iscorrect INT default 0);"
            db.execSQL(createtable)
            info("create teacher mid  $mid table")

            if(chonum>0){
                for(i in 1..chonum){
                    val quesid = chocontent.getJSONObject("$i").getString("quesid")
                    val result = chocontent.getJSONObject("$i").getString("result")
                    val answer = WorkQuesDao.getInstance().selectAnswerbyId(quesid)
                    info("answer:"+WorkQuesDao.getInstance().selectAnswerbyId(quesid))
                    var point = 0
                    if(result == answer) point = 10
                    val sql = "insert into $mid (quesnum,quesid,answer,result,point,iscorrect) " +
                            "values ($quesnum,'$quesid','$answer','$result',$point,1);"
                    info(sql)
                    quesnum++
                    db.execSQL(sql)
                }
            }
            if(shonum>0){
                for(i in 1..shonum){
                    val quesid = shocontent.getJSONObject("$i").getString("quesid")
                    val answer = /*WorkQuesDao.getInstance().selectAnswerbyId(quesid)*/"222"
                    //val rank = WorkQuesDao.getInstance().selectRankbyId(quesid)
                    val result1 = shocontent.getJSONObject("$i").getString("result")
                    val result = Const.DATA_PATH+"$mid/$quesid.png"
                    Utils.decoderBase64File(result1,result)
                    val sql = "insert into $mid (quesnum,quesid,answer,result,point,iscorrect) " +
                            "values ($quesnum,'$quesid','$answer','$result',0,0);"
                    info(sql)
                    quesnum++
                    db.execSQL(sql)
                }
            }
            val sql3 = "insert into $mid (quesnum,quesid,answer,result,point,iscorrect) " +
                    "values (-1,'100','','',0,0);"
            val sql4 = "insert into $mid (quesnum,quesid,answer,result,point,iscorrect) " +
                    "values (-2,'200','','',0,0);"
            db.execSQL(sql3)
            db.execSQL(sql4)
            db.setTransactionSuccessful()
            Log.d(TAG,"createMIdTable--成功执行")
        } catch (e: Exception) {
            info("error:"+e.message)
            info(e.cause)
            Log.d(TAG,"createMIdTable--SQL错误")
        } finally {
            db.endTransaction()
            //db.close()
        }
    }
    /*
     * 创建教师workid表
     */
    fun createTeaWorkidTable(workid: String,js:JSONObject){
        val db = workhelper!!.writableDatabase
        val cho = js.getJSONObject("cho")
        val sho = js.getJSONObject("sho")
        val chonum = cho.getInt("num")
        val shonum = sho.getInt("num")
        val chocontent = cho.getJSONObject("content")
        val shocontent = sho.getJSONObject("content")
        var quesnum = 1
        try {
            db.beginTransaction()
            val createtable = "create table if not exists $workid ( " +
                    "quesnum INT primary key ," +
                    "quesid  VARCHAR( 20 ) ," +
                    "questype INT," +
                    "answer VARCHAR( 500 ));"
            db.execSQL(createtable)
            info("create teacher workid table")

            if(chonum>0){
                for(i in 1..chonum){
                    val quesid = chocontent.getString("$i")
                    val answer = WorkQuesDao.getInstance().selectAnswerbyId(quesid)
                    //val rank = WorkQuesDao.getInstance().selectRankbyId(quesid)
                    val sql = "insert into $workid (quesnum,quesid,questype,answer) " +
                            "values ($quesnum,'$quesid',1,'$answer');"
                    info(sql)
                    quesnum++
                    db.execSQL(sql)
                }
            }
            if(shonum>0){
                for(i in 1..shonum){
                    val quesid = shocontent.getString("$i")
                    val answer = /*WorkQuesDao.getInstance().selectAnswerbyId(quesid)*/"222"
                    //val rank = WorkQuesDao.getInstance().selectRankbyId(quesid)
                    val sql = "insert into $workid (quesnum,quesid,questype,answer) " +
                            "values ($quesnum,'$quesid',2,'$answer');"
                    info(sql)
                    quesnum++
                    db.execSQL(sql)
                }
            }
            val psql = "insert into $workid (quesnum,quesid,questype,answer) " +
                    "values (-1,'100',3,'${js.getString("paperwork")}');"
            val rsql = "insert into $workid (quesnum,quesid,questype,answer) " +
                    "values (-2,'200',4,'${js.getString("radiowork")}');"
            db.execSQL(psql)
            db.execSQL(rsql)
            db.setTransactionSuccessful()
            Log.d(TAG,"createHistory--成功执行")
        } catch (e: Exception) {
            info("error:"+e.message)
            info(e.cause)
            Log.d(TAG,"createHistory--SQL错误")
        } finally {
            db.endTransaction()
            //db.close()
        }
    }

    /*
     * 创建workid新表
     */

    fun createHistory(workid: String,js:JSONObject){
        val db = workhelper!!.writableDatabase
        val cho = js.getJSONObject("cho")
        val sho = js.getJSONObject("sho")
        val chonum = cho.getInt("num")
        val shonum = sho.getInt("num")
        val chocontent = cho.getJSONObject("content")
        val shocontent = sho.getJSONObject("content")
        var quesnum = 1
        try {
            db.beginTransaction()
            val createtable = "create table if not exists $workid ( " +
                    "quesnum INT primary key ," +
                    "quesid  VARCHAR( 20 ) ," +
                    "questype INT," +
                    "result  VARCHAR( 500 ) ," +
                    "answer VARCHAR( 500 )," +
                    "isfinished INT," +
                    "total INT," +
                    "point INT," +
                    "rank INT);"
            db.execSQL(createtable)
            info("create workid table")

            if(chonum>0){
                for(i in 1..chonum){
                    val quesid = chocontent.getString("$i")
                    val answer = WorkQuesDao.getInstance().selectAnswerbyId(quesid)
                    val sql = "insert into $workid (quesnum,quesid,questype,answer,isfinished,total) " +
                            "values ($quesnum,'$quesid',1,'$answer',0,10);"
                    info(sql)
                    quesnum++
                    db.execSQL(sql)
                }
            }
            if(shonum>0){
                for(i in 1..shonum){
                    val quesid = shocontent.getString("$i")
                    val answer = /*WorkQuesDao.getInstance().selectAnswerbyId(quesid)*/"222"
                    //val rank = WorkQuesDao.getInstance().selectRankbyId(quesid)
                    val sql = "insert into $workid (quesnum,quesid,questype,answer,isfinished,total) " +
                            "values ($quesnum,'$quesid',2,'$answer',0,10);"
                    info(sql)
                    quesnum++
                    db.execSQL(sql)
                }
            }
            val psql = "insert into $workid (quesnum,quesid,questype,answer) " +
                    "values (-1,'100',3,'${js.getString("paperwork")}');"
            val rsql = "insert into $workid (quesnum,quesid,questype,answer) " +
                    "values (-2,'200',4,'${js.getString("radiowork")}');"
            db.execSQL(psql)
            db.execSQL(rsql)
            db.setTransactionSuccessful()
            Log.d(TAG,"createHistory--成功执行")
        } catch (e: Exception) {
            info("error:"+e.message)
            info(e.cause)
            Log.d(TAG,"createHistory--SQL错误")
        } finally {
            db.endTransaction()
            //db.close()
        }
    }
    /*
     * 插入一条History,并创建mworkid新表
     */
    fun insertAWork(subject: TodayWork,workid:String){     //插入一条作业数据
        val db = workhelper!!.writableDatabase

        //ZipUtil.unzipFiles(File(Const.DOWNLOAD_PATH+"questions.zip"),Const.DATA_PATH)

        val sq1 ="CREATE TABLE if not exists `exercise` (  `id` varchar(11) DEFAULT NULL,  `title` varchar(774) DEFAULT NULL,  `cho_a` varchar(26) DEFAULT NULL, `cho_b` varchar(25) DEFAULT NULL,  `cho_c` varchar(24) DEFAULT NULL,  `cho_d` varchar(30) DEFAULT NULL,  `answer` varchar(9) DEFAULT NULL,  `img1` varchar(100) DEFAULT '0',  `img2` varchar(100) DEFAULT '0',  `img3` varchar(100) DEFAULT '0',  `img4` varchar(100) DEFAULT '0',  `img5` varchar(100) DEFAULT '0' );"
        WorkQuesDao.getInstance().execSQL(sq1)
        val sq2 = "INSERT INTO `exercise` VALUES ('2207011006','下列算式中，正确的是:', '2x+2y=4xy', '2a^2+2a^3=2a^5', '4a^2-3a^2=1', '-2ba^2+a^2b=-a^2b', 'd', '', '', '', '', ''),('2207011012', '如图，两个正六边形的面积分别为16，9，两个阴影部分的面积分别为a，b（a＜b），则b-a的值为 aiye1', '5', '6', '7',' 8', 'c', '2207011012.jpg', '', '', '', ''),('2207011020', '若单项式2x^3y^a+b与aiye1是同类项，则a，b的值分别为', 'a=-4，b=-1', 'a=-4，b=1', 'a=4，b=-1', 'a=4，b=1', 'd', '2207011020.jpg', '', '', '', ''),('2207012003', '股民李星星在上周星期五以每股11.2元买了一批股票，下表为本周星期一到星期五该股票的涨跌情况求：（1）本周星期三收盘时，每股的钱数．（2）李星星本周内哪一天把股票抛出比较合算，为什么？ aiye1', '', '', '', '', '', '2207012003.jpg', '', '', '', '');"
        WorkQuesDao.getInstance().execSQL(sq2)
        //val StuID = AppContext.User.alia
        //val StuName = AppContext.User.name
        val Subject = subject.subjectName
        val WorkDeadline = subject.deadLine
        val QuesJS = subject.js.toString()
        val teacher = subject.js.getString("teacher")
        try {
            db.beginTransaction()
            val sql = "Insert into HistoryWork (WorkId,TeacherAlia,Subject,WorkDeadline,IsFinished,timeused,Questions,totalpoint,iscorrect) " +
                    "values ('$workid','$teacher','$Subject','$WorkDeadline',0,0,'$QuesJS',0,0);"
            db.execSQL(sql)
            db.setTransactionSuccessful()
            Log.d(TAG,"insertAWork--成功执行")
        }catch (e: Exception){
            Log.e(TAG, "", e.cause)
            Log.d(TAG,"insertAWork--SQL错误")
        }finally {
            db.endTransaction()
            //db.close()
            if(SaveData.UserType==Const.ISSTUDENT)
                createHistory(workid,subject.js)
            else
                createTeaWorkidTable(workid,subject.js)

        }
    }
    /*
     * 获取所有未完成的作业 TodayWork-List
     */
    fun select1():ArrayList<TodayWork>{
        val db = workhelper!!.readableDatabase
        val worklist = ArrayList<TodayWork>()
        val cursor = db.query("HistoryWork", arrayOf("Subject", "WorkDeadline","Questions"), "IsFinished=?", arrayOf("0"), null, null, null)
        while (cursor.moveToNext()) {
            val Subject = cursor.getString(cursor.getColumnIndex("Subject"))
            val WorkDeadline = cursor.getString(cursor.getColumnIndex("WorkDeadline"))
            val js = cursor.getString(cursor.getColumnIndex("Questions"))
            worklist.add(TodayWork(Subject,WorkDeadline,JSONObject(js)))
        }
        cursor.close()
        //db.close()
        return worklist
    }
    /*
    *获取已完成作业
    */
    fun selectIsfinishedWorks():ArrayList<TodayWork>{
        val db = workhelper!!.readableDatabase
        val worklist = ArrayList<TodayWork>()
        val cursor = db.query("HistoryWork", arrayOf("Subject", "WorkDeadline","Questions"), "IsFinished=?", arrayOf("1"), null, null, null)
        while (cursor.moveToNext()) {
            val Subject = cursor.getString(cursor.getColumnIndex("Subject"))
            val WorkDeadline = cursor.getString(cursor.getColumnIndex("WorkDeadline"))
            val js = cursor.getString(cursor.getColumnIndex("Questions"))
            worklist.add(TodayWork(Subject,WorkDeadline,JSONObject(js)))
        }
        cursor.close()
        //db.close()
        return worklist
    }
    /*
     *  Teacher
     */

    /*fun createStudentWork(js:JSONObject){
        val db = workhelper!!.writableDatabase
        val workid = js.getString("workid")
        val stuid = js.getString("stuid")
        val cho = js.getJSONObject("cho")
        val sho = js.getJSONObject("sho")
        val chonum = cho.getInt("num")
        val shonum = sho.getInt("num")
        val chocontent = cho.getJSONObject("content")
        val shocontent = sho.getJSONObject("content")
        for(i in 1..shonum){
            Utils.decoderBase64File(shocontent.getString("$i"),Const.DATA_PATH+workid+stuid+"/"+shocontent.getString("quesid")+".png")
        }
        val ptitle = js.getString("pwork")
        val rtitle = js.getString("rwork")
        var quesnum = 1
        try {
            db.beginTransaction()
            val createtable = "create table if not exists ${workid+stuid}( quesnum INT primary key ,quesid  VARCHAR( 20 ) ,questype INT,result  VARCHAR( 500 ) ,answer VARCHAR( 500 ),iscorrect INT,total INT,point INT,rank INT);"
            db.execSQL(createtable)

            for(i in 1..chonum){
                val cjs = chocontent.getJSONObject("$i")
                val quesid = cjs.getString("quesid")
                val result = cjs.getString("result")
                val answer = WorkQuesDao.getInstance().selectAnswerbyId(quesid)
                val rank = WorkQuesDao.getInstance().selectRankbyId(quesid)
                val total = if (result==answer){ 5 }else{ 0 }
                val sql = "insert into '${workid+stuid}' (quesnum,quesid,questype,result,answer,iscorrect,total,point,rank) " +
                        "values ($quesnum,'$quesid',1,'$result','$answer',1,5,$total,$rank);"
                quesnum++
                db.execSQL(sql)
            }
            for(i in 1..shonum){
                val sjs = shocontent.getJSONObject("$i")
                val quesid = sjs.getString("quesid")
                val answer = WorkQuesDao.getInstance().selectAnswerbyId(quesid)
                val rank = WorkQuesDao.getInstance().selectRankbyId(quesid)
                val sql = "insert into ${workid+stuid}  (quesnum,quesid,questype,result,answer,iscorrect,total,rank) " +
                        "values ($quesnum,'$quesid',2,'$quesid','$answer',0,10,$rank);"
                quesnum++
                db.execSQL(sql)
            }
            val sql2 = "insert into ${workid+stuid}  (quesnum,quesid,questype,result,answer,iscorrect,total,rank) " +
                    "values ($quesnum,'100',3,'$ptitle','',1,10,0);"
            db.execSQL(sql2)
            val sql3 = "insert into ${workid+stuid}  (quesnum,quesid,questype,result,answer,iscorrect,total,rank) " +
                    "values ($quesnum,'200',4,'$rtitle','',1,10,0);"
            db.execSQL(sql3)
            db.setTransactionSuccessful()
            Log.d(TAG,"createHistory--成功执行")
        } catch (e: Exception) {
            info("error:"+e.message)
            Log.d(TAG,"createHistory--SQL错误")
        } finally {
            db.endTransaction()
            //db.close()
        }
    }*/

    fun updateStudentWork(workid: String,stuid:String,quesid:String,point:Int){
        val db = workhelper!!.writableDatabase      //获得数据库
        try {
            db.beginTransaction()
            val sql = "update ${workid+stuid} set point = $point where quesid = '$quesid';"
            db.execSQL(sql)
            db.setTransactionSuccessful()
            Log.d(TAG,"updateStudentWork--成功执行")
        } catch (e: Exception) {
            Log.d(TAG,"updateStudentWork--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {
            db.endTransaction()
            //db.close()
        }
    }

    /*
     * 查询教师是否批改完成
     */
    fun selectStuIsCorrected(workid: String):Boolean{
        val db = workhelper!!.readableDatabase      //获得数据库
        var result:Boolean?=null
        val cursor = db.query("HistoryWork", arrayOf("iscorrect"), "WorkId=?", arrayOf(workid), null, null, null)
        while (cursor.moveToNext()) {
            result = when(cursor.getInt(cursor.getColumnIndex("iscorrect"))){
                0 -> false
                1 -> true
                else -> false
            }
        }
        cursor.close()
        //db.close()
        return result!!
    }
    /*fun selectiscorrect(tablename: String):Boolean{
        var iscorrect = false
        var total:Int?=null
        var num=0
        val db = workhelper!!.readableDatabase    //获得数据库
        try {
            db.beginTransaction()
            val sql = "select count(*) from $tablename;"
            val cursor = db.rawQuery(sql, null)
            if (cursor.moveToNext()) {
                total = cursor.getInt(0)
            }
            cursor.close()
            val sql2 = "select count(*) from $tablename where iscorrect = 1;"
            val cursor2 = db.rawQuery(sql2, null)
            if (cursor2.moveToNext()) {
                num = cursor2.getInt(0)
            }
            db.setTransactionSuccessful()
            Log.d(TAG,"selectiscorrect--成功执行")
        } catch (e: Exception) {
            Log.d(TAG,"selectiscorrect--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {
            db.endTransaction()
            //db.close()
        }
        if(num==total){
            iscorrect = true
        }
        return iscorrect
    }*/

    fun selectSturesult(tablename: String,quesid: String):String {
        var result: String? = null
        val db = workhelper!!.readableDatabase    //获得数据库
        try {
            db.beginTransaction()
            val cursor = db.query(tablename, arrayOf("result"), "quesid=?", arrayOf(quesid), null, null, null)
            while (cursor.moveToNext()) {
                result = cursor.getString(cursor.getColumnIndex("result"))
            }
            cursor.close()
            db.setTransactionSuccessful()
            Log.d(TAG, "tableisexist--成功执行")
        } catch (e: Exception) {
            Log.d(TAG, "tableisexist--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {
            db.endTransaction()
            //db.close()
        }
        return result!!
    }

    fun  selectMid(workid: String?, stu_alia: String): String {
        var mid:String?="-1"
        val db = workhelper!!.readableDatabase    //获得数据库
        try {
            db.beginTransaction()
            val cursor = db.query(RelationalTable, arrayOf("MId"), "WorkId=? and StuId=?", arrayOf(workid,stu_alia), null, null, null)
            while (cursor.moveToNext()) {
                mid = cursor.getString(cursor.getColumnIndex("MId"))
            }
            cursor.close()
            Log.d(TAG, "selectMid--成功执行")
        } catch (e: Exception) {
            Log.d(TAG, "selectMid--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {
            db.endTransaction()
            //db.close()
        }
        return mid!!
    }
    /*
     * 教师获取学生作业答案
     */
    fun selectMidResult(mid: String,quesid: String):String{
        var result:String?=null
        val db = workhelper!!.readableDatabase    //获得数据库
        try {
            db.beginTransaction()
            val cursor = db.query(mid, arrayOf("result"), "quesid=?", arrayOf(quesid), null, null, null)
            while (cursor.moveToNext()) {
                result = cursor.getString(cursor.getColumnIndex("result"))
            }
            cursor.close()
            db.setTransactionSuccessful()
            Log.d(TAG, "selectMid--成功执行")
        } catch (e: Exception) {
            Log.d(TAG, "selectMid--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {
            db.endTransaction()
            //db.close()
        }
        return result!!
    }
    /*
     * 教师获取学生作业用时
     */
    fun selectRelationTime(mid: String):String{
        var time:String?=null
        val db = workhelper!!.readableDatabase    //获得数据库
        try {
            db.beginTransaction()
            val cursor = db.query(RelationalTable, arrayOf("TimeUsed"), "MId=?", arrayOf(mid), null, null, null)
            while (cursor.moveToNext()) {
                time = cursor.getString(cursor.getColumnIndex("TimeUsed"))
            }
            cursor.close()
            db.setTransactionSuccessful()
            Log.d(TAG, "selectMid--成功执行")
        } catch (e: Exception) {
            Log.d(TAG, "selectMid--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {
            db.endTransaction()
            //db.close()
        }
        return time!!
    }
    /*
     * 更新学生作业得分
     */
    fun updatePoint(mid: String,quesid: String,point: Int){
        val db = workhelper!!.writableDatabase      //获得数据库
        try {
            db.beginTransaction()
            val sql = "update $mid set point = $point,iscorrect = 1 where quesid = '$quesid';"
            db.execSQL(sql)
            db.setTransactionSuccessful()
            Log.d(TAG,"updateStudentWork--成功执行")
        } catch (e: Exception) {
            Log.d(TAG,"updateStudentWork--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {
            db.endTransaction()
            //db.close()
        }
    }
    /*
     * 查询学生主观题得分
     */
    fun  selectShoPoint(workid: String, quesid: String): String {
        var result:String?=null
        val db = workhelper!!.readableDatabase    //获得数据库
        try {
            db.beginTransaction()
            val cursor = db.query(workid, arrayOf("point"), "quesid=?", arrayOf(quesid), null, null, null)
            while (cursor.moveToNext()) {
                result = cursor.getString(cursor.getColumnIndex("point"))
            }
            cursor.close()
            db.endTransaction()
            Log.d(TAG, "selectShoPoint--成功执行")
        } catch (e: Exception) {
            Log.d(TAG, "selectShoPoint--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {

            //db.close()
        }
        return result!!
    }
    /*
     * 通过科目查询成绩时间
     */
    fun  selectScorebySubject(subject: String): JSONObject {
        val result = JSONObject()
        var date :String
        var score:Int
        var num = 1
        val db = workhelper!!.readableDatabase    //获得数据库
        try {
            db.beginTransaction()
            val cursor = db.query("HistoryWork", arrayOf("WorkDeadline","totalpoint"), "Subject=?", arrayOf(subject), null, null, null)
            while (cursor.moveToNext()) {
                val js = JSONObject()
                date = cursor.getString(cursor.getColumnIndex("WorkDeadline")).subSequence(5,10).toString()
                score = cursor.getInt(cursor.getColumnIndex("totalpoint"))
                js.put("date",date)
                js.put("score",score)
                result.put("${num++}",js)
            }
            cursor.close()
            db.endTransaction()
            Log.d(TAG, "selectScorebySubject--成功执行")
        } catch (e: Exception) {
            Log.d(TAG, "selectScorebySubject--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {

            //db.close()
        }
        return result
    }

    fun  selectAllSubject(): ArrayList<String> {
        val result = ArrayList<String>()
        var subject :String
        val db = workhelper!!.readableDatabase    //获得数据库
        try {
            db.beginTransaction()
            val cursor = db.query("HistoryWork", arrayOf("Subject"),null, null, null, null, null)
            while (cursor.moveToNext()) {
                subject = cursor.getString(cursor.getColumnIndex("Subject"))
                if(!result.contains(subject)){
                    result.add(subject)
                }

            }
            cursor.close()
            db.endTransaction()
            Log.d(TAG, "selectShoPoint--成功执行")
        } catch (e: Exception) {
            Log.d(TAG, "selectShoPoint--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {
            //db.close()
        }
        return result
    }

    fun  selectAvgPoint(subject: String): Double {
        var point =0.0
        var num = 0.0
        val db = workhelper!!.readableDatabase    //获得数据库
        try {
            val cursor = db.query("HistoryWork", arrayOf("totalpoint"),"Subject=?", arrayOf(subject), null, null, null)
            while (cursor.moveToNext()) {
                point += cursor.getDouble(cursor.getColumnIndex("totalpoint"))
                num+=1.0
            }
            cursor.close()
            db.setTransactionSuccessful()
            Log.d(TAG, "selectShoPoint--成功执行")
        } catch (e: Exception) {
            Log.d(TAG, "selectShoPoint--SQL错误")
            Log.e(TAG, "", e.cause)
        } finally {
            db.endTransaction()
            //db.close()
        }
        return point/num
    }
}