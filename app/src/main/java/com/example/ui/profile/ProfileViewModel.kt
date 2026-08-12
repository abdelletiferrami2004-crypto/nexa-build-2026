package com.example.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ProfileUiState(
    val phoneNumber: String = "",
    val fullName: String = "",
    val city: String = "",
    val bio: String = "",
    val isLoading: Boolean = false,
    val message: String? = null
)

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadUserProfile()
    }

    private suspend fun getUserId(): String {
        val currentUser = auth.currentUser
        if (currentUser != null) return currentUser.uid
        return try {
            val result = auth.signInAnonymously().await()
            result.user?.uid ?: "user_default_session"
        } catch (e: Exception) {
            "user_default_session"
        }
    }

    // 1. تحميل بيانات المستخدم من Firebase Firestore (مجموعة users)
    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val userId = getUserId()
                val doc = db.collection("users").document(userId).get().await()
                if (doc.exists()) {
                    _uiState.value = _uiState.value.copy(
                        phoneNumber = doc.getString("phoneNumber") ?: "",
                        fullName = doc.getString("fullName") ?: "",
                        city = doc.getString("city") ?: "",
                        bio = doc.getString("bio") ?: ""
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(message = "خطأ في تحميل البيانات: ${e.localizedMessage}")
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    // 2. حفظ وتحديث البروفايل في Firestore (مجموعة users)
    fun updateProfile(phone: String, name: String, city: String, bio: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val userId = getUserId()
                val userMap = mapOf(
                    "phoneNumber" to phone,
                    "fullName" to name,
                    "city" to city,
                    "bio" to bio,
                    "updatedAt" to System.currentTimeMillis()
                )
                db.collection("users").document(userId).set(userMap).await()
                _uiState.value = _uiState.value.copy(
                    phoneNumber = phone,
                    fullName = name,
                    city = city,
                    bio = bio,
                    message = "تم حفظ البيانات بنجاح في قاعدة البيانات! 💾"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(message = "فشل الحفظ: ${e.localizedMessage}")
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    // 3. البحث في مجموعة users
    fun searchDatabase(query: String, onResult: (List<Map<String, Any>>) -> Unit) {
        viewModelScope.launch {
            try {
                val snapshot = if (query.isBlank()) {
                    db.collection("users").get().await()
                } else {
                    db.collection("users")
                        .whereGreaterThanOrEqualTo("fullName", query)
                        .get().await()
                }
                val documents = snapshot.documents.mapNotNull { it.data }
                onResult(documents)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(message = "فشل البحث: ${e.localizedMessage}")
            }
        }
    }

    // 4. حظر / إزالة حظر رقم باستخدام مجموعة blocked_users المباشرة
    fun toggleBlockNumber(targetPhone: String) {
        viewModelScope.launch {
            try {
                val userId = getUserId()
                val blockDocRef = db.collection("blocked_users").document(targetPhone)
                val doc = blockDocRef.get().await()
                if (doc.exists()) {
                    blockDocRef.delete().await()
                    _uiState.value = _uiState.value.copy(message = "تم إزالة الحظر عن الرقم من قائمة blocked_users 🟢")
                } else {
                    val blockData = mapOf(
                        "phoneNumber" to targetPhone,
                        "blockedBy" to userId,
                        "blockedAt" to System.currentTimeMillis()
                    )
                    blockDocRef.set(blockData).await()
                    _uiState.value = _uiState.value.copy(message = "تم حظر الرقم بنجاح في قائمة blocked_users 🚫")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(message = "خطأ أثناء عملية الحظر: ${e.localizedMessage}")
            }
        }
    }

    // 5. حذف الحساب نهائياً من Firebase
    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val userId = getUserId()
                db.collection("users").document(userId).delete().await()
                val user = auth.currentUser
                user?.delete()?.await()
                _uiState.value = ProfileUiState(message = "تم حذف الحساب بنجاح 🗑️")
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(message = "فشل حذف الحساب: ${e.localizedMessage}")
            }
        }
    }
}
