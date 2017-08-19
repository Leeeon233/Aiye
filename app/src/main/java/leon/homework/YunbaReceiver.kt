package leon.homework

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.yunba.android.manager.YunBaManager
import leon.homework.Utils.ActivityCollector
import leon.homework.Utils.Utils
import leon.homework.Utils.YunbaUtil
import leon.homework.activities.MainActivity
import leon.homework.broadcast.BroadcastHelper
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
import org.jetbrains.anko.info
import org.json.JSONException
import org.json.JSONObject

/*
 * Created by BC on 2017/2/4 0004.
 */

class YunbaReceiver : BroadcastReceiver(), AnkoLogger {
    override fun onReceive(context: Context, intent: Intent) {
        if (YunBaManager.MESSAGE_RECEIVED_ACTION == intent.action) {
            val topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC)
            val msg = intent.getStringExtra(YunBaManager.MQTT_MSG)
            val showMsg = "Received message from server: " + YunBaManager.MQTT_TOPIC +
                    " = " + topic + " " +
                    YunBaManager.MQTT_MSG + " = " + msg
            Log.d("YUNBARECEIVER", showMsg)
            //test

            if(SaveData.isShowNotifation)
            YunbaUtil.showNotifation(context, topic, msg)
            SendBroadcast(msg)


            /*val mintent = Intent() // Itent就是我们要发送的内容
            mintent.action = "AddMsg" // 设置你这个广播的action
            mintent.putExtra("content", msg)
            AppContext.instance!!.sendBroadcast(mintent) // 发送广播*/
            //上报显示通知栏状态， 以方便后台统计
            /*if (flag)
                YunBaManager.report(context, REPORT_MSG_SHOW_NOTIFICARION, topic)
            else
                YunBaManager.report(context, REPORT_MSG_SHOW_NOTIFICARION_FAILED, topic)*/
        } else if (YunBaManager.PRESENCE_RECEIVED_ACTION == intent.action) {
            //msg from presence.
            val topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC)
            val payload = intent.getStringExtra(YunBaManager.MQTT_MSG)
            val showMsg = "Received message presence: " + YunBaManager.MQTT_TOPIC +
                    " = " + topic + " " +
                    YunBaManager.MQTT_MSG + " = " + payload
            Log.d("DemoReceiver", showMsg)

        }
    }

    fun SendBroadcast(msg:String) {
        val js = JSONObject(msg)
        BroadcastHelper().sendBroadCast(js)
    }
}
