package apk.zeffect.cn.gekotlinling.task.main

import apk.zeffect.cn.gekotlinling.bean.DefaultBean
import apk.zeffect.cn.gekotlinling.mvp.MainIF
import apk.zeffect.cn.gekotlinling.utils.GLCallback
import apk.zeffect.cn.gekotlinling.utils.HttpUtils
import apk.zeffect.cn.gekotlinling.utils.JsonUtils
import okhttp3.Call
import org.json.JSONException
import org.json.JSONObject
import zeffect.cn.common.log.L
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

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
class MainImp(view: MainIF.View) : MainIF.Imp {


    private val mView by lazy { view }
    private val MODULE_CACHE_NAME = "${MainImp::class.java.name}moduledata";

    override fun getModule() {
        mView.showLoading("正在加载数据")
        //先取缓存
        mView.updateBottomType(JsonUtils.parseDefault(mView.getCache(MODULE_CACHE_NAME)))
        //再联网取
        val map = HashMap<String, String>()
        map.put("companyid", "2")
        HttpUtils.postJson(map, HttpUtils.IP + "/index.php/Api2/Index/GetModule", object : GLCallback() {
            override fun onResponse(response: String?, id: Int) {
                L.e("ge ling callback :" + response)
                mView.updateBottomType(JsonUtils.parseDefault(response ?: ""))
                mView.saveCache(MODULE_CACHE_NAME, response ?: "")
            }

            override fun onError(call: Call?, e: Exception?, id: Int) {
            }

            override fun onAfter(id: Int) {
                mView.disDialog()
            }

        })
    }


    override fun init() {
        getModule()
    }


}