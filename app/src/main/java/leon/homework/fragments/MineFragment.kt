package leon.homework.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import leon.homework.R
import leon.homework.activities.*
import leon.homework.data.SaveData
import org.jetbrains.anko.info


class MineFragment : BaseFragment(),View.OnClickListener{
    override fun onClick(v: View) {
        val context = activity
        when (v.id) {
            R.id.btn_settings -> {
                val intent1 = Intent(context, SettingsActivity::class.java)
                context.startActivity(intent1)
            }
            R.id.historyWork -> {
                val intent2 = Intent(context, HistoryWorkActivity::class.java)
                context.startActivity(intent2)
            }
            R.id.personalData -> {
                val intent3 = Intent(context, PersonalData::class.java)
                context.startActivity(intent3)
            }
            R.id.totalMark -> {
                val intent4 = Intent(context, TotalMarkActivity::class.java)
                context.startActivity(intent4)
            }
            R.id.downloadEx -> {
                val intent5 = Intent(context, DownloadActivity::class.java)
                context.startActivity(intent5)
            }
            else -> info("bug")
        }
    }

    override val layoutResourceId: Int = R.layout.fragment_mine
    /*private var mParam1: String? = null
    private var masterLayout :MasterLayout? = null
    private val mhandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Const.DOWNLOAD_OVER -> {
                    masterLayout!!.finalAnimation()
                    toast("下载完成")
                    info("开始解压")
                    ZipUtil.unzipFiles(File(Const.DOWNLOAD_PATH+"questions.zip"),Const.DATA_PATH)
                    info("解压完成")
                    WorkQuesDao.getInstance().execSQL(FileUtil.loadFile(Const.DATA_PATH+"sqls.sql"))
                    WorkQuesDao.getInstance().execSQL(FileUtil.loadFile(Const.DATA_PATH+"ques_choicques.sql"))
                }
                Const.DOWNLOAD_UPDATE_PROGRESS ->{
                    masterLayout!!.cusview.setupprogress(msg.arg1)
                }
                Const.DOWNLOAD_ERROR ->{
                    masterLayout!!.reset()
                    toast("服务器异常，重试")
                }
                Const.DOWNLOAD_FILEEXIST->{
                    info("已存在")
                    val s = FileUtil.loadFile(Const.DATA_PATH+"ques_tfques.sql")
                    WorkQuesDao.getInstance().execSQL(s)
                    WorkQuesDao.getInstance().execSQL(FileUtil.loadFile(Const.DATA_PATH+"ques_choicques.sql"))
                }
                else -> {
                    toast("???")
                }
            }
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
        }
        AppContext.downloadhandler = mhandler*/
    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutResourceId ,container,false)
        initComponent(view)
        /*masterLayout = view.findViewById(R.id.MasterLayout) as MasterLayout
        masterLayout!!.setOnClickListener{
            masterLayout!!.animation()   //Need to call this method for animation and progression
            when (masterLayout!!.flg_frmwrk_mode) {
                1 -> {
                    //download()
                    val s1 = "DROP TABLE IF EXISTS `choicques`;CREATE TABLE `choicques` (`choic_id` varchar(9) DEFAULT NULL,`choic_sbj` varchar(200) DEFAULT NULL,`choic_A` varchar(80) DEFAULT NULL,`choic_B` varchar(80) DEFAULT NULL,`choic_C` varchar(80) DEFAULT NULL,`choic_D` varchar(80) DEFAULT NULL,`choic_ans` varchar(5) DEFAULT NULL,`choic_img` varchar(9) DEFAULT NULL) ;INSERT INTO `choicques` VALUES ('405010200','下面各数中，读出两个零的数是 （  ）','A，5010040',' B，5104000','C，5001004 ','D,2','A',NULL),('405010201','与千万位相邻的两个计数单位是（    ）。','A．万位与百位',' B.亿位与百万位 ',' C.万位与百万位','D,2','B',NULL),('405010202','两位数乘三位数，积(      )是五位数。','A．一定 ','B.不可能',' C.不一定','D,2','C',NULL),('405010203','从一点出发可以画（     ）条射线。','A.1 ','B.2 ','C.无数条','D,2','C',NULL),('405010204','24×6=144，当因数6扩大100倍，另一个因数不变，积是（     ）。','A．1440 ','B.14400 ','C.144','D,2','B',NULL),('405010205','百位、百万位是（     ）。','A、位数  ','B、数位  ','C、计数单位','D,2','A',NULL),('405010206','读数时，每级末尾不管有几个0。（    ）','A、都不读   ','B、都要读  ','C、只读一个零。','D,2','A',NULL),('405010207','98969○100101，在○里填上（    ）。','A、＞ ','B、＜   ',' C、＝','D,2','B',NULL),('405010208','299000○30万，在○里应填写（）。','A、＝  ','B、≈ ',' C、＞','D,2','B',NULL),('405010209','624比X少36，正确的等式是（） ','A、624－X＝36  ','B、X＋36＝624 ','C、X－624＝36','D,2','C',NULL),('405010210','1个数是720万，另一个数比它少150万，这个数是（    ）。 ','A、870万 ','B、570  ','C、570万','D,2','C',NULL),('405010211','不属于锐角的是（    ）。 ','A．89° ','B．91°','C．30°','D,2','C',NULL),('405010212','下面哪个数的近似数是67万（    ）。 ','A．675000	 ','B．663000	','C．666000','D,2','C',NULL),('405010213','下列哪一句话是错误的（    ）。 ','A．平行四边形两组对边分别平行。	 ','B．等腰三角形至少有两个锐角。','C．平行线无限延长也可能相交。','D,2','C',NULL),('405010214','要使□42÷36的商是两位数，□里最小应填（    ）。 ','A．2 ','B．3','C．4','D,2','B',NULL),('405010215','把一个长方形框架拉成一个平行四边形，这个平行四边形的周长比原长方形的周长（   ）。 ','A．大	','B．小','C．一样大 ','D,2','C',NULL);"
                    WorkQuesDao.getInstance().execSQL(s1)
                    val s2 = "DROP TABLE IF EXISTS `tfques`;CREATE TABLE `tfques` (`tf_id` varchar(9) DEFAULT NULL,`tf_sbj` varchar(100) DEFAULT NULL,`tf_ans` varchar(5) DEFAULT NULL,`tf_img` varchar(9) DEFAULT NULL);INSERT INTO `tfques` VALUES ('105010100','像0,1,2,3,4,5,6······这样的数叫自然数。','1','1'),('105010101','18从左边起的第一位是个位。','0','2'),('105010102','小明在小刚的前面，小刚在小明的后面。','1','3'),('105010103','1+1等于2。','1','0'),('105010104','正方形有三条边。','0','3'),('105010105','130读作一百三十。','1','4'),('105010106','1-1=0。','1','0'),('105010107','猴子有两根香蕉吃了一根还剩一根。','1','0'),('105010108','189左边第一位是百位','1','0'),('105010109','189左边第二位是百位','0','0'),('105010110','189左边第三位是百位','0','0'),('105010111','1+3=5','0','0'),('105010112','4+3=7','1','0'),('105010113','4+3=8','0','0'),('105010114','3-1=1','0','0'),('105010115','6-1=1','0','0');"
                    WorkQuesDao.getInstance().execSQL(s2)
                    val s3 = "DROP TABLE IF EXISTS `fillques`;CREATE TABLE `fillques` (`fill_id` varchar(9) DEFAULT NULL,`fill_sbj` varchar(100) DEFAULT NULL,`fill_ans` varchar(20) DEFAULT NULL,`fill_img` varchar(9) DEFAULT NULL);INSERT INTO `fillques` VALUES ('000001','3+2=_','5','00000'),('000002','3+4=_','6','0000000'),('000002','3+22=_','25','0000000');"
                    WorkQuesDao.getInstance().execSQL(s3)
                    //masterLayout!!.cusview.setupprogress(90)
                    //Start state. Call your method
                }
                2 -> {
                    //Running state. Call your method
                }
                3 -> {
                    //End state. Call your method
                }
            }
        }*/
        return view
    }

    fun initComponent(view:View) {
        val setBtn = view.findViewById(R.id.btn_settings) as ImageButton
        val headIcon = view.findViewById(R.id.myImg) as ImageView
        //val workPro = view.findViewById(R.id.process) as TextView
        //val totalAssess = view.findViewById(R.id.achievementTv) as TextView
        val username = view.findViewById(R.id.userIdTv) as TextView
        val historyWork = view.findViewById(R.id.historyWork) as Button
        val personalData = view.findViewById(R.id.personalData) as Button
        val totalMark = view.findViewById(R.id.totalMark) as Button
        val  downloadEx = view.findViewById(R.id.downloadEx) as Button
        setBtn.setOnClickListener(this)
        historyWork.setOnClickListener(this)
        personalData.setOnClickListener(this)
        totalMark.setOnClickListener(this)
        downloadEx.setOnClickListener(this)
        headIcon.setImageBitmap(BitmapFactory.decodeFile(SaveData.avatar_path))
        username.text = SaveData.name
    }

    /*fun download(){
        async() {
            val url = Const.DownloadQuestionsUrl
            val path = Const.DOWNLOAD_PATH
            Downloader().Download(url,path,"1.zip")
            Downloader().Download(url,path,"2.zip")
        }
    }*/


    companion object {
        var minefragment :MineFragment? =null
        fun getInstance(): MineFragment {
            if(minefragment==null){
                minefragment = MineFragment()
            }
            return minefragment!!
        }
    }
}
