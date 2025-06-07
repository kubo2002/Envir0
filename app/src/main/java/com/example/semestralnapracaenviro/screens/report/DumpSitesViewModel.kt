package com.example.semestralnapracaenviro.screens.report

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semestralnapracaenviro.data.model.ReportData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.text.isBlank

/**
 * Dáta pre stav používateľského rozhrania mapovej obrazovky.
 *
 * Obsahuje informácie o stave načítavania, úspechu/nehode pri plánovaní alebo čistení skládky,
 * zoznam skládok a chybové hlásenia.
 */
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

/**
 * ViewModel pre mapovú obrazovku, ktorá zobrazuje a spravuje nahlásené skládky.
 *
 * Zabezpečuje načítanie skládok z Firebase Firestore, označenie skládky ako vyčistenej
 * a aktualizáciu používateľského skóre.
 */
class DumpSitesViewModel : ViewModel() {

    private val _uiState = mutableStateOf(MapScreenUiState(isLoading = true))
    val uiState: State<MapScreenUiState> = _uiState

    private val db = FirebaseFirestore.getInstance()
    var selectedDumpSite = mutableStateOf<ReportData?>(null)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        private const val USERS = "user_profiles"
        private const val DUMP_REPORTS_COLLECTION = "quick_dump_reports"
        private const val POINTS_PER_CLEANED_SITE = 10
    }

    init {
        fetchDumpSites()
    }

    /**
     * Načíta skládky z Firebase Firestore a aktualizuje stav používateľského rozhrania.
     */
    fun fetchDumpSites() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val snapshot: QuerySnapshot = db.collection(DUMP_REPORTS_COLLECTION).get().await()

                if (snapshot.isEmpty) {
                    _uiState.value = MapScreenUiState(dumpSites = emptyList(), isLoading = false)
                    return@launch
                }

                val reports: List<ReportData> = snapshot.toObjects(ReportData::class.java)
                val reportsWithLocation = reports.filter { it.location != null }

                _uiState.value = MapScreenUiState(dumpSites = reportsWithLocation, isLoading = false)

            } catch (e: Exception) {
                _uiState.value = MapScreenUiState(
                    isLoading = false,
                    errorMessage = "Error loading data: ${e.localizedMessage}"
                )
            }
        }
    }

    /**
     * Označí skládku ako vyčistenú. Vymaže ju z databázy a zvýši skóre používateľa.
     *
     * @param siteToClean Objekt skládky, ktorý sa má vyčistiť.
     */
    fun markSiteAsCleaned(siteToClean: ReportData) {
        if (siteToClean.id.isBlank()) {
            _uiState.value = _uiState.value.copy(
                isMarkingAsCleaned = false,
                markingAsCleanedError = "The dump site to delete has no valid ID."
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            isMarkingAsCleaned = true,
            markingAsCleanedError = null,
            markingAsCleanedSuccess = null
        )

        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid

                if (currentUserId != null) {
                    db.collection(USERS).document(currentUserId)
                        .update("score", FieldValue.increment(POINTS_PER_CLEANED_SITE.toLong()))

                    db.collection(DUMP_REPORTS_COLLECTION)
                        .document(siteToClean.id)
                        .delete()
                        .await()

                    if (selectedDumpSite.value?.id == siteToClean.id) {
                        selectedDumpSite.value = null
                    }

                    fetchDumpSites()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isMarkingAsCleaned = false,
                    markingAsCleanedError = "Error deleting site: ${e.localizedMessage}"
                )
            }
        }
    }
}