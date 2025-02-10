package com.example.tetris

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.content.SharedPreferences
import android.widget.TextView
import android.widget.FrameLayout

class MainActivity : AppCompatActivity() {

    private lateinit var menuLayout: View
    private lateinit var gameLayout: LinearLayout
    private lateinit var buttonNewGame: Button
    private lateinit var buttonSettings: Button // Changed back to buttonSettings
    private lateinit var buttonLeft: Button
    private lateinit var buttonRight: Button
    private lateinit var buttonRotate: Button
    private lateinit var buttonDown: Button
    private lateinit var tetrisView: TetrisView
    private lateinit var buttonExitGame: Button
    private lateinit var textViewHighScoreMenu: TextView
    private var highScore = 0
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("TetrisPrefs", MODE_PRIVATE)
        highScore = sharedPreferences.getInt("highScore", 0)

        // Initialize layouts
        menuLayout = findViewById(R.id.menuLayout)
        gameLayout = findViewById(R.id.gameLayout)

        // Initialize menu buttons
        buttonNewGame = menuLayout.findViewById(R.id.buttonNewGame)
        buttonSettings = menuLayout.findViewById(R.id.buttonSettings) // Changed back
        textViewHighScoreMenu = menuLayout.findViewById(R.id.textViewHighScore) // Added

        // Initialize game buttons and TetrisView
        buttonLeft = findViewById(R.id.buttonLeft)
        buttonRight = findViewById(R.id.buttonRight)
        buttonRotate = findViewById(R.id.buttonRotate)
        buttonDown = findViewById(R.id.buttonDown)
        tetrisView = findViewById(R.id.tetrisView)
        buttonExitGame = findViewById(R.id.buttonExitGame)

        // Set onClickListeners for menu buttons
        buttonNewGame.setOnClickListener { startGame() }
        buttonSettings.setOnClickListener { clearHighScore() }  //Сделал сброс счета
        menuLayout.findViewById<Button>(R.id.buttonExit).setOnClickListener { finish() }

        // Set onClickListeners for game buttons
        buttonLeft.setOnClickListener { tetrisView.moveLeft() }
        buttonRight.setOnClickListener { tetrisView.moveRight() }
        buttonRotate.setOnClickListener { tetrisView.rotate() }
        buttonDown.setOnClickListener { tetrisView.moveDown() }
        buttonExitGame.setOnClickListener { showMenu() }

        // Initial setup
        showMenu()
        updateHighScoreDisplay()
    }

    private fun startGame() {
        menuLayout.visibility = View.GONE
        gameLayout.visibility = View.VISIBLE
        tetrisView.startGame()
    }

    private fun showMenu() {
        menuLayout.visibility = View.VISIBLE
        gameLayout.visibility = View.GONE
        tetrisView.stopGame()
        updateHighScoreDisplay()
    }

    private fun saveHighScore() {
        val editor = sharedPreferences.edit()
        editor.putInt("highScore", highScore)
        editor.apply()
    }

    private fun updateHighScoreDisplay() {
        highScore = sharedPreferences.getInt("highScore", 0)
        (menuLayout.findViewById(R.id.textViewHighScore) as TextView).text = "High Score: $highScore"
    }

    private fun clearHighScore() { //Added
        highScore = 0
        saveHighScore()
        updateHighScoreDisplay()
    }
}