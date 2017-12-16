package apk.zeffect.cn.gekotlinling.ui

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import apk.zeffect.cn.gekotlinling.R
import apk.zeffect.cn.gekotlinling.adapter.MainBottomAdapter
import apk.zeffect.cn.gekotlinling.bean.DefaultBean
import apk.zeffect.cn.gekotlinling.mvp.Contract
import apk.zeffect.cn.gekotlinling.mvp.CourseContract
import apk.zeffect.cn.gekotlinling.mvp.TypeContract
import apk.zeffect.cn.gekotlinling.utils.*
import com.bumptech.glide.Glide
import com.zhy.http.okhttp.OkHttpUtils
import kotlinx.android.synthetic.main.fragment_course.*
import okhttp3.Call
import org.jetbrains.anko.find
import java.lang.Exception

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
class CourseActivity : AppCompatActivity(), CourseContract.View {
    override fun createView(bean: DefaultBean): View {
        val tempView = LayoutInflater.from(this).inflate(R.layout.item_layout_main_bottom, null)
        tempView.find<TextView>(R.id.type_name).text = bean.name
        Glide.with(this).load(bean.icon).into(tempView.find<ImageView>(R.id.type_img))
        return tempView
    }

    private val mTab by lazy { find<TabLayout>(R.id.tab) }
    private val mPages by lazy { find<ViewPager>(R.id.pages) }
    /**底部分类，用于切换底部分类**/


    override fun initTitle(beans: List<DefaultBean>) {
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

    override fun showLoading(toast: String) {
    }

    override fun disDialog() {
    }

    override fun saveCache(name: String, cache: String) {
        CacheUtils.saveCache(this, name, cache)
    }

    override fun getCache(name: String): String = CacheUtils.getCache(this, name)


    private val mParams by lazy { intent.getSerializableExtra(Constant.PARAMS) as DefaultBean }
    private val mImp by lazy { CourseImp(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        mImp.init(mParams)
    }


}

/**课程的实现**/
class CourseImp(view: CourseContract.View) : CourseContract.Imp {
    private val mView = view
    private val COURSE_CACHE_NAME = "${CourseImp::class.java.name}courseTopCache"
    override fun getTopTypes(bean: DefaultBean) {
        mView.showLoading("加载分类中")
        //本地
        mView.initTitle(JsonUtils.parseDefault(mView.getCache(COURSE_CACHE_NAME + bean.id)))
        //联网:{"moduleid":"2","companyid":"2","nums":"50"}
        val map = HashMap<String, String>()
        map.put("moduleid", bean.id)
        map.put("companyid", "2")
        map.put("nums", "50")
        HttpUtils.postJson(map, HttpUtils.IP + "/index.php/Api2/Category/getCategory", object : GLCallback() {
            override fun onResponse(response: String?, id: Int) {
                mView.saveCache(COURSE_CACHE_NAME + bean.id, response ?: "")
                mView.initTitle(JsonUtils.parseDefault(response ?: ""))
            }

            override fun onError(call: Call?, e: Exception?, id: Int) {
            }

            override fun onAfter(id: Int) {
                mView.disDialog()
            }
        })
    }

    override fun init(bean: DefaultBean) {
        getTopTypes(bean)
    }

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
class CourseFragment : Fragment(), TypeContract.View {

    private val mImp by lazy { TypeImp(this) }

    override fun showLoading(toast: String) {

    }

    override fun disDialog() {
    }

    override fun saveCache(name: String, cache: String) {
        CacheUtils.saveCache(context, name, cache)
    }

    override fun getCache(name: String) = CacheUtils.getCache(context, name)

    private val mParams by lazy { arguments.getSerializable(Constant.PARAMS) as DefaultBean }

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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val tempView = inflater?.inflate(R.layout.fragment_course, container, false)
        initView(tempView!!)
        mImp.init(mParams)
        return tempView
    }


    private fun initView(view: View) {
        val mRecy = view.find<RecyclerView>(R.id.recy)
        mRecy.layoutManager = GridLayoutManager(context, 2)
        mRecy.adapter = mAdapte
    }

    override fun update(pCourse: List<DefaultBean>) {
        if (pCourse == null || pCourse.isEmpty()) return
        mCoures.clear()
        mCoures.addAll(pCourse)
        mAdapte.notifyDataSetChanged()
    }

}


class TypeImp(view: TypeContract.View) : TypeContract.Imp {
    private val mView = view
    private val CACHE_NAME = "${TypeImp::class.java.name}cache"
    override fun getCourse(bean: DefaultBean) {
        //本地
        mView.update(JsonUtils.parseDefault(mView.getCache(CACHE_NAME + bean.id)))
        //{"companyid":"2","nums":"50","categoryid":"1"}
        val map = HashMap<String, String>()
        map.put("companyid", "2")
        map.put("nums", "50")
        map.put("categoryid", bean.id)
        HttpUtils.postJson(map, HttpUtils.IP + "/index.php/Api2/Course/getCourse", object : GLCallback() {
            override fun onResponse(response: String?, id: Int) {
                mView.update(JsonUtils.parseDefault(response ?: ""))
                mView.saveCache(CACHE_NAME + bean.id, response ?: "")
            }

            override fun onError(call: Call?, e: Exception?, id: Int) {

            }
        })
    }

    override fun init(bean: DefaultBean) {
        getCourse(bean)
    }

}

