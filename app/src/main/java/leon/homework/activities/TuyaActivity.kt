package leon.homework.activities

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_tuya.*
import leon.homework.app.AppContext
import leon.homework.R
import leon.homework.UI.TuyaLayout
import org.jetbrains.anko.onClick

class TuyaActivity : BaseActivity() ,TuyaLayout.OnclickSaveListener {
    override fun changImg() {
        val intent = Intent()
        intent.action = workid
        AppContext.instance!!.sendBroadcast(intent)
        finish()
    }

    override val layoutResourceId: Int = R.layout.activity_tuya
    private var workid:String?=null
    private var quesid:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        workid = intent.getStringExtra("workid")
        quesid = intent.getStringExtra("quesid")
        tuya.workid = workid!!
        tuya.quesid = quesid!!
        tuya.mlistener = this
        btn_finish.onClick {
            finish()
        }
    }
}
