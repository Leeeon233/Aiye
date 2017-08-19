package leon.homework.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import leon.homework.app.AppContext
import leon.homework.javaBean.ShortExercise
import leon.homework.R
import leon.homework.Utils.Utils
import leon.homework.data.Const
import leon.homework.activities.TuyaActivity
import leon.homework.data.SaveData
import leon.homework.sqlite.WorkDao
import org.jetbrains.anko.enabled
import org.jetbrains.anko.info
import java.io.File


class ShortFragment(val mlistener:Shortlistener) : BaseFragment() {
    override val layoutResourceId: Int = R.layout.layout_short

    private var isfinished = 0
    private var imgview:ImageView?=null
    private var workid:String?=null
    private var quesid:String?=null
    private var stuid:String?=null
    private var linePoint :LinearLayout ?=null
    private var qnum:Int?=null
    var path = Const.DATA_PATH + workid + "/" + quesid + ".png"
    private var title:TextView?=null
    private val changeimgreceiver = changeImgReceiver()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutResourceId, container, false)
        val bundle = arguments
        val shortExercise = bundle.getParcelable<ShortExercise>("object")
        val mtitle = shortExercise.title
        val imgs = shortExercise.imgname
        title = view.findViewById(R.id.short_question) as TextView
        title!!.text = Utils.textspan(AppContext.instance!!,mtitle,imgs)
        workid = bundle.getString("workid")
        quesid = bundle.getString("quesid")
        stuid =  bundle.getString("stuid")
        registerBroadCast()
        qnum = shortExercise.quesnum
        imgview = view.findViewById(R.id.short_img) as ImageView
        when(SaveData.UserType){
            Const.ISSTUDENT ->{
                stuinit(shortExercise,view)
            }
            Const.ISTEACHER ->{
                teainit(view)
            }
        }
        return view
    }

    private fun teainit(view: View){
        linePoint = view.findViewById(R.id.point) as LinearLayout
        linePoint!!.visibility = View.VISIBLE
        imgview!!.isClickable = false
        val mid = WorkDao.getInstance().selectMid(workid,stuid!!)
        val etpoint = view.findViewById(R.id.et_point) as EditText
        etpoint.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable) {
                if(p0.toString().toInt()<0 || p0.toString().toInt()>10){
                    etpoint.setText("")
                }else{
                    WorkDao.getInstance().updatePoint(mid,quesid!!,p0.toString().toInt())
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
        path = Const.DATA_PATH+"$mid/$quesid.png"
        if(File(path).exists()){
            val bitmap = BitmapFactory.decodeFile(path)
            imgview!!.setImageBitmap(bitmap)
        }

    }

    private fun stuinit(shortExercise:ShortExercise,view: View){
        imgview!!.setOnClickListener {
            val intent = Intent(activity,TuyaActivity::class.java)
            intent.putExtra("workid",workid)
            intent.putExtra("quesid",quesid)
            startActivity(intent)
        }
        path = Const.DATA_PATH + workid + "/" + quesid + ".png"
        isfinished = shortExercise.isfinished
        if(1==isfinished){

            showAnswer()
            imgview!!.isClickable = false
            if(WorkDao.getInstance().selectStuIsCorrected(workid!!)){
                val point = view.findViewById(R.id.point) as LinearLayout
                point.visibility = View.VISIBLE
                val et = view.findViewById(R.id.et_point) as EditText
                et.enabled = false
                et.setText(WorkDao.getInstance().selectShoPoint(workid!!,quesid!!))

            }

        }
    }

    override fun onResume() {
        super.onResume()
        showAnswer()
    }

    private fun showAnswer(){

        info(File(path).exists())
        if(File(path).exists()){
            val bitmap = BitmapFactory.decodeFile(path)
            imgview!!.setImageBitmap(bitmap)
            info("展示图片2")
        }
    }

    interface Shortlistener {
        fun ShortResult(qnum:Int,result: String)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity.unregisterReceiver(changeimgreceiver)
    }
    fun registerBroadCast(){
        val intentFilter = IntentFilter()
        intentFilter.addAction(workid)
        activity.registerReceiver(changeimgreceiver,intentFilter)
    }
    inner class changeImgReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            showAnswer()
            mlistener.ShortResult(qnum!!,Const.DATA_PATH + workid + "/" + quesid + ".png")
        }
    }
}
