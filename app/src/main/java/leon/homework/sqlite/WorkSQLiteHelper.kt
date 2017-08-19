package leon.homework.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import leon.homework.data.Const
import leon.homework.data.SaveData
import org.jetbrains.anko.AnkoLogger

/**
 * Created by BC on 2017/2/20 0020.
 */
class WorkSQLiteHelper(context: Context, name:String, factory: SQLiteDatabase.CursorFactory?=null, version:Int=1): SQLiteOpenHelper(context, name, factory, version),AnkoLogger  {


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE HistoryWork ("+
                "WorkId       VARCHAR( 17 )  PRIMARY KEY ," +
                "Stu_id       VARCHAR( 16 ), " +
                "TeacherAlia  VARCHAR( 20 ), " +
                "Subject      VARCHAR( 10 ), " +
                "WorkDeadline VARCHAR( 10 )," +
                "timeused     VARCHAR( 16 ) default ('0')," +
                "IsFinished   INT default 0, " +
                "totalpoint   INT default 0, " +
                "Questions    VARCHAR( 1000 )," +
                "iscorrect    INT default 0);")
        Log.d("WorkSQLiteHelper","创建表 HistoryWork    数据库名$databaseName")
        if(SaveData.UserType == Const.ISTEACHER){
            db.execSQL("CREATE TABLE RelationalTable ("+
                    "WorkId  VARCHAR( 17 )  ," +
                    "StuId   VARCHAR( 16 ), " +
                    "MId     VARCHAR( 20 ) PRIMARY KEY , " +
                    "TimeUsed     VARCHAR( 16 ) default ('0')," +
                    "IsFinished   INT default 0, " +
                    "TotalPoint   INT default 0 );")
            Log.d("WorkSQLiteHelper","创建表 RelationalTable")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

    }

}