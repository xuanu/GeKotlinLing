package apk.zeffect.cn.gekotlinling.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import apk.zeffect.cn.gekotlinling.R
import apk.zeffect.cn.gekotlinling.bean.DefaultBean
import apk.zeffect.cn.gekotlinling.mvp.PlayContract
import apk.zeffect.cn.gekotlinling.utils.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shuyu.gsyvideoplayer.listener.StandardVideoAllCallBack
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.zhy.http.okhttp.OkHttpUtils
import okhttp3.Call
import okhttp3.Response
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.json.JSONException
import org.json.JSONObject
import zeffect.cn.common.log.L
import java.lang.Exception
import java.lang.StringBuilder

/**
 * <pre>
 *      author  ：zzx
 *      e-mail  ：zhengzhixuan18@gmail.com
 *      time    ：2017/12/15
 *      desc    ：
 *      version:：1.0
 * </pre>
 * @author zzx
 */
class PlayActivity : AppCompatActivity(), PlayContract.View, StandardVideoAllCallBack {
    //    private val mChoseBtn by lazy { find<Button>(R.id.chose_video) }
    private val mVideos by lazy { find<RecyclerView>(R.id.videos) }

    override fun showVideos() {

    }

    override fun closeView() {

    }

    override fun showToast(toast: String) {
        toast(toast ?: "")
    }

    private var playPosition = -1

    override fun changePlayIndex(i: Int) {
        playPosition = i
    }

    override fun onClickResumeFullscreen(url: String?, vararg objects: Any?) {
    }

    override fun onEnterFullscreen(url: String?, vararg objects: Any?) {
    }

    override fun onClickResume(url: String?, vararg objects: Any?) {
    }

    override fun onClickSeekbarFullscreen(url: String?, vararg objects: Any?) {
    }

    override fun onClickBlankFullscreen(url: String?, vararg objects: Any?) {
    }

    override fun onPrepared(url: String?, vararg objects: Any?) {
    }

    override fun onClickStartIcon(url: String?, vararg objects: Any?) {
    }

    override fun onAutoComplete(url: String?, vararg objects: Any?) {
        //播放完成，放下一个。找到当前和下一个。
        playNext()
    }


    private fun playNext() {
        if (playPosition + 1 > mBeans.size - 1) return
        val bean = mBeans.get(playPosition + 1)
        val fileurl = bean.fileurl;
        val name = fileurl.substring(fileurl.lastIndexOf("/") + 1, fileurl.lastIndexOf("."))
        mImp.getVideo(playPosition, name)
    }

    override fun onQuitSmallWidget(url: String?, vararg objects: Any?) {
    }

    override fun onTouchScreenSeekVolume(url: String?, vararg objects: Any?) {
    }

    override fun onClickBlank(url: String?, vararg objects: Any?) {
        //点击屏蔽会调用这一个
        mVideos.visibility = if (mVideos.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
    }

    override fun onClickStop(url: String?, vararg objects: Any?) {
    }

    override fun onTouchScreenSeekLight(url: String?, vararg objects: Any?) {
    }

    override fun onClickSeekbar(url: String?, vararg objects: Any?) {
    }

    override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
    }

    override fun onPlayError(url: String?, vararg objects: Any?) {
    }

    override fun onClickStartThumb(url: String?, vararg objects: Any?) {
    }

    override fun onEnterSmallWidget(url: String?, vararg objects: Any?) {
    }

    override fun onClickStopFullscreen(url: String?, vararg objects: Any?) {
    }

    override fun onClickStartError(url: String?, vararg objects: Any?) {
    }

    override fun onTouchScreenSeekPosition(url: String?, vararg objects: Any?) {
    }

    private val mBeans = arrayListOf<DefaultBean>()

    override fun saveBeans(beans: List<DefaultBean>) {
        if (beans == null || beans.isEmpty()) return
        mBeans.clear()
        mBeans.addAll(beans)
        mAdapter.notifyDataSetChanged()
    }

    override fun play(url: String, title: String) {
        if (url.isEmpty()) return
        mVideo.setUp(url, true, title)
        mVideo.startPlayLogic()
    }

    private val mVideo by lazy { find<StandardGSYVideoPlayer>(R.id.play) }

    private val mImp by lazy { PlayIm(this) }

    override fun showLoading(toast: String) {
    }

    override fun disDialog() {
    }

    override fun saveCache(name: String, cache: String) {
        CacheUtils.saveCache(this, name, cache)
    }

    override fun getCache(name: String) = CacheUtils.getCache(this, name)


    private val mAdapter by lazy {
        VideoAdapter(R.layout.item_video, mBeans).apply {
            this.setOnItemClickListener { adapter, view, position ->
                changePlayIndex(position - 1)
                playNext()
                mVideos.visibility = android.view.View.INVISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        mImp.init(intent.getSerializableExtra(Constant.PARAMS) as DefaultBean)
        mVideo.backButton.setOnClickListener { this.finish() }
        mVideo.setStandardVideoAllCallBack(this)
        mVideos.layoutManager = LinearLayoutManager(this)
        mVideos.adapter = mAdapter
    }

    override fun onPause() {
        super.onPause()
        mVideo.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        mVideo.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoPlayer.releaseAllVideos()
        mVideo.release()
    }
}

class PlayIm(view: PlayContract.View) : PlayContract.Imp {
    override fun getVideo(index: Int, name: String) {
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
                        L.e("play url" + url)
                        mView.play(url, "")
                        mView.changePlayIndex(index + 1)
                        mView.showToast("加载成功")
                    }
                } else {
                    uiThread { mView.showToast("加载失败") }
                }
            }

        }
    }

    private val mView = view
    override fun init(bean: DefaultBean) {
        getVideos(bean)
    }

    override fun getVideos(bean: DefaultBean) {
        //http://tv.abc5.cn
        val map = HashMap<String, String>()
        //{"uid":"3577","courseid":"601","companyid":"2","nums":"5"}
        map.put("uid", "3577")
        map.put("courseid", bean.id)
        map.put("companyid", "2")
        map.put("nums", "10000")
        HttpUtils.postJson(map, HttpUtils.IP + "/index.php/Api2/Video/videoList", object : GLCallback() {
            override fun onResponse(response: String?, id: Int) {
                val beans = arrayListOf<DefaultBean>()
                beans.addAll(JsonUtils.parseDefault(response ?: ""))
                mView.saveBeans(beans)
                //发给界面
                if (beans.isNotEmpty()) {
                    val bean = beans[0]
                    val fileurl = bean.fileurl;
                    val name = fileurl.substring(fileurl.lastIndexOf("/") + 1, fileurl.lastIndexOf("."))
                    getVideo(-1, name)
                }
            }

            override fun onError(call: Call?, e: Exception?, id: Int) {
            }
        })
    }

}


class VideoAdapter(layoutResId: Int, data: MutableList<DefaultBean>?) : BaseQuickAdapter<DefaultBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder?, item: DefaultBean?) {
        helper?.setText(R.id.item_name, item?.name)
    }

}