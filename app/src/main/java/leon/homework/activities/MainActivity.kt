package leon.homework.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.ashokvarma.bottomnavigation.BadgeItem
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import io.yunba.android.manager.YunBaManager
import leon.homework.R
import leon.homework.data.Const
import leon.homework.data.SaveData
import leon.homework.fragments.CorrectFragment
import leon.homework.fragments.MessageFragment
import leon.homework.fragments.MineFragment
import leon.homework.fragments.WriteFragment
import leon.homework.sqlite.ChatDao
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.jetbrains.anko.info
import java.util.*



class MainActivity : BaseActivity(), BottomNavigationBar.OnTabSelectedListener {

    val updatereceiver = updateReceiver()
    override val layoutResourceId: Int = R.layout.activity_main
    private val badgemessage = BadgeItem()
    private var fragments: ArrayList<Fragment>? = null
    private var navigationBar :BottomNavigationBar ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        YunBaManager.start(applicationContext)
        fragments = ArrayList<Fragment>()
        initView()
        registerBroadCast()
        YunBaManager.subscribe(applicationContext, arrayOf("aiye"), object : IMqttActionListener {
            override fun onSuccess(arg0: IMqttToken) {
                Log.d("YunBaApplication", "订阅频道成功: "+arg0.topics[0])
            }

            override fun onFailure(arg0: IMqttToken, arg1: Throwable) {
                Log.d("YunBaApplication", "Subscribe topic failed",arg1)
            }
        })
        updateview()
    }

    override fun onPause() {
        super.onPause()
        info("OnPause")
    }

    override fun onResume() {
        super.onResume()
        info("OnResume")
    }

    override fun onStop() {
        super.onStop()
        info("OnStop")
    }
    override fun onNewIntent(intent: Intent) {

        super.onNewIntent(intent)
        info("MainACtiviry  onNewIntent")
        val status = intent.getIntExtra("message",0)
        info("状态"+status)
        //setCurFragment(status)
        info("sss")
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.remove(fragments!![1])
        ft.commit()
        navigationBar!!.selectTab(status)
    }

    /**
     * 创建视图
     */
    private fun initView() {

        //得到BottomNavigationBar控件
        navigationBar = findViewById(R.id.bottom_navigation_bar) as BottomNavigationBar
        //设置BottomNavigationBar的模式
        navigationBar!!.setMode(BottomNavigationBar.MODE_DEFAULT)
        //设置BottomNavigationBar的背景风格
        navigationBar!!.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)



        //我这里增加了3个Fragment
        val BNI_message = BottomNavigationItem(R.mipmap.ic_email_black_48dp, "消息").setBadgeItem(badgemessage)
        val BNI_write = BottomNavigationItem(R.mipmap.ic_local_library_black_48dp, "作业")
        val BNI_mine = BottomNavigationItem(R.mipmap.ic_person_black_48dp, "我的")
        BNI_message.setActiveColor("#25a2f3")
        BNI_write.setActiveColor("#25a2f3")
        BNI_mine.setActiveColor("#25a2f3")
        //BottomNavigationItem("底部导航ico","底部导航名字")



        navigationBar!!.addItem(BNI_message)
                .addItem(BNI_write)
                .addItem(BNI_mine)
                .setFirstSelectedPosition(0)//默认选择索引为1的菜单
                .initialise()//对导航进行重绘

        fragments = getFragments()
        setDefaultFragment(0)
        navigationBar!!.setTabSelectedListener(this)
    }

    /**
     * 设置默认显示的fragment
     */
    private fun setDefaultFragment(flag:Int) {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.id_content, fragments!![flag])
        transaction.commit()
    }
    /*
     *跳转
     */
    private fun setCurFragment(position: Int){
        if (fragments != null) {
            if (position < fragments!!.size) {
                val fm = supportFragmentManager
                val ft = fm.beginTransaction()
                when(position){
                    0->{
                        if(fragments!![0].isAdded){
                            ft.show(fragments!![0])
                        }
                        else{
                            ft.add(R.id.id_content,fragments!![0])
                            ft.show(fragments!![0])
                        }
                        ft.hide(fragments!![1])
                        ft.hide(fragments!![2])
                        ft.commit()
                    }
                    1->{
                        if(fragments!![1].isAdded){
                            ft.show(fragments!![1])
                        }
                        else{
                            ft.add(R.id.id_content,fragments!![1])
                            ft.show(fragments!![1])
                        }
                        ft.hide(fragments!![0])
                        ft.hide(fragments!![2])
                        ft.commit()
                    }
                    2->{
                        if(fragments!![2].isAdded){
                            ft.show(fragments!![2])
                        }
                        else{
                            ft.add(R.id.id_content,fragments!![2])
                            ft.show(fragments!![2])
                        }
                        ft.hide(fragments!![1])
                        ft.hide(fragments!![0])
                        ft.commit()
                    }
                }
            }
        }
    }
    /**
     * 获取fragment
     * @return fragment列表
     */
    private fun getFragments(): ArrayList<Fragment> {
        val fragments = ArrayList<Fragment>()
        fragments.add(MessageFragment.getInstance())
        if(1 == SaveData.UserType){
            fragments.add(WriteFragment.getInstance())
        }else if(2 == SaveData.UserType){
            fragments.add(CorrectFragment.getInstance())
        }
        fragments.add(MineFragment.getInstance())
        return fragments
    }


    override fun onTabSelected(position: Int) {
        setCurFragment(position)
    }

    override fun onTabUnselected(position: Int) {

    }

    override fun onTabReselected(position: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(updatereceiver)
    }

    fun registerBroadCast(){
        val intentFilter = IntentFilter()
        intentFilter.addAction(Const.UPDATE_MAINACTIVITY_UI)
        intentFilter.addAction(Const.ACTION_FINISH_MAIN)
        registerReceiver(updatereceiver,intentFilter)
    }
    inner class updateReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action){
                Const.UPDATE_MAINACTIVITY_UI->{
                    updateview()
                }
                Const.ACTION_FINISH_MAIN->{
                    finish()
                }
            }
        }
    }

    fun updateview(){
        val num = ChatDao.getInstance().selectunread()
        if(0==num){
            badgemessage.hide()
        }else{
            badgemessage.show()
            badgemessage.setText(ChatDao.getInstance().selectunread().toString())
        }

    }
}