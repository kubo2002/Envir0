import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper // Potrebné pre requestLocationUpdates s callbackom
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue // Potrebné pre delegované properties
import androidx.compose.runtime.mutableStateOf // Potrebné pre delegované properties
import androidx.compose.runtime.setValue // Potrebné pre delegované properties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow // Pre konverziu na StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Predpokladajme, že máte túto dátovú triedu niekde definovanú
// data class DumpSite(val id: String, val title: String, val description: String?, val location: LatLng)

class ReportViewModel : ViewModel() {

    // Stavy pre formulár nahlasovania
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var submissionStatus by mutableStateOf<String?>(null) // Na zobrazenie výsledku odoslania
    var isProcessingSubmission by mutableStateOf(false) // Indikátor pre odosielanie

    // Stavy pre polohu
    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow() // Správna konverzia

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallbackInstance: LocationCallback? = null // Premenovanie pre jasnosť

    companion object {
        private const val TAG = "ReportViewModel"
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(context: Context) {
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        }
        stopLocationUpdates() // Zastavíme predchádzajúce, ak existujú

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
            .setMinUpdateIntervalMillis(5000L)
            .build()

        // Uistite sa, že locationCallbackInstance je inicializovaný pred použitím
        if (locationCallbackInstance == null) {
            locationCallbackInstance = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        _currentLocation.value = LatLng(location.latitude, location.longitude)
                        Log.d(TAG, "Sledovaná poloha aktualizovaná: ${_currentLocation.value}")
                        // Pre účely nahlasovania možno chceme len jednu polohu, takže potom zastavíme
                        // stopLocationUpdates() // Odkomentujte, ak chcete len jednu aktualizáciu
                    }
                }
            }
        }
        // Použitie locationCallbackInstance
        try {
            fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallbackInstance!!, Looper.getMainLooper())
            Log.d(TAG, "Žiadosť o sledovanie polohy odoslaná.")
        } catch (e: SecurityException) {
            Log.e(TAG, "Chyba povolenia pri žiadaní o aktualizácie polohy.", e)
            Toast.makeText(context, "Chýba povolenie pre polohu.", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopLocationUpdates() {
        locationCallbackInstance?.let {
            fusedLocationClient?.removeLocationUpdates(it)
            Log.d(TAG, "Sledovanie polohy zastavené.")
        }
        // Nechceme tu nulovať _currentLocation.value, aby používateľ videl poslednú získanú polohu
    }

    @SuppressLint("MissingPermission")
    fun getSingleCurrentLocation(context: Context) { // Zmenené z getLastKnownLocation pre jasnejší účel
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        }
        stopLocationUpdates() // Zastavíme periodické, ak bežia

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L) // Krátky interval
            .setWaitForAccurateLocation(true) // Počká na presnú polohu
            .setMinUpdateIntervalMillis(1000L)
            .setMaxUpdates(1) // Kľúčové pre získanie len jednej aktualizácie
            .build()

        val singleUpdateCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    _currentLocation.value = LatLng(location.latitude, location.longitude)
                    Log.d(TAG, "Jednorazová poloha získaná: ${location.latitude}, ${location.longitude}")
                    Toast.makeText(context, "Poloha získaná", Toast.LENGTH_SHORT).show()
                } ?: run {
                    Log.w(TAG, "Jednorazová poloha: LocationResult.lastLocation je null")
                    Toast.makeText(context, "Nepodarilo sa získať presnú polohu.", Toast.LENGTH_SHORT).show()
                }
                // Callback sa automaticky odregistruje po MaxUpdates, ale pre istotu môžeme
                fusedLocationClient?.removeLocationUpdates(this)
            }
        }
        try {
            fusedLocationClient?.requestLocationUpdates(locationRequest, singleUpdateCallback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            Log.e(TAG, "Chyba povolenia pri žiadaní o jednorazovú polohu.", e)
            Toast.makeText(context, "Chýba povolenie pre polohu.", Toast.LENGTH_SHORT).show()
        }
    }


    // Funkcia na "odoslanie" hlásenia
    fun submitReport(context: Context) {
        if (title.isBlank()) {
            submissionStatus = "Názov skládky je povinný."
            Toast.makeText(context, submissionStatus, Toast.LENGTH_LONG).show()
            return
        }
        if (_currentLocation.value == null) {
            submissionStatus = "Poloha nie je k dispozícii. Získajte polohu pred odoslaním."
            Toast.makeText(context, submissionStatus, Toast.LENGTH_LONG).show()
            return
        }

        isProcessingSubmission = true
        submissionStatus = "Odosielam hlásenie..."
        Log.d(
            TAG,
            "Odosielam hlásenie: Názov='${title}', Popis='${description}', Poloha='${_currentLocation.value}'"
        )

        // Simulácia sieťovej operácie
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // Počkáme 2 sekundy

            // Tu by bola reálna logika pre ukladanie do Firebase Firestore
            // napr. db.collection("dump_sites").add(dumpSiteData).await()

            val finalStatusMessage = "Hlásenie úspešne odoslané!" // Alebo chybová správa z reálnej operácie
            submissionStatus = finalStatusMessage
            isProcessingSubmission = false // Toto by malo byť pred Toastom a Logom, aby UI reagovalo správne
            Toast.makeText(context, finalStatusMessage, Toast.LENGTH_LONG).show()
            Log.d(TAG, finalStatusMessage) // Použijeme lokálnu non-null premennú

            // Vyčistenie formulára po úspešnom odoslaní
            title = ""
            description = ""
            // _currentLocation.value = null // Rozhodnite sa, či chcete polohu vyčistiť
        }
    }
}