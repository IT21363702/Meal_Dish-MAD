package com.example.firebasekotlin.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.firebasekotlin.databinding.ActivityViewPaymentInformationBinding
import com.example.firebasekotlin.models.EmployeePaymentInfoModel
import com.google.firebase.database.*

class ReadPaymentInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPaymentInformationBinding
    private lateinit var firebaseRef:DatabaseReference
    private var userData:EmployeePaymentInfoModel? = null
    private var wasPaused:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPaymentInformationBinding.inflate(layoutInflater)

        // disabling the interface for loading screen
        binding.loadingLabel.visibility = View.VISIBLE
        binding.textAccountHoldersName.visibility = View.GONE
        binding.textAccountNo.visibility = View.GONE
        binding.textBankName.visibility = View.GONE
        binding.textBankBranchName.visibility = View.GONE
        binding.labelAccountHoldersName.visibility = View.GONE
        binding.labelAccountNo.visibility = View.GONE
        binding.labelBankName.visibility = View.GONE
        binding.labelBankBranchName.visibility = View.GONE
        binding.layoutForButtons.visibility = View.GONE

        setContentView(binding.root)

        supportActionBar?.title = "Payment information"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        firebaseRef = FirebaseDatabase
            .getInstance("https://firebasekotlindb-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("PaymentInformation")

        val sharedPrefs = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        val query = firebaseRef.orderByChild("uid")
            .equalTo(sharedPrefs.getString("uid", null))
            .limitToFirst(1)

        query.addListenerForSingleValueEvent(object: ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userData = snapshot.children.first().getValue(EmployeePaymentInfoModel::class.java)

                    binding.textAccountNo.text = userData?.accNo
                    binding.textAccountHoldersName.text = userData?.accHoldersName
                    binding.textBankName.text = userData?.bankName
                    binding.textBankBranchName.text = userData?.bankBranchName

                    binding.approvedSign.text = userData?.approval
                    if(userData?.approval?.lowercase() == EmployeePaymentInfoModel.PENDING.lowercase()) {
                        binding.approvedSign.setTextColor(ContextCompat.getColor(
                            this@ReadPaymentInformationActivity,
                            com.example.firebasekotlin.R.color.yellow_warning))
                    }
                    else if(userData?.approval?.lowercase() == EmployeePaymentInfoModel.ACCEPTED.lowercase()) {
                        binding.approvedSign.setTextColor(ContextCompat.getColor(
                            this@ReadPaymentInformationActivity,
                            com.example.firebasekotlin.R.color.green_success))
                    }
                    else{
                        binding.approvedSign.setTextColor(ContextCompat.getColor(
                            this@ReadPaymentInformationActivity,
                            com.example.firebasekotlin.R.color.red_danger))
                    }
                    // re-enabling the interface and the buttons
                    binding.loadingLabel.visibility = View.GONE

                    binding.textAccountHoldersName.visibility = View.VISIBLE
                    binding.textAccountNo.visibility = View.VISIBLE
                    binding.textBankName.visibility = View.VISIBLE
                    binding.textBankBranchName.visibility = View.VISIBLE
                    binding.labelAccountHoldersName.visibility = View.VISIBLE
                    binding.labelAccountNo.visibility = View.VISIBLE
                    binding.labelBankName.visibility = View.VISIBLE
                    binding.labelBankBranchName.visibility = View.VISIBLE
                    binding.layoutForButtons.visibility = View.VISIBLE

                }
                else {
                    // handle if payment info not provided yet

                    val intent = Intent(this@ReadPaymentInformationActivity, CreatePaymentInformationActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText( this@ReadPaymentInformationActivity , "Fetch failed: ${error.message}" , Toast.LENGTH_LONG).show()
            }
        })

        binding.btnDeletePaymentInfo.setOnClickListener {
            val intent = Intent(this, DeletePaymentInformationActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnUpdatePaymentInfo.setOnClickListener{
            val intent = Intent(this, UpdatePaymentInfoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onPause() {
        wasPaused = true
        super.onPause()
    }

    override fun onResume() {
        if (wasPaused) {
            wasPaused = false
            recreate()
        }
        super.onResume()
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}