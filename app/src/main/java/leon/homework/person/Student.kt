package leon.homework.person

import android.util.Log
import leon.homework.Utils.DataUtils
import leon.homework.Utils.HttpUtils
import leon.homework.Utils.ImgHelper
import leon.homework.Utils.Utils
import leon.homework.Utils.Utils.getid
import leon.homework.data.Const
import leon.homework.data.SaveData
import leon.homework.javaBean.MsgObject
import leon.homework.sqlite.ChatDao
import org.jetbrains.anko.info
import org.json.JSONObject


/*
 * Created by BC on 2017/2/3 0003.
 */

class Student : User() {
    /*override var phonenum: String = SaveData.phonenum
    override var name: String = SaveData.name
    override var id: String = SaveData.id
    override var alia: String = SaveData.alia
    override var avatar_path: String = SaveData.avatar_path
    override var cls: String = SaveData.cls*/
    fun Register(phonenum: String, pwd: String):Int {
        val id = getid()
        val js = JSONObject()
        js.put("Stu_pho",phonenum)
        js.put("pswd",pwd)
        js.put("ay_id",id)
        val data = JSONObject(HttpUtils.RegisterStudent(js))
        return when(data.getString("status")){
            "0" ->{
                Const.REGISTER_FAIL
            }
            "1" ->{
                this.phonenum = phonenum
                this.alia = id
                SaveData.phonenum = phonenum
                SaveData.UserType = Const.ISSTUDENT
                SaveData.alia = id
                Log.d("Student","已保存手机，别名")
                Alia(this.alia!!)
                Const.REGISTER_SUCCESS
            }
            else ->{
                Const.REGISTER_ERROR
            }
        }
    }

    fun completelogin(phonenum: String,Js:JSONObject):Int{
        /*if(phonenum == SaveData.phonenum){         //在本机注册过再登录的情况
            info("注册过再登录的情况")
            this.phonenum = SaveData.phonenum
            this.alia = SaveData.alia
            Alia(SaveData.alia)
            this.name = SaveData.name
            this.id = SaveData.id
            this.avatar_path = SaveData.avatar_path
            this.cls = SaveData.cls
            return Const.LOGIN_SUCCESS
        }else{*/
            this.phonenum = phonenum
            this.alia = Js.getString("ay_id")
            SaveData.alia = this.alia!!
            Alia(this.alia!!)
            if(!Js./*getString("Stu_id").isNullOrEmpty()*/has("Stu_nam")){
                info("name isNullOrEmpty")
                return Const.LOGIN_NEED_COMPLETE
            }else{
                info("complete")
                this.name = Js.getString("Stu_nam")
                this.id = Js.getString("Stu_id")
                this.avatar_path = getImgPath(Js.getString("base64"),this.alia!!)
                Js.remove("base64")
                info(Js)
                this.cls = Js.getString("Stu_cls")
                SaveData()
                val teacher = JSONObject(Js.getString("teainfo"))
                if(!Utils.checkDataBase(SaveData.phonenum+"ChatLog.db")){
                for(i in 1..teacher.length()){
                    val teaalia = teacher.getString("tea$i")
                    var title:String?=null
                    var path:String?=null
                    when(i){
                        1->{
                            title = "语文老师"
                            path = "语文老师"
                        }
                        2->{
                            title = "数学老师"
                            path = "数学老师"
                        }
                        3->{
                            title = "英语老师"
                            path = "英语老师"
                        }
                        4->{
                            title = "物理老师"
                            path = "物理老师"
                        }
                        5->{
                            title = "化学老师"
                            path = "化学老师"
                        }
                        6->{
                            title = "生物老师"
                            path = "生物老师"
                        }
                        7->{
                            title = "政治老师"
                            path = "政治老师"
                        }
                        8->{
                            title = "历史老师"
                            path = "历史老师"
                        }
                        9->{
                            title = "地理老师"
                            path = "地理老师"
                        }
                    }

                        val msgob = MsgObject(title!!,teaalia,path!!,"我是$title", DataUtils.getTodayDateTime().substring(11,16))
                        ChatDao.getInstance().insertChat(msgob)
                    }
                }
                Subscribe(this.cls!!)

                return Const.LOGIN_SUCCESS
                //return Const.LOGIN_ERROR
            }
       // }
    }


