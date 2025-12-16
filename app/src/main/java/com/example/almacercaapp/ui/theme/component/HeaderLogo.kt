package com.example.almacercaapp.ui.theme.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.almacercaapp.R

@Composable
fun HeaderLogo(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.zanahoria_logo),
        contentDescription = "Logo AlmaCerca",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}