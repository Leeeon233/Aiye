package leon.homework.Utils

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.*
import android.graphics.Bitmap
import android.text.Spannable
import leon.homework.data.Const
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.regex.Pattern
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteDatabase
import leon.homework.app.AppContext


/*
 * Created by mjhzds on 2017/1/22.
 */

object Utils :AnkoLogger{

    fun checkDataBase(DB_NAME:String): Boolean {
        var checkDB: SQLiteDatabase? = null
        try {
            val myPath = "/data/data/${AppContext.instance!!.packageName}/databases/"+DB_NAME
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY or SQLiteDatabase.NO_LOCALIZED_COLLATORS)
        } catch (e: SQLiteException) {
            info("不存在数据库: $DB_NAME")
        }
        if (checkDB != null) {
            checkDB.close()
        }
        return checkDB != null
    }

    fun isActivityRunning(mContext:Context,activityClassName:String):Boolean{
        val activityManager =  mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = activityManager.getRunningTasks(1)
        if(info != null && info.size > 0){
            val component = info.get(0).topActivity
            if(activityClassName.equals(component.className)){
                return true
            }
        }
        return false;
    }

    fun isBackground(context: Context): Boolean {
        val activityManager = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager
                .runningAppProcesses
        for (appProcess in appProcesses) {
            if (appProcess.processName == context.packageName) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                Log.i(context.packageName, "此appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()="
                        + context.javaClass.name)
                if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.packageName, "处于后台" + appProcess.processName)
                    return true
                } else {
                    Log.i(context.packageName, "处于前台" + appProcess.processName)
                    return false
                }
            }
        }
        return false
    }

    fun textspan(context: Context,title:String,imgs:Array<String>):SpannableStringBuilder{
        val imgnum = imgs.size
        val imgpath = Const.DOWNLOAD_PATH+"img/"
        val bitmaps = ArrayList<Bitmap>()
        val mtitle = "\t\t\t\t"+title
        val builder = SpannableStringBuilder(mtitle)
        if(imgnum>0){
            for(i in 0..imgnum-1){
                if(!imgs[i].isNullOrEmpty()){
                    val file = File(imgpath+imgs[i])
                    val bit = BitmapFactory.decodeFile(file.path)
                    bitmaps.add(bit)
                }
            }
            for(i in 0..bitmaps.size-1){
                val rexgString = "aiye"+(i+1).toString()
                val pattern = Pattern.compile(rexgString)
                val matcher = pattern.matcher(mtitle)
                //val start = title.indexOf("aiye")
                while (matcher.find()) {
                    info( matcher.start().toString()+":::"+ matcher.end())
                    builder.setSpan(ImageSpan(context, bitmaps[i]), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
        info(builder)
        return builder
    }

    fun zoomBitmap(oldBitmap: Bitmap,newWidth:Int,newHeight:Int):Bitmap{
        val width = oldBitmap.getWidth()
        val height = oldBitmap.getHeight()
        // 计算缩放比例
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // 取得想要缩放的matrix参数
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        // 得到新的图片
        info("h "+height+" w "+width)
        val newbm = Bitmap.createBitmap(oldBitmap, 0, 0, width, height, matrix, true)

        return newbm
    }
    /**
     * Save image to the SD card

     * @param photoBitmap
     * *
     * @param photoName
     * *
     * @param path
     */
    fun savePhoto(photoBitmap: Bitmap?, path: String, photoName: String): String {
        var localPath: String? = null
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val photoFile = File(path, photoName + ".png")
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(photoFile)
            if (photoBitmap != null) {
                if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) { // 转换完成
                    localPath = photoFile.path
                    fileOutputStream.flush()
                }
            }
        } catch (e: FileNotFoundException) {
            photoFile.delete()
            localPath = null
            e.printStackTrace()
        } catch (e: IOException) {
            photoFile.delete()
            localPath = null
            e.printStackTrace()
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return localPath!!
    }

    /**
     * 转换图片成圆形
     * @param bitmap
     * *            传入Bitmap对象
     * *
     * @param tempUri
     * *
     * @return
     */
    fun toRoundBitmap(bitmap: Bitmap, tempUri: Uri): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        val roundPx: Float
        val left: Float
        val top: Float
        val right: Float
        val bottom: Float
        val dst_left: Float
        val dst_top: Float
        val dst_right: Float
        val dst_bottom: Float
        if (width <= height) {
            roundPx = (width / 2).toFloat()
            left = 0f
            top = 0f
            right = width.toFloat()
            bottom = width.toFloat()
            height = width
            dst_left = 0f
            dst_top = 0f
            dst_right = width.toFloat()
            dst_bottom = width.toFloat()
        } else {
            roundPx = (height / 2).toFloat()
            val clip = ((width - height) / 2).toFloat()
            left = clip
            right = width - clip
            top = 0f
            bottom = height.toFloat()
            width = height
            dst_left = 0f
            dst_top = 0f
            dst_right = height.toFloat()
            dst_bottom = height.toFloat()
        }

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = 0xff424242.toInt()
        val paint = Paint()
        val src = Rect(left.toInt(), top.toInt(), right.toInt(),
                bottom.toInt())
        val dst = Rect(dst_left.toInt(), dst_top.toInt(),
                dst_right.toInt(), dst_bottom.toInt())
        val rectF = RectF(dst)

        paint.isAntiAlias = true// 设置画笔无锯齿

        canvas.drawARGB(0, 0, 0, 0) // 填充整个Canvas
        paint.color = color

        // 以下有两种方法画圆,drawRounRect和drawCircle
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);//
        // 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        canvas.drawCircle(roundPx, roundPx, roundPx, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint) // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

        return output
    }

    fun getid(): String {
        val time = System.currentTimeMillis().toString()
        val num = (Math.random()*900+100).toInt()
        val id = time+num.toString()
        return id
    }

    fun getWorkId():String{
        val time = System.currentTimeMillis().toString()
        val num = (Math.random()*900+100).toInt()
        val id = num.toString()+time
        return id
    }

    fun Info(TAG:String,msg: String) {
        val strLength = msg.length
        var start = 0
        var end = 2000
        for (i in 0..99) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.d(TAG + i, msg.substring(start, end))
                start = end
                end += 2000
            } else {
                Log.d(TAG, msg.substring(start, strLength))
                break
            }
        }
    }

    @Throws(Exception::class)
    fun encodeBase64File(path: String): String {
        val file = File(path)
        val inputFile = FileInputStream(file)
        val buffer = ByteArray(file.length().toInt())
        inputFile.read(buffer)
        inputFile.close()
        return Base64.encodeToString(buffer, Base64.DEFAULT)
    }

    @Throws(Exception::class)
    fun decoderBase64File(base64Code: String, savePath: String) {
        //byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        if(!File(savePath).parentFile.exists()){
            File(savePath).parentFile.mkdirs()
        }
        val buffer = Base64.decode(base64Code, Base64.DEFAULT)
        val out = FileOutputStream(savePath)
        out.write(buffer)
        out.close()
    }

    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun px2sp(context: Context, pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    fun dp2px(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun sp2px(context: Context, dp: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (dp * fontScale + 0.5f).toInt()
    }

}
