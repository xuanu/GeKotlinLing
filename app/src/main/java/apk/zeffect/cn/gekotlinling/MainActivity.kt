package apk.zeffect.cn.gekotlinling

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.WindowManager
import apk.zeffect.cn.gekotlinling.R.id.class_type
import apk.zeffect.cn.gekotlinling.adapter.MainBottomAdapter
import apk.zeffect.cn.gekotlinling.bean.DefaultBean
import apk.zeffect.cn.gekotlinling.mvp.MainIF
import apk.zeffect.cn.gekotlinling.task.main.MainImp
import apk.zeffect.cn.gekotlinling.ui.CourseActivity
import apk.zeffect.cn.gekotlinling.utils.CacheUtils
import apk.zeffect.cn.gekotlinling.utils.Constant
import com.afollestad.materialdialogs.MaterialDialog
import com.liaoinstan.springview.widget.SpringView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find
import zeffect.cn.common.log.L

class MainActivity : AppCompatActivity(), MainIF.View {
    override fun getCache(name: String): String = CacheUtils.getCache(this, name)

    override fun saveCache(name: String, cache: String) {
        CacheUtils.saveCache(this, name, cache)
    }

    override fun updateBottomType(types: List<DefaultBean>) {
        mClassTypes.clear()
        mClassTypes.addAll(types)
        mAdapter.notifyDataSetChanged()
        mSpringView.onFinishFreshAndLoad()
    }

    private val mDialog: MaterialDialog by lazy {
        MaterialDialog.Builder(this)
                .title("")
                .content("")
                .build()
    }

    override fun showLoading(toast: String) {
        mDialog.builder.title(toast)
        mDialog.show()
    }

    override fun disDialog() {
        mDialog.dismiss()
    }

    private val mImp by lazy { MainImp(this) }

    private val mSpringView by lazy {
        find<SpringView>(R.id.springview).apply {
            this.setListener(object : SpringView.OnFreshListener {
                override fun onLoadmore() {
                }

                override fun onRefresh() {
                    mImp.init()
                }
            })
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        L.isDebug = true
        initView()
        Looper.myQueue().addIdleHandler { mSpringView.callFresh();false }
    }


    private val mClassTypes = arrayListOf<DefaultBean>()
    private val mAdapter: MainBottomAdapter by lazy { MainBottomAdapter(R.layout.item_layout_main_bottom, mClassTypes) };


    private fun initView() {
        class_type.layoutManager = GridLayoutManager(this, 2)
        class_type.adapter = mAdapter
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val temp = mClassTypes[position]
            startActivity(Intent(this, CourseActivity::class.java).putExtra(Constant.PARAMS, temp))
        }
    }


}
