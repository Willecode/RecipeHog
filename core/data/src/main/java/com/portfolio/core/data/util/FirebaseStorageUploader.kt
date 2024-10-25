package com.portfolio.core.data.util

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.portfolio.core.data.work.DeleteStorageFileScheduler
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.io.File

class FirebaseStorageUploader(
    private val storage: FirebaseStorage,
    private val deleteStorageFileScheduler: DeleteStorageFileScheduler
) {
    private var storagePath: String? = null

    /**
     * @return URL to the uploaded file
     * @throws FirebaseFirestoreException if failure
     */
    suspend fun tryUploadImage(
        localImageFilePath: String,
        storageUploadPath: String
    ) = try {
        if (storagePath != null)
            throw FirebaseFirestoreException(
                "Uploader instance can only upload one image",
                com.google.firebase.firestore.FirebaseFirestoreException.Code.UNKNOWN
            )
        storagePath = storageUploadPath
        uploadImageFile(localImageFilePath)
    } catch (e: StorageException) {
        // throw an exception that the generic safecall can handle
        throw FirebaseFirestoreException(
            "Failed to upload image",
            com.google.firebase.firestore.FirebaseFirestoreException.Code.UNKNOWN
        )
    } catch (e: TimeoutCancellationException) {
        throw FirebaseFirestoreException(
            "Failed to upload image",
            com.google.firebase.firestore.FirebaseFirestoreException.Code.UNKNOWN
        )
    }

    private suspend fun uploadImageFile(localImageFilePath: String): String {
        val file = Uri.fromFile(File(localImageFilePath))
        val imageRef = storage.reference.child(storagePath!!)

        try {
            withTimeout(20_000L) {
                imageRef.putFile(file).await()
            }
        } catch (e: TimeoutCancellationException) {
            throw e
        }

        try {
            val imageUrlSnapshot = withTimeout(20_000L) {
                imageRef.downloadUrl.await()
            }
            val imgUrl = imageUrlSnapshot.toString()
            return imgUrl
        } catch (e: StorageException) {
            // failed to download URL, need to delete the file from the database since nothing can reference it
            scheduleUploadedFileDeletion()
            throw e
        } catch (e: TimeoutCancellationException) {
            scheduleUploadedFileDeletion()
            throw e
        }
    }

    /**
     * Schedules a deletion request for the uploaded file.
     */
    suspend fun scheduleUploadedFileDeletion() {
        storagePath?.let {
            deleteStorageFileScheduler.scheduleFileDeletion(it)
        }
    }
}