package com.example.semestralnapracaenviro.viewmodels

import android.util.Log
import androidx.compose.animation.core.copy
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.isEmpty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semestralnapracaenviro.data.model.ReportData // Vaša dátová trieda
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.text.isBlank

data class MapScreenUiState(
    val schedulingSuccess: Boolean? = null,
    val schedulingError: String? = null,
    val dumpSites: List<ReportData> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isMarkingAsCleaned: Boolean = false,
    val markingAsCleanedSuccess: Boolean? = null,
    val markingAsCleanedError: String? = null
)

class DumpSitesViewModel : ViewModel() {

    private val _uiState = mutableStateOf(MapScreenUiState(isLoading = true))
    val uiState: State<MapScreenUiState> = _uiState
    private val db = FirebaseFirestore.getInstance()
    var selectedDumpSite = mutableStateOf<ReportData?>(null)
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "DumpSitesViewModel"
        private const val DUMP_REPORTS_COLLECTION = "quick_dump_reports"
    }

    init {
        fetchDumpSites()
    }

    fun fetchDumpSites() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val snapshot: QuerySnapshot = db.collection("quick_dump_reports").get().await()

                if (snapshot.isEmpty) {
                    _uiState.value = MapScreenUiState(dumpSites = emptyList(), isLoading = false)
                    return@launch
                }

                val reports: List<ReportData> = snapshot.toObjects(ReportData::class.java)
                val reportsWithLocation = reports.filter { it.location != null }

                _uiState.value = MapScreenUiState(dumpSites = reports, isLoading = false)


            } catch (e: Exception) {

                _uiState.value = MapScreenUiState(
                    isLoading = false,
                    errorMessage = "Chyba pri načítavaní dát: ${e.localizedMessage}"
                )
            }
        }
    }

    fun markSiteAsCleaned(siteToClean: ReportData) {

        if (siteToClean.id.isBlank()) {

            _uiState.value = _uiState.value.copy(
                isMarkingAsCleaned = false,
                markingAsCleanedError = "Skládka na vymazanie nemá platné ID."
            )
            return
        }

        _uiState.value = _uiState.value.copy(isMarkingAsCleaned = true, markingAsCleanedError = null, markingAsCleanedSuccess = null)

        viewModelScope.launch {
            try {
                db.collection(DUMP_REPORTS_COLLECTION)
                    .document(siteToClean.id)
                    .delete()
                    .await()

                if (selectedDumpSite.value?.id == siteToClean.id) {
                    selectedDumpSite.value = null
                }

                fetchDumpSites()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isMarkingAsCleaned = false,
                    markingAsCleanedError = "Chyba pri vymazávaní: ${e.localizedMessage}"
                )
            }
        }
    }
}