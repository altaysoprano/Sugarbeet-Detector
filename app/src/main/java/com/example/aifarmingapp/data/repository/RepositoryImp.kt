package com.example.aifarmingapp.data.repository

import android.util.Log
import com.example.aifarmingapp.data.model.User
import com.example.aifarmingapp.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RepositoryImp(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseFirestore
) : Repository {

    override fun signIn(email: String, pass: String, result: (UiState<String>) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                result.invoke(
                    UiState.Success(it.result.toString())
                )
            } else {
                result.invoke(
                    UiState.Failure("Kullanıcı yok veya şifre hatalı")
                )
            }
        }
    }

    override fun signUp(email: String, pass: String, result: (UiState<String>) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                val user = firebaseAuth.currentUser
                if (user != null) {
                    val uid = user.uid
                    database.collection("user").document(uid)
                        .set(User(email, pass))
                        .addOnSuccessListener { result.invoke(UiState.Success(uid)) }
                        .addOnFailureListener { exception ->
                            result.invoke(UiState.Failure(exception.localizedMessage))
                        }
                } else {
                    result.invoke(UiState.Failure("Kullanıcı bilgileri alınamadı"))
                }
            } else {
                result.invoke(UiState.Failure("Kullanıcı zaten var veya farklı bir hata oluştu"))
            }
        }
    }

    override fun updateSugarBeetCount(count: Int, result: (UiState<String>) -> Unit) {
        val uid = firebaseAuth.currentUser?.uid
        val userRef = uid?.let {
            Log.d("Mesaj: ", uid.toString())
            database.collection("user").document(it)
        }

        userRef?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val currentCount = documentSnapshot.getLong("sugarBeetCount") ?: 0

                    val updatedCount = currentCount + count

                    userRef.update("sugarBeetCount", updatedCount)
                        .addOnSuccessListener {
                            result.invoke(UiState.Success("Sugarbeet count updated"))
                            Log.d("Mesaj: ", "updated")
                            Log.d("Mesaj: ", "numberofsugarbeets: $count")
                        }
                        .addOnFailureListener { exception ->
                            Log.d("Mesaj: ", exception.localizedMessage.toString())
                            result.invoke(UiState.Failure(exception.localizedMessage))
                        }
                } else {
                    Log.d("Mesaj: ", "user not found")
                    result.invoke(UiState.Failure("User not found"))
                }
            }
            ?.addOnFailureListener { exception ->
                Log.d("Mesaj: ", exception.localizedMessage.toString())
                result.invoke(UiState.Failure(exception.localizedMessage))
            }
    }

    override fun getSugarBeetCount(result: (UiState<Int>) -> Unit) {
        val uid = firebaseAuth.currentUser?.uid
        val userRef = uid?.let {
            database.collection("user").document(it)
        }

        userRef?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val sugarBeetCount = documentSnapshot.getLong("sugarBeetCount")?.toInt() ?: 0
                    result.invoke(UiState.Success(sugarBeetCount))
                } else {
                    result.invoke(UiState.Failure("User not found"))
                }
            }
            ?.addOnFailureListener { exception ->
                result.invoke(UiState.Failure(exception.localizedMessage))
            }
    }
}