   /* override*/ /*fun Login(phonenum: String, pwd: String):Int{
        val js = JSONObject()
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
                if(phonenum == SaveData.phonenum){         //在本机注册过再登录的情况
                    if(SaveData.id.isNullOrEmpty()){
                        Log.d("Student_if","if")
                        this.alia = SaveData.alia
                        this.phonenum = SaveData.phonenum
                        Const.LOGIN_NEED_COMPLETE
                    }else{
                        Log.d("Student_if","else")
                        this.phonenum = SaveData.phonenum
                        this.alia = SaveData.alia
                        Alia(SaveData.alia)
                        this.name = SaveData.name
                        this.id = SaveData.id
                        this.avatar_path = SaveData.avatar_path
                        this.cls = SaveData.cls
                        Subscribe(SaveData.cls)
                        Log.d("Student-91","订阅频道")
                        Const.LOGIN_SUCCESS
                    }
                }else{
                    val Js = JSONObject(data.getString("info"))
                    this.phonenum = phonenum
                    this.alia = Js.getString("ay_id")
                    SaveData.alia = this.alia
                    Alia(this.alia)
                    if(!Js.has("Stu_id")){
                        Log.d("Login","Stu_nam isNullOrEmpty()")
                        Const.LOGIN_NEED_COMPLETE
                    }else{
                        Log.d("Login","Complete")
                        this.name = Js.getString("Stu_nam")
                        this.id = Js.getString("Stu_id")
                        this.avatar_path = getImgPath(Js.getString("img_id"),this.alia)
                        this.cls = Js.getString("Stu_cls")
                        Subscribe(this.cls)
                        SaveData()
                        Const.LOGIN_SUCCESS
                    }
                }
            }
            else ->{
                Const.LOGIN_ERROR
            }
        }
    }*/
    fun CompleteInfo(name:String,Stu_Id:String,avatar_path:String,/*birthday:String,sex:String,*/cls:String):Int{
        val js = JSONObject()
        js.put("Stu_id",Stu_Id)
        js.put("ay_id",this.alia)
        js.put("Stu_pho",this.phonenum)
        js.put("Stu_nam", name)
        /*js.put("Stu_bir",birthday)
        js.put("Stu_sex",sex)*/
        js.put("Stu_cls",cls)
        js.put("Stu_img", ImgHelper.encodeImage(avatar_path))  //base64
        val data = JSONObject(HttpUtils.RegisterStuUpdata(js))
        return when(data.getString("status")){
                "1"->{
                    this.name=name
                    this.id=Stu_Id
                    this.avatar_path=avatar_path
                    this.cls=cls
                    Subscribe(cls)
                    SaveData()
                    val teacher = JSONObject(data.getString("info"))
                    for(i in 1..teacher.length()) {
                        val teaalia = teacher.getString("tea$i")
                        var title: String? = null
                        var path: String? = null
                        when (i) {
                            1 -> {
                                title = "语文老师"
                                path = "语文老师"
                            }
                            2 -> {
                                title = "数学老师"
                                path = "数学老师"
                            }
                            3 -> {
                                title = "英语老师"
                                path = "英语老师"
                            }
                            4 -> {
                                title = "物理老师"
                                path = "物理老师"
                            }
                            5 -> {
                                title = "化学老师"
                                path = "化学老师"
                            }
                            6 -> {
                                title = "生物老师"
                                path = "生物老师"
                            }
                            7 -> {
                                title = "政治老师"
                                path = "政治老师"
                            }
                            8 -> {
                                title = "历史老师"
                                path = "历史老师"
                            }
                            9 -> {
                                title = "地理老师"
                                path = "地理老师"
                            }
                        }

                        val msgob = MsgObject(title!!, teaalia, path!!, "我是$title", DataUtils.getTodayDateTime().substring(11, 16))
                        ChatDao.getInstance().insertChat(msgob)
                    }
                    Const.COMPLETEINFO_SUCCESS
                    //Const.COMPLETEINFO_ERROR
                }
                "0"->{
                    Const.COMPLETEINFO_FAIL
                }
                else->{
                    Const.COMPLETEINFO_ERROR
                }
        }
    }
    fun RetrievePwd(phonenum: String,pwd: String):Int{
        val js = JSONObject()
        js.put("Stu_pho",phonenum)
        js.put("pswd",pwd)
        val data = JSONObject(HttpUtils.ForgetPwdStu(js))
        return when(data.getString("status")){
            "0" ->{
                Const.Retrieve_FAIL
            }
            "1" ->{
                Const.Retrieve_SUCCESS
            }
            else ->{
                Const.Retrieve_ERROR
            }
        }
    }

}
