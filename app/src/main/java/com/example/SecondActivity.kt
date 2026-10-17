package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tracker.LifecycleTracker

class SecondActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LifecycleTracker.log(this, "onCreate")

        setContent {
            SecondScreen(onBack = { finish() })
        }
    }

    override fun onStart() {
        super.onStart()
        LifecycleTracker.log(this, "onStart")
    }

    override fun onResume() {
        super.onResume()
        LifecycleTracker.log(this, "onResume")
    }

    override fun onPause() {
        super.onPause()
        LifecycleTracker.log(this, "onPause")
    }

    override fun onStop() {
        super.onStop()
        LifecycleTracker.log(this, "onStop")
    }

    override fun onRestart() {
        super.onRestart()
        LifecycleTracker.log(this, "onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        LifecycleTracker.log(this, "onDestroy")
    }
}

@Composable
fun SecondScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Second Activity",
            fontSize = 20.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "MainActivity is now stopped. Pressing the button below will return to MainActivity and trigger onRestart() -> onStart() -> onResume().",
            fontSize = 14.sp,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("second_activity_back_button")
        ) {
            Text("Back to Main Activity")
        }
    }
}
