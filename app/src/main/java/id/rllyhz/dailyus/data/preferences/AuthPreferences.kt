package id.rllyhz.dailyus.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val USER_TOKEN_KEY = stringPreferencesKey("token")
private val USER_FULL_NAME_KEY = stringPreferencesKey("full_name")
private val USER_EMAIL_KEY = stringPreferencesKey("email")
private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")

private const val PREFERENCES_NAME = "user_preference"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

@Singleton
class AuthPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val _dataStore = context.dataStore

    /*
     * User token
     *
     * @return LiveData<String>
     */
    val userToken = _dataStore.data.map { preferences ->
        preferences[USER_TOKEN_KEY] ?: ""
    }.distinctUntilChanged().asLiveData()

    /*
     * Save user token
     *
     * @param token String
     */
    suspend fun saveUserToken(token: String) {
        _dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = token
        }
    }

    /*
     * User full name
     *
     * @return LiveData<String>
     */
    val userFullName = _dataStore.data.map { preferences ->
        preferences[USER_FULL_NAME_KEY] ?: ""
    }.distinctUntilChanged().asLiveData()

    /*
     * Save user full name
     *
     * @param name String
     */
    suspend fun saveUserFullName(fullName: String) {
        _dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = fullName
        }
    }

    /*
     * User email
     *
     * @return LiveData<String>
     */
    val userEmail = _dataStore.data.map { preferences ->
        preferences[USER_EMAIL_KEY] ?: ""
    }.distinctUntilChanged().asLiveData()

    /*
     * Save user email
     *
     * @param email String
     */
    suspend fun saveUserEmail(email: String) {
        _dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = email
        }
    }

    /*
     * Is user already logged in
     *
     * @return Flow<Boolean>
     */
    val isLoggedIn = _dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_KEY] ?: false
    }.distinctUntilChanged().asLiveData()

    /*
     * Save login state
     *
     * @param isLoggedIn Boolean
     */
    suspend fun isLoggedIn(value: Boolean) {
        _dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = value
        }
    }

    /*
     * Remove all user preferences data
     */
    suspend fun clear() {
        _dataStore.edit { it.clear() }
    }
}