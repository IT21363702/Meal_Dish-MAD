package com.example.firebasekotlin.activities;

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasekotlin.R
import com.example.firebasekotlin.databinding.ActivityCustomerHomeBinding

class CustomerHomeActivity : AppCompatActivity() {

    private lateinit var drawerBinding: ActivityCustomerHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        drawerBinding = ActivityCustomerHomeBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_main)
//        setSupportActionBar(drawerBinding.)
    }
}