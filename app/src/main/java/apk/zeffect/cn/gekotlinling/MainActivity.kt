package apk.zeffect.cn.gekotlinling

import android.arch.lifecycle.*
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.WindowManager
import apk.zeffect.cn.gekotlinling.adapter.MainBottomAdapter
import apk.zeffect.cn.gekotlinling.bean.DefaultBean
import apk.zeffect.cn.gekotlinling.bean.IndexBean
import apk.zeffect.cn.gekotlinling.ui.CourseActivity
import apk.zeffect.cn.gekotlinling.utils.*
import com.liaoinstan.springview.widget.SpringView
import com.zhy.http.okhttp.OkHttpUtils
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Call
import okhttp3.Response
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import zeffect.cn.common.log.L
import java.lang.Exception
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private fun updateBottomType(types: List<DefaultBean>) {
        mClassTypes.clear()
        mClassTypes.addAll(types)
        mAdapter.notifyDataSetChanged()
        mSpringView.onFinishFreshAndLoad()
    }


    private val mSpringView by lazy {
        find<SpringView>(R.id.springview).apply {
            this.setListener(object : SpringView.OnFreshListener {
                override fun onLoadmore() {
                }

                override fun onRefresh() {
                    mViewModel.getModule()
                }
            })
        }
    }

    private val mViewModel: MainViewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        L.isDebug = true
        initView()
        Looper.myQueue().addIdleHandler { mSpringView.callFresh();false }
        mViewModel.getDatas().observe(this, Observer<List<DefaultBean>> {
            updateBottomType(it ?: emptyList())
        })
    }


    private val mClassTypes = arrayListOf<DefaultBean>()
    private val mAdapter: MainBottomAdapter by lazy { MainBottomAdapter(R.layout.item_layout_main_bottom, mClassTypes) }


    private fun initView() {
        class_type.layoutManager = GridLayoutManager(this, 2)
        class_type.adapter = mAdapter
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val temp = mClassTypes[position]
            startActivity(Intent(this, CourseActivity::class.java).putExtra(Constant.PARAMS, temp))
        }
    }

}


class MainViewModel : ViewModel() {
    private val datas: MutableLiveData<List<DefaultBean>> = MutableLiveData()

    private val playUrl: MutableLiveData<String> = MutableLiveData()

    fun getPlayUrl(): LiveData<String> {
        return playUrl
    }

    fun getDatas(): LiveData<List<DefaultBean>> {
        return datas
    }

    fun getModule() {
        doAsync {
            //再联网取
            val map = HashMap<String, String>()
            map.put("companyid", "2")
            val beans = JsonUtils.parseDefault(HttpUtils.postJson(map, HttpUtils.IP + "/index.php/Api2/Index/GetModule"))
            uiThread {
                datas.value = beans
            }
        }
    }

    fun getTopTypes(bean: DefaultBean) {
        doAsync {
            //联网:{"moduleid":"2","companyid":"2","nums":"50"}
            val map = HashMap<String, String>()
            map.put("moduleid", bean.id)
            map.put("companyid", "2")
            map.put("nums", "50")
            val beans = JsonUtils.parseDefault(HttpUtils.postJson(map, HttpUtils.IP + "/index.php/Api2/Category/getCategory"))
            uiThread {
                datas.value = beans
            }
        }

    }

    fun getCourse(bean: DefaultBean) {
        doAsync {
            //{"companyid":"2","nums":"50","categoryid":"1"}
            val map = HashMap<String, String>()
            map.put("companyid", "2")
            map.put("nums", "50")
            map.put("categoryid", bean.id)
            val beans = JsonUtils.parseDefault(HttpUtils.postJson(map, HttpUtils.IP + "/index.php/Api2/Course/getCourse"))
            uiThread {
                datas.value = beans
            }
        }

    }


    fun getVideos(bean: DefaultBean) {
        doAsync {
            //http://tv.abc5.cn
            val map = HashMap<String, String>()
            //{"uid":"3577","courseid":"601","companyid":"2","nums":"5"}
            map.put("uid", "3577")
            map.put("courseid", bean.id)
            map.put("companyid", "2")
            map.put("nums", "10000")
            val beans = JsonUtils.parseDefault(HttpUtils.postJson(map, HttpUtils.IP + "/index.php/Api2/Video/videoList"))
            uiThread {
                datas.value = beans
                if (beans.isNotEmpty()) {
                    val bean = beans[0]
                    val fileurl = bean.fileurl;
                    val name = fileurl.substring(fileurl.lastIndexOf("/") + 1, fileurl.lastIndexOf("."))
                    getVideo(name)
                }
            }
        }
    }

    fun getVideo(name: String) {
        if (name.isEmpty()) return
        doAsync {
            val map = HashMap<String, String>();
            map.put("uid", "-1")
            map.put("filename", name)
            val newMap = MyUtils.sortMapByKey(map)
            val dataBu = StringBuilder()
            newMap.forEach {
                dataBu.append("${it.value}")
            }
            val time = System.currentTimeMillis()
            newMap.put("sign", MD5Crypto.Md5PassWord(dataBu.append(time).append("tiku").toString()).toLowerCase())//是Value的MD5
            newMap.put("time", "" + time)
            val response: Response = OkHttpUtils.post()
                    .params(newMap)
                    .url("http://119.23.66.94/Api/Vedio/getVideoUrl")
                    .build().execute()
            if (response.isSuccessful) {
                val content = response.body().string()
                val dataJson = JSONObject(content)
                val code = dataJson.getInt("code")
                if (code == 9) {
                    val url = dataJson.getString("url")
                    uiThread {
                        playUrl.value = url
                    }
                }
            }

        }
    }
}
