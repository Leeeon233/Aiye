package leon.homework.Utils

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;



/**
 * Created by meitingliu on 2017/6/20.
 */
object ActivityCollector {

    /**
     * 存放activity的列表
     */
    var activities: HashMap<Class<*>, Activity>? = LinkedHashMap()

    /**
     * 添加Activity

     * @param activity
     */
    fun addActivity(activity: Activity, clz: Class<*>) {
        activities!!.put(clz, activity)
    }

    /**
     * 判断一个Activity 是否存在

     * @param clz
     * *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun <T : Activity> isActivityExist(clz: Class<T>): Boolean {
        val res: Boolean
        val activity = getActivity(clz)
        if (activity == null) {
            res = false
        } else {
            res = !(activity.isFinishing || activity.isDestroyed)
        }

        return res
    }

    /**
     * 获得指定activity实例

     * @param clazz Activity 的类对象
     * *
     * @return
     */
    fun <T : Activity> getActivity(clazz: Class<T>): T? {
        return activities!![clazz] as T
    }

    /**
     * 移除activity,代替finish

     * @param activity
     */
    fun removeActivity(activity: Activity) {
        if (activities!!.containsValue(activity)) {
            activities!!.remove(activity.javaClass)
        }
    }

}