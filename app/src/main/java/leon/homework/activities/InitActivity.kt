package leon.homework.activities

import android.content.Intent
import android.os.Bundle
import leon.homework.app.AppContext
import leon.homework.R
import leon.homework.data.Const
import leon.homework.data.SaveData
import leon.homework.person.Student
import leon.homework.person.Teacher
import org.jetbrains.anko.async
import org.jetbrains.anko.info

/*
 * Created by mjhzds on 2017/1/15.
 */

class InitActivity : BaseActivity() {
    override val layoutResourceId: Int=R.layout.welcome
    private val TIME = 2000
    //private val GO_GUIDE = 1001
    /*private val GO_MAIN = 1002
    private val GO_LOGIN = 1003
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                //GO_WELCOME -> goWelcome()
                GO_LOGIN -> goLogin()
                //GO_GUIDE -> goGuide()
                GO_MAIN -> goMain()
            }
        }
    }*/

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        async {
            Thread.sleep(TIME.toLong())
            if (!SaveData.isLogined) {
                goLogin()
            } else {
                goMain()
            }
        }
        /*if(SaveData.isFirstIn){
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, TIME.toLong())
        }else{*/
        /*if (!SaveData.isLogined) {
            mHandler.sendEmptyMessageDelayed(GO_LOGIN, TIME.toLong())
        } else {
            mHandler.sendEmptyMessageDelayed(GO_MAIN, TIME.toLong())
        }*/
    //}
        /*if(SaveData.UserType=="1"){
            initStu()
        }else if(SaveData.UserType=="2"){
            initTch()
        }else{
            Log.d("InitAct",SaveData.UserType)
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, TIME.toLong())
        }*/
    }

    /*private fun goWelcome() {
        startActivity(Intent(this@InitActivity, WelcomeActivity::class.java))
        finish()
    }*/
    private fun goMain(){
        when(SaveData.UserType){
            Const.ISSTUDENT ->{
                info("InitACT---我是学生")
                val student = Student()
                student.phonenum = SaveData.phonenum        //登录过
                student.alia = SaveData.alia
                student.Alia(SaveData.alia)
                student.name = SaveData.name
                student.id = SaveData.id
                student.avatar_path = SaveData.avatar_path
                student.cls = SaveData.cls
                student.Subscribe(SaveData.cls)
                info("别名！！！"+SaveData.alia)
                AppContext.User = student
            }
            Const.ISTEACHER ->{
                info("InitACT---我是老师")
                val teacher = Teacher()
                teacher.phonenum = SaveData.phonenum        //登录过
                teacher.alia = SaveData.alia
                teacher.Alia(SaveData.alia)
                teacher.name = SaveData.name
                teacher.id = SaveData.id
                teacher.avatar_path = SaveData.avatar_path
                teacher.cls = SaveData.cls
                teacher.subject = SaveData.subject
                AppContext.User = teacher
            }
        }
        startActivity(Intent(this@InitActivity, MainActivity::class.java))
        finish()
    }
    /*private fun goGuide() {
        startActivity(Intent(this@InitActivity, GuideActivity::class.java))
        finish()
    }*/
    private fun goLogin() {
        startActivity(Intent(this@InitActivity, LoginActivity::class.java))
        finish()
    }
}
