package leon.homework.data

import leon.homework.app.AppContext
import leon.homework.Utils.Preference

/*
 * Created by BC on 2017/2/10 0010.
 */
object SaveData{
    var isLogined:Boolean by Preference(AppContext.instance!!,"default",Const.SAVE_LOGIN_MODEL,false)
    //var isFirstIn:Boolean by Preference(AppContext.instance!!,"default", Const.SAVE_ISFIRSTIN,true)
    var UserType:Int by Preference(AppContext.instance!!,"default", Const.SAVE_UserType,-1) //1 student 2 teacher -1 default
    var isShowNotifation :Boolean by Preference(AppContext.instance!!,"default", Const.SAVE_ISSHOWNOTIFATION,true)

    var phonenum:String by Preference(AppContext.instance!!, "default", Const.SAVE_PHONENUM,"")
    var alia:String by Preference(AppContext.instance!!,"default", Const.SAVE_ALIA,"")
    var name: String by Preference(AppContext.instance!!,"default", Const.SAVE_NAME,"")
    var id: String by Preference(AppContext.instance!!,"default", Const.SAVE_ID,"")
    var avatar_path: String by Preference(AppContext.instance!!,"default", Const.SAVE_IMG,"")
    var cls: String by Preference(AppContext.instance!!,"default", Const.SAVE_CLS,"")
    var subject: String by Preference(AppContext.instance!!,"default", Const.SAVE_SUBJECT,"")

}