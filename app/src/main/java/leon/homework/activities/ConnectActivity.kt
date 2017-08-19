package leon.homework.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.connect.*

import leon.homework.R
import org.jetbrains.anko.onClick

/**
 * Created by mjhzds on 2017/4/5.
 */

class ConnectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.connect)
        btn_finish.onClick { finish() }
    }
}
