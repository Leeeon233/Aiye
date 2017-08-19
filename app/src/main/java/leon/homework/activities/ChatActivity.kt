package leon.homework.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_chat.*
import leon.homework.app.AppContext
import leon.homework.R
import leon.homework.Utils.DataUtils
import leon.homework.Utils.YunbaUtil
import leon.homework.adapter.MsgAdapter
import leon.homework.data.Const
import leon.homework.fragments.MessageFragment
import leon.homework.javaBean.Msg
import leon.homework.sqlite.ChatDao
import org.jetbrains.anko.info
import org.json.JSONObject
import java.util.*

class ChatActivity : BaseActivity(), View.OnClickListener {
    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_chat_finish->{
                ChatDao.getInstance().unreadnumclear(useralia!!)
                val intent = Intent()
                intent.action = Const.UPDATE_MAINACTIVITY_UI
                AppContext.instance!!.sendBroadcast(intent)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        ChatDao.getInstance().unreadnumclear(useralia!!)
        val intent = Intent()
        intent.action = Const.UPDATE_MAINACTIVITY_UI
        AppContext.instance!!.sendBroadcast(intent)
        finish()
    }

    override val layoutResourceId: Int = R.layout.activity_chat
    private var useralia :String? = null
    private var msgAdapter: MsgAdapter? = null
    var msgList = ArrayList<Msg>()
    //private var shownum:Int?  = null
    val messagereceiver = getMessageReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        registerBroadCast()
        btn_chat_finish.setOnClickListener(this)
        useralia = intent.getStringExtra("UserName")
        val title = intent.getStringExtra("title")
        chat_title.text = title
        msgAdapter = MsgAdapter(this, R.layout.msg_item, msgList)
        msg_list_view.adapter = msgAdapter
        updateview()
        send!!.setOnClickListener({
            val content = input_text.text.toString()
            if ("" != content) {
                val time = DataUtils.getTodayDateTime().substring(11,16)
                val msg = Msg(content, Msg.TYPE_SENT,time)
                msgList.add(msg)
                msgAdapter!!.notifyDataSetChanged()    //当有新消息时刷新ListView中的显示
                msg_list_view.setSelection(msgList.size)   //将ListView定位到最后一行
                ChatDao.getInstance().insertMsg(useralia!!,msg)
                ChatDao.getInstance().updateChat(useralia!!,content,time)
                input_text.setText("")
                MessageFragment.getInstance().updateview()
                val js = JSONObject()
                js.put("ACTION", Const.ACTION_CHAT)
                js.put("content",content)
                js.put("time",time)
                js.put("user", AppContext.User.alia)
                YunbaUtil.publishToAlia(useralia!!,js.toString())
                info(js)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        ChatDao.getInstance().unreadnumclear(useralia!!)
        MessageFragment.getInstance().updateview() //看到消息后刷新
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(messagereceiver)
    }

    private fun updateview() {
        msgList.clear()
        msgList.addAll(ChatDao.getInstance().selectMsg(useralia!!))
        msgAdapter!!.notifyDataSetChanged()    //当有新消息时刷新ListView中的显示
        msg_list_view.setSelection(msgList.size)   //将ListView定位到最后一行
    }
    fun registerBroadCast(){
        val intentFilter = IntentFilter()
        intentFilter.addAction(Const.ACTION_CHAT)
        registerReceiver(messagereceiver,intentFilter)
    }
    inner class getMessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateview()
        }
    }
}
