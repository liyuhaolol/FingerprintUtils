package spa.lyh.cn.fingerprintutils

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import spa.lyh.cn.fingerprintutils.databinding.ActivityMainBinding
import spa.lyh.cn.fingerprintutils.dialog.FingerDialog
import spa.lyh.cn.fingerprintutils.fp.FingerprintCallback
import spa.lyh.cn.fingerprintutils.fp.IFingerprint
import spa.lyh.cn.fingerprintutils.sb.OnButtonClickListener
import spa.lyh.cn.lib_utils.translucent.TranslucentUtils

class MainActivity : AppCompatActivity() {
    private lateinit var b:ActivityMainBinding
    private lateinit var fingerDialog: FingerDialog
    private lateinit var sharedPreferences:SharedPreferences
    private var type:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        TranslucentUtils.setTranslucentBoth(window)
        initView()
    }

    private val fingerCallback = object :FingerprintCallback{
        override fun onHwUnavailable() {

        }

        override fun onNoneEnrolled() {

        }

        override fun onSucceeded(msg: String) {
            fingerDialog.showTip(msg,R.color.biometricprompt_color_82C785)
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                fingerDialog.dismiss()
                b.sb.isChecked = true
                val editor = sharedPreferences.edit()
                editor.putBoolean("enable", true)
                editor.apply()
               },300)
        }

        override fun onFailed(failString: String) {
            fingerDialog.showTip(failString,R.color.biometricprompt_color_FF5555)
        }

        override fun onCancel() {
            Toast.makeText(this@MainActivity,"取消认证",Toast.LENGTH_SHORT).show()
        }
    }


    private fun initView(){
        sharedPreferences = getSharedPreferences("finger", Context.MODE_PRIVATE)
        //获取当前设备的生物识别具体状态
        type = FingerprintVerifyManager.canAuthenticate(this,FingerData.enableBiometricPrompt)
        //根据type判断UI到底如何显示
        //我这里采取了只要有硬件设备就显示开关的逻辑，你当然可以采取只有能授权才显示开关的逻辑，都一样，看你自己的判断
        when(type){
            IFingerprint.CAN_AUTHENTICATE,IFingerprint.NONE_ENROLLED ->{
                b.tvFingerprint.visibility = View.VISIBLE
                b.alert.visibility = View.GONE
            }
            IFingerprint.HW_UNAVAILABLE ->{
                b.tvFingerprint.visibility = View.GONE
                b.alert.visibility = View.VISIBLE
                b.alert.text = "手机没有生物识别硬件"
            }
        }
        //判断开关的初始状态
        b.sb.isChecked = checkEnable()
        b.sb.setOnButtonClickListener(object :OnButtonClickListener{
            override fun onClick(): Boolean {
                if (b.sb.isChecked){
                    //当前为打开，所以去关闭
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("enable", false)
                    editor.apply()
                    return true
                }else{
                    //当前为关闭，需要根据实际情况判断是否要打开
                    if (type == IFingerprint.NONE_ENROLLED){
                        //没有设置生物识别
                        Toast.makeText(this@MainActivity,"请先设置一个生物认证信息",Toast.LENGTH_SHORT).show()
                    }else{
                        FingerprintVerifyManager.Builder(this@MainActivity)
                            .setFingerDialog(fingerDialog)
                            .enableBiometricPrompt(FingerData.enableBiometricPrompt)
                            .callback(fingerCallback)
                            .build()
                    }
                    return false
                }
            }
        })

        fingerDialog = FingerDialog(this)

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
