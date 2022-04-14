package com.kiosk.example.db.decryption

import android.content.Context
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.kiosk.example.db.encryption.Storable
import java.security.AlgorithmParameters
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class Encryption {
    companion object {
        private val HEX_CHARS = "0123456789ABCDEF".toCharArray()
        private lateinit var rawByteKey: ByteArray
        private var dbCharKey: CharArray = charArrayOf();

        @RequiresApi(Build.VERSION_CODES.O)
        fun generateRandomKey(): ByteArray = ByteArray(32).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SecureRandom.getInstanceStrong().nextBytes(this)
            } else {
                SecureRandom().nextBytes(this)
            }
        }

        fun ByteArray.toHex(): CharArray {
            val result = StringBuilder()
            forEach {
                val octet = it.toInt()
                val firstIndex = (octet and 0xf0).ushr(4)
                val secondIndex = octet and 0x0F
                result.append(HEX_CHARS[firstIndex])
                result.append((HEX_CHARS[secondIndex]))
            }
            return result.toString().toCharArray();
        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun createNewKey() {
            // This is the raw key that we'll be encrypting + storing
            rawByteKey = generateRandomKey()
            // This is the key that will be used by Room
            dbCharKey = rawByteKey.toHex()
        }

        fun persistRawKey(context: Context, userPasscode: CharArray) {
            val storable = toStorable(rawByteKey, userPasscode)
            // Implementation explained in next step
            saveToPrefs(context, storable)
        }

        /**
         * Returns a [Storable] instance with the db key encrypted using PBE.
         *
         * @param rawDbKey the raw database key
         * @param userPasscode the user's passcode
         * @return storable instance
         */
        fun toStorable(rawDbKey: ByteArray, userPasscode: CharArray): Storable {
            // Generate a random 8 byte salt
            val salt = ByteArray(8).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    SecureRandom.getInstanceStrong().nextBytes(this)
                } else {
                    SecureRandom().nextBytes(this)
                }
            }
            val secret: SecretKey = generateSecretKey(userPasscode, salt)

            // Now encrypt the database key with PBE
            val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secret)
            val params: AlgorithmParameters = cipher.parameters
            val iv: ByteArray = params.getParameterSpec(IvParameterSpec::class.java).iv
            val ciphertext: ByteArray = cipher.doFinal(rawDbKey)

            // Return the IV and CipherText which can be stored to disk
            return Storable(
                Base64.encodeToString(iv, Base64.DEFAULT),
                Base64.encodeToString(ciphertext, Base64.DEFAULT),
                Base64.encodeToString(salt, Base64.DEFAULT)
            )
        }

        private fun generateSecretKey(passcode: CharArray, salt: ByteArray): SecretKey {
            // Initialize PBE with password
            val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec: KeySpec = PBEKeySpec(passcode, salt, 65536, 256)
            val tmp: SecretKey = factory.generateSecret(spec)
            return SecretKeySpec(tmp.encoded, "AES")
        }

        fun saveToPrefs(context: Context, storable: Storable) {
            val serialized = Gson().toJson(storable)
            val prefs = context.getSharedPreferences(
                "database",
                Context.MODE_PRIVATE
            )
            prefs.edit().putString("key", serialized).apply()
        }

        // Decryption
        fun getStorable(context: Context): Storable? {
            val prefs = context.getSharedPreferences(
                "database",
                Context.MODE_PRIVATE
            )
            val serialized = prefs.getString("key", null)
            if (serialized.isNullOrBlank()) {
                return null
            }

            return try {
                Gson().fromJson(
                    serialized,
                    object : TypeToken<Storable>() {}.type
                )
            } catch (ex: JsonSyntaxException) {
                null
            }
        }

        fun getRawByteKey(passcode: CharArray, storable: Storable): ByteArray {
            val aesWrappedKey = Base64.decode(storable.key, Base64.DEFAULT)
            val iv = Base64.decode(storable.iv, Base64.DEFAULT)
            val salt = Base64.decode(storable.salt, Base64.DEFAULT)
            val secret: SecretKey = generateSecretKey(passcode, salt)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secret, IvParameterSpec(iv))
            return cipher.doFinal(aesWrappedKey)
        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun getCharKey(passcode: CharArray, context: Context): CharArray {
            if (dbCharKey.isEmpty()) {
                initKey(passcode, context)
            }
            return dbCharKey ?: error("Failed to decrypt database key")
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun initKey(passcode: CharArray, context: Context) {
            val storable = getStorable(context)
            if (storable == null) {
                createNewKey()
                persistRawKey(context, passcode)
            } else {
                rawByteKey = getRawByteKey(passcode, storable)
                dbCharKey = rawByteKey.toHex()
            }
        }
    }
}