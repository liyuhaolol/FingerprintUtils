package spa.lyh.cn.fingerprintutils.sb

import android.content.Context
import android.util.AttributeSet
import com.kyleduo.switchbutton.SwitchButton

class MySwitchButton(context:Context, attrs: AttributeSet?, defStyle:Int) :SwitchButton(context,attrs,defStyle){
    constructor(context:Context, attrs: AttributeSet):this(context,attrs,0)
    constructor(context:Context):this(context,null,0)

    private var listener:OnButtonClickListener? = null


    override fun performClick(): Boolean {
        var canSwitch = listener?.onClick()
        if (canSwitch == null){
            canSwitch = true
        }
        if (canSwitch){
            super.performClick()
        }else{
            refreshDrawableState()//刷新UI，避免出现颜色错乱问题
        }
        return false
    }

    fun setOnButtonClickListener(listener:OnButtonClickListener){
        this.listener = listener
    }
}