# Permissions

## Ссылки

* [RomanAndrushchenko](https://www.youtube.com/watch?v=cGPPZqp8qis&ab_channel=RomanAndrushchenko)
* [Developers Android](https://developer.android.com/guide/topics/permissions/overview)

## Два способа запроса разрешений

* [Стандартный способ проверки и запроса разрешений:](ReadMe.md#standard)
    - Проверка разрешений: ContextCompat.checkSelfPermission()
    - Запрос разрешений:
        * ActivityCompat.requestPermissions()
        * Activity.requestPermissions()
        * Fragment.requestPermissions()
    - Методы обратного вызова для обработки результата:
        * Activity.onRequestPermissionsResult()
        * Fragment.onRequestPermissionsResult()
* Контракты Activity Result API:
    - RequestPermission()
    - RequestMultiplePermissions()

## Решение

### Core

Добавить permissions в Manifest

### Standard

```kotlin
// Проверка разрешений, а дано ли нашему приложению разрешение
    if (ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    ) {
        // Вот здесь мы можем безопастно работать с камерой
        onCameraPermissionGranted()
    } else {
//Запрос разрешений
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            RQ_PERMISSIONS_FOR_FEATURE_1_CODE
        )
    }

    binding.feature2Button.setOnClickListener {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            RQ_PERMISSIONS_FOR_FEATURE_2_CODE
        )
    }

// Callback метод для обработки результата запроса:
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Проверяем результаты запроса
        when (requestCode) {
            RQ_PERMISSIONS_FOR_FEATURE_1_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Если пользователь сразу разрешил permission
                    // Вот здесь мы можем безопастно работать с камерой
                    onCameraPermissionGranted()
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        // Нам запретили навсегда
                        // Показываем диалог с пояснением зачем нам permission
                        askUserForOpeningAppSettings()
                    } else {
                        // Нам запретили не навсегда
                        // Можно вывести тоаст с пояснением какие функции в приложении не будут работать
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
        // Здесь проверяем что такая активити существует, т.е. система может показать настройки приложения
        if (packageManager.resolveActivity(appSettingsIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            Toast.makeText(this, "Permissions are denied forever", Toast.LENGTH_SHORT).show()
        } else {
            // Подробная инструкция как пользователь может изменить свое решение
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

    private companion object {
        const val RQ_PERMISSIONS_FOR_FEATURE_1_CODE = 1
        const val RQ_PERMISSIONS_FOR_FEATURE_2_CODE = 2
    }
```
