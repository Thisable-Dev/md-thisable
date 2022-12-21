<h1 align="center"> TeDi - Teman Disabilitas</h1> <br>
<p align="center">
  <a href="https://user-images.githubusercontent.com/36506828/208969534-2c0a1d21-f1a6-4241-98e0-76bd39899885.png">
    <img alt="TeDi" title="TeDi" src="https://user-images.githubusercontent.com/36506828/208969534-2c0a1d21-f1a6-4241-98e0-76bd39899885.png" width="500">
  </a>
</p>

# Tedi (Teman Disablitas)

## <a name="introduction"></a> Introduction :
TeDi, an all-in-one accessibility app to make everyday life easier for disabled people. There are 6 main features, namely BISINDO Translation, Object Detection, Color Detection, Money Detection, Text Detection, and Family Help. In addition, there are in-app bug reports to report crashes within the application.. TeDi App Powered by Kotlin, Android Jetpack, and Koin, also implementing [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Libraries](#libraries)
- [Project Structure](#project-structures)

## <a name="features"></a> Features :
A few features SumbanginAja's has on this app :

* Google Login
* <b>BISINDO Translator</b> : Feature to translating BISINDO (Bahasa Isyarat Indonesia)
* <b>Object Detection</b> : Feature to detecting objects around user
* <b>Text Detection</b> : Feature to read the text from the book, paper, etc 
* <b>Currency Detection</b> : Feature to classify the money paper (Rupiah)
* <b>Color Detection</b> : Feature to detecting the color
* <b>Family Help</b> : Feature to for emergency purpose 
* <b>Bug Report<b> : Feature for reporting if the users found out some bugs or error inside of this app 


## <a name="libraries"></a> Libraries :
  - [Android Jetpack](https://developer.android.com/jetpack)
  - [Lifecycle & Livedata](https://developer.android.com/jetpack/androidx/releases/lifecycle)
  - [Kotlin Flow](https://developer.android.com/kotlin/flow)
  - [Navigation Component](https://developer.android.com/jetpack/androidx/releases/navigation)
  - [Kotlin Coroutines](https://developer.android.com/kotlin/coroutines)
  - [Retrofit](https://square.github.io/retrofit/)
  - [Hilt as Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android?hl=id)   
  - [Ok Http 3](https://square.github.io/okhttp/) 

## <a name="project-structures"></a> Project Structure :
* `adapter`
* `analysis`
* `analyzer`
* `base`
* `core_model`
* `custom_view`
* `data`
* `di`
* `factory`
* `interfaces`
  - `observer_analyzer`
  - `observer_cloud`
  - `observer_cloudstorage`!
  - `observer_core`
  - `observer_keyboard`
* `notification`
* `presentation`
  - `about`
  - `bugreport`
  - `dialog`
  - `familyhelp`
  - `faq`
  - `feature_cloud`
  - `feature_color`
  - `feature_currency`
  - `feature_object`
  - `feature_sign_language`
  - `feature_text`
  - `help`
  - `home`
  - `instruction`
  - `onboarding`
  - `profile`
  - `splash`
  - `termagreement`
  - `view`
* `utils`
  - `dialogs`
  - `ext`
  - `image_utility`  

