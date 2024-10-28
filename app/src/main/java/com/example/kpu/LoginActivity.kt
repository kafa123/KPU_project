package com.example.kpu

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kpu.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            btnLogin.setOnClickListener {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                if (check()){
                    startActivity(intent)
                }
            }
        }
    }

    fun check():Boolean{
        if (binding.textUsername.text.toString()=="Kafa" && binding.textPassword.text.toString()=="1234"){
            return true
        }else{
            binding.textPassword.error = "wrong password"
            return false
        }
    }
}