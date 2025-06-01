package com.example.semestralnapracaenviro.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semestralnapracaenviro.data.model.ReportData // Vaša dátová trieda
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class MapScreenUiState(
    val dumpSites: List<ReportData> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DumpSitesViewModel : ViewModel() {

    private val _uiState = mutableStateOf(MapScreenUiState(isLoading = true))
    val uiState: State<MapScreenUiState> = _uiState

    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val TAG = "DumpSitesViewModel"
    }

    init {
        fetchDumpSites()
    }

    fun fetchDumpSites() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val snapshot: QuerySnapshot = db.collection("quick_dump_reports").get().await()
                val reports = snapshot.toObjects<ReportData>() // Konverzia na zoznam ReportData objektov
                _uiState.value = MapScreenUiState(dumpSites = reports, isLoading = false)
                Log.d(TAG, "Úspešne načítaných ${reports.size} skládok.")
            } catch (e: Exception) {
                Log.e(TAG, "Chyba pri načítavaní skládok z Firestore", e)
                _uiState.value = MapScreenUiState(
                    isLoading = false,
                    errorMessage = "Chyba pri načítavaní dát: ${e.localizedMessage}"
                )
            }
        }
    }
}