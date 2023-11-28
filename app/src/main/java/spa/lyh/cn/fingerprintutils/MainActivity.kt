package spa.lyh.cn.fingerprintutils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import spa.lyh.cn.fingerprintutils.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var b:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
    }
}