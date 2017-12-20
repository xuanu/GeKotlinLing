package apk.zeffect.cn.gekotlinling.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import apk.zeffect.cn.gekotlinling.R
import apk.zeffect.cn.gekotlinling.bean.DefaultBean
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
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
class MainBottomAdapter(layoutResId: Int, data: MutableList<DefaultBean>?) : BaseQuickAdapter<DefaultBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder?, item: DefaultBean?) {
        helper?.setText(R.id.type_name, item?.name)
        Glide.with(this.mContext).load(item?.icon).into(helper?.getView(R.id.type_img))
    }

}

class DefaultAdapte(context: Context, data: MutableList<DefaultBean>) : RecyclerView.Adapter<DefaultAdapte.MyViewHolder>() {

    private val mContext = context

    private val mDatas = data


    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        holder?.show?.text = mDatas[position].name
        Glide.with(mContext).load(mDatas[position].icon).into(holder?.mImage)
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_layout_main_bottom, parent, false))
    }


    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val show = itemView?.find<TextView>(R.id.type_name)
        val mImage = itemView?.find<ImageView>(R.id.type_img)
    }
}