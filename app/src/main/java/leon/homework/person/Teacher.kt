package leon.homework.person

import android.util.Log
import leon.homework.data.Const
import leon.homework.data.SaveData
import leon.homework.sqlite.ClassDao
import org.jetbrains.anko.info
import org.json.JSONObject
import java.util.ArrayList

/*
 * Created by BC on 2017/2/3 0003.
 */

class Teacher:User(){
     /*var phonenum: String = SaveData.phonenum
     var name: String = SaveData.name
     var id: String = SaveData.id
     var alia: String = SaveData.alia
     var avatar_path: String = SaveData.avatar_path
     var cls: String = SaveData.cls*/
     var classes:ArrayList<String> = ArrayList()
     var subject:String = SaveData.subject

    fun completelogin(phonenum:String,Js:JSONObject,cls:JSONObject,stujs:JSONObject):Int{

        /*if(phonenum == SaveData.phonenum){         //登录过
            this.phonenum = SaveData.phonenum
            this.alia = SaveData.alia
            Alia(SaveData.alia)
            this.name = SaveData.name
            this.id = SaveData.id
            this.avatar_path = SaveData.avatar_path
            this.cls = SaveData.cls
            this.subject = SaveData.subject
            return Const.LOGIN_SUCCESS
        }else{*/
        this.phonenum = phonenum
        this.alia = Js.getString("ay_id")
        this.id = alia
        Alia(this.alia!!)
        Log.d("Login","Complete")
        this.name = Js.getString("Tea_nam")
        //this.id = Js.getString("Tea_id")
        //this.avatar_path = getImgPath(Js.getString("img_id"),this.alia!!)
        //this.cls = Js.getString("Tea_cls")
        //classes.addAll(cls!!.split(","))
        this.subject = Js.getString("Tea_sub")
        this.avatar_path = subject
        this.cls = cls.toString()
        info("正在保持用户信息")
        SaveData()
        info("保持用户信息完成")
        if(cls.length()>0){
            info(cls.toString())
            var mcls :JSONObject
            var clsid :String
            var clsname :String
            for(i in 1..cls.length()){
                mcls = cls.getJSONObject("cls$i")
                clsid = mcls.getString("id")
                clsname = mcls.getString("name")
                info("正在创建班级")
                ClassDao.getInstance().createClass(clsid,clsname)
                info("班级创建完成")
            }
        }
        var sjs:JSONObject
        var classid:String
        var stuid :String
        var stuname:String
        if(stujs.length()>0){
            info("教师登录 获取到学生信息"+stujs.toString())
            for(i in 1..stujs.length()){
                sjs = stujs.getJSONObject("stu$i")
                classid = sjs.getString("class$i")
                stuid = sjs.getString("ay_id")
                stuname = sjs.getString("name")
                ClassDao.getInstance().insertAstudent(classid,stuid,stuname)
            }
        }
        return Const.LOGIN_SUCCESS
    }
    override fun SaveData(){
        super.SaveData()
        info("save")
        SaveData.subject = this.subject
    }
    /*override fun Login(phonenum: String, pwd: String):Int{
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
                        this.subject = Js.getString("Tea_sub")
                        this.name = Js.getString("Tea_nam")
                        this.id = Js.getString("Tea_id")
                        this.avatar_path = getImgPath(Js.getString("img_id"),this.alia)
                        this.cls = Js.getString("Tea_cls")
                        Subscribe(this.cls)
                        SaveData.subject = Js.getString("Tea_sub")
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

}
