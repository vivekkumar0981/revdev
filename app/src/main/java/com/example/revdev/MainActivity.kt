package com.example.revdev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.runtime.Composable

import com.example.revdev.navigation.RevdevNavigation
import com.example.revdev.ui.theme.RevdevTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RevdevTheme {
                RevdevApp()
            }
        }
    }
}

@Composable
fun RevdevApp() {
    RevdevNavigation()
}
