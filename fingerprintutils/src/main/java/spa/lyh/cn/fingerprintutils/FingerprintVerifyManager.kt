package spa.lyh.cn.fingerprintutils

import android.app.Activity
import android.os.Build
import spa.lyh.cn.fingerprintutils.fp.FingerprintCallback
import spa.lyh.cn.fingerprintutils.fp.FingerprintSDK23
import spa.lyh.cn.fingerprintutils.fp.IFingerprint

class FingerprintVerifyManager(private val builder:Builder) {

    init {
        var iFingerprint:IFingerprint? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            //Android10
            if (builder.enableBiometricPrompt){}else{
                iFingerprint = FingerprintSDK23.newInstance()
            }
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //Android6.0
            iFingerprint = FingerprintSDK23.newInstance()
        }else{
            //Android6.0以下
            builder.fingerprintCallback?.onHwUnavailable()
        }
        if (iFingerprint != null){
            //这个参数有意义
            //检测指纹硬件是否存在或者是否可用，若false，不再弹出指纹验证框
            if (iFingerprint.canAuthenticate(builder.context,builder.fingerprintCallback)){
                //指纹可以被校验
                iFingerprint.authenticate(builder.context,builder.fingerprintCallback)
            }
        }
    }


    class Builder(val context:Activity){
        var fingerprintCallback:FingerprintCallback? = null
        var enableBiometricPrompt = false//默认不启动生物识别


        fun callback(fingerprintCallback:FingerprintCallback):Builder{
            this.fingerprintCallback = fingerprintCallback
            return this
        }

        fun enableBiometricPrompt(enable:Boolean):Builder{
            enableBiometricPrompt = enable
            return this
        }

        fun build(): FingerprintVerifyManager {
            return FingerprintVerifyManager(this)
        }
    }
}