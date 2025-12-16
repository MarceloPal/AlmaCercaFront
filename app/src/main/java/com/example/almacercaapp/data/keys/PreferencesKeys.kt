package com.example.almacercaapp.data

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val USER_ID = stringPreferencesKey("user_id")
    val USER_ROLE = stringPreferencesKey("user_role")
}