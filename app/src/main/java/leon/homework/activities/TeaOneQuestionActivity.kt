package leon.homework.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.view.View
import kotlinx.android.synthetic.main.activity_tea_one_question.*
import leon.homework.R
import leon.homework.adapter.FragmentAdapter
import leon.homework.fragments.ChoiFragment
import leon.homework.fragments.ShortFragment
import leon.homework.javaBean.ChoiceExercise
import leon.homework.javaBean.ShortExercise
import leon.homework.sqlite.WorkDao
import leon.homework.sqlite.WorkQuesDao
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class TeaOneQuestionActivity : BaseActivity(),View.OnClickListener {
    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_work_sumbit ->{
                finish()
            }
            R.id.btn_ques_back ->{
                finish()
            }
        }
    }

    /*private fun showSumbitDialog(){
        val normalDialog = AlertDialog.Builder(this@TeaOneQuestionActivity)
        normalDialog.setTitle("确认提交").setMessage("还有未批改的题目，请确认后再提交？")
        normalDialog.setNeutralButton("好",
                { dialog, which ->
                    dialog.cancel()
                })
        // 创建实例并显示
        normalDialog.show()
    }*/

    override val layoutResourceId: Int = R.layout.activity_tea_one_question
    private val choiceExercises = ArrayList<ChoiceExercise>()
    private val shortExercises = ArrayList<ShortExercise>()
    private val fragmentList = ArrayList<Fragment>()
    private var choiNum: Int = 0
    private var tolNum: Int = 0
    private var shortNum: Int = 0
    private var workid:String = ""
    private var correctnum = 0
    private var stuid:String?=null
    private var mid:String?=null
    private var vp: ViewPager?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        val defaultCurrent = intent.getIntExtra("number", 0) % 1000-1
        val jString = intent.getStringExtra("js")
        stuid = intent.getStringExtra("stuid")
        mid = intent.getStringExtra("mid")
        workid = JSONObject(jString).getString("workid")
        time.text = WorkDao.getInstance().selectRelationTime(mid!!)
        vp = findViewById(R.id.ques_viewpager) as ViewPager
        initJson(jString)
        tolNum = choiNum+shortNum
        initview(defaultCurrent)
        vp!!.adapter.notifyDataSetChanged()
    }

    private fun initview(defaultCurrent:Int){
        val fragmentManager = supportFragmentManager
        val adapter = FragmentAdapter(fragmentManager,fragmentList)
        vp!!.adapter = adapter
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
        quesNoTol.text = "共"+ tolNum +"题"
    }

    private fun initJson(jString: String) {
        var qnum=1
        try {
            val choJson: JSONObject
            val shoJson: JSONObject
            val choContent: JSONObject
            val shorContent: JSONObject
            val exerciseJson = JSONObject(jString)
            choJson = exerciseJson.getJSONObject("cho")
            shoJson = exerciseJson.getJSONObject("sho")
            choiNum = choJson.getInt("num")
            shortNum = shoJson.getInt("num")
            if (shortNum > 0) {
                shorContent = shoJson.getJSONObject("content")
                for (i in 1..shortNum) {
                    val shortId = shorContent.getString(i.toString())
                    val shortExercise = WorkQuesDao.getInstance().selectSho(shortId)
                    shortExercise.quesnum = qnum++
                    shortExercise.result = WorkDao.getInstance().selectMidResult(mid!!,shortId)
                    val bundle = Bundle()
                    bundle.putParcelable("object", shortExercise)
                    bundle.putString("workid", workid)
                    bundle.putString("stuid",stuid)
                    bundle.putString("mid",mid)
                    bundle.putString("quesid", shortId)
                    val shortFragment = ShortFragment(object :ShortFragment.Shortlistener{
                        override fun ShortResult(qnum: Int, result: String) {
                            correctnum++
                        }

                    })
                    shortFragment.arguments = bundle
                    shortExercises.add(shortExercise)
                    fragmentList.add(shortFragment)
                }
            }
            if (choiNum > 0) {
                choContent = choJson.getJSONObject("content")
                for (i in 1..choiNum) {
                    val choId = choContent.getString(i.toString())
                    val choiceExercise = WorkQuesDao.getInstance().selectChoice(choId)
                    choiceExercise.result = WorkDao.getInstance().selectMidResult(mid!!,choId)
                    choiceExercise.quesnum = qnum++
                    val bundle = Bundle()
                    bundle.putParcelable("object", choiceExercise)
                    val choiceFragment = ChoiFragment(object :ChoiFragment.Choilistener{
                        override fun ChoiResult(qnum: Int, result: String) {

                        }

                    })
                    choiceFragment.arguments = bundle
                    choiceExercises.add(choiceExercise)
                    fragmentList.add(choiceFragment)
                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}
