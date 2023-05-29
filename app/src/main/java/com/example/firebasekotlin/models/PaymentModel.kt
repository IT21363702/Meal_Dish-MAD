package com.example.firebasekotlin.models

import com.google.firebase.database.ServerValue
import java.lang.Long

data class PaymentModel(
    var uid:String? = null,
    var payment_id:String? = null,
    var amount:String? = null,
    var currency:String? = null,
    var description:String? = null,
    var time: String? = null
)