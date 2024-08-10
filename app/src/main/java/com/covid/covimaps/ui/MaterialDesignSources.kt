package com.covid.covimaps.ui

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.covid.covimaps.R

object GoogleFonts {

    val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )

    val archivoBlack = GoogleFont(name = "Archivo Black")
    val archivoBlackFamily = FontFamily(
        Font(
            googleFont = archivoBlack,
            fontProvider = provider,
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        )
    )

    val shadowsIntoLight = GoogleFont(name = "Shadows Into Light")
    val shadowsIntoLightFamily = FontFamily(
        Font(
            googleFont = shadowsIntoLight,
            fontProvider = provider,
            style = FontStyle.Normal
        )
    )
}