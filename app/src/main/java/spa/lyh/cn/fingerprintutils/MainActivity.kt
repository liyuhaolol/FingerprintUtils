package spa.lyh.cn.fingerprintutils

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import spa.lyh.cn.fingerprintutils.databinding.ActivityMainBinding
import spa.lyh.cn.fingerprintutils.dialog.FingerDialog
import spa.lyh.cn.fingerprintutils.fp.FingerprintCallback
import spa.lyh.cn.fingerprintutils.fp.IFingerprint

class MainActivity : AppCompatActivity() {
    private lateinit var b:ActivityMainBinding
    private lateinit var fingerDialog: FingerDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        initView()
    }

    private val fingerCallback = object :FingerprintCallback{
        override fun onHwUnavailable() {

        }

        override fun onNoneEnrolled() {

        }

        override fun onSucceeded(msg: String) {
            fingerDialog.showTip(msg,R.color.biometricprompt_color_82C785)
            Handler(Looper.getMainLooper()).postDelayed(Runnable { fingerDialog.dismiss() },300)
        }

        override fun onFailed(failString: String) {
            fingerDialog.showTip(failString,R.color.biometricprompt_color_FF5555)
        }

        override fun onCancel() {

        }
    }


    private fun initView(){
        when(FingerprintVerifyManager.canAuthenticate(this,FingerData.enableBiometricPrompt)){
            IFingerprint.CAN_AUTHENTICATE ->{
                b.tvFingerprint.visibility = View.VISIBLE
                b.alert.visibility = View.GONE
            }
            IFingerprint.HW_UNAVAILABLE ->{
                b.tvFingerprint.visibility = View.GONE
                b.alert.visibility = View.VISIBLE
                b.alert.text = "手机没有生物识别硬件"
            }
            IFingerprint.NONE_ENROLLED ->{
                b.tvFingerprint.visibility = View.GONE
                b.alert.visibility = View.VISIBLE
                b.alert.text = "手机没有设置生物识别信息"
            }
        }



        fingerDialog = FingerDialog(this)
        b.tvFingerprint.setOnClickListener {
            FingerprintVerifyManager.Builder(this@MainActivity)
                .setFingerDialog(fingerDialog)
                .enableBiometricPrompt(FingerData.enableBiometricPrompt)
                .callback(fingerCallback)
                .build()
        }
    }
}