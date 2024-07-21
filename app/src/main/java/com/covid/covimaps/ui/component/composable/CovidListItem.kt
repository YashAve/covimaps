package com.covid.covimaps.ui.component.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.covid.covimaps.ui.theme.CoviMapsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainItem(modifier: Modifier = Modifier) {
    val stateName = "AN"
    ElevatedCard(
        onClick = { },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.fillMaxWidth().padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedCard(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(5.dp),
            ) {
                Column(
                    modifier = Modifier
                        .heightIn(min = 50.dp)
                        .widthIn(50.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stateName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainItemPreview() {
    CoviMapsTheme {
        /*"Nicobars": {
                "delta7": {
                    "vaccinated1": 62,
                    "vaccinated2": 811
                },
                "meta": {
                    "population": 36842
                },
                "total": {
                    "vaccinated1": 25394,
                    "vaccinated2": 20313
                }*/
        MainItem()
    }
}