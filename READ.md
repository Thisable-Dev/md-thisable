# Android Development - Thisable

Thisable built using Kotlin Programming Language and implemented a recommended MVVM Architecture

![mvvmarchitecture](https://www.journaldev.com/wp-content/uploads/2018/04/android-mvvm-pattern.png)

## 1. Login using Google Authentication
To make it easier to grant access to application, we provide login feature using Google Authentication. 

## 2. BISINDO (Bahasa Isyarat Indonesia) Translator
This feature helps us to translate Indonesian sign language words. This feature developed by using ML Kit as a real time detection package.

## 3. Object Detection Feature
This feature helps find objects around you. This feature developed by using ML Kit as a real time detection package.

## 4. Text Detection Feature
This feature help read text in front of you. This feature developed by using Cloud Vision API provided by the Cloud Computing team.

## 5. Bug Report Feature
This feature to provide feedback for our application for future development. To store the feedback to Firestore Database, we use the REST API by the Cloud Computing team.

## 6. Color Detection
This feature help to inform what color of the object that direct to your camera. This feature develop using OpenCV 4.5.5 Library

## 7. Family Help
This feature help to make call your emergency contact

## Project Setup
  - ##### Prerequisites
    - [Android Studio](https://developer.android.com/studio)
    - [JRE & JDK](https://www.oracle.com/java/technologies/downloads/)
  - ##### Installation
    - Clone the project to your local storage
      ``` git clone https://github.com/Thisable-Dev/md-thisable.git```
    - Setup your Cloud Vision API KEY
      ```defaultConfig {buildConfigField("String", "CLOUD_VISION_KEY", '"Your Api Key"')}```

## Used library in this project :
  - [Android Jetpack](https://developer.android.com/jetpack)
  - [Lifecycle & Livedata](https://developer.android.com/jetpack/androidx/releases/lifecycle)
  - [Kotlin Flow](https://developer.android.com/kotlin/flow)
  - [Navigation Component](https://developer.android.com/jetpack/androidx/releases/navigation)
  - [Kotlin Coroutines](https://developer.android.com/kotlin/coroutines)    
  - [Retrofit](https://square.github.io/retrofit/)
  - [Dependency Injection with Hilt](https://developer.android.com/training/dependency-injection/hilt-android)   
  - [Firebase](https://firebase.google.com/docs/)    
  - [Ok Http 3](https://square.github.io/okhttp/) 
  - [ML Kit](https://developers.google.com/ml-kit)
  - [Cloud Vision API](https://cloud.google.com/vision)
