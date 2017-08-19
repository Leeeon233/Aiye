package leon.homework.activities

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_choose_student.*
import leon.homework.R
import leon.homework.adapter.StudentAdapter
import leon.homework.javaBean.StudentObject
import leon.homework.sqlite.ClassDao
import leon.homework.sqlite.WorkDao
import org.jetbrains.anko.info
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import org.json.JSONObject

class ChooseStudentActivity : BaseActivity() {
    override val layoutResourceId: Int = R.layout.activity_choose_student
    private var students = ArrayList<StudentObject>()
    private var studentadapter: StudentAdapter?=null
    private var classid:String?=null
    private var workid:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        classid = intent.getStringExtra("classid")
        val js = intent.getStringExtra("js")
        workid = JSONObject(js).getString("workid")
        info(workid)
        students = ClassDao.getInstance().selectStudents(classid!!)
        studentadapter = StudentAdapter(this,R.layout.students_list_item,students,workid)
        list_view_choose_student.adapter = studentadapter
        list_view_choose_student.setOnItemClickListener { parent, view, position, id ->
            val mstudent = students[position]
            val mid = WorkDao.getInstance().selectMid(workid,mstudent.Stu_alia)
            if(!WorkDao.getInstance().tableisexist(mid)){
                toast("此学生还未提交作业")
            }else{
                val intent = Intent(this,CorrectActivity::class.java)
                intent.putExtra("mid",mid)
                intent.putExtra("stuid",mstudent.Stu_alia)
                intent.putExtra("js",js)
                startActivity(intent)
            }

        }
        studentadapter!!.setNotifyOnChange(true)
        btn_finish.onClick {
            finish()
        }
    }
}
