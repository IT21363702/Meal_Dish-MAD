package com.example.firebasekotlin.models

data class EmployeePaymentInfoModel(
    val uid:String? = null,
    val accHoldersName:String? = null,
    val accNo:String? = null,
    val bankName:String? = null,
    val bankBranchName:String? = null,
    var approval:String? = PENDING
)
{
    companion object {
        const val ACCEPTED:String = "Accepted"
        const val INVALID:String = "Invalid"
        const val PENDING:String = "Pending"
    }
}