package com.kiosk.example.db;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.kiosk.example.db.decryption.Encryption;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

//import com.navigation.example.db.encryption.EncryptDatabase;

@Database(entities = {ProductModal.class}, version = 1)
public abstract class ProductDatabase extends RoomDatabase {
    // below line is to create instance
    // for our database class.
    private static ProductDatabase instance;
//    private EncryptDatabase encryptDatabase;

    // below line is to create
    // abstract variable for dao.
    public abstract Dao Dao();

    // on below line we are getting instance for our database.
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static synchronized ProductDatabase getInstance(Context context) {
        // below line is to check if
        // the instance is null or not.
        if (instance == null) {
            // if the instance is null we
            // are creating a new instance
            String passcode = "password";
            final char[] password = passcode.toCharArray();
            char[] dbKey = Encryption.Companion.getCharKey(password, context);
            SupportFactory supportFactory = new SupportFactory(SQLiteDatabase.getBytes(dbKey));
            instance =
                    // for creating a instance for our database
                    // we are creating a database builder and passing
                    // our database class with our database name.
                    Room.databaseBuilder(context.getApplicationContext(),
                            ProductDatabase.class, "product_database")
                            // below line is use to add fall back to
                            // destructive migration to our database.
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            // below line is to add callback
                            // to our database.
                            .addCallback(roomCallback)
                            .openHelperFactory(supportFactory)
                            // below line is to
                            // build our database.
                            .build();


        }
        // after creating an instance
        // we are returning our instance
        return instance;
    }

    // below line is to create a callback for our room database.
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // this method is called when database is created
            // and below line is to populate our data.
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    // we are creating an async task class to perform task in background.
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        PopulateDbAsyncTask(ProductDatabase instance) {
            Dao dao = instance.Dao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
