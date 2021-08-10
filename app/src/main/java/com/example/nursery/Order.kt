package com.example.nursery

import com.google.firebase.firestore.DocumentReference

data class Order (
    var dateAndTime:String?=null,
    var pQuantity: Long = 1,
    var plant: Plant,
    var oRef: DocumentReference
)