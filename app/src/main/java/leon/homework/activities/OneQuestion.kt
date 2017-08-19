package leon.homework.activities

import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.view.View
import kotlinx.android.synthetic.main.layout_fragment.*
import leon.homework.R
import leon.homework.adapter.FragmentAdapter
import leon.homework.fragments.ChoiFragment
import leon.homework.fragments.ShortFragment
import leon.homework.javaBean.ChoiceExercise
import leon.homework.javaBean.JudgExercise
import leon.homework.javaBean.ShortExercise
import leon.homework.sqlite.WorkDao
import leon.homework.sqlite.WorkQuesDao
import org.jetbrains.anko.info
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class OneQuestion : BaseActivity(),View.OnClickListener,ChoiFragment.Choilistener,ShortFragment.Shortlistener {

    override val layoutResourceId: Int = R.layout.layout_fragment
    private var finished:ArrayList<Int> = ArrayList()
    private val choiceExercises = ArrayList<ChoiceExercise>()
    private val shortExercises = ArrayList<ShortExercise>()
    private val fragmentList = ArrayList<Fragment>()
    private var choiNum: Int = 0
    private var tolNum: Int = 0
    private var shortNum: Int = 0
    private var workid:String = ""
    private var teacher:String = ""
    private var isfirst = true
    private val ResultJs:JSONObject = JSONObject()
    private var vp:ViewPager?=null
    private var isfinished:Boolean?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        val intent = intent
        val defaultCurrent = intent.getIntExtra("number", 0) % 1000-1
        val jString = intent.getStringExtra("exercise")
        workid = JSONObject(jString).getString("workid")
        isfinished = intent.getBooleanExtra("isfinished",false)
        vp = findViewById(R.id.ques_viewpager) as ViewPager
        initJson(jString)
        initview(defaultCurrent)
        vp!!.adapter.notifyDataSetChanged()
    }

    private fun showExitDialog() {
        val normalDialog = AlertDialog.Builder(this@OneQuestion)
        normalDialog.setTitle("确认退出").setMessage("需要保存已经完成的作业么？")
        normalDialog.setPositiveButton("保存",
                { dialog, which ->
                    updatedb(ResultJs)
                    dialog.cancel()
                    finish()
                })
        normalDialog.setNeutralButton("直接退出",
                { dialog, which ->
                    dialog.cancel()
                    finish()
                })
        // 创建实例并显示
        normalDialog.show()
    }

    private fun showSumbitDialog(){
        val normalDialog = AlertDialog.Builder(this@OneQuestion)
        normalDialog.setTitle("确认提交").setMessage("还有未完成的题目，仍要提交作业么？")
        normalDialog.setPositiveButton("提交",
                { dialog, which ->
                    updatedb(ResultJs)
                    //packWorkData()
                    dialog.cancel()
                    finish()
                })
        normalDialog.setNeutralButton("取消",
                { dialog, which ->
                    dialog.cancel()
                })
        // 创建实例并显示
        normalDialog.show()
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_ques_back->{
                showExitDialog()
            }
            R.id.btn_work_sumbit->{
                if(ResultJs.length()!=tolNum){
                    showSumbitDialog()
                }else{
                    pasueTime()
                    updatedb(ResultJs)
                    finish()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pasueTime()
    }

    override fun onBackPressed() {
        info(SystemClock.elapsedRealtime() - time.base)
    }

    private fun startTime(){
        time.base = SystemClock.elapsedRealtime()-WorkDao.getInstance().selectTime(workid)
        info("StartTime"+time.base)
        time.format = "0${((SystemClock.elapsedRealtime() - time.base) / 1000 / 60)}:%s"
        time.start()
    }

    private fun pasueTime(){
        time.stop()
        info("PauseTime"+(SystemClock.elapsedRealtime() - time.base))
        WorkDao.getInstance().updateTime(workid,SystemClock.elapsedRealtime() - time.base)
    }

    /*private fun packWorkData(){
        val choicJson = JSONObject()
        val judgeJson = JSONObject()
        val shortJson = JSONObject()
        val choicJSArray = JSONArray()
        val juageJSArray = JSONArray()
        val shortJSArray = JSONArray()
        var qnum=1
        for(i in 0..choiNum-1){
            val choiceExercise = choiceExercises[i]
            val js = JSONObject()
            js.put("quesid",choiceExercise.choId)
            js.put("result",ResultJs.get(qnum.toString()))
            choicJSArray.put(i,js)
            qnum++
        }
        for(i in 0..judgeNum-1){
            val judgExercise = judgExercises[i]
            val js = JSONObject()
            js.put("quesid",judgExercise.judgeId)
            js.put("result",ResultJs.get(qnum.toString()))
            juageJSArray.put(i,js)
            qnum++
        }
        for(i in 0..shortNum-1){
            val shortExercise = shortExercises[i]
            val js = JSONObject()
            js.put("quesid",shortExercise.shortId)
            js.put("result",ResultJs.get(qnum.toString()))
            shortJSArray.put(i,js)
            qnum++
        }
        choicJson.put("num",choiNum)
        choicJson.put("content",choicJSArray)
        judgeJson.put("num",judgeNum)
        judgeJson.put("content",juageJSArray)
        shortJson.put("num",shortNum)
        shortJson.put("content",shortJSArray)
        val resultJson = JSONObject()
        resultJson.put("workid",workid)
        resultJson.put("stualia",AppContext.stuUser!!.alia)
        resultJson.put("time",worktime)
        resultJson.put("choiceques",choicJson)
        resultJson.put("judgeques",judgeJson)
        resultJson.put("shortques",shortJson)
        info(resultJson)
    }*/

    private fun initview(defaultCurrent:Int){
        val fragmentManager = supportFragmentManager
        val adapter = FragmentAdapter(fragmentManager,fragmentList)
        vp!!.adapter = adapter
        //vp!!.offscreenPageLimit=tolNum    //TODO  BUG!!!
        btn_work_sumbit.setOnClickListener(this)
        btn_ques_back.setOnClickListener(this)
        vp!!.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                val num = position+1
                quesNo.text="第"+num+"题"
            }
        })
        vp!!.setCurrentItem(defaultCurrent, false)
        tolNum = choiNum+shortNum
        quesNoTol.text = "共"+ tolNum +"题"
        startTime()
        if(isfinished!!){
            btn_work_sumbit.visibility = View.GONE
            btn_ques_back.setOnClickListener { finish() }
            time.stop()
        }
    }

    private fun initJson(jString: String) {
        var qnum=1
        try {
            val choJson: JSONObject
            val shoJson: JSONObject
            val choContent: JSONObject
            val shorContent: JSONObject
            val exerciseJson = JSONObject(jString)
            teacher = exerciseJson.getString("teacher")
            isfirst = !WorkDao.getInstance().tableisexist(workid)
            choJson = exerciseJson.getJSONObject("cho")
            shoJson = exerciseJson.getJSONObject("sho")
            choiNum = choJson.getInt("num")
            shortNum = shoJson.getInt("num")
            if (choiNum > 0) {
                choContent = choJson.getJSONObject("content")
                for (i in 1..choiNum) {
                    val choId = choContent.getString(i.toString())
                    val choiceExercise = WorkQuesDao.getInstance().selectChoice(choId)
                    choiceExercise.quesnum = qnum++
                    if(!isfirst){
                        choiceExercise.isfinished = WorkDao.getInstance().selectIsfinished(workid,qnum-1)
                        info("isfinished:>>>"+choiceExercise.isfinished)
                        if(1==choiceExercise.isfinished){
                            choiceExercise.result = WorkDao.getInstance().selectResult(workid,qnum-1)
                            info("result:>>>"+choiceExercise.result)
                        }
                    }
                    val bundle = Bundle()
                    bundle.putParcelable("object", choiceExercise)
                    bundle.putBoolean("workfinished",WorkDao.getInstance().selectWorkIsfinished(workid))
                    val choiceFragment = ChoiFragment(this)
                    choiceFragment.arguments = bundle
                    choiceExercises.add(choiceExercise)
                    fragmentList.add(choiceFragment)
                }
            }
            if (shortNum > 0) {
                shorContent = shoJson.getJSONObject("content")
                for (i in 1..shortNum) {
                    val shortId = shorContent.getString(i.toString())
                    val shortExercise = WorkQuesDao.getInstance().selectSho(shortId)
                    shortExercise.quesnum = qnum++
                    if(!isfirst) {
                        shortExercise.isfinished = WorkDao.getInstance().selectIsfinished(workid, qnum - 1)
                        if (1 == shortExercise.isfinished) {
                            shortExercise.result = WorkDao.getInstance().selectResult(workid, qnum - 1)
                        }
                    }
                    val bundle = Bundle()
                    bundle.putParcelable("object", shortExercise)
                    bundle.putString("workid", workid)
                    bundle.putString("quesid", shortId)
                    bundle.putBoolean("workfinished",WorkDao.getInstance().selectWorkIsfinished(workid))
                    val shortFragment = ShortFragment(this)
                    shortFragment.arguments = bundle
                    shortExercises.add(shortExercise)
                    fragmentList.add(shortFragment)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun updatedb(result:JSONObject){
        WorkDao.getInstance().updateResult(workid,tolNum,result)
    }

    override fun ShortResult(qnum: Int, result: String) {
        //vp!!.currentItem+=1
        info(result)
        if(!finished.contains(qnum)){
            finished.add(qnum)
        }
        if(ResultJs.has(qnum.toString())){
            if(ResultJs.getString(qnum.toString()) != result){
                ResultJs.remove(qnum.toString())
                ResultJs.put(qnum.toString(),result)
            }
        }else{
            ResultJs.put(qnum.toString(),result)
        }
    }



    override fun ChoiResult(qnum: Int, result: String) {
        //vp!!.currentItem+=1
        if(!finished.contains(qnum)){
            finished.add(qnum)
        }
        if(ResultJs.has(qnum.toString())){
            if(ResultJs.getString(qnum.toString()) != result){
                ResultJs.remove(qnum.toString())
                ResultJs.put(qnum.toString(),result)
            }
        }else{
            ResultJs.put(qnum.toString(),result)
        }
    }

}
