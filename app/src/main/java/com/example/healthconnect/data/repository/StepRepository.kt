package com.example.healthconnect.data.repository

import com.example.healthconnect.data.model.StepData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class StepRepository {

    private val firestore = Firebase.firestore
    private val stepCollection = firestore.collection("steps")

    fun saveStep(step: StepData) = stepCollection.add(step)
}