package apk.zeffect.cn.gekotlinling.adapter

import apk.zeffect.cn.gekotlinling.R
import apk.zeffect.cn.gekotlinling.bean.DefaultBean
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

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