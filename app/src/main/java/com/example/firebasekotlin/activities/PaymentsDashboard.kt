package com.example.firebasekotlin.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.example.firebasekotlin.databinding.ActivityPaymentDashboardBinding
class PaymentsDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentDashboardBinding
    private lateinit var payAmount : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.buttonProceedToPay.setOnClickListener {
            payAmount = binding.paymentAmount.text.toString()

            if ( payAmount == null || payAmount.toDouble() < 500.00 ) {
                Toast.makeText(this, "Invalid. Amount should be > 500", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            var intent = Intent(this, PaymentConfirmActivity::class.java)
            intent.putExtra("amount", payAmount)
            startActivity(intent)
        }

        binding.buttonPaymentHistory.setOnClickListener {
            var intent = Intent(this, PaymentHistoryActivity::class.java)
            startActivity(intent)
        }

        binding.buttonEnterPaymentInformation.setOnClickListener {
            var intent = Intent(this, ReadPaymentInformationActivity::class.java)
            startActivity(intent)
        }
    }
}