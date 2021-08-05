package com.example.nursery

import com.google.firebase.firestore.DocumentReference

data class OrderItem(
    var pQuantity: Long = 1,
    var plant: Plant,
    var pRef: DocumentReference,
    var oRef: DocumentReference
)