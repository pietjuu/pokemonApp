package com.example.pokemonmobileapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val API_KEY = "8d88b86a-5596-47ee-9c21-613a6d385bdf"
    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    private val database =
        FirebaseDatabase.getInstance("https://mobilesecurityevaluation-156a5-default-rtdb.europe-west1.firebasedatabase.app/")


    private val deviceInfoRef = database.reference


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // API CALL PAGE
        imageView = findViewById(R.id.imageView)
        button = findViewById(R.id.button)
        button.setOnClickListener {
            getRandomPokemon()
            saveDeviceInfoToFirebase()
        }
    }

    private fun getRandomPokemon() {
        val minPokedexNumber = 0
        val maxPokedexNumber = 151
        val randomOrder = (1..151).random()
        val url =
            "https://api.pokemontcg.io/v2/cards?q=nationalPokedexNumbers:[$minPokedexNumber%20TO%20$maxPokedexNumber]&orderBy=random($randomOrder)"
        val request = Request.Builder()
            .url(url)
            .header("X-Api-Key", API_KEY)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch data", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                Log.d(TAG, "API response: $json")
                try {
                    val jsonObject = JSONObject(json!!)
                    if (jsonObject.has("data")) {
                        val dataArray = jsonObject.getJSONArray("data")
                        if (dataArray.length() > 0) {
                            val randomIndex = (0 until dataArray.length()).random()
                            val data = dataArray.getJSONObject(randomIndex)
                            val images = data.getJSONObject("images")
                            val imageUrl = images.getString("large")
                            runOnUiThread {
                                Glide.with(this@MainActivity)
                                    .load(imageUrl)
                                    .into(imageView)
                            }
                        } else {
                            Log.e(TAG, "JSON response does not contain data object")
                        }
                    } else {
                        Log.e(TAG, "JSON response does not contain data object")
                    }
                } catch (e: JSONException) {
                    Log.e(TAG, "Failed to parse JSON", e)
                }
            }
        })
    }

    private fun saveDeviceInfoToFirebase() {
        val brand = Build.BRAND
        val model = Build.MODEL
        val androidVersion = Build.VERSION.RELEASE
        val sdkVersion = Build.VERSION.SDK_INT

        val deviceInfo = DeviceInfoActivity.DeviceInfo(brand, model, androidVersion, sdkVersion)

        // Generate a unique key for the device info entry
        val key = deviceInfoRef.push().key

        // Save the device info to the database using the generated key
        key?.let {
            deviceInfoRef.child("device_info").child(it).setValue(deviceInfo)
                .addOnSuccessListener {
                    println("Device info saved successfully")
                }
                .addOnFailureListener {
                    println("Failed to save device info: ${it.message}")
                }
        }
    }

}
