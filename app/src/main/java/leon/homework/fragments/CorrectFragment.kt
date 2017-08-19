package leon.homework.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView

import leon.homework.R
import leon.homework.activities.ChooseClassActivity
import leon.homework.activities.ExerciseActivity
import leon.homework.adapter.SubjectAdapter
import leon.homework.javaBean.TodayWork
import leon.homework.sqlite.WorkDao
import org.jetbrains.anko.info
import java.util.ArrayList


class CorrectFragment : BaseFragment() {
    override val layoutResourceId: Int = R.layout.fragment_correct
    private var subjectList = ArrayList<TodayWork>()
    var objAdapter:SubjectAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutResourceId, container, false)
        objAdapter = SubjectAdapter(activity, R.layout.works_list_item, subjectList)
        val listview = view.findViewById(R.id.list_view_correct) as ListView
        listview.setOnItemClickListener({ parent, view, position, id ->
            val subject = subjectList[position]
            val intent = Intent(activity, ChooseClassActivity::class.java)
            intent.putExtra("js",subject.js.toString())
            startActivity(intent)
        })
        objAdapter!!.setNotifyOnChange(true)
        listview.adapter = objAdapter
        updateview()
        return view
    }

    fun updateview(){
        subjectList.clear()
        subjectList.addAll(WorkDao.getInstance().select1())
        objAdapter!!.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        updateview()
    }

    companion object {
        var correctfragment:CorrectFragment? = null
        fun getInstance(): CorrectFragment {
            if(correctfragment==null){
                correctfragment = CorrectFragment()
            }
            return correctfragment!!
        }
    }

}
