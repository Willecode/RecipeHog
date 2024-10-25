package com.portfolio.core.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.tasks.await

class DeleteStorageFileWorker(
    context: Context,
    private val params: WorkerParameters,
    private val storage: FirebaseStorage
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        if(runAttemptCount >= 5) {
            return Result.failure()
        }

        val filePath = params.inputData.getString(STORAGE_FILE_PATH) ?: return Result.failure()
        try {
            storage.reference.child(filePath).delete().await()
            return Result.success()
        } catch (e: StorageException) {
            return Result.failure()
        }
    }

    companion object {
        const val STORAGE_FILE_PATH = "STORAGE_FILE_PATH"
    }
}