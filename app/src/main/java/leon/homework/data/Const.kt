package leon.homework.data

import android.os.Environment

/**
 * Created by BC on 2017/2/5 0005.
 */

object Const {
    /**
     *>>>>>>>>>>>>>>>>>>>>>>>>>>>>PATH
     */
    //val DATA_PATH = Environment.getExternalStorageDirectory().path +"/"+"Aiye/"
    val DATA_PATH = /*AppContext.instance!!.filesDir.absolutePath*/Environment.getExternalStorageDirectory().absolutePath+"/"+"Aiye/"
    val AVATAR_PATH = DATA_PATH+"Avatar/"
    val DOWNLOAD_PATH = DATA_PATH+"Download/"
    /**
     * >>>>>>>>>>>>>>>>>URL
     */
    val mainUrl = "http://zz503379238.oicp.net/"//"http://aiye.applinzi.com/"
    val SubmitWorkUrl = mainUrl+"work"
    val RegisterStuUrl = mainUrl+ "Info"
    val RegisterStuUpdataUrl = mainUrl+ "Info2"
    val LoginCheckUrl = mainUrl+ "Info3"
    val ForgetPwdStuUrl = mainUrl+ "fpswd"
    val GetPointUrl = mainUrl + "point"
    val GetStuWorkUrl = mainUrl+"ptwork"
    val DownloadQuestionsUrl = mainUrl+ "down/questions.zip"
    /**
     * >>>>>>>>>>>>>>>>>>>收到推送
     */
    val ACTION_GET_POINT = "ACTION_GET_POINT"     //去获取教师判后作业
    val ACTION_FINISH_MAIN = "ACTION_FINISH_MAIN"     //结束主界面
    val ACTION_TOGET_STU_HOMEWORK = "TO_GET_STU_HOMEWORK"    //教师接收作业
    val UPDATE_MAINACTIVITY_UI = "UPDATE_MAINACTIVITY_UI"
    val ACTION_SYSTEM = "ACTION_SYSTEM"
    val ACTION_RECEIVE_HOMEWORK = "ACTION_RECEIVE_HOMEWORK"   //学生初次收到作业
    val ACTION_CHAT = "ACTION_CHAT"    //聊天消息
    /**
     * >>>>>>>>>>>>>>>>>config
     */
    val SHOW_MESSAGE_FRAGMENT = 0
    val SHOW_WORK_FRAGMENT = 1
    val SHOW_MINE_FRAGMENT = 2
    val ISSTUDENT = 1
    val ISTEACHER = 2
    val SHOW = 1
    val HIDE = 0
    val REGISTER_SUCCESS = 1
    val REGISTER_FAIL = 0
    val REGISTER_ERROR = -1
    val LOGIN_SUCCESS = 1
    val LOGIN_WRONGPWD = -1
    val LOGIN_NOEXIST = 0
    val LOGIN_NEED_COMPLETE = 2
    val LOGIN_ERROR = -2
    val COMPLETEINFO_SUCCESS = 1
    val COMPLETEINFO_FAIL = 0
    val COMPLETEINFO_ERROR = -1
    val Retrieve_SUCCESS = 1
    val Retrieve_FAIL = 0
    val Retrieve_ERROR = -1

    /**
     * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>Save
     */
    val SAVE_ISSHOWNOTIFATION = "isShowNotifation"
    val SAVE_LOGIN_MODEL = "IsLogined"
    val SAVE_ISFIRSTIN = "IsFirst"
    val SAVE_UserType = "UserType"
    val SAVE_PHONENUM = "PHONE_NUM"
    val SAVE_ALIA = "Alia"
    val SAVE_NAME = "Name"
    val SAVE_ID = "Id"
    val SAVE_IMG = "Img"
    val SAVE_CLS = "Cls"
    val SAVE_SUBJECT = "Subject"
    /**
     * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>download
     */
    val DOWNLOAD_ERROR = -1
    val DOWNLOAD_UPDATE_PROGRESS = 0
    val DOWNLOAD_OVER = 1
    val DOWNLOAD_FILEEXIST = 2

    /**
     * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>others
     */
    fun SMSCode(status: String): String {
        when (status) {
                "456" -> return "请输入手机号"
                "457" -> return "手机号格式错误"
                "466" -> return "请输入验证码"
                "467" -> return "校验验证码请求频繁"
                "468" -> return "验证码错误"
                "477" -> return "当前手机号尝试次数过多"
                "500" -> return "服务器内部错误"
                "601" -> return "短信发送受限"
                "603" -> return "请填写正确的手机号码"
                "604" -> return "当前服务暂不支持此国家"
                else -> return "未知错误，错误代码:" + status
        }
    }




}
