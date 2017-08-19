package leon.homework.javaBean

/**
 * Created by mjhzds on 2017/2/14.
 */

class Msg(val content: String, val msgType: Int,val msgtime: String) {
    companion object {
        val TYPE_RECEIVED = 0
        val TYPE_SENT = 1
    }
}
