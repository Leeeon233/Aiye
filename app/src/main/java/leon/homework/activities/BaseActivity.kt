package leon.homework.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import leon.homework.Utils.ActivityCollector
import leon.homework.data.Const
import leon.homework.dialogs.LoadingDialog
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.async

/*
 * Created by vslimit on 16/1/12.
 */
abstract class BaseActivity : AppCompatActivity(), AnkoLogger {

    abstract val layoutResourceId: Int
    var loadingDialog: LoadingDialog? = null
    fun loading(msg:Int){

            when(msg){
                Const.SHOW -> loadingDialog?.show()//显示进度对话框
                Const.HIDE -> loadingDialog?.hide()//隐藏进度对话框，不可使用dismiss()、cancel(),否则再次调用show()时，显示的对话框小圆圈不会动。
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        ActivityCollector.addActivity(this,javaClass)
        loadingDialog = LoadingDialog(this)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }


    override fun onStop() {
        super.onStop()
        loadingDialog!!.dismiss()
    }


}