package leon.homework.UI

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import leon.homework.R





/**
 * Created by BC on 2017/3/21 0021.
 */

class TuyaLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs), View.OnClickListener{
    private var rubber: ImageView? = null
    private var pencil: ImageView? = null
    private var undo:ImageView? =null
    private var reset: ImageView? = null
    private var save: ImageView? = null
    private var rubbersizebar: SeekBar? = null
    private var mtuyaView: TuyaView? = null
    var mlistener:OnclickSaveListener? =null
    var workid:String = "aiye"
    var quesid:String = "123456"
    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val root = inflater.inflate(R.layout.tuya_layout,this)
        initView(root)
    }

    private fun initView(root: View) {
        mtuyaView = root.findViewById(R.id.tuyaview) as TuyaView
        rubber = root.findViewById(R.id.imgview_rubber) as ImageView
        pencil = root.findViewById(R.id.imgview_pencil) as ImageView
        reset = root.findViewById(R.id.imgview_reset) as ImageView
        undo = root.findViewById(R.id.imgview_undo) as ImageView
        save = root.findViewById(R.id.imgview_save) as ImageView
        rubber!!.setOnClickListener(this)
        pencil!!.setOnClickListener(this)
        reset!!.setOnClickListener(this)
        undo!!.setOnClickListener(this)
        save!!.setOnClickListener(this)
        rubbersizebar =root.findViewById(R.id.tuya_seekbar) as SeekBar
        rubbersizebar!!.max = 150
        rubbersizebar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar) {

            }

            override fun onStopTrackingTouch(p0: SeekBar) {

            }

            override fun onProgressChanged(p0: SeekBar, progress: Int, fromUser: Boolean) {
                mtuyaView!!.selectPaintSize(progress)
            }

        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.imgview_pencil -> {
                mtuyaView!!.selectPaintStyle(0)
            }
            R.id.imgview_reset -> {
                mtuyaView!!.redo()
            }
            R.id.imgview_undo -> {
                mtuyaView!!.undo()
            }
            R.id.imgview_rubber -> {
                mtuyaView!!.selectPaintStyle(1)
            }
            R.id.imgview_save ->{
                mtuyaView!!.saveToSDCard(workid,quesid)
                mlistener!!.changImg()
            }
        }
    }
    interface OnclickSaveListener{
        fun changImg()
    }

}
