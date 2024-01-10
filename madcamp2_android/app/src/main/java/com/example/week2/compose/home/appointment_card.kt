package com.example.week2.compose.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun appointment_card(idx: Int){
    val shape = RoundedCornerShape(24f)

    Box(
        Modifier
            .background(Color.White, shape)
            .fillMaxWidth()
            .height(50.dp)
            .clickable { /* TODO */ }){

        Text("약속 $idx",
            Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            textAlign = TextAlign.Center, fontSize = 18.sp, fontWeight = FontWeight.Bold
        )
    }
}