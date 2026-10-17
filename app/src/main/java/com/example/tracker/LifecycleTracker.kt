package com.example.tracker

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.model.LifecycleEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong

object LifecycleTracker {
    const val TAG = "ActivityLifecycle"
    private const val PREFS_NAME = "lifecycle_prefs"
    private const val KEY_USER_NAME = "user_name"
    private const val DEFAULT_NAME = "Nathan Lei C. Santos"

    private val idCounter = AtomicLong(1)
    private val _events = MutableStateFlow<List<LifecycleEvent>>(emptyList())
    val events: StateFlow<List<LifecycleEvent>> = _events.asStateFlow()

    private val _currentUserName = MutableStateFlow(DEFAULT_NAME)
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    private var isInitialized = false

    // Action state tracking
    var pendingAction: String? = null
    var isRecreatingForRotation = false
    var userPressedBack = false
    var userLeftApp = false

    fun init(context: Context) {
        if (!isInitialized) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedName = prefs.getString(KEY_USER_NAME, DEFAULT_NAME) ?: DEFAULT_NAME
            _currentUserName.value = savedName
            isInitialized = true
        }
    }

    fun setUserName(context: Context, newName: String) {
        val trimmed = newName.trim().ifEmpty { DEFAULT_NAME }
        _currentUserName.value = trimmed
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USER_NAME, trimmed).apply()
    }

    fun getUserName(context: Context? = null): String {
        if (!isInitialized && context != null) {
            init(context)
        }
        return _currentUserName.value
    }

    fun log(activity: Activity, callbackName: String, explicitAction: String? = null) {
        val name = getUserName(activity.applicationContext)
        val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
        val timestamp = timeFormat.format(Date())

        val action = explicitAction ?: resolveAction(activity, callbackName)
        val message = "[$timestamp] $name is calling $callbackName() [Action: $action]"

        Log.d(TAG, message)

        val event = LifecycleEvent(
            id = idCounter.getAndIncrement(),
            timestamp = timestamp,
            callbackName = callbackName,
            action = action,
            message = message
        )
        _events.value = listOf(event) + _events.value
    }

    private fun resolveAction(activity: Activity, callbackName: String): String {
        // Priority 1: Configuration change / device rotation
        if (activity.isChangingConfigurations || isRecreatingForRotation) {
            if (callbackName == "onResume") {
                isRecreatingForRotation = false
            }
            return "Rotating the device"
        }

        // Priority 2: Pressing Back / finishing
        if (userPressedBack || activity.isFinishing) {
            return "Pressing Back"
        }

        // Priority 3: Reopening the app
        if (callbackName == "onRestart") {
            pendingAction = "Reopening the app"
            userLeftApp = false
            return "Reopening the app"
        }
        if (pendingAction == "Reopening the app") {
            if (callbackName == "onResume") pendingAction = null
            return "Reopening the app"
        }

        // Priority 4: Home button pressed / app switched
        if (userLeftApp) {
            return "Pressing Home / Switching to another app"
        }

        // Priority 5: Explicit pending action
        if (pendingAction != null) {
            val act = pendingAction!!
            if (callbackName == "onStop" || callbackName == "onResume") {
                pendingAction = null
            }
            return act
        }

        // Priority 6: Default initial startup
        if (callbackName == "onCreate" || callbackName == "onStart" || callbackName == "onResume") {
            return "Launching the app"
        }

        return "Active session"
    }

    fun clearLogs() {
        _events.value = emptyList()
    }
}
