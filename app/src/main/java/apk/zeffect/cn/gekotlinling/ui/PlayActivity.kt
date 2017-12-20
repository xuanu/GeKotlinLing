package apk.zeffect.cn.gekotlinling.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import apk.zeffect.cn.gekotlinling.MainViewModel
import apk.zeffect.cn.gekotlinling.R
import apk.zeffect.cn.gekotlinling.bean.DefaultBean
import apk.zeffect.cn.gekotlinling.utils.Constant
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shuyu.gsyvideoplayer.listener.StandardVideoAllCallBack
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import org.jetbrains.anko.find
import org.jetbrains.anko.toast

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
class PlayActivity : AppCompatActivity(), StandardVideoAllCallBack {
    //    private val mChoseBtn by lazy { find<Button>(R.id.chose_video) }
    private val mVideos by lazy { find<RecyclerView>(R.id.videos) }


    fun showToast(toast: String) {
        toast(toast ?: "")
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
        playNext(mAdapter.playIndex)
    }


    private fun playNext(nowPlay: Int) {
        if (nowPlay + 1 > mBeans.size - 1) return
        val bean = mBeans.get(nowPlay + 1)
        val fileurl = bean.fileurl;
        val name = fileurl.substring(fileurl.lastIndexOf("/") + 1, fileurl.lastIndexOf("."))
        mViewModel.getVideo(name)
        mAdapter.playIndex = nowPlay + 1
        mAdapter.notifyDataSetChanged()
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

    fun saveBeans(beans: List<DefaultBean>) {
        if (beans == null || beans.isEmpty()) return
        mBeans.clear()
        mBeans.addAll(beans)
        mAdapter.notifyDataSetChanged()
    }

    fun play(url: String, title: String) {
        if (url.isEmpty()) return
        mVideo.setUp(url, true, title)
        mVideo.startPlayLogic()
    }

    private val mVideo by lazy { find<StandardGSYVideoPlayer>(R.id.play) }


    private val mAdapter by lazy {
        VideoAdapter(R.layout.item_video, mBeans).apply {
            this.setOnItemClickListener { adapter, view, position ->
                playNext(position - 1)
                mVideos.visibility = android.view.View.INVISIBLE
            }
        }
    }

    private val mViewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        mViewModel.getDatas().observe(this, Observer { saveBeans(it ?: emptyList()) })
        mViewModel.getPlayUrl().observe(this, Observer {
            play(it ?: "", "")
            showToast("加载成功")
        })
        mViewModel.getVideos(intent.getSerializableExtra(Constant.PARAMS) as DefaultBean)
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

    override fun onStop() {
        super.onStop()
        GSYVideoPlayer.releaseAllVideos()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoPlayer.releaseAllVideos()
        mVideo.release()
    }
}


class VideoAdapter(layoutResId: Int, data: MutableList<DefaultBean>?) : BaseQuickAdapter<DefaultBean, BaseViewHolder>(layoutResId, data) {
    public var playIndex = 0


    override fun convert(helper: BaseViewHolder?, item: DefaultBean?) {
        helper?.setText(R.id.item_name, item?.name)
        helper?.setTextColor(R.id.item_name, if (helper.adapterPosition == playIndex) Color.parseColor("#85cc75") else Color.parseColor("#ffffff"))
    }

}