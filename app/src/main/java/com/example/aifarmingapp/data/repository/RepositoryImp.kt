package com.example.aifarmingapp.data.repository

import com.example.aifarmingapp.data.model.User
import com.example.aifarmingapp.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RepositoryImp(
    val firebaseAuth: FirebaseAuth,
    val database: FirebaseFirestore
    ): Repository {

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
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                database.collection("user").add(User(email, pass))
                    .addOnSuccessListener { result.invoke(UiState.Success(it.id)) }
                    .addOnFailureListener { result.invoke((UiState.Failure(it.localizedMessage))) }
            } else {
                result.invoke(UiState.Failure("Kullanıcı zaten var veya farklı bir hata oluştu"))
            }
        }
    }
}