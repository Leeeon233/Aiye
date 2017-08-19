package leon.homework.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import io.yunba.android.manager.YunBaManager
import kotlinx.android.synthetic.main.activity_settings.*
import leon.homework.app.AppContext

import leon.homework.R
import leon.homework.data.Const
import leon.homework.data.SaveData
import org.jetbrains.anko.onCheckedChange
import org.jetbrains.anko.onClick
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class SettingsActivity : BaseActivity(),AnkoLogger {
    override val layoutResourceId: Int = R.layout.activity_settings//To change initializer of created properties use File | Settings | File Templates.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        val listView = findViewById(R.id.list_view) as ListView
        val items = arrayOf("意见反馈", "关于艾叶", "联系我们")
        val adapter = ArrayAdapter(this@SettingsActivity, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    val intent = Intent(this@SettingsActivity, SuggestActivity::class.java)
                    startActivity(intent)
                }
                1 -> {
                    val intent1 = Intent(this@SettingsActivity, AboutActivity::class.java)
                    startActivity(intent1)
                }
                2 -> {
                    val intent2 = Intent(this@SettingsActivity, ConnectActivity::class.java)
                    startActivity(intent2)
                }
            }
        }
        push_btn.isChecked = SaveData.isShowNotifation
        push_btn.textOff = "关"
        push_btn.textOn = "开"
        push_btn.onCheckedChange { compoundButton, isChecked ->
            SaveData.isShowNotifation = isChecked
        }
        btn_settings_finish.onClick {
            finish()
        }
        btn_settings_exit.onClick {
            SaveData.isLogined = false
            SaveData.isShowNotifation = true
            startActivity(Intent(this@SettingsActivity,LoginActivity::class.java))
            val intent = Intent()
            intent.action = Const.ACTION_FINISH_MAIN
            AppContext.instance!!.sendBroadcast(intent)
            if(SaveData.UserType == Const.ISSTUDENT){
                YunBaManager.unsubscribe(applicationContext, SaveData.cls,
                        object : IMqttActionListener {

                            override fun onSuccess(asyncActionToken: IMqttToken) {
                                info("取消订阅成功")
                            }

                            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                                if (exception is MqttException) {
                                    val msg = "unSubscribe failed with error code : " + exception.reasonCode
                                    info("取消订阅失败：$msg")
                                }
                            }
                        }
                )
            }

            finish()
        }
    }
}
