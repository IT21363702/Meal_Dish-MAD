package com.example.firebasekotlin.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasekotlin.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()

        binding.redirToSignUp.setOnClickListener{
            var intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener{
            val email = binding.editTextEmailAddress.text.toString()
            val password = binding.editTextPassword.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty() ) {
                    binding.btnLogin.isEnabled = false
                    binding.btnLogin.text = "Signing in..."
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if(it.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            if (user == null) {
                                Toast.makeText(this, "Login Failed, Try again", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                val sharedPrefs = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
                                val editor = sharedPrefs.edit()
                                editor.putString("uid", user.uid )
                                editor.apply()

                                binding.btnLogin.isEnabled = true
                                binding.btnLogin.text = "Sign in"

                                val intent = Intent(this, PaymentsDashboard::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Toast.makeText( this, it.exception?.message?.toString() , Toast.LENGTH_SHORT).show()
                            binding.btnLogin.isEnabled = true
                            binding.btnLogin.text = "Sign in"
                        }
                    }
            } else {
                Toast.makeText( this, "Email and password are required.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}