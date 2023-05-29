package com.example.firebasekotlin.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.android.volley.toolbox.StringRequest
import com.example.firebasekotlin.databinding.ActivityPaymentConfirmBinding
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.firebasekotlin.models.PaymentModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.PaymentSheetResultCallback
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class PaymentConfirmActivity : AppCompatActivity() {
    // layout binding
    private lateinit var binding: ActivityPaymentConfirmBinding

    //variables for firebase uploads
    val firebase: DatabaseReference = FirebaseDatabase
        .getInstance("https://firebasekotlindb-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("Payments")
    var payData: JSONObject = JSONObject()

    // variables for payemnt gateway
    var Publishable_key: String =
        "pk_test_51N2BPpISxwbF0W8Rj1bW0rAUSkgtQjXQAPzZKI6qX2wHH6dydBnfwBmo4gldTk2wuribBwbSlaMv3kZMrTHg3SRq0059vDJdLs"
    var SecretKey: String =
        "sk_test_51N2BPpISxwbF0W8RB3c51bGc0GmhADzgieHSXE7tChwSG15rOFzmFUE1PjWRfzkOob4SEgbLAx4zcNHO1UhVDPk400bPQaSwDO"
    lateinit var CustomerId: String
    lateinit var EphericalKey: String
    lateinit var ClientSecret: String
    lateinit var paymentSheet: PaymentSheet

    //variables from app
    private var payDescription: String? = "Food delivery payment"
    private var payAmount: String? = "50000"
    private var payCurrency: String? = "lkr"
    private var payType: String? = "card"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        payAmount = intent.getStringExtra("amount")
        binding = ActivityPaymentConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Confirm Payment"

        //adjusting payment amount
        if (payAmount.toString().contains(".")) {
            val decIndex = payAmount.toString().indexOf(".")
            val digAtfDec = payAmount.toString().length - decIndex - 1

            if (digAtfDec > 2)
                payAmount = payAmount.toString().substring(0, decIndex + 3)
            else if (digAtfDec == 1)
                payAmount += "0"
            else if (digAtfDec == 0)
                payAmount += "00"

            payAmount = payAmount.toString().replace(".", "")
        }
        else
            payAmount += "00"

        binding.paymentAmountViewer.text =
            payAmount.toString().substring(0, payAmount.toString().length - 2) +
            "." +
            payAmount.toString().substring(payAmount.toString().length - 2) +
            " LKR"

        Toast.makeText(this, payAmount, Toast.LENGTH_SHORT).show()

        binding.paymentLoadingMessage.visibility = View.VISIBLE
        binding.paymentAmountViewer.visibility = View.GONE
        binding.proceedToPayment.isEnabled = false

        binding.paymentAmountViewer.setOnClickListener{
            binding.proceedToPayment.isEnabled = binding.paymentAmountViewer.text.isDigitsOnly()
        }

        binding.backFromPaymentConfirm.setOnClickListener {
            finish()
        }

        binding.proceedToPayment.setOnClickListener {
            paymentFlow()
        }

        PaymentConfiguration.init(this, Publishable_key)
        paymentSheet = PaymentSheet(this, PaymentSheetResultCallback {
            onPaymentSheetResult(it)
        })

        var req = object : StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
            Response.Listener<String> { response ->
                try {
                    var obj: JSONObject = JSONObject(response)
                    CustomerId = obj.getString("id")
//                    Toast.makeText(this, "CK $CustomerId", Toast.LENGTH_SHORT).show()

                    getEmphericalKey()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $SecretKey"
                return headers
            }
        }

        val reqQue = Volley.newRequestQueue(this)
        reqQue.add(req)
    }

    private fun paymentFlow() {
        paymentSheet.presentWithPaymentIntent(
            ClientSecret, PaymentSheet.Configuration(
                "Odyssey",
                PaymentSheet.CustomerConfiguration(CustomerId, EphericalKey)
            )
        )
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        if (paymentSheetResult is PaymentSheetResult.Completed) {

            // entry to firebase
            val sharedPrefs = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
            val pid: String = firebase.push().key!!

            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currDateTime = formatter.format(Date())

            val paymentEntry = PaymentModel(
                sharedPrefs.getString("uid", null),
                payData.getString("id"),
                payData.getString("amount"),
                payData.getString("currency"),
                payDescription,
                currDateTime
            )

            firebase.child(pid).setValue(paymentEntry)
                .addOnCompleteListener {
                    Toast.makeText(this, "Payment completed", Toast.LENGTH_LONG).show()
                    finish()
                }.addOnFailureListener { err ->
                    Toast.makeText(this, "Payment failed : ${err.message}", Toast.LENGTH_LONG)
                        .show()
                    finish()
                }
        }
    }

    private fun getEmphericalKey() {
        val params = HashMap<String, String>()
        params["customer"] = CustomerId

        val req =
            object : StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
                Response.Listener<String> { response ->
                    try {
                        var obj: JSONObject = JSONObject(response)
                        EphericalKey = obj.getString("id")
//                        Toast.makeText(this, "EK $EphericalKey", Toast.LENGTH_SHORT).show()

                        getClientSecret(CustomerId, EphericalKey)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $SecretKey"
                    headers["Stripe-Version"] = "2022-11-15"
                    return headers
                }

                override fun getParams(): MutableMap<String, String> {
                    return params
                }
            }

        val reqQue = Volley.newRequestQueue(this)
        reqQue.add(req)
    }

    private fun getClientSecret(customerId: String, ephericalKey: String) {

        val params = HashMap<String, String>()
        params["customer"] = customerId

        val req =
            object : StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
                Response.Listener<String> { response ->
                    try {
                        var obj: JSONObject = JSONObject(response)
                        ClientSecret = obj.getString("client_secret")

//                        Toast.makeText(this, "CS $ClientSecret", Toast.LENGTH_SHORT).show()

                        //enable the interface to continue
                        binding.paymentLoadingMessage.visibility = View.GONE
                        binding.paymentAmountViewer.visibility = View.VISIBLE
                        binding.proceedToPayment.isEnabled = true

                        payData.put("id", obj.getString("id"))
                        payData.put("amount", obj.getString("amount"))
                        payData.put("currency", obj.getString("currency"))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $SecretKey"
                    headers["Stripe-Version"] = "2022-11-15"
                    return headers
                }

                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["amount"] = payAmount.toString()
                    params["currency"] = payCurrency.toString()
                    params["payment_method_types[]"] = payType.toString()

                    return params
                }
            }

        val reqQue = Volley.newRequestQueue(this)
        reqQue.add(req)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}