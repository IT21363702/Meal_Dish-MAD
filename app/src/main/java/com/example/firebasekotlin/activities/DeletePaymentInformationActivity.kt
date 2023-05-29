package com.example.firebasekotlin.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasekotlin.databinding.ActivityDeletePaymentInformationBinding
import com.google.firebase.database.*

class DeletePaymentInformationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeletePaymentInformationBinding
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeletePaymentInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNoKeep.setOnClickListener {
            finish()
        }

        binding.btnYesDelete.setOnClickListener {
            binding.btnNoKeep.isEnabled = false
            binding.btnYesDelete.isEnabled = false
            binding.deleteWaringMessage.visibility = View.GONE

            sharedPrefs = getSharedPreferences("userPrefs", MODE_PRIVATE)
            firebaseRef = FirebaseDatabase
                .getInstance("https://firebasekotlindb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("PaymentInformation")

            val query = firebaseRef.orderByChild("uid")
                .equalTo(sharedPrefs.getString("uid", null))
                .limitToFirst(1)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.children.first().ref.removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@DeletePaymentInformationActivity,
                                    "Payment Information Removed Successfully", Toast.LENGTH_LONG
                                ).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this@DeletePaymentInformationActivity,
                                    "An error occurred : ${it.message}", Toast.LENGTH_LONG
                                ).show()
                                finish()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@DeletePaymentInformationActivity,
                        "An error occurred. Try again.", Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            })
        }
    }
}