package leon.homework.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import leon.homework.app.AppContext
import leon.homework.R
import leon.homework.Utils.Utils
import leon.homework.data.Const
import leon.homework.data.SaveData
import leon.homework.javaBean.ChoiceExercise


class ChoiFragment(val mlistener:Choilistener) : BaseFragment(){
    var qnum: Int? = null
    var isfinished:Int = 0
    var radiogroup_choi: RadioGroup? = null
    override val layoutResourceId: Int = R.layout.layout_choice
    private var title: TextView? = null
    private var optionA: RadioButton? = null
    private var optionB: RadioButton? = null
    private var optionC: RadioButton? = null
    private var optionD: RadioButton? = null
    private var workfinished=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutResourceId, container, false)
        val bundle = arguments
        val choiceExercise = bundle.getParcelable<ChoiceExercise>("object")
        when(SaveData.UserType){
            Const.ISSTUDENT ->{
                stuinit(view,bundle,choiceExercise)
            }
            Const.ISTEACHER ->{
                teainit(view,bundle,choiceExercise)
            }
        }
        return view
    }

    private fun stuinit(view: View,bundle:Bundle,choiceExercise:ChoiceExercise){
        workfinished = bundle.getBoolean("workfinished")
        title = view.findViewById(R.id.question) as TextView
        radiogroup_choi = view.findViewById(R.id.radiogroup_choi) as RadioGroup
        optionA = view.findViewById(R.id.optionA) as RadioButton
        optionB = view.findViewById(R.id.optionB) as RadioButton
        optionC = view.findViewById(R.id.optionC) as RadioButton
        optionD = view.findViewById(R.id.optionD) as RadioButton
        val mtitle = choiceExercise.title
        val options = choiceExercise.choices
        val imgs = choiceExercise.imgname
        title!!.text = Utils.textspan(AppContext.instance!!,mtitle,imgs)
        qnum = choiceExercise.quesnum
        optionA!!.text = options[0]
        optionB!!.text = options[1]
        optionC!!.text = options[2]
        optionD!!.text = options[3]
        radiogroup_choi!!.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.optionA->{
                    mlistener.ChoiResult(qnum!!,"a")
                }
                R.id.optionB->{
                    mlistener.ChoiResult(qnum!!,"b")
                }
                R.id.optionC->{
                    mlistener.ChoiResult(qnum!!,"c")
                }
                R.id.optionD->{
                    mlistener.ChoiResult(qnum!!,"d")
                }
            }
        }

        isfinished = choiceExercise.isfinished
        if(1==isfinished){
            when(choiceExercise.result){
                "a"-> radiogroup_choi!!.check(R.id.optionA)
                "b"-> radiogroup_choi!!.check(R.id.optionB)
                "c"-> radiogroup_choi!!.check(R.id.optionC)
                "d"-> radiogroup_choi!!.check(R.id.optionD)
            }
        }

        if(workfinished){
            optionA!!.isClickable = false
            optionB!!.isClickable = false
            optionC!!.isClickable = false
            optionD!!.isClickable = false
            val tv_answer = view.findViewById(R.id.tv_answer) as TextView
            tv_answer.visibility = View.VISIBLE
            tv_answer.text = "本题答案为："+choiceExercise.answer+" 你的选择为："+choiceExercise.result
        }
    }

    private fun teainit(view: View,bundle:Bundle,choiceExercise:ChoiceExercise){
        val mtitle = choiceExercise.title
        val options = choiceExercise.choices
        val imgs = choiceExercise.imgname
        title = view.findViewById(R.id.question) as TextView
        radiogroup_choi = view.findViewById(R.id.radiogroup_choi) as RadioGroup
        optionA = view.findViewById(R.id.optionA) as RadioButton
        optionB = view.findViewById(R.id.optionB) as RadioButton
        optionC = view.findViewById(R.id.optionC) as RadioButton
        optionD = view.findViewById(R.id.optionD) as RadioButton
        optionA!!.isClickable = false
        optionB!!.isClickable = false
        optionC!!.isClickable = false
        optionD!!.isClickable = false
        title!!.text = Utils.textspan(AppContext.instance!!,mtitle,imgs)
        optionA!!.text = options[0]
        optionB!!.text = options[1]
        optionC!!.text = options[2]
        optionD!!.text = options[3]
        when(choiceExercise.result){
            "A"-> radiogroup_choi!!.check(R.id.optionA)
            "B"-> radiogroup_choi!!.check(R.id.optionB)
            "C"-> radiogroup_choi!!.check(R.id.optionC)
            "D"-> radiogroup_choi!!.check(R.id.optionD)
        }
        val tv_answer = view.findViewById(R.id.tv_answer) as TextView
        tv_answer.text = "本题答案为："+choiceExercise.answer+" 选择为："+choiceExercise.result
    }

    interface Choilistener{
        fun ChoiResult(qnum:Int,result:String)
    }
}
