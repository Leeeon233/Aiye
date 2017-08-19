package leon.homework.activities

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_choose_class.*
import leon.homework.R
import leon.homework.adapter.ClassAdapter
import leon.homework.javaBean.ClassObject
import leon.homework.sqlite.ClassDao
import org.jetbrains.anko.onClick

class ChooseClassActivity : BaseActivity() {
    override val layoutResourceId: Int = R.layout.activity_choose_class
    private val classes = ArrayList<ClassObject>()
    private var classadapter:ClassAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        classadapter = ClassAdapter(this,R.layout.class_list_item,classes)
        val js = intent.getStringExtra("js")
        //val workid = JSONObject(js).getString("workid")
        classes.addAll(ClassDao.getInstance().selectClasses())
        list_view_choose_class.adapter = classadapter
        list_view_choose_class.setOnItemClickListener { parent, view, position, id ->
            val mclass = classes[position]
            val intent = Intent(this,ChooseStudentActivity::class.java)
            intent.putExtra("classid",mclass.clsid)
            intent.putExtra("js",js)
            startActivity(intent)
        }
        classadapter!!.setNotifyOnChange(true)
        btn_finish.onClick {
            finish()
        }
    }
}
