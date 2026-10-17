package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tracker.LifecycleTracker

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LifecycleTracker.init(this)

        if (savedInstanceState != null) {
            LifecycleTracker.isRecreatingForRotation = true
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                LifecycleTracker.userPressedBack = true
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        })

        LifecycleTracker.log(this, "onCreate")
        enableEdgeToEdge()

        setContent {
            BareBonesScreen(
                onPressHome = {
                    LifecycleTracker.userLeftApp = true
                    moveTaskToBack(true)
                },
                onSwitchActivity = {
                    LifecycleTracker.pendingAction = "Switching to another app / activity"
                    val intent = Intent(this, SecondActivity::class.java)
                    startActivity(intent)
                },
                onRotateDevice = {
                    LifecycleTracker.isRecreatingForRotation = true
                    recreate()
                },
                onPressBack = {
                    LifecycleTracker.userPressedBack = true
                    finish()
                }
            )
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        LifecycleTracker.userLeftApp = true
    }

    override fun onStart() {
        super.onStart()
        LifecycleTracker.log(this, "onStart")
    }

    override fun onResume() {
        super.onResume()
        LifecycleTracker.log(this, "onResume")
        LifecycleTracker.userLeftApp = false
        LifecycleTracker.userPressedBack = false
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
fun BareBonesScreen(
    onPressHome: () -> Unit,
    onSwitchActivity: () -> Unit,
    onRotateDevice: () -> Unit,
    onPressBack: () -> Unit
) {
    val context = LocalContext.current
    val events by LifecycleTracker.events.collectAsState()
    val currentUserName by LifecycleTracker.currentUserName.collectAsState()
    var nameInput by remember(currentUserName) { mutableStateOf(currentUserName) }

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Activity Lifecycle (Logcat)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Filter: adb logcat -s ${LifecycleTracker.TAG}",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.DarkGray
            )

            HorizontalDivider(color = Color.LightGray)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Your Name") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("name_text_field")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { LifecycleTracker.setUserName(context, nameInput) },
                    modifier = Modifier.testTag("save_name_button")
                ) {
                    Text("Set Name")
                }
            }

            Text(
                text = "Perform Exact Actions:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            // Row 1: Press Home & Switch Activity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPressHome,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_press_home")
                ) {
                    Text("Press Home", fontSize = 13.sp)
                }

                Button(
                    onClick = onSwitchActivity,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_switch_activity")
                ) {
                    Text("Switch Activity", fontSize = 13.sp)
                }
            }

            // Row 2: Rotate Device & Press Back
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRotateDevice,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_rotate_device")
                ) {
                    Text("Rotate Device", fontSize = 13.sp)
                }

                Button(
                    onClick = onPressBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB91C1C)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_press_back")
                ) {
                    Text("Press Back", fontSize = 13.sp)
                }
            }

            HorizontalDivider(color = Color.LightGray)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Logs with Timestamps & Actions (${events.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                OutlinedButton(
                    onClick = { LifecycleTracker.clearLogs() },
                    modifier = Modifier.testTag("btn_clear_logs")
                ) {
                    Text("Clear")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(events, key = { it.id }) { event ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF3F4F6))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = event.message,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
