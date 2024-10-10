package com.example.soundmeter.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.soundmeter.screen.DecibelText
import com.example.soundmeter.screen.GraphUi
import com.example.soundmeter.screen.Meter

@Composable
fun PermissionScreen() {

    val context = LocalContext.current

    var hasAudioRecordPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasAudioRecordPermission = isGranted
    }

    // 권한 요청을 다시 시도하도록 LaunchedEffect 사용
    LaunchedEffect(Unit) {
        if (!hasAudioRecordPermission) {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
        ,
        contentAlignment = Alignment.Center
    ) {
        if (hasAudioRecordPermission) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Meter(
                    modifier = Modifier.padding(top = 60.dp)
                )
                DecibelText()
                GraphUi()
            }
            //UI변경하기

//            PulsatingCircles()


        } else {
            Text(
                modifier = Modifier.padding(top = 130.dp, start = 20.dp, end = 20.dp),
                text = "권한이 필요합니다. 이 앱은 음성을 녹음하기 위해 권한이 필요합니다.",
                style = MaterialTheme.typography.bodyLarge
            )
            TextButton(
                onClick = { moveToSetting(context) },
                modifier = Modifier.padding(top = 16.dp, start = 20.dp)
            ) {
                Text("권한 요청 다시 시도")
            }
        }
    }
}

private fun moveToSetting(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}