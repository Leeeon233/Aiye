package leon.homework.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import lecho.lib.hellocharts.gesture.ContainerScrollType
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.view.LineChartView
import leon.homework.R
import leon.homework.sqlite.WorkDao
import org.jetbrains.anko.info
import java.util.ArrayList


class LineChartFragment : BaseFragment() {
    override val layoutResourceId: Int = R.layout.fragment_line_chart
    private var date = ArrayList<String>()
    private var score = ArrayList<Int>()
    private var lineChart: LineChartView? = null
    private val mPointValues = ArrayList<PointValue>()
    private val mAxisXValues = ArrayList<AxisValue>()
    private var subject:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            subject = arguments.getString(LineChartFragment.SUBJECT)
            val js = WorkDao.getInstance().selectScorebySubject(subject!!)
            date.add("0-0")
            score.add(0)
            if(js.length()>0){
                info(js.toString())
                for(i in 1..js.length()){
                    date.add(i,js.getJSONObject("$i").getString("date"))
                    score.add(i,js.getJSONObject("$i").getInt("score"))
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutResourceId, container, false)
        lineChart = view.findViewById(R.id.line_chart) as LineChartView
        getAxisXLables()//获取x轴的标注
        getAxisPoints()//获取坐标点
        initLineChart()//初始化
        return view
    }

    private fun initLineChart() {
        val line = Line(mPointValues).setColor(Color.parseColor("#FFCD41"))  //折线的颜色（橙色）
        val lines = ArrayList<Line>()
        line.shape = ValueShape.CIRCLE//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.isCubic = false//曲线是否平滑，即是曲线还是折线
        line.isFilled = false//是否填充曲线的面积
        line.setHasLabels(true)//曲线的数据坐标是否加上备注
        //      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true)//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true)//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line)
        val data = LineChartData()
        data.lines = lines

        //坐标轴
        val axisX = Axis() //X轴
        axisX.setHasTiltedLabels(true)  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.textColor = Color.BLUE  //设置字体颜色
        //axisX.setName("date");  //表格名称
        axisX.textSize = 10//设置字体大小
        axisX.maxLabelChars = 8 //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.values = mAxisXValues  //填充X轴的坐标名称
        data.axisXBottom = axisX //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true) //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限
        val axisY = Axis()  //Y轴
        axisY.textColor = Color.BLUE
        axisY.name = ""//y轴标注
        axisY.textSize = 10//设置字体大小
        data.axisYLeft = axisY  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边

        //设置行为属性，支持缩放、滑动以及平移
        lineChart!!.isInteractive = true
        lineChart!!.zoomType = ZoomType.HORIZONTAL
        lineChart!!.maxZoom = 2.toFloat()//最大放大比例
        lineChart!!.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL)
        lineChart!!.lineChartData = data
        lineChart!!.visibility = View.VISIBLE
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        val v = Viewport(lineChart!!.maximumViewport)
        v.left = 0f
        v.right = 7f
        lineChart!!.currentViewport = v
    }

    private fun getAxisXLables() {
        for (i in date.indices) {
            mAxisXValues.add(AxisValue(i.toFloat()).setLabel(date[i]))
        }
    }

    private fun getAxisPoints() {
        for (i in score.indices) {
            mPointValues.add(PointValue(i.toFloat(), score[i].toFloat()))
        }
    }


    companion object {
        private val SUBJECT = "subject"
        fun getInstance(subject:String): LineChartFragment {
            val fragment = LineChartFragment()
            val args = Bundle()
            args.putString(LineChartFragment.SUBJECT, subject)
            fragment.arguments = args
            return fragment
        }
    }

}
