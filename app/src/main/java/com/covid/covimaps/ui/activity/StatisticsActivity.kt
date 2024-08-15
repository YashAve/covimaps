package com.covid.covimaps.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.covid.covimaps.ui.composable.Statistics
import com.covid.covimaps.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class StatisticsActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            Statistics(viewModel = viewModel) {
                finish()
            }
        }
    }
}