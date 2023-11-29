package spa.lyh.cn.fingerprintutils.fp

import android.app.Activity
import android.content.Context
import spa.lyh.cn.fingerprintutils.dialog.BaseFingerDialog

interface IFingerprint {
    companion object{
        const val CAN_AUTHENTICATE = 0//可以检测
        const val HW_UNAVAILABLE = 1//没有对应硬件或其他的一些错误
        const val NONE_ENROLLED = 2//有硬件但未设置任何生物信息
    }
    /**
     * 检测指纹硬件是否可用，及是否添加指纹
     * @param context
     * @param callback
     * @return
     */
    fun canAuthenticate(context: Context, callback: FingerprintCallback?): Int

    /**
     * 初始化并调起指纹验证
     *
     * @param context
     * @param callback
     */
    fun authenticate(
        context: Activity,
        baseFingerDialog: BaseFingerDialog?,
        callback: FingerprintCallback?
    )
}