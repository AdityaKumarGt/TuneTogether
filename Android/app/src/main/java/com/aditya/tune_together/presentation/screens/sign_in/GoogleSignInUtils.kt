package com.aditya.tune_together.presentation.screens.sign_in

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.aditya.tune_together.R
import com.aditya.tune_together.domain.model.sign_in.User
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class GoogleSignInUtils {

    companion object {
        suspend fun doGoogleSignIn(
            context: Context,
            launcher: ManagedActivityResultLauncher<Intent, ActivityResult>?
        ): User? {
            val credentialManager = CredentialManager.create(context)

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(getCredentialOptions(context))
                .build()

            return try {
                val result = credentialManager.getCredential(context, request)
                when (result.credential) {
                    is CustomCredential -> {
                        if (result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(result.credential.data)
                            val googleTokenId = googleIdTokenCredential.idToken
                            val authCredential =
                                GoogleAuthProvider.getCredential(googleTokenId, null)
                            val user =
                                Firebase.auth.signInWithCredential(authCredential).await().user
                            user?.let {
                                if (!it.isAnonymous) {
                                    return User(
                                        it.uid,
                                        it.displayName ?: "",
                                        it.email ?: "",
                                       it.photoUrl?.toString() ?: "",
                                        fcmToken = ""
                                    )
                                }
                            }
                        }
                    }
                }
                null
            } catch (e: NoCredentialException) {
                launcher?.launch(getIntent())
                null
            } catch (e: GetCredentialException) {
                e.printStackTrace()
                null
            }
        }


        private fun getIntent(): Intent {
            return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
            }
        }

        private fun getCredentialOptions(context: Context): CredentialOption {
            return GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .build()
        }
    }
}
