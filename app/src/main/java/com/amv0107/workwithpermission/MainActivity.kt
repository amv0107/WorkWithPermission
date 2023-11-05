package com.amv0107.workwithpermission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amv0107.workwithpermission.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.feature1Button.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Вот здесь мы можем безопастно работать с камерой
                onCameraPermissionGranted()
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA),
                    RQ_PERMISSIONS_FOR_FEATURE_1_CODE
                )
            }
        }
        binding.feature2Button.setOnClickListener {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                RQ_PERMISSIONS_FOR_FEATURE_2_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Проверяем результаты запроса
        when (requestCode) {
            RQ_PERMISSIONS_FOR_FEATURE_1_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

            RQ_PERMISSIONS_FOR_FEATURE_2_CODE -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Toast.makeText(this, "Location & record audio permissions granted", Toast.LENGTH_SHORT).show()
                }
            }
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

    private companion object {
        const val RQ_PERMISSIONS_FOR_FEATURE_1_CODE = 1
        const val RQ_PERMISSIONS_FOR_FEATURE_2_CODE = 2
    }
}