package leon.homework.extensions

import leon.homework.activities.BaseActivity
import leon.homework.fragments.BaseFragment
import org.jetbrains.anko.support.v4.act

fun BaseFragment.loading(msg: Int) = (act as BaseActivity).loading(msg)
