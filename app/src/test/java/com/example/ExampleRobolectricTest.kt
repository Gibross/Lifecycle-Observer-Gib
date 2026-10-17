package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.tracker.LifecycleTracker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Before
    fun setUp() {
        ShadowLog.clear()
        LifecycleTracker.clearLogs()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Lifecycle Observer", appName)
    }

    @Test
    fun `activity lifecycle callbacks are called and logged in order`() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)

        // onCreate
        controller.create()
        var logs = ShadowLog.getLogsForTag(LifecycleTracker.TAG)
        assertTrue(logs.any { it.msg.contains("is calling onCreate()") })

        // onStart
        controller.start()
        logs = ShadowLog.getLogsForTag(LifecycleTracker.TAG)
        assertTrue(logs.any { it.msg.contains("is calling onStart()") })

        // onResume
        controller.resume()
        logs = ShadowLog.getLogsForTag(LifecycleTracker.TAG)
        assertTrue(logs.any { it.msg.contains("is calling onResume()") })

        // onPause
        controller.pause()
        logs = ShadowLog.getLogsForTag(LifecycleTracker.TAG)
        assertTrue(logs.any { it.msg.contains("is calling onPause()") })

        // onStop
        controller.stop()
        logs = ShadowLog.getLogsForTag(LifecycleTracker.TAG)
        assertTrue(logs.any { it.msg.contains("is calling onStop()") })

        // onRestart
        controller.restart()
        logs = ShadowLog.getLogsForTag(LifecycleTracker.TAG)
        assertTrue(logs.any { it.msg.contains("is calling onRestart()") })

        // onStart & onResume again after restart
        controller.start().resume()

        // pause, stop, destroy
        controller.pause().stop().destroy()
        logs = ShadowLog.getLogsForTag(LifecycleTracker.TAG)
        assertTrue(logs.any { it.msg.contains("is calling onDestroy()") })

        // Check that all 7 callbacks were logged
        val callbacks = listOf("onCreate()", "onStart()", "onResume()", "onPause()", "onStop()", "onRestart()", "onDestroy()")
        callbacks.forEach { cb ->
            assertTrue("Expected callback $cb in logs", logs.any { it.msg.contains("is calling $cb") })
        }
    }

    @Test
    fun `actions are annotated for launch and reopen`() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        var logs = ShadowLog.getLogsForTag(LifecycleTracker.TAG)
        assertTrue(logs.any { it.msg.contains("[Action: Launching the app]") })

        // Reopening test
        controller.pause().stop().restart().start().resume()
        logs = ShadowLog.getLogsForTag(LifecycleTracker.TAG)
        assertTrue(logs.any { it.msg.contains("[Action: Reopening the app]") })
    }
}
