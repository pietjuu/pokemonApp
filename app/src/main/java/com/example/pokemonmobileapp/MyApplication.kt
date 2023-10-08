package com.example.pokemonmobileapp

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}