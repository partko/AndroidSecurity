package com.example.inventory.data

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys

object KeyManager {
    val DB_KEY_ALIAS = "dbKeyAlias"

    val dbKeySpec = KeyGenParameterSpec.Builder(
        DB_KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    ).apply {
        setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        setKeySize(256)
        setUserAuthenticationRequired(false)
    }.build()

    val dbKey: String
        get() = MasterKeys.getOrCreate(dbKeySpec)
}