package leon.homework.person

import android.util.Log
import io.yunba.android.manager.YunBaManager
import leon.homework.app.AppContext
import leon.homework.Utils.HttpUtils
import leon.homework.Utils.ImgHelper
import leon.homework.data.Const
import leon.homework.data.SaveData
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttException
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

/*
 * Created by Administrator on 2017/3/27.
 */
open class User : AnkoLogger{
    var phonenum: String ?= null
    var name: String ?= null
    var id: String ?= null
    var alia: String ?= null
    var avatar_path: String ?= null
    var cls: String ?= null

    fun Login(phonenum: String, pwd: String):Int{
        val js = JSONObject()
        SaveData.phonenum = phonenum
        js.put("pho",phonenum)
        js.put("pswd",pwd)
        val data = JSONObject(HttpUtils.LoginCheck(js))
        return when(data.getString("status")){
            "0" ->{
                Const.LOGIN_NOEXIST
            }
            "1" ->{
                Const.LOGIN_WRONGPWD
            }
            "2"->{
                val Info  = JSONObject(data.getString("info"))
                val usertype = Info.getInt("User_type")
                SaveData.UserType = usertype
                when(usertype){
                    Const.ISTEACHER -> {
                        val teacher = Teacher()
                        AppContext.User = teacher
                        teacher.completelogin(phonenum,Info,data.getJSONObject("clsinfo"),data.getJSONObject("stuinfo"))
                    }
                    Const.ISSTUDENT -> {
                        info("是学生》》》》》")
                        val student = Student()
                        AppContext.User = student
                        student.completelogin(phonenum,Info)
                    }
                    else -> {
                        info("js解析错误")
                        Const.LOGIN_ERROR
                    }
                }
                /*if(phonenum == SaveData.phonenum){         //在本机注册过再登录的情况
                    if(SaveData.id.isNullOrEmpty()){
                        this.alia = SaveData.alia
                        this.phonenum = SaveData.phonenum
                        Const.LOGIN_NEED_COMPLETE
                    }else{
                        this.phonenum = SaveData.phonenum
                        this.alia = SaveData.alia
                        Alia(SaveData.alia)
                        this.name = SaveData.name
                        this.id = SaveData.id
                        this.avatar_path = SaveData.avatar_path
                        this.cls = SaveData.cls
                        Subscribe(SaveData.cls)
                        Log.d("Tea--49","订阅频道")
                        Const.LOGIN_SUCCESS
                    }
                }else{
                    val Js = JSONObject(data.getString("info"))
                    this.phonenum = phonenum
                    this.alia = Js.getString("ay_id")
                    SaveData.alia = this.alia
                    Alia(this.alia)
                    if(!Js.has("Tea_id")){
                        Log.d("Login","Stu_nam isNullOrEmpty()")
                        Const.LOGIN_NEED_COMPLETE
                    }else{
                        Log.d("Login","Complete")
                        this.name = Js.getString("Tea_nam")
                        this.id = Js.getString("Tea_id")
                        this.avatar_path = getImgPath(Js.getString("img_id"),this.alia)
                        this.cls = Js.getString("Tea_cls")
                        Subscribe(this.cls)
                        SaveData.subject = Js.getString("Tea_sub")
                        SaveData()
                        Const.LOGIN_SUCCESS
                    }
                }*/
            }
            else ->{
                Const.LOGIN_ERROR
            }
        }
    }



    fun Alia(Alia :String){
        YunBaManager.setAlias(AppContext.instance, Alia,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        //YunbaUtil.showToast("success", applicationContext)
                        info("设置别名成功:"+Alia)
                    }
                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        if (exception is MqttException) {
                            val msg = "setAlias failed with error code : " + exception.reasonCode
                            info(msg)
                            //YunbaUtil.showToast(msg, AppContext.instance)
                        }
                    }
                }
        )
    }

    fun Subscribe(topic:String){
        YunBaManager.subscribe(AppContext.instance, topic,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        Log.d("Student","订阅频道成功:"+topic)
                    }
                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        if (exception is MqttException) {
                            val msg = "Subscribe failed with error code : " + exception.reasonCode
                            info(msg)
                        }

                    }
                }
        )
    }

    open fun SaveData(){
        SaveData.phonenum = this.phonenum!!
        SaveData.alia = this.alia!!
        SaveData.id = this.id!!
        SaveData.name = this.name!!
        SaveData.avatar_path = this.avatar_path!!
        SaveData.cls = this.cls!!
        Log.d("USER","已保存用户数据")
    }

    fun getImgPath(base64: String,filename:String):String{
        val imgFilePath = Const.AVATAR_PATH+filename+".png"
        val bts = ImgHelper.decode(base64)
        val file = File(imgFilePath)
        if(!file.parentFile.exists()){
            file.parentFile.mkdirs()
        }
        val fos = FileOutputStream(file)
        fos.write(bts)
        fos.close()
        return file.path
    }
}