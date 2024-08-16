package com.covid.covimaps.ui.composable

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.covid.covimaps.util.hideSoftKeyBoard

@Composable
fun SearchBar(modifier: Modifier = Modifier, onChange: (String) -> Unit = {}) {
    var value by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current
    val activity = context as Activity

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(value = value, onValueChange = {
            value = it
            onChange(it)
        }, leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = ""
            )
        }, trailingIcon = {
            if (value != "") {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            value = ""
                            onChange(value)
                            activity.hideSoftKeyBoard()
                        }
                )
            }
        }, placeholder = {
            Text(text = "Search")
        }, maxLines = 1, modifier = Modifier.fillMaxWidth())
    }
}