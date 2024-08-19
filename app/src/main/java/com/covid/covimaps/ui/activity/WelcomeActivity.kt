package com.covid.covimaps.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.covid.covimaps.R
import com.covid.covimaps.ui.activity.ui.theme.CoviMapsTheme
import com.covid.covimaps.ui.theme.DarkGreen
import com.covid.covimaps.ui.theme.GoogleFonts.shadowsIntoLightFamily

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoviMapsTheme {
                Splash()
            }
        }
    }
}

@Composable
fun Splash(modifier: Modifier = Modifier) {

    val scope = rememberCoroutineScope()
    var image by rememberSaveable { mutableIntStateOf(R.drawable.covid_face_mask) }

    /*LaunchedEffect(Unit) {
        scope.launch {
            var counter = 20
            while (counter > 0) {
                delay(100)
                image = if (counter % 2 == 0) R.drawable.covid_face_mask else R.drawable.covid_hand_sanitizer
                counter--
            }
        }
    }*/

    Scaffold(modifier = Modifier.fillMaxSize().background(color = DarkGreen)) { scaffold ->
        Box(
            modifier = Modifier
                .padding(scaffold)
                .fillMaxSize()
                .background(color = DarkGreen)
        ) {
            /*LazyVerticalGrid(
                columns = GridCells.Fixed(10),
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = false
            ) {
                items(200) {
                    val rotation = if (it % 2 == 0) 50f else if (it % 3 == 0) 45f else 175f
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = "",
                        modifier = Modifier
                            .size(50.dp)
                            .rotate(rotation)
                    )
                }
            }*/
            Text(
                text = "covimaps",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontFamily = shadowsIntoLightFamily,
                    fontSize = 100.sp,
                    color = Color.White
                ),
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashPreview() {
    Splash()
}