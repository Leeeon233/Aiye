package leon.homework.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import kotlinx.android.synthetic.main.about.*
import leon.homework.app.AppContext
import leon.homework.R
import leon.homework.UI.MasterLayout
import leon.homework.Utils.Downloader
import leon.homework.Utils.FileUtil
import leon.homework.Utils.ZipUtil
import leon.homework.data.Const
import leon.homework.sqlite.WorkQuesDao
import org.jetbrains.anko.*
import java.io.File

class DownloadActivity : AppCompatActivity(),AnkoLogger {
    private var masterLayout : MasterLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        AppContext.downloadhandler = mhandler
        masterLayout = findViewById(R.id.btn_download) as MasterLayout
        masterLayout!!.setOnClickListener{
            masterLayout!!.animation()   //Need to call this method for animation and progression
            when (masterLayout!!.flg_frmwrk_mode) {
                1 -> {
                    download()
                    //masterLayout!!.cusview.setupprogress(90)
                    //Start state. Call your method
                }
                2 -> {
                    //Running state. Call your method
                }
                3 -> {
                    //End state. Call your method
                }
            }
        }
        btn_finish.onClick {
            finish()
        }
    }

    private val mhandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Const.DOWNLOAD_OVER -> {
                    masterLayout!!.finalAnimation()
                    toast("下载完成")
                    info("开始解压")
                    ZipUtil.unzipFiles(File(Const.DOWNLOAD_PATH+"questions.zip"),Const.DOWNLOAD_PATH)
                    info("解压完成")
                    WorkQuesDao.getInstance().execSQL(FileUtil.loadFile(Const.DATA_PATH+"sqls.sql"))
                }
                Const.DOWNLOAD_UPDATE_PROGRESS ->{
                    masterLayout!!.cusview.setupprogress(msg.arg1)
                }
                Const.DOWNLOAD_ERROR ->{
                    masterLayout!!.reset()
                    toast("服务器异常，重试")
                }
                Const.DOWNLOAD_FILEEXIST->{
                    toast("文件已存在,无需下载")
                    WorkQuesDao.getInstance().execSQL(FileUtil.loadFile(Const.DATA_PATH+"sqls.sql"))
                }
                else -> {
                    toast("???")
                }
            }
        }
    }

    private fun download(){
        async() {
            val url = Const.DownloadQuestionsUrl
            val path = Const.DOWNLOAD_PATH
            Downloader().Download(url,path,"questions.zip")
        }
    }
}
