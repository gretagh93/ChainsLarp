package com.chains.larp.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        color = BlueConsole
    ),
    h2 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        color = BlueConsole
    ),
    button = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        color = BlueConsole
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        color = BlueConsole
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = BlueConsole
    ),
    body2 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        color = BlueConsole
    ),
    caption = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = BlueConsole,
    ),
    defaultFontFamily = FontFamily.SansSerif,
)