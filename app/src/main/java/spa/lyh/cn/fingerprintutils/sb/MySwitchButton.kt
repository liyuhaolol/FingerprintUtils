package spa.lyh.cn.fingerprintutils.sb

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.kyleduo.switchbutton.SwitchButton

class MySwitchButton(context:Context, attrs: AttributeSet?, defStyle:Int) :SwitchButton(context,attrs,defStyle){
    constructor(context:Context, attrs: AttributeSet):this(context,attrs,0)
    constructor(context:Context):this(context,null,0)

    private var listener:OnButtonClickListener? = null


    override fun performClick(): Boolean {
        //return super.performClick()
        var canSwitch = listener?.onClick()
        if (canSwitch == null){
            canSwitch = true
        }
        if (canSwitch){
            super.performClick()
        }
        return false
    }

    fun setOnButtonClickListener(listener:OnButtonClickListener){
        this.listener = listener
    }
}