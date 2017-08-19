package leon.homework.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_history_work.*
import leon.homework.R
import leon.homework.adapter.SubjectAdapter
import leon.homework.app.AppContext
import leon.homework.javaBean.TodayWork
import leon.homework.sqlite.WorkDao
import org.jetbrains.anko.onClick
import java.util.*

class HistoryWorkActivity : AppCompatActivity() {
    private var subjectList = ArrayList<TodayWork>()
    var objAdapter: SubjectAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_work)
        btn_finish.onClick { finish() }
        subjectList.addAll(WorkDao.getInstance().selectIsfinishedWorks())
        objAdapter = SubjectAdapter(this, R.layout.works_list_item, subjectList)
        val listview = findViewById(R.id.list_view) as ListView
        objAdapter!!.setNotifyOnChange(true)
        listview.adapter = objAdapter
        listview.setOnItemClickListener({ parent, view, position, id ->
            val subject = subjectList[position]
            val intent = Intent(AppContext.instance!!, ExerciseActivity::class.java)
            intent.putExtra("exercise",subject.js.toString())
            intent.putExtra("isfinished",true)
            startActivity(intent)
        })
        objAdapter!!.notifyDataSetChanged()
    }
}
