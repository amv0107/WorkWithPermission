package com.amv0107.workwithpermission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.amv0107.workwithpermission.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val feature1PermissionsRequestLauncher = registerForActivityResult(
        RequestPermission(),
        ::onGotPermissionsResultForFeature1
    )

    private val feature2PermissionsRequestLauncher = registerForActivityResult(
        RequestMultiplePermissions(),
        ::onGotPermissionsResultForFeature2
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.feature1Button.setOnClickListener {
            feature1PermissionsRequestLauncher.launch(Manifest.permission.CAMERA)
        }
        binding.feature2Button.setOnClickListener {
            feature2PermissionsRequestLauncher.launch(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private fun onGotPermissionsResultForFeature1(granted: Boolean) {
        if (granted) {
            // Вот здесь мы можем безопастно работать с камерой
            onCameraPermissionGranted()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // Показываем диалог с пояснением зачем нам permission
                askUserForOpeningAppSettings()
            } else {
                // oops, can't do anything
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onGotPermissionsResultForFeature2(grantResults: Map<String, Boolean>) {
        if (grantResults.entries.all { it.value }) {
            Toast.makeText(this, "Location & record audio permissions granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askUserForOpeningAppSettings() {

        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,// Создаем интент на запуск системной активити, которая буде показывать системные настройки
            Uri.fromParts(
                "package",
                packageName,
                null
            ) // именно нашего приложения. Эта строчка указывает на ссылку на наше приложение
        )
        if (packageManager.resolveActivity(appSettingsIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            Toast.makeText(this, "Permissions are denied forever", Toast.LENGTH_SHORT).show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Permissions denied")
                .setMessage(
                    "You have denied permissions forever. " +
                            "You can change your decision in app settings.\n\n" +
                            "Would you like to open app settings?"
                )
                .setPositiveButton("Open") { _, _ ->
                    startActivity(appSettingsIntent)
                }
                .create()
                .show()
        }
    }

    private fun onCameraPermissionGranted() {
        Toast.makeText(this, "Camera permission is granted", Toast.LENGTH_SHORT).show()
    }
}