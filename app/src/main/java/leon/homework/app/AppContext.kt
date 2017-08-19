package leon.homework.app

import android.app.Application
import android.os.Handler
import leon.homework.person.User


/*
 * Created by BC on 2017/2/4 0004.
 */

class AppContext : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        //YunBaManager.start(applicationContext)
        //context.getClass().getClassLoader().getResourceAsStream("assets/"+资源名)
    }
    companion object{
        var instance: AppContext?= null
        var User: User =User()
        var downloadhandler:Handler? = null
    }

}
