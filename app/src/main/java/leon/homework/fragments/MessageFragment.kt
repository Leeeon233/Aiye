package leon.homework.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import leon.homework.R
import leon.homework.activities.ChatActivity
import leon.homework.adapter.MsgObjAdapter
import leon.homework.javaBean.MsgObject
import leon.homework.sqlite.ChatDao
import java.util.*




class MessageFragment : BaseFragment() {
    override val layoutResourceId: Int = R.layout.fragment_message
    private val msgObjList = ArrayList<MsgObject>()
    var adapter: MsgObjAdapter? = null
    //val messagereceiver = getMessageReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //registerBroadCast()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(layoutResourceId, container, false)
        adapter = MsgObjAdapter(activity, R.layout.message_item, msgObjList)
        val list_view_message = view.findViewById(R.id.list_view_message) as ListView
        list_view_message.adapter = adapter
        list_view_message.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val msgObject = msgObjList[position]
            if(!ChatDao.getInstance().tableisexist(msgObject.alia)){
                ChatDao.getInstance().CreateTable(msgObject.alia)
            }
            val intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra("UserName",msgObject.alia)
            intent.putExtra("title",msgObject.title)
            startActivity(intent)
        }
        updateview()
        return view
    }

    private fun init(){
        val sysMsg = MsgObject("系统消息","aiye","1","你好啊","14:42")
        val teaMsg = MsgObject("语文老师","1486870124129204","1","你好啊","14:42")//     1487738441727795
        ChatDao.getInstance().insertChat(sysMsg)
        ChatDao.getInstance().insertChat(teaMsg)
    }


    /*fun registerBroadCast(){
        val intentFilter = IntentFilter()
        intentFilter.addAction(Const.BROADCAST_CHAT)
        AppContext.instance!!.registerReceiver(messagereceiver,intentFilter)
    }
    inner class getMessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val msg = intent.extras.getString("content")
            val js = JSONObject(msg)

            Log.i("Recevier", "接收到:" + msg)
        }
    }*/
    fun updateview(){
        msgObjList.clear()
        msgObjList.addAll(ChatDao.getInstance().selectChat())
        adapter!!.notifyDataSetChanged()
    }

    companion object {
        var messageFragment :MessageFragment? =null
        fun getInstance(): MessageFragment {
            if(messageFragment==null){
                messageFragment = MessageFragment()
            }
            return messageFragment!!
        }
    }
}
