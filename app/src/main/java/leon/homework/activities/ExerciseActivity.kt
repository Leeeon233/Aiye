package leon.homework.activities

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.bm.library.PhotoView
import kotlinx.android.synthetic.main.work_enter.*
import leon.homework.app.AppContext
import leon.homework.R
import leon.homework.Utils.*
import leon.homework.data.Const
import leon.homework.fragments.WriteFragment
import leon.homework.javaBean.WorkResult
import leon.homework.sqlite.WorkDao
import org.jetbrains.anko.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class ExerciseActivity : BaseActivity() ,View.OnClickListener{
    override val layoutResourceId: Int = R.layout.work_enter
    private var mAudioRecoderUtils: AudioRecoderUtils? = null
    private var tempUri: Uri? = null
    val TAKE_PICTURE = 1
    var mRecorder: MediaRecorder?=null
    private var choiNum: Int = 0
    private var shortNum: Int = 0
    private var jString:JSONObject? = null
    private var workid:String? =null
    private var mImageView: ImageView? = null
    private var photoview :PhotoView? =null
    private var mTextView: TextView? = null
    private var radiopath:String? = null
    private var paperpath:String? = null
    private var teacher:String?=null
    private var soundFile:File ?=null
    private var isfinished :Boolean ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        photoview = findViewById(R.id.iv_paper_work) as PhotoView
        val intent = intent
        jString =JSONObject(intent.getStringExtra("exercise"))
        isfinished = intent.getBooleanExtra("isfinished",false)
        initJson(jString!!)
        paperpath = Environment.getExternalStorageDirectory().absolutePath+"/"+workid+"/"+"$workid.png"
        initView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) { // 如果返回码是可以用的
            when (requestCode) {
                TAKE_PICTURE -> {
                    val file = File(paperpath)
                    val bit = BitmapFactory.decodeFile(file.absolutePath)
                    val mbit = Utils.zoomBitmap(bit,photoview!!.width,photoview!!.width*bit.height/bit.width)
                    photoview!!.setImageBitmap(mbit)
                }
            }
        }
    }

    private fun initView(){
        btn_workenter_back.setOnClickListener(this)
        btn_paper_work.setOnClickListener(this)
        btn_workenter_sumbit.setOnClickListener(this)
        btn_listen.setOnClickListener(this)

        val paperfile = File(paperpath)
        if(!paperfile.parentFile.exists()){
            paperfile.parentFile.mkdirs()
        }
        if(paperfile.exists()){
            val bit = BitmapFactory.decodeFile(paperfile.absolutePath)
            val mbit = Utils.zoomBitmap(bit,bit.width,bit.height)
            photoview!!.setImageBitmap(mbit)
        }

        val file = File(radiopath!!)
        if(file.exists()) {
            btn_listen.text = "播放"
        }
        tv_work_paper.text=jString!!.getString("paperwork")
        tv_work_recoder.text = jString!!.getString("radiowork")
        val linear:LinearLayout = findViewById(R.id.work_linear) as LinearLayout
        enterWorkTitle.text = jString!!.getString("title")
        loadButtons(choiNum, shortNum)
        val view = View.inflate(this, R.layout.layout_microphone, null)
        val mPop = PopupWindowFactory(this, view)
        mImageView =  view.findViewById(R.id.iv_recording_icon) as ImageView
        mTextView =  view.findViewById(R.id.tv_recording_time) as TextView
        mAudioRecoderUtils = AudioRecoderUtils(Const.DATA_PATH+workid)

        mAudioRecoderUtils!!.setOnAudioStatusUpdateListener(object : AudioRecoderUtils.OnAudioStatusUpdateListener {

            //录音中....db为声音分贝，time为录音时长
            override fun onUpdate(db: Double, time: Long) {
                mImageView!!.drawable.level = (3000 + 6000 * db / 100).toInt()
                mTextView!!.text = TimeUtils.long2String(time)
            }

            //录音结束，filePath为保存路径
            override fun onStop(filePath: String) {
                mTextView!!.text = TimeUtils.long2String(0)
            }
        })
        btn_recoder.setOnTouchListener({ v, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    mPop.showAtLocation(linear, Gravity.CENTER, 0, 0)
                    btn_recoder.text = "松开保存"
                    mAudioRecoderUtils!!.startRecord(workid)
                    //record()
                }

                MotionEvent.ACTION_UP -> {
                    info("手势抬起")
                    //recordstop()
                    mAudioRecoderUtils!!.stopRecord()
                    //                        mAudioRecoderUtils.cancelRecord();    //取消录音（不保存录音文件）
                    mPop.dismiss()
                    btn_listen.text = "播放"
                    btn_recoder.text = "按住说话"
                }
            }
            true
        })
        if(isfinished!!){
            btn_paper_work.visibility = View.GONE
            btn_recoder.visibility = View.GONE
            btn_workenter_sumbit.visibility = View.GONE
        }
    }

    private fun recordstop(){
        if (soundFile != null && soundFile!!.exists())
        {
            // 停止录音
            mRecorder!!.stop()  //②
            // 释放资源
            mRecorder!!.release()  //③
            mRecorder = null
        }
    }

    private fun record(){
        if (!Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED))
                {
                    Toast.makeText(this, "SD卡不存在，请插入SD卡！",
                        Toast.LENGTH_SHORT).show();
                    return;
                }
                try
                {
                    // 创建保存录音的音频文件
                    soundFile =  File(Const.DATA_PATH+workid+"/$workid.amr")
                    mRecorder =  MediaRecorder()
                    // 设置录音的声音来源
                    mRecorder!!.setAudioSource(MediaRecorder
                        .AudioSource.MIC);
                    // 设置录制的声音的输出格式（必须在设置声音编码格式之前设置）
                    mRecorder!!.setOutputFormat(MediaRecorder
                        .OutputFormat.AMR_NB);
                    // 设置声音编码的格式
                    mRecorder!!.setAudioEncoder(MediaRecorder
                        .AudioEncoder.AMR_NB);
                    mRecorder!!.setOutputFile(soundFile!!.absolutePath)
                    mRecorder!!.prepare();
                    // 开始录音
                    mRecorder!!.start();  //①
                }
                catch ( e:Exception)
                {
                    e.printStackTrace();
                }
    }


    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_workenter_back->{
                finish()
            }
            R.id.btn_workenter_sumbit->{
                showSumbitDialog()
            }
            R.id.btn_paper_work->{
                val openCameraIntent = Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE)
                val file =File(paperpath)
                val pfile = file.parentFile
                //判断文件夹是否存在,如果不存在则创建文件夹
                if (!pfile.exists()) {
                    pfile.mkdir()
                }
                tempUri = Uri.fromFile(file)
                // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri)
                startActivityForResult(openCameraIntent, TAKE_PICTURE)

            }
            R.id.btn_listen -> {
                val file:File = File(Const.DATA_PATH+workid+"/"+workid+".amr")
                if(file.exists()) {
                    info("播放")
                    createAudioPlayer(file).start()
                }else{
                    info("文件不存在")
                }
            }
        }
    }

    private fun showSumbitDialog(){
        val normalDialog = AlertDialog.Builder(this@ExerciseActivity)
        normalDialog.setTitle("确认提交")
        normalDialog.setPositiveButton("提交",
                { dialog, which ->
                    WorkDao.getInstance().updateIsfinished(workid!!)
                    //TODO 更新数据库,发送作业
                    packWorkData(workid!!)
                    WriteFragment.getInstance().updateview()
                    dialog.cancel()
                    finish()
                })
        normalDialog.setNeutralButton("取消",
                { dialog, which ->
                    dialog.cancel()
                })
        normalDialog.show()
    }

    private fun packWorkData(workid:String){
        var workresult :WorkResult
        async {
            workresult = WorkDao.getInstance().selectWorkResult(workid,choiNum,shortNum)
            val mResult = JSONObject()
            mResult.put("id","m${Utils.getWorkId()}")
            mResult.put("workid",workresult.workid)
            mResult.put("teacher",teacher)
            mResult.put("stuid",workresult.stuid)
            mResult.put("timeused",workresult.timeused)
            mResult.put("ework",workresult.result)
            val pwork = JSONObject()
            pwork.put("title",jString!!.getString("paperwork"))
            if(File(paperpath).exists()){
                pwork.put("base64",Utils.encodeBase64File(paperpath!!))
            }else{
                pwork.put("base64","")
            }
            val rwork = JSONObject()
            rwork.put("title",jString!!.getString("radiowork"))
            if(File(radiopath).exists()){
                rwork.put("base64",Utils.encodeBase64File(radiopath!!))
            }else{
                rwork.put("base64","")
            }
            mResult.put("pwork",pwork)
            mResult.put("rwork",rwork)
            HttpUtils.SubmitWork(mResult)
            uiThread {
                info(mResult.toString())
                toast("作业已提交！")
            }
        }
    }

    @Throws(IOException::class)
    private fun createAudioPlayer(audioFile: File): MediaPlayer {
        val mPlayer = MediaPlayer()
        val fis = FileInputStream(audioFile)
        mPlayer.reset()
        mPlayer.setDataSource(fis.fd)
        mPlayer.prepare()
        mPlayer.setOnCompletionListener {
            info("释放播放器资源")
            mPlayer.release()
        }
        return mPlayer
    }

    private fun initJson(exerciseJson:JSONObject) {
        try {
            workid = exerciseJson.getString("workid")
            teacher = exerciseJson.getString("teacher")
            radiopath = Const.DATA_PATH+workid+"/"+workid+".amr"
            val choJson = exerciseJson.getJSONObject("cho")
            val shoJson = exerciseJson.getJSONObject("sho")
            choiNum = choJson.getInt("num")
            shortNum = shoJson.getInt("num")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

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
            if(WorkDao.getInstance().selectIsfinished(workid!!,(tag+i)%1000)==0){
                val xrp = resources.getXml(R.drawable.selector_textcolor)
                try {
                    val csl = ColorStateList.createFromXml(resources, xrp)
                    Btn[i]!!.setTextColor(csl)
                } catch (e: Exception) {
                    info(e.message)
                }
                Btn[i]!!.background = ContextCompat.getDrawable(this, R.drawable.selector_button_green_cir)
            }else{
                try {

                    Btn[i]!!.setTextColor(Color.WHITE)
                } catch (e: Exception) {
                    info(e.message)
                }
                Btn[i]!!.background = ContextCompat.getDrawable(this, R.drawable.item_deepgreen_cir)
            }

            Btn[i]!!.setOnClickListener({ v ->
                val intent = Intent(this, OneQuestion::class.java)
                intent.putExtra("number", v.tag.toString().toInt())
                intent.putExtra("exercise", jString!!.toString())
                intent.putExtra("isfinished",isfinished)
                startActivity(intent)
            })
            val btParams = GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED,1), GridLayout.spec(GridLayout.UNDEFINED,1))  //设置按钮的宽度和高度
            btParams.leftMargin = Utils.dp2px(AppContext.instance!!,20.toFloat())    //横坐标定位
            btParams.topMargin = Utils.dp2px(AppContext.instance!!,24.toFloat())
            btParams.width=Utils.dp2px(AppContext.instance!!,48.toFloat())
            btParams.height=Utils.dp2px(AppContext.instance!!,48.toFloat())
            GdLayout.addView(Btn[i],btParams)
        }
    }

    override fun onResume() {
        super.onResume()
        loadButtons(choiNum, shortNum)
    }

}

