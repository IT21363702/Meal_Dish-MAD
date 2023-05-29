package com.example.firebasekotlin.activities

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasekotlin.databinding.ActivityCreatePaymentInformationBinding
import com.example.firebasekotlin.models.EmployeePaymentInfoModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreatePaymentInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePaymentInformationBinding
    private lateinit var firebaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePaymentInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPayInfoSubmit.setOnClickListener {
            submitPaymentInformation()
        }
        supportActionBar?.title = "Add Payment Information"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun submitPaymentInformation() {
        binding.btnPayInfoSubmit.isEnabled = false
        binding.btnPayInfoSubmit.text = "Uploading..."

        firebaseRef = FirebaseDatabase
            .getInstance("https://firebasekotlindb-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("PaymentInformation")
        val loc: String = firebaseRef.push().key!!
        val sharedPrefs = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)

        val accHoldersName = binding.editTextAccountHoldersName.text.toString()
        val accNo = binding.editTextAccountNo.text.toString()
        val bankName = binding.editTextBankName.text.toString()
        val bankBranchName = binding.editTextBankBranchName.text.toString()

        val empPayInfo = EmployeePaymentInfoModel(
            sharedPrefs.getString("uid", null).toString(),
            accHoldersName,
            accNo,
            bankName,
            bankBranchName
        )

        firebaseRef.child(loc).setValue(empPayInfo)
            // handle if payment info not provided yet
            .addOnCompleteListener {
                Toast.makeText(this, "Payment Info added", Toast.LENGTH_LONG).show()
                finish()
            }.addOnFailureListener { err ->
                Toast.makeText(
                    this,
                    "Payment info upload failed : ${err.message}",
                    Toast.LENGTH_LONG
                )
                    .show()
                finish()
            }
    }
}