package spa.lyh.cn.fingerprintutils.dialog

import android.app.Dialog
import android.content.Context
import android.util.Log
import spa.lyh.cn.fingerprintutils.R

open class BaseFingerDialog(context: Context, themeResId:Int) :Dialog(context,themeResId){

    constructor(context: Context):this(context, R.style.FingerDialog)

    private var actionListener: OnDialogActionListener? = null


    override fun dismiss() {
        super.dismiss()
        actionListener?.onDismiss()
        Log.e("qwer","dismiss")

    }

    override fun cancel(){
        super.cancel()
        actionListener?.onCancel()
        Log.e("qwer","cancel")
    }

    fun setActionListener(actionListener: OnDialogActionListener){
        this.actionListener = actionListener
    }

    interface OnDialogActionListener {
        fun onCancel()
        fun onDismiss()
    }
}