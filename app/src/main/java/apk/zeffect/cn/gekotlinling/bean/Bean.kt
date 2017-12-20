package apk.zeffect.cn.gekotlinling.bean

import java.io.Serializable

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
data class DefaultBean(val id: String, val name: String, val icon: String, val bgurl: String, val icon2: String = "", val icon3: String = "", val fileurl: String = "") : Serializable

data class IndexBean(val url: String, val index: Int)