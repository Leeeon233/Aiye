package leon.homework.broadcast

import android.content.Intent
import leon.homework.app.AppContext
import leon.homework.Utils.HttpUtils
import leon.homework.Utils.Utils
import leon.homework.data.Const
import leon.homework.data.SaveData
import leon.homework.fragments.CorrectFragment
import leon.homework.fragments.MessageFragment
import leon.homework.fragments.WriteFragment
import leon.homework.javaBean.Msg
import leon.homework.javaBean.TodayWork
import leon.homework.sqlite.ChatDao
import leon.homework.sqlite.WorkDao
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.async
import org.jetbrains.anko.info
import org.jetbrains.anko.uiThread
import org.json.JSONObject

/*
 * Created by leon on 2017/8/4.
 */
class BroadcastHelper :AnkoLogger{
    fun sendBroadCast(js:JSONObject){
        val action = js.get("ACTION")
        when(action){
            Const.ACTION_RECEIVE_HOMEWORK->{    //学生接收作业
                receiveHomework(js)
            }
            Const.ACTION_TOGET_STU_HOMEWORK->{
                getStuHomeWork(js)
            }
            Const.ACTION_CHAT->{
                chat(js)
            }
            Const.ACTION_GET_POINT ->{
                val workid = js.getString("workid")
                val total = js.getInt("totalpoint")
                val result = js.getJSONObject("result")
                var quesid :String ?=null
                var point = 0
                for( i in 1..result.length()){
                    quesid = result.getJSONObject("$i").getString("quesid")
                    point = result.getJSONObject("$i").getInt("point")
                    WorkDao.getInstance().updateStuPoint(workid,quesid,point)
                }
                WorkDao.getInstance().updateStuTolPoint(workid,total)
            }
            Const.ACTION_SYSTEM->{

            }
        }
    }

    private fun  getTeaHomeWork(js: JSONObject) {
        val mid = js.getString("id")

    }

    /*
     * 聊天
     */
    private fun chat(js:JSONObject){
        val mmsg = Msg(js.getString("content"), Msg.TYPE_RECEIVED,js.getString("time"))
        ChatDao.getInstance().updateChat(js.getString("user"),js.getString("content"),js.getString("time"))
        ChatDao.getInstance().unreadnumadd(js.getString("user"))
        ChatDao.getInstance().insertMsg(js.getString("user"),mmsg)
        val intent = Intent()
        if(Utils.isActivityRunning(AppContext.instance!!,"MainActivity")){
            intent.action = Const.ACTION_CHAT
            AppContext.instance!!.sendBroadcast(intent)
            intent.action = Const.UPDATE_MAINACTIVITY_UI
            AppContext.instance!!.sendBroadcast(intent)
            MessageFragment.getInstance().updateview()
        }
    }
    /*
     * 学生，教师接收作业
     */
    private fun receiveHomework(js: JSONObject){
        info("收到作业 ")
        val subject = js.getString("subject")
        val deadline = js.getString("deadline")
        val workid = js.getString("workid")
        js.remove("ACTION")
        js.remove("subject")
        js.remove("deadline")
        val work= TodayWork(subject, deadline,js)
        WorkDao.getInstance().insertAWork(work,workid)
        if(Utils.isActivityRunning(AppContext.instance!!,"MainActivity")){
            if(SaveData.UserType == Const.ISSTUDENT)
                WriteFragment.getInstance().updateview()
            else if (SaveData.UserType == Const.ISTEACHER)
                CorrectFragment.getInstance().updateview()
        }
    }
    /*
     * 获取学生完成的作业
     */
    private fun getStuHomeWork(js: JSONObject){
        val mid = js.getString("id")
        val mjs = JSONObject()
        mjs.put("MId",mid)
        async {
            var jss =  HttpUtils.GetStuHomeWork(mjs)
            jss = jss.subSequence(7,jss.lastIndex-1).toString()
            uiThread {
                val s = JSONObject(jss)
                info(s)
                WorkDao.getInstance().insertRelationalTable(s)
            }
        }
    }
}