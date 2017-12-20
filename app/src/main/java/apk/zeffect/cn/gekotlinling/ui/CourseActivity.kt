package apk.zeffect.cn.gekotlinling.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import apk.zeffect.cn.gekotlinling.MainViewModel
import apk.zeffect.cn.gekotlinling.R
import apk.zeffect.cn.gekotlinling.adapter.DefaultAdapte
import apk.zeffect.cn.gekotlinling.adapter.MainBottomAdapter
import apk.zeffect.cn.gekotlinling.bean.DefaultBean
import apk.zeffect.cn.gekotlinling.utils.Constant
import com.bumptech.glide.Glide
import com.liaoinstan.springview.widget.SpringView
import org.jetbrains.anko.find

/**
 * <pre>
 *      author  ：zzx
 *      e-mail  ：zhengzhixuan18@gmail.com
 *      time    ：2017/12/14
 *      desc    ：
 *      version:：1.0
 * </pre>
 * @author zzx
 */
class CourseActivity : AppCompatActivity() {


    fun createView(bean: DefaultBean): View {
        val tempView = LayoutInflater.from(this).inflate(R.layout.item_layout_main_bottom, null)
        tempView.find<TextView>(R.id.type_name).text = bean.name
        Glide.with(this).load(bean.icon).into(tempView.find<ImageView>(R.id.type_img))
        return tempView
    }

    private val mTab by lazy { find<TabLayout>(R.id.tab) }
    private val mPages by lazy { find<ViewPager>(R.id.pages) }
    /**底部分类，用于切换底部分类**/


    fun initTitle(beans: List<DefaultBean>) {
        mTab.removeAllTabs()
        if (beans == null || beans.isEmpty()) return
        val titls = arrayListOf<String>()
        val mFragments = (0 until beans.size).map {
            CourseFragment().apply {
                this.arguments = Bundle().apply {
                    this.putSerializable(Constant.PARAMS, beans[it])
                    titls.add(beans[it].name)
                }
            }
        }
        val adapter = CourseAdapter(supportFragmentManager, mFragments, titls)
        mPages.adapter = adapter
        mTab.setupWithViewPager(mPages)


    }


    private val mParams by lazy { intent.getSerializableExtra(Constant.PARAMS) as DefaultBean }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        mViewModel.getDatas().observe(this, Observer { initTitle(it ?: emptyList()) })
        mViewModel.getTopTypes(mParams)
    }

    private val mViewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }


}


class CourseAdapter(manager: FragmentManager, fragments: List<Fragment>, titls: List<String>) : FragmentPagerAdapter(manager) {
    private val mFragment = fragments
    private val mTitls = titls
    override fun getItem(position: Int) = mFragment[position]


    override fun getCount() = mFragment.size

    override fun getPageTitle(position: Int): CharSequence {
        return mTitls[position]
    }

}


/***
 *
 */
class CourseFragment : Fragment() {

    private val mContext by lazy { context }


    private val mParams by lazy { arguments?.getSerializable(Constant.PARAMS) as DefaultBean }

    private val mCoures = arrayListOf<DefaultBean>()

    private val mAdapte by lazy {
        MainBottomAdapter(R.layout.item_layout_main_bottom, mCoures).apply {
            this.setOnItemClickListener { adapter, view, position ->
                startActivity(android.content.Intent(context, PlayActivity::class.java).putExtra(Constant.PARAMS, mCoures[position]))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private var mView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mView == null) {
            mView = inflater?.inflate(R.layout.fragment_course, container, false)
            initView(mView!!)
            mViewModel.getDatas().observe(this, Observer { update(it ?: emptyList()) })
            Looper.myQueue().addIdleHandler { mSpringView.callFresh();false }
        }
        return mView
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private lateinit var mSpringView: SpringView


    private fun initView(view: View) {
        val mRecy = view.find<RecyclerView>(R.id.recy)
        mSpringView = view.find(R.id.springview)
        mRecy.layoutManager = GridLayoutManager(context, 2)
        mRecy.adapter = mAdapte
        val mEmptyView = LayoutInflater.from(context).inflate(R.layout.layout_empty, mSpringView, false)
        mAdapte.emptyView = mEmptyView
        mSpringView.setListener(object : SpringView.OnFreshListener {
            override fun onLoadmore() {
            }

            override fun onRefresh() {
                mViewModel.getCourse(mParams)
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    fun update(pCourse: List<DefaultBean>) {
        mCoures.clear()
        mCoures.addAll(pCourse)
        mAdapte.notifyDataSetChanged()
        mSpringView.onFinishFreshAndLoad()
    }

    private val mViewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }


}

