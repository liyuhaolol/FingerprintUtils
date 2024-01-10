package spa.lyh.cn.fingerprintutils.fp

interface FingerprintCallback {
    /**
     * 无指纹硬件或者指纹硬件不可用
     */
    fun onHwUnavailable()

    /**
     * 未添加指纹
     */
    fun onNoneEnrolled()

    /**
     * 验证成功
     */
    fun onSucceeded(msg:String)

    /**
     * 验证失败
     */
    fun onFailed(failString:String)

    /**
     * 验证帮助
     */
    fun onHelp(helpString:String)


    /**
     * 取消验证
     */
    fun onCancel()
}