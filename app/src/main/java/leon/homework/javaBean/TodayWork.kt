package leon.homework.javaBean

import leon.homework.R
import org.json.JSONObject

/*
 * Created by mjhzds on 2017/2/6.
 */

class TodayWork(val subjectName: String, val deadLine: String,val js:JSONObject) {
    val subjectImage: Int
    init {
        this.subjectImage = getSubjectImg(subjectName)
    }
    fun getSubjectImg(subject:String):Int{
        return when(subject){
            "语文"->{
                R.mipmap.chinese
            }
            "英语"->{
                R.mipmap.english
            }
            "数学"->{
                R.mipmap.math
            }
            "物理"->{
                R.mipmap.physics
            }
            "化学"->{
                R.mipmap.chemistry
            }
            "生物"->{
                R.mipmap.biology
            }
            "历史"->{
                R.mipmap.history
            }
            "政治"->{
                R.mipmap.politics
            }
            "地理"->{
                R.mipmap.geography
            }
            else -> {
                R.drawable.teacher //TODO set default
            }
        }
    }
}
