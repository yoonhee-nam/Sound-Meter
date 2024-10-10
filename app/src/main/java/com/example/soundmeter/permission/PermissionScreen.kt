package com.example.soundmeter.permission

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.soundmeter.R
import com.example.soundmeter.screen.BannersAds
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

    LaunchedEffect(hasAudioRecordPermission) {
        if (!hasAudioRecordPermission) {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    DisposableEffect(context) {
        val listener = object : Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity : Activity) {
                hasAudioRecordPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            }
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        }
        val app = context.applicationContext as Application
        app.registerActivityLifecycleCallbacks(listener)

        onDispose {
            app.unregisterActivityLifecycleCallbacks(listener)
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
                BannersAds()
            }


        } else {

            TextButton(
                onClick = { moveToSetting(context) },
                modifier = Modifier.padding(top = 16.dp, start = 20.dp)
            ) {
                Text(text = stringResource(id = R.string.permission_request))
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