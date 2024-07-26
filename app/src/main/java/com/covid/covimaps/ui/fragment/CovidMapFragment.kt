package com.covid.covimaps.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import com.covid.covimaps.ui.theme.CoviMapsTheme

class CovidMapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                CoviMapsTheme {
                    Main()
                }
            }
        }
    }
}

@Composable
private fun Main(modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colorScheme.primary) {
        Scaffold {
            Column(modifier = Modifier.padding(it)) {
                Text(text = "CoviMapFragment")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainPreview() {
    Main()
}