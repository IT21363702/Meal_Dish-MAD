package com.example.firebasekotlin.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasekotlin.databinding.ActivityEditPaymentInformationBinding
import com.example.firebasekotlin.models.EmployeePaymentInfoModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.HashMap

class UpdatePaymentInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPaymentInformationBinding
    private var userData: EmployeePaymentInfoModel? = null
    private var firebaseRef = FirebaseDatabase
        .getInstance("https://firebasekotlindb-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("PaymentInformation")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPaymentInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPayInfoSubmit.setOnClickListener {
            submitPaymentInformation()
        }
        supportActionBar?.title = "Update Payment Information"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sharedPrefs = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        val query = firebaseRef.orderByChild("uid")
            .equalTo(sharedPrefs.getString("uid", null))
            .limitToFirst(1)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userData =
                        snapshot.children.first().getValue(EmployeePaymentInfoModel::class.java)

                    binding.editTextAccountNo.setText(userData?.accNo)
                    binding.editTextAccountHoldersName.setText(userData?.accHoldersName)
                    binding.editTextBankName.setText(userData?.bankName)
                    binding.editTextBankBranchName.setText(userData?.bankBranchName)

                    if (userData?.approval.isNullOrEmpty()) {
                        // warning label
                    }

                    binding.loadingLabel.visibility = View.GONE

                    binding.editTextAccountHoldersName.visibility = View.VISIBLE
                    binding.editTextAccountNo.visibility = View.VISIBLE
                    binding.editTextBankName.visibility = View.VISIBLE
                    binding.editTextBankBranchName.visibility = View.VISIBLE
                    binding.labelAccountHoldersName.visibility = View.VISIBLE
                    binding.labelAccountNo.visibility = View.VISIBLE
                    binding.labelBankName.visibility = View.VISIBLE
                    binding.labelBankBranchName.visibility = View.VISIBLE
                    binding.btnPayInfoSubmit.visibility = View.VISIBLE

                } else {
                    // handle if payment info not provided yet
                    Toast.makeText(
                        this@UpdatePaymentInfoActivity,
                        "Please add payment information first",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(
                        this@UpdatePaymentInfoActivity,
                        CreatePaymentInformationActivity::class.java
                    )
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@UpdatePaymentInfoActivity,
                    "Fetch failed: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun submitPaymentInformation() {

        val sharedPrefs = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)

        val accHoldersName = binding.editTextAccountHoldersName.text.toString()
        val accNo = binding.editTextAccountNo.text.toString()
        val bankName = binding.editTextBankName.text.toString()
        val bankBranchName = binding.editTextBankBranchName.text.toString()

        val childUpdates = HashMap<String, Any>()

        childUpdates["isApproved"] = "Pending"
        if (userData?.accHoldersName !== accHoldersName)
            childUpdates["accHoldersName"] = accHoldersName
        if (userData?.accNo !== accNo)
            childUpdates["accNo"] = accNo
        if (userData?.bankName !== bankName)
            childUpdates["bankName"] = bankName
        if (userData?.bankBranchName !== bankBranchName)
            childUpdates["bankBranchName"] = bankBranchName

        val query = firebaseRef.orderByChild("uid")
            .equalTo(sharedPrefs.getString("uid", null))
            .limitToFirst(1)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.first().ref.updateChildren(childUpdates)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@UpdatePaymentInfoActivity,
                                "Update success",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this@UpdatePaymentInfoActivity,
                                "Update failed ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                } else {
                    // handle if payment info not provided yet
                    Toast.makeText(
                        this@UpdatePaymentInfoActivity,
                        "Error : Could not execute update",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@UpdatePaymentInfoActivity,
                    "Error : Update cancelled",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        })
    }

}
