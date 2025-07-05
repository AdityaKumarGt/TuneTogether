package com.aditya.tune_together.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.aditya.tune_together.domain.model.sign_in.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("user_prefs") }
    )

    companion object {
        val UID = stringPreferencesKey("uid")
        val NAME = stringPreferencesKey("name")
        val EMAIL = stringPreferencesKey("email")
        val PROFILE_PHOTO = stringPreferencesKey("profile_photo")
        val FCM_TOKEN = stringPreferencesKey("fcm_token")

    }

    val userFlow: Flow<User?> = dataStore.data
        .map { prefs ->
            val uid = prefs[UID]
            val name = prefs[NAME]
            val email = prefs[EMAIL]
            val profilePhoto = prefs[PROFILE_PHOTO]
            val fcmToken = prefs[FCM_TOKEN]
            if (uid != null && name != null && email != null && fcmToken != null  && profilePhoto != null) {
                User(uid, name, email, profilePhoto, fcmToken)
            } else null
        }

    suspend fun saveUser(user: User) {
        dataStore.edit { prefs ->
            prefs[UID] = user.uid
            prefs[NAME] = user.name
            prefs[EMAIL] = user.email
            prefs[PROFILE_PHOTO] = user.profilePhoto
            prefs[FCM_TOKEN] = user.fcmToken

        }
    }

    suspend fun clearUser() {
        dataStore.edit { it.clear() }
    }
}
