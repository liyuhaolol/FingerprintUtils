package spa.lyh.cn.fingerprintutils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import spa.lyh.cn.fingerprintutils.databinding.ActivityLoginBinding
import spa.lyh.cn.fingerprintutils.dialog.FingerDialog
import spa.lyh.cn.fingerprintutils.fp.FingerprintCallback
import spa.lyh.cn.fingerprintutils.fp.IFingerprint
import spa.lyh.cn.lib_utils.translucent.TranslucentUtils
import spa.lyh.cn.lib_utils.translucent.statusbar.StatusBarFontColorControler

class LoginActivity :AppCompatActivity(){
    private lateinit var b:ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var type:Int = 0
    private lateinit var fingerDialog: FingerDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)
        TranslucentUtils.setTranslucentBoth(window)
        b.login.setOnClickListener {
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        initView()
    }

    fun initView(){
        fingerDialog = FingerDialog(this)
        sharedPreferences = getSharedPreferences("finger", Context.MODE_PRIVATE)
        //获取当前设备的生物识别具体状态
        type = FingerprintVerifyManager.canAuthenticate(this,FingerData.enableBiometricPrompt)

        if (checkEnable()){
            //启动了生物识别则去识别
            FingerprintVerifyManager.Builder(this@LoginActivity)
                .setFingerDialog(fingerDialog)
                .enableBiometricPrompt(FingerData.enableBiometricPrompt)
                .callback(fingerCallback)
                .build()
        }
    }

    private val fingerCallback = object : FingerprintCallback {
        override fun onHwUnavailable() {

        }

        override fun onNoneEnrolled() {

        }

        override fun onSucceeded(msg: String) {
            fingerDialog.showTip(msg,R.color.biometricprompt_color_82C785)
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                fingerDialog.dismiss()
            },300)
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                val intent = Intent(this@LoginActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            },150)
        }

        override fun onFailed(failString: String) {
            fingerDialog.showTip(failString,R.color.biometricprompt_color_FF5555)
        }

        override fun onCancel() {
            Toast.makeText(this@LoginActivity,"取消认证", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkEnable():Boolean{
        if (type != IFingerprint.HW_UNAVAILABLE){
            val enable = sharedPreferences.getBoolean("enable",false)
            if (enable){
                //额外判断
                if (type == IFingerprint.NONE_ENROLLED){
                    //用户后续移除了生物认证信息
                    return false
                }
            }
            return enable
        }else{
            return false
        }
    }
}