package leon.homework.javaBean

import android.graphics.Bitmap
import android.graphics.BitmapFactory

import java.io.File

import leon.homework.app.AppContext
import leon.homework.R

/**
 * Created by mjhzds on 2017/2/14.
 */

class MsgObject(val title: String, var alia: String, val bmpath: String, val lastcontent: String, val time: String) {
    var unreadnum = 0
    val bm: Bitmap
        get() =
            when(bmpath){
                "aiye"->{
                    BitmapFactory.decodeResource(AppContext.instance!!.resources, R.mipmap.icon)
                }
                "数学老师"->{
                    BitmapFactory.decodeResource(AppContext.instance!!.resources, R.mipmap.math)
                }
                "语文老师"->{
                    BitmapFactory.decodeResource(AppContext.instance!!.resources, R.mipmap.chinese)
                }
                "英语老师"->{
                    BitmapFactory.decodeResource(AppContext.instance!!.resources, R.mipmap.english)
                }
                "物理老师"->{
                    BitmapFactory.decodeResource(AppContext.instance!!.resources, R.mipmap.physics)
                }
                "化学老师"->{
                    BitmapFactory.decodeResource(AppContext.instance!!.resources, R.mipmap.chemistry)
                }
                "政治老师"->{
                    BitmapFactory.decodeResource(AppContext.instance!!.resources, R.mipmap.politics)
                }
                "历史老师"->{
                    BitmapFactory.decodeResource(AppContext.instance!!.resources, R.mipmap.history)
                }
                "地理老师"->{
                    BitmapFactory.decodeResource(AppContext.instance!!.resources, R.mipmap.geography)
                }
                "生物老师"->{
                    BitmapFactory.decodeResource(AppContext.instance!!.resources, R.mipmap.biology)
                }
                else->{
                    val file = File(bmpath)
                    if (file.exists())
                        BitmapFactory.decodeFile(bmpath)
                    else
                        BitmapFactory.decodeResource(AppContext.instance!!.resources, R.mipmap.ic_person_outline_black_48dp)
                }
            }
}
