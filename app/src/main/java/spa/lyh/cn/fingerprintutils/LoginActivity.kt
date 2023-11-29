package spa.lyh.cn.fingerprintutils

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import spa.lyh.cn.fingerprintutils.databinding.ActivityLoginBinding

class LoginActivity :AppCompatActivity(){
    private lateinit var b:ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.login.setOnClickListener {
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}