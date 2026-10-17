package com.example.model

data class LifecycleEvent(
    val id: Long,
    val timestamp: String,
    val callbackName: String,
    val action: String,
    val message: String
)
