package com.example.mydiscscollection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mydiscscollection.navigation.DiscogsNavGraph
import com.example.mydiscscollection.ui.theme.DiscogsAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiscogsAppTheme {
                DiscogsNavGraph()
            }
        }
    }
}

