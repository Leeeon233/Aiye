package leon.homework.activities


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.AdapterView
import fr.ganfra.materialspinner.MaterialSpinner
import leon.homework.R
import android.widget.ArrayAdapter
import leon.homework.fragments.LineChartFragment
import leon.homework.fragments.PieChartFragment
import leon.homework.sqlite.WorkDao
import org.jetbrains.anko.info
import org.jetbrains.anko.onItemSelectedListener


class TotalMarkActivity : BaseActivity() {
    override val layoutResourceId: Int = R.layout.activity_total_mark

    private var curFragment : Fragment?= null
    private val SHOWHISTORY = 1
    private val SHOWPIE = 2
    private var KindSpinner : MaterialSpinner ?= null
    private var SubjectSpinner : MaterialSpinner ?= null
    private var kind = 0
    private var subject = 0
    private var KindItems = ArrayList<String>()
    private var SubjectItems = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        SubjectSpinner = findViewById(R.id.subjectspinner) as MaterialSpinner
        KindItems.add("历史成绩")
        KindItems.add("科目平均成绩")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, KindItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        KindSpinner = findViewById(R.id.kindspinner) as MaterialSpinner
        KindSpinner!!.adapter = adapter
        KindSpinner!!.onItemSelectedListener{
            onItemSelected { adapterView, view, i, l ->
                info("点击了 $i")
                if(kind != i){
                    when(kind){
                        SHOWHISTORY ->{
                            SubjectSpinner!!.visibility = View.VISIBLE
                        }
                        SHOWPIE ->{
                            SubjectSpinner!!.visibility = View.GONE
                            SubjectSpinner!!.setSelection(0)
                            setCurFragment(SHOWPIE,"")
                        }
                    }
                }
            }
        }

           /* */


        SubjectItems = WorkDao.getInstance().selectAllSubject()
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, SubjectItems)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        SubjectSpinner!!.adapter = adapter2
        SubjectSpinner!!.onItemSelectedListener{
            onItemSelected { adapterView, view, i, l ->
                if(kind == SHOWHISTORY){
                    setCurFragment(kind,SubjectItems[i])
                }
            }
        }
        if(SubjectItems.size>0)
        setDefaultFragment()
    }

    private fun setDefaultFragment() {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.id_content, LineChartFragment.getInstance(SubjectItems[0]))
        transaction.commit()
    }

    private fun setCurFragment(kind:Int,subject: String){
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        when(kind){
            SHOWHISTORY ->{
                val fragment = LineChartFragment.getInstance(subject)
                ft.show(fragment)
                ft.remove(curFragment!!)
                curFragment = fragment
                ft.commit()
            }
            SHOWPIE -> {
                val fragment = PieChartFragment.getInstance()
                ft.show(fragment)
                ft.remove(curFragment!!)
                curFragment = fragment
                ft.commit()
            }
        }
    }

}
