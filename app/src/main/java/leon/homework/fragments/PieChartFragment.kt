package leon.homework.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.view.PieChartView
import leon.homework.R
import leon.homework.sqlite.WorkDao
import java.util.ArrayList


class PieChartFragment : BaseFragment() {
    override val layoutResourceId: Int = R.layout.fragment_pie_chart
    private var pieChart: PieChartView ?= null
    private var pieChardata = PieChartData()
    private var values: MutableList<SliceValue> = ArrayList()
    private val data = ArrayList<Double>()
    private var totalpoint =0.0
    private val subject = arrayOf("数学", "语文", "英语", "物理", "化学", "生物", "政治", "历史", "地理")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for(s in subject){
            data.add(WorkDao.getInstance().selectAvgPoint(s))
            totalpoint+=WorkDao.getInstance().selectAvgPoint(s)
        }
        var point = 0.0
        for(i in 0..data.size-1){
            point = data[i]/totalpoint
            data[i]=point
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutResourceId, container, false)
        pieChart = view.findViewById(R.id.pie_chart) as PieChartView
        pieChart!!.onValueTouchListener = selectListener
        setPieChartData()
        initPieChart()
        return view
    }

    private fun setPieChartData() {
        for (i in data.indices) {
            val sliceValue = SliceValue(data[i].toFloat(), pickColor())//随机选择颜色
            sliceValue.setLabel(subject[i] + sliceValue.value * 100 + "%")
            values.add(sliceValue)
        }
    }

    private fun pickColor(): Int {
        val COLOR_BLUE = Color.parseColor("#33B5E5")
        val COLOR_VIOLET = Color.parseColor("#AA66CC")
        val COLOR_GREEN = Color.parseColor("#99CC00")
        val COLOR_ORANGE = Color.parseColor("#FFBB33")
        val COLOR_RED = Color.parseColor("#FF4444")
        val COLORS = intArrayOf(COLOR_BLUE, COLOR_VIOLET, COLOR_GREEN, COLOR_ORANGE, COLOR_RED)
        return COLORS[Math.round(Math.random() * (COLORS.size - 1)).toInt()]
    }

    private fun initPieChart() {
        pieChardata.setHasLabels(true)//显示百分比
        pieChardata.setHasLabelsOnlyForSelected(true)//点击显示占的百分比
        pieChardata.setHasLabelsOutside(false)//占的百分比是否显示在饼图外面
        pieChardata.setHasCenterCircle(true)//是否是环形显示
        pieChardata.values = values//填充数据
        pieChardata.centerCircleColor = Color.WHITE//设置环形中间的颜色
        pieChardata.centerCircleScale = 0.5f//设置环形的大小级别
        pieChardata.centerText1 = "科目情况"//环形中间的文字1
        pieChardata.centerText1Color = Color.BLACK//文字颜色
        pieChardata.centerText1FontSize = 14//文字大小

        pieChardata.centerText2 = "科目情况"
        pieChardata.centerText2Color = Color.BLACK
        pieChardata.centerText2FontSize = 18
        /**这里也可以自定义你的字体   Roboto-Italic.ttf这个就是你的字体库 */
        //      Typeface tf = Typeface.createFromAsset(this.getAssets(), "Roboto-Italic.ttf");
        //      data.setCenterText1Typeface(tf);

        pieChart!!.pieChartData = pieChardata
        pieChart!!.isValueSelectionEnabled = true//选择饼图某一块变大
        pieChart!!.alpha = 0.9f//设置透明度
        pieChart!!.circleFillRatio = 1f//设置饼图大小
    }


    private val selectListener = object : PieChartOnValueSelectListener {

        override fun onValueDeselected() {
            // TODO Auto-generated method stub

        }

        override fun onValueSelected(arg0: Int, value: SliceValue) {
            // TODO Auto-generated method stub
            Toast.makeText(activity, "Selected: " + subject[arg0] + value.value * 100 + "%", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun getInstance(): PieChartFragment {
            val fragment = PieChartFragment()
            return fragment
        }
    }

}
