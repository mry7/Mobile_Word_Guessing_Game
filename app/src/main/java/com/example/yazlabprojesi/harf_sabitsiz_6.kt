package com.example.yazlabprojesi

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class harf_sabitsiz_6 : ComponentActivity() {

    private val kelimeeListesii = listOf("gezgin", "hazine", "istifa", "bahane", "hikaye", "merhem", "kaynak","pekmez","dikkat","mektup","zengin","volkan","tahmin","umutlu","ilgili")
    private var gecerliiKelimeeIndexii = 0
    private var kalannDenemeeHakkii = maxDenemeeHakkiii
    private var skorrPuanii = 0
    private lateinit var gecerliiKelimee: String
    private lateinit var kelimeeninnHarflerii: MutableList<Char>
    private lateinit var kutularinnDuzenii: GridLayout
    private lateinit var skorrTextii: TextView
    private lateinit var kelimeeGirisii: EditText

    companion object {
        private const val maxDenemeeHakkiii = 6
        private const val KUTUU_BOYUTUU = 100
        private const val KUTUU_MESAFESİİ = 10
        private const val DOGRUU_HARFF_DOGRUU_POZZ_PUANİİ = 10
        private const val DOGRUU_HARFF_YANLİSS_POZZ_PUANİİ = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layouttDuzenii = LinearLayout(this)
        layouttDuzenii.orientation = LinearLayout.VERTICAL
        setContentView(layouttDuzenii)

        // Kelime girişi için EditText
        kelimeeGirisii = EditText(this)
        layouttDuzenii.addView(kelimeeGirisii)

        // Oyun başladığında rastgele bir kelime seç
        rastgeleeKelimeeSecc()

        // Harf kutularını oluştur
        kutulariiOlusturr(layouttDuzenii)

        // Skor göstermek için TextView oluştur
        skorrTextii = TextView(this)
        skorrTextii.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        skorrTextii.gravity = Gravity.CENTER
        layouttDuzenii.addView(skorrTextii)

        // Onayla ve Yeni Oyun butonlarını ekle
        val buttonnDuzeniiParamm = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        buttonnEklee(layouttDuzenii, "Onayla", buttonnDuzeniiParamm) {
            tahminiiYerlestiirr(kelimeeGirisii.text.toString().toLowerCase(), layouttDuzenii)
            kelimeeGirisii.setText("")
        }

        buttonnEklee(layouttDuzenii, "Yeni Oyun", buttonnDuzeniiParamm) {
            oyunuuSifirlaa(layouttDuzenii)
        }

    }


    // Rastgele bir kelime seç
    private fun rastgeleeKelimeeSecc() {
        // Kelime listesinden rastgele bir kelime seçme
        gecerliiKelimee = kelimeeListesii.random()
        // Seçilen kelimenin harflerini bir listeye dönüştürme
        kelimeeninnHarflerii = gecerliiKelimee.toCharArray().toMutableList()
    }

    // Harf kutularını oluştur
    private fun kutulariiOlusturr(layouttDuzenii: LinearLayout) {
        // GridLayout oluşturulur
        kutularinnDuzenii = GridLayout(this)
        kutularinnDuzenii.rowCount = 6 // Sabit satır sayısı 6
        kutularinnDuzenii.columnCount = 6 // Sabit sütun sayısı 6
        kutularinnDuzenii.setBackgroundColor(Color.WHITE)
        kutularinnDuzenii.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        // Satır ve sütun sayısına göre harf kutuları oluşturulur
        for (i in 0 until 6) { // Sabit satır sayısı 6
            for (j in 0 until 6) { // Sabit sütun sayısı 6
                val kutularr = TextView(this)
                val parametreleerr = GridLayout.LayoutParams().apply {
                    width = KUTUU_BOYUTUU
                    height = KUTUU_BOYUTUU
                    setMargins(KUTUU_MESAFESİİ, KUTUU_MESAFESİİ, KUTUU_MESAFESİİ, KUTUU_MESAFESİİ)
                }
                kutularr.layoutParams = parametreleerr
                kutularr.gravity = Gravity.CENTER
                kutularr.setBackgroundColor(Color.WHITE)
                kutularr.textSize = 24f
                kutularinnDuzenii.addView(kutularr)
            }
        }
        // Oluşturulan GridLayout, ana düzene eklenir
        layouttDuzenii.addView(kutularinnDuzenii)
    }

    // Buton eklemek için yardımcı fonksiyon
    private fun buttonnEklee(layouttDuzenii: LinearLayout, text: String, parametreleerr: LinearLayout.LayoutParams, onClick: () -> Unit) {
        val bbuuttoonn = Button(this)
        // Butonun metni belirlenir
        bbuuttoonn.text = text
        // Butonun parametreleri ayarlanır
        bbuuttoonn.layoutParams = parametreleerr
        // Butona tıklama olayı atanır
        bbuuttoonn.setOnClickListener { onClick.invoke() }
        // Buton, belirtilen düzene eklenir
        layouttDuzenii.addView(bbuuttoonn)
    }

    // Tahmin yerleştir
    private fun tahminiiYerlestiirr(girilennKelimee: String, layouttDuzenii: LinearLayout) {
        // Girilen kelimenin uzunluğu, seçilen kelimenin uzunluğuna eşit değilse hata mesajı gösterilir
        if (girilennKelimee.length != gecerliiKelimee.length) {
            toasttMesajii("Tahmin uzunluğu kelime ile aynı olmalıdır.")
            return
        }
        // Satır indeksi, seçilen kelimenin indeksini alır ve bir sonraki kelimeye geçiş için artırır
        val satiirrIndex = gecerliiKelimeeIndexii
        gecerliiKelimeeIndexii++

        // Doğru harf ve doğru pozisyon sayacı
        var dogruuHarff = 0
        var dogruuPozz = 0

        // Girilen kelimenin her harfi için kontrol yapılır
        for (i in girilennKelimee.indices) {
            val harffTahminii = girilennKelimee[i]
            val kutularr = kutularinnDuzenii.getChildAt(satiirrIndex * gecerliiKelimee.length + i) as TextView

            // Tahmin edilen harf, seçilen kelimenin ilgili pozisyonundaki harfle aynıysa
            if (harffTahminii == gecerliiKelimee[i]) {
                // Eğer tahmin edilen harf, seçilen kelimenin ilgili pozisyonundaki harfle aynıysa
                // - Kutu metni tahmin edilen harfle güncellenir.
                // - Kutu arkaplan rengi yeşil olarak ayarlanır.
                kutularr.text = harffTahminii.toString()
                kutularr.setBackgroundColor(Color.GREEN)
                // Doğru pozisyon sayacı artırılır
                dogruuPozz++
            } else if (gecerliiKelimee.contains(harffTahminii)) {
                // Eğer tahmin edilen harf, kelimenin içinde bulunuyorsa (ancak doğru pozisyonda değilse)
                // - Kutu metni tahmin edilen harfle güncellenir.
                // - Kutu arkaplan rengi sarı olarak ayarlanır.
                kutularr.text = harffTahminii.toString()
                kutularr.setBackgroundColor(Color.YELLOW)
                // Doğru harf sayacı artırılır
                dogruuHarff++
            } else {
                // Eğer tahmin edilen harf, kelimenin içinde bulunmuyorsa
                // - Kutu metni tahmin edilen harfle güncellenir.
                // - Kutu arkaplan rengi gri olarak ayarlanır.
                kutularr.text = harffTahminii.toString()
                kutularr.setBackgroundColor(Color.GRAY)
            }
        }

        // Skoru güncelle ve göster
        skorrPuanii = dogruuPozz * DOGRUU_HARFF_DOGRUU_POZZ_PUANİİ + dogruuHarff * DOGRUU_HARFF_YANLİSS_POZZ_PUANİİ

        // Skoru güncelle ve göster
        skoruuGuncellee()

        // Eğer tüm harfler doğru tahmin edilmişse
        if (dogruuPozz == gecerliiKelimee.length) {
            toasttMesajii("Tebrikler! Kelimeyi doğru bildiniz.")
            // Tahmin yapmayı devre dışı bırak
            kelimeeGirisii.isEnabled = false
            // Puanı göster
            skoruuGuncellee()
            // Oyunu sıfırla
            oyunuuSifirlaa(layouttDuzenii)
        } else {
            kalannDenemeeHakkii--
            // Kalan deneme hakkı kontrol edilir
            if (kalannDenemeeHakkii <= 0) {
                toasttMesajii("Maalesef, doğru kelimeyi bulamadınız. Yeniden deneyin.")
                oyunuuSifirlaa(layouttDuzenii)
            } else {
                toasttMesajii("Yanlış kelime. Kalan deneme hakkınız: $kalannDenemeeHakkii")
            }
        }
    }

    // Oyunu sıfırla
    private fun oyunuuSifirlaa(layouttDuzenii: LinearLayout) {
        // Seçilen kelime indeksini sıfırla
        gecerliiKelimeeIndexii = 0
        // Yeni bir rastgele kelime seç
        rastgeleeKelimeeSecc()
        // Eski harf kutularını düzenden kaldır
        layouttDuzenii.removeView(kutularinnDuzenii)
        // Yeni harf kutularını oluştur ve düzene ekle
        kutulariiOlusturr(layouttDuzenii)
        // Kalan deneme hakkını sıfırla
        kalannDenemeeHakkii = maxDenemeeHakkiii
        skoruuGuncellee()
        skorrPuanii = 0
        // Tahmin yapmayı etkinleştir
        kelimeeGirisii.isEnabled = true
    }

    // Puanı güncelle ve göster
    private fun skoruuGuncellee() {
        skorrTextii.text = "Puanınız: $skorrPuanii"
    }

    // Kullanıcıya mesaj göster
    private fun toasttMesajii(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
