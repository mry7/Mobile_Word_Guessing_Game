package com.example.yazlabprojesi

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.yazlabprojesi.ui.theme.YazlabProjesiTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SunucuİstemciActivity : ComponentActivity() {
    private val authNeesneesii = FirebaseAuth.getInstance()
    private val firebaseeVeriTabanii = FirebaseDatabase.getInstance()
    private val kullaniciiLiistesii = mutableStateListOf<String>()
    private var secilenKullaniicii: String by mutableStateOf("")
    private var istekkGonderildiiMii: Boolean by mutableStateOf(false)
    private var zamanlayiicii: CountDownTimer? = null
    private var kaalaanZaamaan: Long by mutableStateOf(0)
    private var girisYapanKulEmail: String? = null
    private var gelennIsteekk: OyunIstegi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Giriş yapan kullanıcının bilgisini al
        val girisYapanKull = authNeesneesii.currentUser
        girisYapanKulEmail = girisYapanKull?.email

        setContent {
            YazlabProjesiTheme {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White
                ) {
                    ButtonnTasaraimii(girisYapanKulEmail ?: "")
                }
            }
        }

        kullaniiciiMailleriniiSiralaa()
        kullaniiciiIstekleriniiSiralaa(girisYapanKulEmail ?: "")
    }

    @Composable
    fun ButtonnTasaraimii(girisYapanKulEmail: String) {
        // "Oyun isteği" metni
        val oyunIstegi = "Oynamak ister misin?"

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Üst kısım (Gelen istekler)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .shadow(elevation = 4.dp) // Gölge efekti uygulanıyor
                    .background(Color.LightGray)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    gelennIsteekk?.let { iisteekk ->
                        Column {
                            Text(
                                text = "Gelen İstek",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )

                            Text(
                                text = "Gönderen: ${iisteekk.gondereennEmaill}",
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Button(
                                    onClick = { kabulEtButtonuu(iisteekk) },
                                    modifier = Modifier.weight(1f),
                                    enabled = !istekkGonderildiiMii
                                ) {
                                    Text("Kabul Et")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = { reddetButtonuu(iisteekk) },
                                    modifier = Modifier.weight(1f),
                                    enabled = !istekkGonderildiiMii
                                ) {
                                    Text("Reddet")
                                }
                            }
                        }
                    } ?: run {
                        Text(
                            text = "Gelen bir istek bulunmamaktadır.",
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }

            // Alt kısım (İstek gönder)
            Column {
                Text(
                    text = "Kişiye İstek Gönder",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    kullaniciiLiistesii.forEach { kullaniicii ->
                        if (kullaniicii != girisYapanKulEmail) { // Kullanıcı kendi kendine mesaj atamasın
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = secilenKullaniicii == kullaniicii,
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) {
                                            secilenKullaniicii = kullaniicii
                                        }
                                    }
                                )
                                Text(
                                    text = kullaniicii,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { istekGonder(oyunIstegi, girisYapanKulEmail ?: "", secilenKullaniicii) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = secilenKullaniicii.isNotEmpty() && !istekkGonderildiiMii
                ) {
                    Text("Gönder")
                }
            }

            // Zamanı göster
            if (kaalaanZaamaan > 0) {
                Text(
                    text = "Kalan Zaman: ${kaalaanZaamaan / 1000} saniye",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Button(
                onClick = {
                    // OyunActivity'e yönlendirme işlemi
                    val iinntteenntt = Intent(this@SunucuİstemciActivity, OyunActivity::class.java)
                    startActivity(iinntteenntt)
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) {
                Text("Oyuna Başla")
            }
        }


    }



    private fun kullaniiciiMailleriniiSiralaa() {
        // Firebase Realtime Database'deki "kullanicilar" düğümüne referans alınır
        val referanssVeriTabanii = firebaseeVeriTabanii.reference.child("kullanicilar")
        // ValueEventListener ile veritabanı işlemleri dinlenir
        referanssVeriTabanii.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Kullanıcı listesi temizlenir
                kullaniciiLiistesii.clear()
                // Her bir çocuk düğümü için döngü
                for (childSnapshot in snapshot.children) {
                    // Çocuk düğümünden e-posta adresi alınır
                    val mailKullaniicii = childSnapshot.child("email").getValue(String::class.java)
                    mailKullaniicii?.let {
                        // E-posta adresi boş değilse ve giriş yapan kullanıcının e-posta adresi değilse
                        if (it != girisYapanKulEmail) { // Kullanıcı kendi bilgisini tekrar gösterme
                            kullaniciiLiistesii.add(it) // Kullanıcı listesine e-posta adresi eklenir

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SunucuİstemciActivity, "Bir hata oluştu, lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun kullaniiciiIstekleriniiSiralaa(girisYapanKulEmail: String) {
        // Firebase Realtime Database'deki "OyunIstekleri" düğümüne referans alınır
        val referanssVeriTabanii = firebaseeVeriTabanii.reference.child("OyunIstekleri")
        // ValueEventListener ile veritabanı işlemleri dinlenir
        referanssVeriTabanii.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Her bir çocuk düğümü için döngü
                for (childSnapshot in snapshot.children) {
                    // Çocuk düğümünden gönderen ve alıcı e-posta adresleri alınır
                    val gondereennEmaill = childSnapshot.child("gonderenEmail").getValue(String::class.java)
                    val aliiciiEmaill = childSnapshot.child("aliciEmail").getValue(String::class.java)
                    // Alıcı e-posta adresi, giriş yapan kullanıcının e-posta adresiyle eşleşiyorsa
                    if (aliiciiEmaill == girisYapanKulEmail) {
                        // Gelen istek bilgileri OyunIstegi sınıfıyla oluşturulur
                        gelennIsteekk = OyunIstegi(gondereennEmaill ?: "", aliiciiEmaill ?: "")
                        // İşlem tamamlandığında döngüden çıkılır
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SunucuİstemciActivity, "Bir hata oluştu, lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun istekGonder(oyunIstegi: String, gondereennEmaill: String, aliiciiEmaill: String) {
        if (aliiciiEmaill.isNotEmpty() && !istekkGonderildiiMii) {
            // İsteği gönder
            Toast.makeText(this, "İstek gönderildi: $oyunIstegi", Toast.LENGTH_SHORT).show()

            // İsteğin kabul edilmesi için zamanlayıcı başlat
            zamanlayiiciiyiiBaslatt()

            // İsteğin gönderildiğini işaretle
            istekkGonderildiiMii = true

            // İsteği veritabanına ekle
            val isteekkReferanss = firebaseeVeriTabanii.reference.child("OyunIstekleri").push()
            val iisteekk = mapOf(
                "gonderenEmail" to gondereennEmaill,
                "aliciEmail" to aliiciiEmaill
            )
            isteekkReferanss.setValue(iisteekk)
        }
    }


    private fun kabulEtButtonuu(iisteekk: OyunIstegi) {
        // İsteği kabul et
        Toast.makeText(this, "${iisteekk.gondereennEmaill} tarafından gelen istek kabul edildi.", Toast.LENGTH_SHORT).show()
        // Reddedilen oyuncuya mesaj gönder
        mesajjGonderr(iisteekk.gondereennEmaill, "İsteğiniz kabul edildi.")
        // İsteği veritabanından sil
        val isteekkReferansii = firebaseeVeriTabanii.reference.child("OyunIstekleri").orderByChild("aliciEmail").equalTo(girisYapanKulEmail)
        isteekkReferansii.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val gondereennEmaill = snapshot.child("gonderenEmail").getValue(String::class.java)
                    if (gondereennEmaill == iisteekk.gondereennEmaill) {
                        snapshot.ref.removeValue()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@SunucuİstemciActivity, "Bir hata oluştu, lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show()
            }
        })
        // Oyunu başlat
        val iinntteenntt = Intent(this, OyunActivity::class.java)
        iinntteenntt.putExtra("gonderenEmail", iisteekk.gondereennEmaill)
        iinntteenntt.putExtra("aliciEmail", girisYapanKulEmail)
        startActivity(iinntteenntt)
    }



    private fun reddetButtonuu(iisteekk: OyunIstegi) {
        // İsteği reddet
        Toast.makeText(this, "${iisteekk.gondereennEmaill} tarafından gelen istek reddedildi.", Toast.LENGTH_SHORT).show()
        // Reddedilen oyuncuya mesaj gönder
        mesajjGonderr(iisteekk.gondereennEmaill, "İsteğiniz reddedildi.")
        // İsteği veritabanından sil
        val isteekkReferansii = firebaseeVeriTabanii.reference.child("OyunIstekleri").orderByChild("aliciEmail").equalTo(girisYapanKulEmail)
        isteekkReferansii.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val gondereennEmaill = snapshot.child("gonderenEmail").getValue(String::class.java)
                    if (gondereennEmaill == iisteekk.gondereennEmaill) {
                        // Veritabanından isteği sil
                        snapshot.ref.removeValue()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@SunucuİstemciActivity, "Bir hata oluştu, lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mesajjGonderr(receiverEmail: String, message: String) {
        // TODO: Mesaj gönderme işlemi
    }

    private fun zamanlayiiciiyiiBaslatt() {
        // Zamanlayıcıyı başlat (örneğin, 10 saniye)
        zamanlayiicii = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Her saniyede bir işlem yapılabilir
                kaalaanZaamaan = millisUntilFinished
            }

            override fun onFinish() {
                // Zamanlayıcı tamamlandığında
                // İsteği yeniden gönder veya başka bir kullanıcıya gönder
                yeniiIsteekk()
            }
        }.start()
    }

    private fun yeniiIsteekk() {
        // İsteğin yeniden gönderilmesi veya başka bir kullanıcıya gönderilmesi gereken durumları ele al
        istekkGonderildiiMii = false
        zamanlayiicii?.cancel()
        // İsteğin gönderileceği kullanıcıyı sıfırla veya kullanıcı listesinden başka bir kullanıcı seç
        secilenKullaniicii = ""
        kaalaanZaamaan = 0
    }

    override fun onDestroy() {
        super.onDestroy()
        // Aktivite yok edildiğinde, zamanlayıcıyı iptal et
        zamanlayiicii?.cancel()
    }

    data class OyunIstegi(val gondereennEmaill: String, val aliiciiEmaill: String)
}
