package com.example.yazlabprojesi


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.yazlabprojesi.ui.theme.YazlabProjesiTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    private val authNeesneesii = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YazlabProjesiTheme {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White
                ) {
                    TasariimmEkranii()
                }
            }
        }
    }

    @Composable
    fun TasariimmEkranii() {
        var emailKullaniciAdi by remember { mutableStateOf("") }
        var siiffree by remember { mutableStateOf("") }

        Column(
            modifier = Modifier.fillMaxSize() // Ekranın tamamını kaplamak için
                .padding(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hoş Geldiniz",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top=90.dp)
            )
            OutlinedTextField(
                value = emailKullaniciAdi,
                onValueChange = { emailKullaniciAdi = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(9.dp))
            OutlinedTextField(
                value = siiffree,
                onValueChange = { siiffree = it },
                label = { Text("Şifre") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(9.dp))
            Button(
                onClick = { kaayiittOll(emailKullaniciAdi, siiffree) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kayıt Ol")
            }
            Spacer(modifier = Modifier.height(9.dp))
            GirissButtonuu(emailKullaniciAdi, siiffree)
        }
    }


    @Composable
    fun GirissButtonuu(emailKullaniciAdi: String, siiffree: String) {
        val contteexxtt = LocalContext.current // alanlarin dolu olup olmadigini kontrol eder

        Button(
            onClick = {
                if (emailKullaniciAdi.isNotEmpty() && siiffree.isNotEmpty()) {
                    giiriissYapp(emailKullaniciAdi, siiffree)
                } else {
                    Toast.makeText(
                        contteexxtt,
                        "Lütfen alanları doldurun.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Giriş Yap")
        }
    }

    private fun kaayiittOll(emailKullaniciAdi: String, siiffree: String) {
        // E-posta ve şifre alanlarının boş olmadığı kontrol edilir
        if (emailKullaniciAdi.isNotEmpty() && siiffree.isNotEmpty()) {
            // Firebase Authentication kullanarak kullanıcı oluşturulur
            authNeesneesii.createUserWithEmailAndPassword(emailKullaniciAdi, siiffree)
                .addOnCompleteListener(this) { taskGorevv ->
                    if (taskGorevv.isSuccessful) {
                        // Kullanıcı başarıyla oluşturulduysa, kullanıcının UID'sini al
                        val kullaniicii = authNeesneesii.currentUser
                        val kullaniiciiKimliigii = kullaniicii?.uid

                        // Firebase Realtime Database referansını al
                        val firebaseeVeriTabanii = FirebaseDatabase.getInstance()
                        val referanssVeriTabanii = firebaseeVeriTabanii.getReference("kullanicilar")

                        // Kullanıcı bilgilerini bir Map'e ekleyerek Realtime Database'e kaydet
                        val kullanicilarinMapi = hashMapOf<String, String>()
                        kullanicilarinMapi["email"] = emailKullaniciAdi

                        // Kullanıcı bilgilerini Realtime Database'e kaydetme işlemi
                        kullaniiciiKimliigii?.let {
                            referanssVeriTabanii.child(kullaniiciiKimliigii).setValue(kullanicilarinMapi)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        // Kayıt başarılı mesajı gösterilir
                                        this@MainActivity,
                                        "Kayıt Başarılı",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(
                                        // Kayıt başarısız mesajı gösterilir
                                        this@MainActivity,
                                        "Kayıt Başarısız: ${exception.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Kayıt Başarısız: ${taskGorevv.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(
                // Eğer alanlar boş ise uyarı mesajı gösterilir
                this@MainActivity,
                "Lütfen alanları doldurun.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun giiriissYapp(emailKullaniciAdi: String, siiffree: String) {
        // Firebase Authentication ile kullanıcı girişi yap
        authNeesneesii.signInWithEmailAndPassword(emailKullaniciAdi, siiffree)
            .addOnCompleteListener(this) { taskGorevv ->
                // Giriş işlemi tamamlandığında geri çağrı
                if (taskGorevv.isSuccessful) {
                    // Giriş başarılı ise
                    val kullaniicii = authNeesneesii.currentUser
                    val mailKullaniicii = kullaniicii?.email
                    Toast.makeText(
                        this@MainActivity,
                        "Giriş Başarılı: $mailKullaniicii",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Giriş başarılı olduğunda yeni aktiviteye yönlendir
                    val iinntteenntt = Intent(this@MainActivity, OdaActivity::class.java)
                    startActivity(iinntteenntt)
                } else {
                    // Giriş başarısız ise
                    Toast.makeText(
                        this@MainActivity,
                        "Giriş Başarısız: ${taskGorevv.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
