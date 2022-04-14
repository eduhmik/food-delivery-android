package com.kiosk.example.db;

import android.content.Context;
import android.os.Build;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "product-database";
    private static String DB_PATH = "";
    private SQLiteDatabase mDatabase;
    private Context context;
    private final static String password = "";

    private static DBHelper instance;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        if (Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir+"/databases/";
        else
            DB_PATH = "/data/data"+context.getPackageName()+"/databases/";
        this.context = context;
    }

    static public synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance =  new DBHelper(context);
        }
        return instance;
    }

    public void createDatabase() throws IOException {
        boolean isExistingDB = checkExistingDB();
        if (!isExistingDB) {
            this.getReadableDatabase(password);
            this.close();
            try {
                copyDatabase();
            } catch (Exception e) {
                
            }
        }
    }

    private void copyDatabase() {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(DB_NAME);
            String outputFileName = new StringBuilder(DB_PATH).append(DB_NAME).toString();
            OutputStream outputStream = new FileOutputStream(outputFileName);
            byte[] mBuffer = new byte[1024];
            int length;
            while ((length = inputStream.read(mBuffer)) > 0) {
                outputStream.write(mBuffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkExistingDB() {
        File dbFile = new File(new StringBuilder(DB_PATH).append(DB_NAME).toString());
        return dbFile.exists();
    }

    public boolean openDatabase() throws SQLiteException {
        String path = new StringBuilder(DB_PATH).append(DB_NAME).toString();
        mDatabase = SQLiteDatabase.openDatabase(path, password, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDatabase != null;
    }

    @Override
    public synchronized void close() {
        if (mDatabase != null)
           mDatabase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
