package com.example.dodge_obstacles_game

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dodge_obstacles_game.model.leaderboardEntry
import com.example.dodge_obstacles_game.utilities.Constants
import com.example.dodge_obstacles_game.utilities.SharedPreferencesManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView

class ScoreActivity : AppCompatActivity() {

    /* ───────────────────────── UI REFERENCES ───────────────────────── */
    private lateinit var score_LBL_title: MaterialTextView
    private lateinit var score_BTN_newGame: MaterialButton
    private lateinit var score_BTN_back: MaterialButton

    /* ───────────────────────── LOCATION ───────────────────────── */
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    /* ───────────────────────── ACTIVITY LIFECYCLE ───────────────────────── */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_score)

        applySystemInsets()
        findViews()
        initViews()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2001 && resultCode == RESULT_OK) {
            requestLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation()
        }
    }

    /* ───────────────────────── INITIALIZATION ───────────────────────── */
    private fun applySystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun findViews() {
        score_LBL_title = findViewById(R.id.score_LBL_title)
        score_BTN_newGame = findViewById(R.id.score_BTN_newGame)
        score_BTN_back = findViewById(R.id.score_BTN_back)
    }

    private fun initViews() {
        val bundle = intent.extras
        val score = bundle?.getInt(Constants.BundleKeys.SCORE_KEY, 0)
        val timeMillis = bundle?.getString(Constants.BundleKeys.TIME_KEY, "00:00:00")

        displayScore(score, timeMillis)
        setupButtons()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocation()
        showNameDialog(score, timeMillis)
    }

    /* ───────────────────────── UI HANDLING ───────────────────────── */
    private fun displayScore(score: Int?, timeMillis: String?) {
        score_LBL_title.text = buildString {
            append("Game Over!\nScore: ")
            append(score)
            append("\nTime: ")
            append(timeMillis)
        }
    }

    private fun setupButtons() {
        score_BTN_newGame.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        score_BTN_back.setOnClickListener {
            startActivity(Intent(this, StartActivity::class.java))
            finish()
        }
    }

    /* ───────────────────────── LOCATION HANDLING ───────────────────────── */
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    }

    private fun requestEnableGPS() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()
        val client = LocationServices.getSettingsClient(this)

        client.checkLocationSettings(settingsRequest)
            .addOnSuccessListener { requestLocation() }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try { exception.startResolutionForResult(this, 2001) } catch (_: Exception) {}
                }
            }
    }

    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }

        if (!isLocationEnabled()) {
            requestEnableGPS()
            return
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                location?.let {
                    currentLatitude = it.latitude
                    currentLongitude = it.longitude
                }
            }
    }

    /* ───────────────────────── NAME INPUT / LEADERBOARD ───────────────────────── */
    private fun showNameDialog(score: Int?, time: String?) {
        val input = EditText(this).apply { hint = "Enter your name"; maxLines = 1 }
        val gameMode = SharedPreferencesManager.getInstance()
            .getString(Constants.SP_KEYS.GAME_MODE, Constants.GAME_MODE.BUTTONS_NORMAL)

        val dialog = MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        )
            .setTitle("Save Score")
            .setView(input)
            .setCancelable(true)
            .setPositiveButton("Save") { _, _ ->
                var name = input.text.toString().trim()
                if (name.isEmpty()) name = ""
                SharedPreferencesManager.getInstance().saveLeaderboardEntry(
                    gameMode,
                    leaderboardEntry(
                        name = name,
                        score = score,
                        time = time,
                        latitude = currentLatitude,
                        longitude = currentLongitude
                    )
                )
            }
            .setNegativeButton("Cancel", null)
            .show()

        // Center buttons equally
        dialog.apply {
            val positiveButton = getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = getButton(AlertDialog.BUTTON_NEGATIVE)
            val layoutParams = positiveButton?.layoutParams as? LinearLayout.LayoutParams
            layoutParams?.apply { weight = 1f; gravity = Gravity.CENTER }
            positiveButton?.layoutParams = layoutParams
            negativeButton?.layoutParams = layoutParams
        }
    }
}