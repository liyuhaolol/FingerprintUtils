package spa.lyh.cn.fingerprintutils.fp

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import android.util.Log
import androidx.annotation.RequiresApi
import spa.lyh.cn.fingerprintutils.R
import spa.lyh.cn.fingerprintutils.dialog.BaseFingerDialog
import spa.lyh.cn.fingerprintutils.utils.CipherHelper

@RequiresApi(api = Build.VERSION_CODES.Q)
class BiometricPromptSDK29 :IFingerprint{

    companion object{
        private val instance = BiometricPromptSDK29()
        //指纹加密
        private var cryptoObject: BiometricPrompt.CryptoObject? = null

        @JvmStatic
        fun newInstance(): BiometricPromptSDK29 {
            //指纹加密，提前进行Cipher初始化，防止指纹认证时还没有初始化完成
            try {
                cryptoObject = BiometricPrompt.CryptoObject(CipherHelper().createCipher())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return instance
        }
    }

    /**
     * 认证结果回调
     */
    private val authenticationCallback: BiometricPrompt.AuthenticationCallback =
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != 5){
                    fingerprintCallback?.onFailed(errString.toString())
                }else{
                    fingerprintCallback?.onCancel()
                }
            }

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
                super.onAuthenticationHelp(helpCode, helpString)
                fingerprintCallback?.onFailed(helpString.toString())
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                fingerprintCallback?.onSucceeded(context!!.getString(R.string.biometricprompt_verify_success))
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                fingerprintCallback?.onFailed(context!!.getString(R.string.biometricprompt_verify_failed))
            }
        }
    override fun canAuthenticate(context: Context, callback: FingerprintCallback?): Int {
        val biometricManager = context.getSystemService(Context.BIOMETRIC_SERVICE)
        if (biometricManager is BiometricManager){
            when (biometricManager.canAuthenticate()) {
                BiometricManager.BIOMETRIC_SUCCESS ->{
                    // 设备支持生物识别并且已经注册生物识别数据
                    return IFingerprint.CAN_AUTHENTICATE
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->{
                    // 设备不支持生物识别功能
                    callback?.onHwUnavailable()
                    return IFingerprint.HW_UNAVAILABLE
                }

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->{
                    // 生物识别功能当前不可用
                    callback?.onHwUnavailable()
                    return IFingerprint.HW_UNAVAILABLE
                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->{
                    // 设备支持生物识别功能，但用户未注册生物识别数据
                    callback?.onNoneEnrolled()
                    return IFingerprint.NONE_ENROLLED
                }
            }
        }
        //啥都没识别到，直接失败
        return IFingerprint.HW_UNAVAILABLE
    }

    private var context: Context? = null
    private var fingerprintCallback:FingerprintCallback? = null
    private var cancellationSignal: CancellationSignal? = null

    override fun authenticate(
        context: Activity,
        baseFingerDialog: BaseFingerDialog?,
        callback: FingerprintCallback?
    ) {
        this.context = context
        fingerprintCallback = callback
        /*
         * 初始化 BiometricPrompt.Builder
         */
        val builder = BiometricPrompt.Builder(context)
            .setTitle(context.getString(R.string.biometricprompt_fingerprint_verification))
            .setNegativeButton(context.getString(R.string.biometricprompt_cancel),
                { command: Runnable? ->
                    if (!cancellationSignal!!.isCanceled){
                        cancellationSignal!!.cancel()
                    }
                }
            ) { dialog: DialogInterface?, which: Int -> }

        //构建 BiometricPrompt
        val biometricPrompt = builder.build()

        //取消扫描，每次取消后需要重新创建新示例
        cancellationSignal = CancellationSignal()
        //cancellationSignal!!.setOnCancelListener {}
        /*
         * 拉起指纹验证模块，等待验证
         * Executor：
         * context.getMainExecutor()
         */
        biometricPrompt.authenticate(
            cryptoObject!!,
            cancellationSignal!!, context.mainExecutor, authenticationCallback
        )
    }
}