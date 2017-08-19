package leon.homework.Utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import io.yunba.android.manager.YunBaManager
import leon.homework.app.AppContext
import leon.homework.R
import leon.homework.activities.MainActivity
import leon.homework.data.Const
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttException
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.json.JSONObject
import java.util.*
import android.content.ComponentName





/**
 * Created by BC on 2017/2/4 0004.
 */

object YunbaUtil:AnkoLogger{
    private fun isEmpty(s: String?): Boolean {
        return null == s || s.isEmpty() || s.trim { it <= ' ' }.isEmpty()
    }
    fun publishToAlia(alias:String,msg: String){
        val js = JSONObject(msg)
        YunBaManager.publishToAlias(AppContext.instance, alias, js.toString(),
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        info("向$alias 发送消息成功")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        info("向$alias 发送消息失败")
                    }
                }
        )
    }
    fun publish(topic: String,msg: String){
        YunBaManager.publish(AppContext.instance, topic, msg,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {

                    }

                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        if (exception is MqttException) {

                        }
                    }
                }
        )
    }
    fun showNotifation(context: Context, topic: String, msg: String): Boolean {
        try {
            val alarmSound = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val pattern = longArrayOf(500, 500, 500)
            var title:String?=null
            var msgcontent:String?=null
            var resultIntent:Intent?=null
            // Creates an explicit intent for an Activity in your app
            val js = JSONObject(msg)
            val action = js.getString("ACTION")
            info("action: "+action)
            when(action){
                Const.ACTION_RECEIVE_HOMEWORK->{
                    info(">>>>>>>>>>>收到作业 ")
                    title = "有一条新作业"
                    msgcontent = js.getString("title")
                    resultIntent = Intent(Intent.ACTION_MAIN)
                    resultIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    resultIntent.component = ComponentName(AppContext.instance, MainActivity::class.java)//用ComponentName得到class对象
                    resultIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    //resultIntent = Intent(context, MainActivity::class.java)
                    resultIntent.putExtra("message",Const.SHOW_WORK_FRAGMENT)
                }
                Const.ACTION_TOGET_STU_HOMEWORK ->{
                    info(">>>>>>>>有学生提交作业")
                    title = "有学生提交作业"
                    msgcontent = "请查看"
                    resultIntent = Intent(Intent.ACTION_MAIN)
                    resultIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    resultIntent.component = ComponentName(AppContext.instance, MainActivity::class.java)//用ComponentName得到class对象
                    resultIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    resultIntent.putExtra("message",Const.SHOW_WORK_FRAGMENT)
                }
                Const.ACTION_CHAT->{
                    info(">>>>>>>>有一条新消息")
                    title = "有一条新消息"
                    msgcontent = js.getString("content")
                    resultIntent = Intent(Intent.ACTION_MAIN)
                    resultIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    resultIntent.component = ComponentName(AppContext.instance, MainActivity::class.java)//用ComponentName得到class对象
                    resultIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    resultIntent.putExtra("message",Const.SHOW_MESSAGE_FRAGMENT)
                }
                Const.ACTION_GET_POINT ->{
                    info(">>>>>>>>你的作业已判完")
                    title = "你的作业已判完，请到历史作业查看成绩"
                    msgcontent = js.getString("stuname")
                    resultIntent = Intent(Intent.ACTION_MAIN)
                    resultIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    resultIntent.component = ComponentName(AppContext.instance, MainActivity::class.java)//用ComponentName得到class对象
                    resultIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    resultIntent.putExtra("message",Const.SHOW_MINE_FRAGMENT)
                }
                Const.ACTION_SYSTEM->{

                }
            }

            val pIntent = PendingIntent.getActivity(AppContext.instance,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT)
            val mBuilder = NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.icon)
                    .setContentTitle(title)//设置通知栏标题
                    .setContentText(msgcontent)
                    .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                    .setSound(alarmSound) //铃声
                    .setVibrate(pattern) //震动
                    .setContentIntent(pIntent)
            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.notify(Random().nextInt(), mBuilder.build())
/*
            // The stack builder object will contain an artificial back stack
            // for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads

            // of
            // your application to the Home screen.
            val stackBuilder = TaskStackBuilder.create(context)
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity::class.java)

            // Adds the Intent that starts the Activity to the top of the

            stackBuilder.addNextIntent(resultIntent)
            val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            mBuilder.setContentIntent(resultPendingIntent)//设置通知栏点击意图
            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // mId allows you to update the notification later on.
            val r = Random()
            mNotificationManager.notify(r.nextInt(), mBuilder.build())*/



        } catch (e: Exception) {
            return false
        }

        return true
    }
}
