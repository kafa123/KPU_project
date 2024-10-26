package com.example.kpu

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.example.kpu.databinding.ActivityDetailDataBinding

class DetailDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailDataBinding
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseHelper = DatabaseHelper(this)

        val userId = intent.getIntExtra("user_id", -1)
        if (userId != -1) {
            val user = databaseHelper.getDetailsUser(userId)
            user?.let {
                with(binding){
                    textNama.setText(user.name)
                    textNik.setText(user.nik)
                    textAlamat.setText(user.address)
                    textNoHP.setText(user.phone)
                    textGender.setText(user.gender)
                    DatePicker.setText(user.date)
                    Glide.with(this@DetailDataActivity).load(user.image).centerCrop().into(image)
                }
            }
        }
    }
    
}