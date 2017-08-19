package leon.homework.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import kotlinx.android.synthetic.main.activity_correct.*
import leon.homework.app.AppContext
import leon.homework.R
import leon.homework.Utils.HttpUtils
import leon.homework.Utils.Utils
import leon.homework.Utils.YunbaUtil
import leon.homework.data.Const
import leon.homework.sqlite.WorkDao
import org.jetbrains.anko.async
import org.jetbrains.anko.enabled
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class CorrectActivity : BaseActivity(), View.OnClickListener{
    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_correct_back ->{
                finish()
            }
            R.id.btn_listen -> {
                createAudioPlayer(File(radiopath)).start()
            }
            R.id.btn_correct_sumbit -> {
                WorkDao.getInstance().updateStuPoint(workid!!,"100",et_correct_paperwork.text.toString().toInt())
                WorkDao.getInstance().updateStuPoint(workid!!,"200",et_correct_radiowork.text.toString().toInt())
                val js = WorkDao.getInstance().selectCorrectResult(mid!!,workid!!)
                YunbaUtil.publishToAlia(stuid!!,js.toString())
                async {
                    HttpUtils.CorrectWork(js)
                }
                toast("提交成功")
                finish()
            }
        }
    }

    override val layoutResourceId: Int = R.layout.activity_correct
    private var js = JSONObject()
    private var stuid :String?=null
    private var mid :String ?=null
    private var choiNum: Int = 0
    private var shortNum: Int = 0
    private var workid:String? =null
    private var paperpath :String?=null
    private var radiopath :String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        js = JSONObject(intent.getStringExtra("js"))
        stuid = intent.getStringExtra("stuid")
        mid = intent.getStringExtra("mid")

        paperpath = Const.DATA_PATH+"$mid/pwork.png"
        radiopath = Const.DATA_PATH+"$mid/rwork.amr"
        initJson(js)
        initView()
    }

    private fun initJson(js:JSONObject){
        workid = js.getString("workid")
        val choJson = js.getJSONObject("cho")
        val shoJson = js.getJSONObject("sho")
        choiNum = choJson.getInt("num")
        shortNum = shoJson.getInt("num")
    }

    private fun initView(){
        btn_correct_back.setOnClickListener(this)
        btn_listen.setOnClickListener(this)
        btn_correct_sumbit.setOnClickListener(this)
        val paperfile = File(paperpath)
        if(paperfile.exists()){
            val bit = BitmapFactory.decodeFile(paperpath)
            val mbit = Utils.zoomBitmap(bit,iv_paper_work.width,iv_paper_work.width*bit.height/bit.width)
            iv_paper_work.setImageBitmap(mbit)
        }
        val file = File(radiopath)
        if(file.exists()) {
            btn_listen.text = "播放"
        }else{
            btn_listen.text = "无录音"
            btn_listen.enabled = false
        }
        tv_work_paper.text = js.getString("paperwork")
        tv_work_recoder.text = js.getString("radiowork")
        correctWorkTitle.text = js.getString("title")
        loadButtons(choiNum, shortNum)
    }

    @Throws(IOException::class)
    private fun createAudioPlayer(audioFile: File): MediaPlayer {
        val mPlayer = MediaPlayer()
        val fis = FileInputStream(audioFile)
        mPlayer.reset()
        mPlayer.setDataSource(fis.fd)
        mPlayer.prepare()
        return mPlayer
    }

    fun loadButtons(choiNum: Int, shortNum: Int) {
        createButton(choiNum, 2001, 0, R.id.GridChoi)
        createButton(shortNum, 3001 + choiNum, choiNum, R.id.GridShort)
    }
    fun createButton(num: Int, tag: Int, before: Int, layout: Int) {
        val GdLayout: GridLayout = findViewById(layout) as GridLayout
        GdLayout.removeAllViews()
        val Btn = arrayOfNulls<Button>(num)
        for (i in 0..num-1) {
            Btn[i] = Button(this)
            Btn[i]!!.setPadding(0, 0, 3, 3)
            Btn[i]!!.tag = tag + i
            Btn[i]!!.text = (i + 1 + before).toString()
            Btn[i]!!.textSize = 18f
            if(WorkDao.getInstance().selectTeaCorIsfinished(mid!!,(tag+i)%1000)==0){
                val xrp = resources.getXml(R.drawable.selector_textcolor)
                try {
                    val csl = ColorStateList.createFromXml(resources, xrp)
                    Btn[i]!!.setTextColor(csl)
                } catch (e: Exception) {
                    info(e.message)
                }
                Btn[i]!!.setBackgroundResource(R.drawable.selector_button_green_cir)
            }else{
                try {
                    Btn[i]!!.setTextColor(Color.WHITE)
                } catch (e: Exception) {
                    info(e.message)
                }
                Btn[i]!!.background = ContextCompat.getDrawable(this, R.drawable.item_deepgreen_cir)
            }

            Btn[i]!!.setOnClickListener({ v ->
                val intent = Intent(this, TeaOneQuestionActivity::class.java)
                intent.putExtra("number", v.tag.toString().toInt())
                intent.putExtra("js", js.toString())
                intent.putExtra("mid",mid)
                intent.putExtra("stuid",stuid)
                toast(v.tag.toString())
                startActivity(intent)
            })
            val btParams = GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED,1), GridLayout.spec(GridLayout.UNDEFINED,1))  //设置按钮的宽度和高度
            btParams.leftMargin = Utils.dp2px(AppContext.instance!!,24.toFloat())    //横坐标定位
            btParams.topMargin = Utils.dp2px(AppContext.instance!!,16.toFloat())
            btParams.width=Utils.dp2px(AppContext.instance!!,64.toFloat())
            btParams.height=Utils.dp2px(AppContext.instance!!,64.toFloat())
            GdLayout.addView(Btn[i],btParams)
        }
    }

}
