package leon.homework.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import leon.homework.R
import leon.homework.activities.ExerciseActivity
import leon.homework.adapter.SubjectAdapter
import leon.homework.data.Const
import leon.homework.javaBean.TodayWork
import leon.homework.sqlite.WorkDao
import org.jetbrains.anko.async
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*




class WriteFragment : BaseFragment(),SwipeRefreshLayout.OnRefreshListener {

    override val layoutResourceId: Int =R.layout.fragment_write
    private var subjectList = ArrayList<TodayWork>()
    private var swiperefresh: SwipeRefreshLayout? = null
    var objAdapter:SubjectAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        objAdapter = SubjectAdapter(activity, R.layout.works_list_item, subjectList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(layoutResourceId, container, false)

        val listview = view.findViewById(R.id.list_view) as ListView
        subjectList.clear()
        swiperefresh = view.findViewById(R.id.SwipeRL_write) as SwipeRefreshLayout
        swiperefresh!!.setOnRefreshListener(this)
        listview.setOnItemClickListener({ parent, view, position, id ->
            val subject = subjectList[position]
            info(subject.subjectName)
            val intent = Intent(activity, ExerciseActivity::class.java)
            intent.putExtra("exercise",subject.js.toString())
            startActivity(intent)
        })
        objAdapter!!.setNotifyOnChange(true)
        listview.adapter = objAdapter
        updateview()
        return view
    }

    fun addSubject(js:JSONObject){
        val subject = js.getString("subject")
        val deadline = js.getString("deadline")
        val workid = js.getString("workid")
        js.remove("subject")
        js.remove("deadline")
        val work= TodayWork(subject, deadline,js)
        WorkDao.getInstance().insertAWork(work,workid)
        subjectList.add(work)
        objAdapter!!.notifyDataSetChanged()
        info("addSubject")
    }

    fun updateview(){
        subjectList.clear()
        subjectList.addAll(WorkDao.getInstance().select1())
        objAdapter!!.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        updateview()
    }

    override fun onRefresh() {
        async() {
            val jjjsc = JSONObject()
            jjjsc.put("1","2207011012")
            jjjsc.put("2","2207011020")
            jjjsc.put("3","2207011006")
            val jjjsr = JSONObject()
            jjjsr.put("1","2207012003")
            val jjs1 = JSONObject()
            jjs1.put("num",3)
            jjs1.put("content",jjjsc)
            val jjs2 = JSONObject()
            jjs2.put("num",1)
            jjs2.put("content",jjjsr)
            val js = JSONObject()
            js.put("ACTION",Const.ACTION_RECEIVE_HOMEWORK)
            js.put("subject","英语")
            js.put("teacher","1490426851079272")
            js.put("deadline","2017-02-20")
            val formatter = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ")
            val curDate = Date(System.currentTimeMillis())//获取当前时间
            val str = formatter.format(curDate)
            js.put("title","2017-02-20 英语作业")
            js.put("workid","m1488460032822")
            js.put("cho",jjs1)
            js.put("sho",jjs2)
            js.put("paperwork","五三p100~102")
            js.put("radiowork","背诵陋室铭")
            info(js)
            swiperefresh!!.isRefreshing = false
            uiThread {
                addSubject(js)
                toast("停")
            }
        }
    }
    companion object {
        var writefragment:WriteFragment? = null
        fun getInstance(): WriteFragment {
            if(writefragment==null){
                writefragment = WriteFragment()
            }
            return writefragment!!
        }
    }
}
