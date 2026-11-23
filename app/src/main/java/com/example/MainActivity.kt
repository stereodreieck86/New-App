package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ContentRepository
import com.example.data.CreatorDeskDatabase
import com.example.data.ThemePreferencesRepository
import com.example.ui.CreatorDeskApp
import com.example.ui.theme.CreatorDeskTheme
import com.example.viewmodel.CreatorDeskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = CreatorDeskDatabase.getInstance(applicationContext)
        val contentRepository = ContentRepository(database.contentItemDao())
        val preferencesRepository = ThemePreferencesRepository(applicationContext)
        val factory = CreatorDeskViewModel.factory(
            contentRepository = contentRepository,
            preferencesRepository = preferencesRepository
        )

        setContent {
            val viewModel: CreatorDeskViewModel = viewModel(factory = factory)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            CreatorDeskTheme(themeMode = uiState.themeMode) {
                CreatorDeskApp(viewModel = viewModel)
            }
        }
    }
}
