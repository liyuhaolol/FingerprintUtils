package spa.lyh.cn.fingerprintutils.fp

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import spa.lyh.cn.fingerprintutils.R
import spa.lyh.cn.fingerprintutils.dialog.BaseFingerDialog
import spa.lyh.cn.fingerprintutils.utils.CipherHelper

@RequiresApi(api = Build.VERSION_CODES.M)
class FingerprintSDK23 :IFingerprint{

    companion object{
        private val instance = FingerprintSDK23()
        //指纹加密
        private var cryptoObject: FingerprintManagerCompat.CryptoObject? = null

        @JvmStatic
        fun newInstance(): FingerprintSDK23 {
            //指纹加密，提前进行Cipher初始化，防止指纹认证时还没有初始化完成
            try {
                cryptoObject = FingerprintManagerCompat.CryptoObject(CipherHelper().createCipher())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return instance
        }
    }

    private val authenticationCallback = object :FingerprintManagerCompat.AuthenticationCallback(){
        override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
            super.onAuthenticationError(errMsgId, errString)
            if (errMsgId != 5){
                fingerprintCallback?.onFailed(errString.toString())
            }
        }

        override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
            super.onAuthenticationHelp(helpMsgId, helpString)
            fingerprintCallback?.onFailed(helpString.toString())
        }

        override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            fingerprintCallback?.onSucceeded(context!!.getString(R.string.biometricprompt_verify_success))
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            fingerprintCallback?.onFailed(context!!.getString(R.string.biometricprompt_verify_failed))
        }
    }

    private val dialogActionListener = object :BaseFingerDialog.OnDialogActionListener{
        override fun onCancel() {
            fingerprintCallback?.onCancel()
        }

        override fun onDismiss() {
            if (!cancellationSignal!!.isCanceled){
                cancellationSignal!!.cancel()
            }
        }
    }

    override fun canAuthenticate(context: Context, callback: FingerprintCallback?): Int {
        //硬件是否支持指纹识别
        if (!FingerprintManagerCompat.from(context).isHardwareDetected) {
            callback?.onHwUnavailable()
            return IFingerprint.HW_UNAVAILABLE
        }
        //是否已添加指纹
        if (!FingerprintManagerCompat.from(context).hasEnrolledFingerprints()) {
            callback?.onNoneEnrolled()
            return IFingerprint.NONE_ENROLLED
        }
        return IFingerprint.CAN_AUTHENTICATE
    }

    private var context:Context? = null
    private var fingerprintCallback:FingerprintCallback? = null
    private var fingerprintManagerCompat:FingerprintManagerCompat? = null
    private var cancellationSignal: CancellationSignal? = null
    private var baseFingerDialog: BaseFingerDialog? = null
    override fun authenticate(context: Activity, baseFingerDialog: BaseFingerDialog?,callback: FingerprintCallback?) {
        this.context = context
        fingerprintCallback = callback
        fingerprintManagerCompat = FingerprintManagerCompat.from(context)
        //取消扫描，每次取消后需要重新创建新示例
        cancellationSignal = CancellationSignal()
        cancellationSignal!!.setOnCancelListener { baseFingerDialog?.dismiss() }
        this.baseFingerDialog = baseFingerDialog
        //调起指纹验证
        fingerprintManagerCompat!!.authenticate(cryptoObject,0,cancellationSignal,authenticationCallback,null)
        baseFingerDialog?.setActionListener(dialogActionListener)
        baseFingerDialog?.show()
    }
}