package spa.lyh.cn.fingerprintutils.dialog

import android.content.Context
import androidx.annotation.ColorRes
import spa.lyh.cn.fingerprintutils.R
import spa.lyh.cn.fingerprintutils.databinding.DialogFingerBinding

class FingerDialog(context: Context):BaseFingerDialog(context) {
    private lateinit var b:DialogFingerBinding
    init {
        initDialogStyle()
    }

    fun initDialogStyle(){
        b = DialogFingerBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.tvCancel.setOnClickListener {
            cancel()
        }
    }

    fun showTip(content:String, @ColorRes colorId:Int){
        b.tvTip.text = content
        b.tvTip.setTextColor(context.getResources().getColor(colorId))
    }

    override fun show() {
        super.show()
        b.tvTip.text = context.getString(R.string.biometricprompt_verify_fingerprint)
        b.tvTip.setTextColor(context.getResources().getColor(R.color.biometricprompt_color_333333))
    }
}