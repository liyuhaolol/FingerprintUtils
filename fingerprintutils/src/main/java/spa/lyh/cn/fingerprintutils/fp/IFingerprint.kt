package spa.lyh.cn.fingerprintutils.fp

import android.app.Activity
import android.content.Context
import spa.lyh.cn.fingerprintutils.dialog.BaseFingerDialog

interface IFingerprint {
    /**
     * 检测指纹硬件是否可用，及是否添加指纹
     * @param context
     * @param callback
     * @return
     */
    fun canAuthenticate(context: Context, callback: FingerprintCallback?): Boolean

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