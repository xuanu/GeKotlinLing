package apk.zeffect.cn.gekotlinling.utils

import android.content.Context
import apk.zeffect.cn.gekotlinling.bean.DefaultBean
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.Callback
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import zeffect.cn.common.encode.Md5Encrypt
import zeffect.cn.common.file.FileUtils
import zeffect.cn.common.log.L
import java.io.File
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

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
object HttpUtils {
    val IP: String = "http://tv.abc5.cn/"
    val GE_LING = "6xdusmo1vmjgyoak81se7bddyuvg60n6"

    fun postJson(params: Map<String, String>, url: String, callback: Callback<String>) {
        val paramString = buildJson(params)
        if (paramString.isEmpty()) return
        OkHttpUtils.postString()
                .mediaType(MediaType.parse("appliaction/json; charset=utf-8"))
                .content(paramString)
                .url(url)
                .build()
                .execute(callback)
    }

    fun postJson(params: Map<String, String>, url: String): Response {
        val paramString = buildJson(params)
        return OkHttpUtils.postString()
                .mediaType(MediaType.parse("appliaction/json; charset=utf-8"))
                .content(paramString)
                .url(url)
                .build()
                .execute()
    }


    fun buildJson(params: Map<String, String>): String {
        try {
            val tempParams = MyUtils.sortMapByKey(params)
            val upJson = JSONObject()
            val dataJson = JSONObject()
            val dataBu = StringBuilder()
            tempParams.forEach {
                dataJson.put(it.key, it.value)
                if (dataBu.isEmpty()) dataBu.append(it.value)
                else dataBu.append(",${it.value}")
            }
            dataBu.append(GE_LING)
            upJson.put("data", Base64.encode(dataJson.toString().toByteArray()))//是参数的JSON
            upJson.put("sign", MD5Crypto.Md5PassWord(dataBu.toString()).toLowerCase())//是Value的MD5
            return upJson.toString()
        } catch (e: JSONException) {
            return ""
        }

    }
}

/***
 * 用于控制缓存
 */
object CacheUtils {
    /**首页，几个推荐的专题或课程缓存**/
    val MAIN_GEN_CACHE = "main_gen_cache"

    fun getCache(context: Context, name: String): String {
        if (context == null || name.isEmpty()) return ""
        return FileUtils.read(File(context.cacheDir, name).absolutePath)
    }

    fun saveCache(context: Context, name: String, content: String): Boolean {
        if (context == null || name.isEmpty()) return false
        var saveContent = content
        if (saveContent == null) saveContent = ""
        return FileUtils.write(File(context.cacheDir, name).absolutePath, saveContent)
    }
}


abstract class GLCallback : Callback<String>() {

    override fun parseNetworkResponse(response: Response?, id: Int): String {
        val content = response?.body()?.string()
        L.e("http　response :" + content)
        var data = ""
        try {
            val contentJson = JSONObject(content)
            if (!contentJson.isNull("data")) {
                val dataString = contentJson.getString("data")
                data = String(Base64.decode(dataString))
            }
        } catch (e: JSONException) {

        }
        return data
    }
}


object JsonUtils {
    fun toMainBottom(content: String): DefaultBean? {
        val dataJson = JSONObject(content)
        return DefaultBean(if (!dataJson.isNull("id")) dataJson.getString("id") else ""
                , if (!dataJson.isNull("name")) dataJson.getString("name") else ""
                , if (!dataJson.isNull("icon")) dataJson.getString("icon") else ""
                , if (!dataJson.isNull("bgurl")) dataJson.getString("bgurl") else ""
                , if (!dataJson.isNull("icon2")) dataJson.getString("icon2") else ""
                , if (!dataJson.isNull("icon3")) dataJson.getString("icon3") else ""
                , if (!dataJson.isNull("fileurl")) dataJson.getString("fileurl") else ""
        )
    }

    fun parseDefault(data: String): List<DefaultBean> {
        if (data.isEmpty()) return Collections.emptyList()
        val bottoms = arrayListOf<DefaultBean>()
        try {
            val json = JSONObject(data)
            val code = json.getInt("code")
            if (code == 200) {
                val lists = json.getJSONArray("lists")
                (0 until lists.length()).mapNotNullTo(bottoms) { JsonUtils.toMainBottom(lists.getString(it)) }
            }
        } catch (e: JSONException) {
        }
        return bottoms
    }

    fun parseDefault(response: Response?): List<DefaultBean> {
        if (response?.isSuccessful == false) return emptyList()
        val content = response?.body()?.string()
        var data = ""
        try {
            val contentJson = JSONObject(content)
            if (!contentJson.isNull("data")) {
                val dataString = contentJson.getString("data")
                data = String(Base64.decode(dataString))
            }
            if (data.isEmpty()) return Collections.emptyList()
            val bottoms = arrayListOf<DefaultBean>()
            val json = JSONObject(data)
            val code = json.getInt("code")
            if (code == 200) {
                val lists = json.getJSONArray("lists")
                (0 until lists.length()).mapNotNullTo(bottoms) { JsonUtils.toMainBottom(lists.getString(it)) }
            }
            return bottoms
        } catch (e: JSONException) {
            return emptyList()
        }
    }

}


object Constant {
    val PARAMS = "params"
}