//package com.navigation.example.db.encryption
//
//import android.os.Build
//import android.util.Base64
//import java.security.AlgorithmParameters
//import java.security.SecureRandom
//import java.security.spec.KeySpec
//import javax.crypto.Cipher
//import javax.crypto.SecretKey
//import javax.crypto.SecretKeyFactory
//import javax.crypto.spec.PBEKeySpec
//import javax.crypto.spec.SecretKeySpec
//
//class EncryptAndStoreKey {
//    fun persistRawKey(userPasscode: CharArray) {
//        val storable = toStorable(rawByteKey, userPasscode)
//        // Implementation explained in next step
//        saveToPrefs(storable)
//    }
//
//    /**
//     * Returns a [Storable] instance with the db key encrypted using PBE.
//     *
//     * @param rawDbKey the raw database key
//     * @param userPasscode the user's passcode
//     * @return storable instance
//     */
//    fun toStorable(rawDbKey: ByteArray, userPasscode: CharArray): Storable {
//        // Generate a random 8 byte salt
//        val salt = ByteArray(8).apply {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                SecureRandom.getInstanceStrong().nextBytes(this)
//            } else {
//                SecureRandom().nextBytes(this)
//            }
//        }
//        val secret: SecretKey = generateSecretKey(userPasscode, salt)
//
//        // Now encrypt the database key with PBE
//        val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
//        cipher.init(Cipher.ENCRYPT_MODE, secret)
//        val params: AlgorithmParameters = cipher.parameters
//        val iv: ByteArray = params.getParameterSpec(IvParameterSpec::class.java).iv
//        val ciphertext: ByteArray = cipher.doFinal(key)
//
//        // Return the IV and CipherText which can be stored to disk
//        return Storable(
//            Base64.encodeToString(iv, Base64.DEFAULT),
//            Base64.encodeToString(ciphertext, Base64.DEFAULT),
//            Base64.encodeToString(salt, Base64.DEFAULT)
//        )
//    }
//
//    private fun generateSecretKey(passcode: CharArray, salt: ByteArray): SecretKey {
//        // Initialize PBE with password
//        val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
//        val spec: KeySpec = PBEKeySpec(passcode, salt, 65536, 256)
//        val tmp: SecretKey = factory.generateSecret(spec)
//        return SecretKeySpec(tmp.encoded, "AES")
//    }
//}