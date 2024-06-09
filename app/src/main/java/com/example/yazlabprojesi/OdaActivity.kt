package com.example.yazlabprojesi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class OdaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ButtonnTasaraimii()
        }
    }

    @Composable
    fun ButtonnTasaraimii() {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    // Button 1'e tıklama işlemleri buraya yazılacak
                },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()

            ) {
                Text("harf sabiti + 5 harfli ", textAlign = TextAlign.Center)
            }

            Button(
                onClick = {
                    // Button 2'ye tıklama işlemleri buraya yazılacak
                },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
            ) {
                Text("harf sabiti + 6 harfli ", textAlign = TextAlign.Center)
            }

            Button(
                onClick = {
                    yonlendirr()

                },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
            ) {
                Text("kelime + 5 harfli", textAlign = TextAlign.Center)
            }

            Button(
                onClick = {
                    yonlendir()
                },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
            ) {
                Text("kelime + 6 harfli", textAlign = TextAlign.Center)
            }
        }
    }

    private fun yonlendirr() {
        val iinntteenntt = Intent(this@OdaActivity, SunucuİstemciActivity::class.java)
        startActivity(iinntteenntt)
    }

    private fun yonlendir() {
        val iinntteenntt = Intent(this@OdaActivity, kanal_sabitsiz_6::class.java)
        startActivity(iinntteenntt)
    }
}
