package com.kiosk.example.db.encryption

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

abstract class EncryptDatabase: RoomDatabase() {
    companion object {

        @RequiresApi(Build.VERSION_CODES.O)
        public fun buildDatabase(
            passcode: CharArray,
            context: Context
        ): EncryptDatabase {
            // DatabaseKeyMgr is a singleton that all of the above code is wrapped into.
            // Ideally this should be injected through DI but to simplify the sample code
            // we'll retrieve it as follows
            val dbKey =  Encryption.getCharKey(passcode, context)
            val supportFactory = SupportFactory(SQLiteDatabase.getBytes(dbKey))
            return Room.databaseBuilder(context, EncryptDatabase::class.java,
                "encrypted-db").openHelperFactory(supportFactory).build()
        }
    }
}