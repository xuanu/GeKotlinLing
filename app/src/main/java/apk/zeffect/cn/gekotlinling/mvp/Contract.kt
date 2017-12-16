package apk.zeffect.cn.gekotlinling.mvp

import android.view.View
import apk.zeffect.cn.gekotlinling.bean.DefaultBean

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
class Contract {
    interface BaseView {
        fun showLoading(toast: String)
        fun disDialog()
        fun saveCache(name: String, cache: String)
        fun getCache(name: String): String
    }
}

/**主页**/
class MainIF {
    interface Imp {
        fun init()
        fun getModule()
    }

    interface View : Contract.BaseView {
        fun updateBottomType(types: List<DefaultBean>)
    }
}

class CourseContract {
    interface Imp {
        fun init(bean: DefaultBean)
        fun getTopTypes(bean: DefaultBean)
    }

    interface View : Contract.BaseView {
        fun initTitle(beans: List<DefaultBean>)
        fun createView(bean: DefaultBean): android.view.View
    }
}

class TypeContract {
    interface Imp {
        fun init(bean: DefaultBean)
        fun getCourse(bean: DefaultBean)
    }

    interface View : Contract.BaseView {
        fun update(pCourse: List<DefaultBean>)
    }
}

class PlayContract {
    interface Imp {
        fun init(bean: DefaultBean)

        fun getVideos(bean: DefaultBean)

        fun getVideo(index: Int, name: String)

    }

    interface View : Contract.BaseView {
        fun play(url: String, title: String)
        fun saveBeans(beans: List<DefaultBean>)
        fun changePlayIndex(i: Int)
        fun showToast(toast: String)
        fun showVideos()
        fun closeView()
    }
}