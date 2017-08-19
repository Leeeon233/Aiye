package leon.homework.Utils

import android.util.Log
import leon.homework.data.Const
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder


/**
 * Created by BC on 2017/1/22 0022.
 */
object HttpUtils:AnkoLogger{
    fun RegisterStuUpdata(json: JSONObject) = PostJson(json, Const.RegisterStuUpdataUrl)
    fun LoginCheck(json: JSONObject) = PostJson(json, Const.LoginCheckUrl)
    fun RegisterStudent(json: JSONObject) = PostJson(json, Const.RegisterStuUrl)
    fun ForgetPwdStu(json: JSONObject) = PostJson(json,Const.ForgetPwdStuUrl)
    fun SubmitWork(json:JSONObject) = PostJson(json,Const.SubmitWorkUrl)
    fun GetStuHomeWork(json: JSONObject) = PostJson(json,Const.GetStuWorkUrl)
    fun CorrectWork(json: JSONObject) = PostJson(json,Const.GetPointUrl)
    //fun LoginCheckTea(json: JSONObject) = PostJson(json,Const.LoginCheckUrl)

    private fun PostJson(json: JSONObject, url: String): String {
        val data :String
        info("json数据》》》》》》$json")
        try {
            val mjs = URLEncoder.encode(json.toString(),"UTF-8")
            val js = JSONObject()
            js.put("js",mjs)
            val content = js.toString()
            val result = URL(url)
            val conn = result.openConnection() as HttpURLConnection
            conn.connectTimeout = 3000
            conn.doOutput = true
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            val os = conn.outputStream
            os.write(content.toByteArray())
            os.close()
            val sb = StringBuilder()
            val Is = conn.inputStream
            val isr = InputStreamReader(Is)
            val br = BufferedReader(isr)
            var msg = br.readLine()
            while (msg != null) {
                sb.append(msg)
                msg = br.readLine()
            }
            data = URLDecoder.decode(sb.toString(),"UTF-8").toString()
            info("返回码>>>>>>>>>>>>>>>>>>>>" + data)
        } catch (e: Exception) {
            e.printStackTrace()
            val js=JSONObject()
            js.put("status","error")
            return js.toString()
        }
        return data
    }




}