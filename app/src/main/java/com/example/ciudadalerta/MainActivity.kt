package com.example.ciudadalerta

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ciudadalerta.ui.CiudadAlertaUi
import com.example.ciudadalerta.ui.theme.CiudadAlertaTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CiudadAlertaTheme {
                CiudadAlertaUi()
            }
        }
    }
}