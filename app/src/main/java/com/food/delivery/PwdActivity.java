package com.food.delivery;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.food.delivery.db.DBHelper;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.IOException;

public class PwdActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnEnter;
    private boolean isAuthenticated = false;
    private TextInputEditText txtPassword;
    private String password;
    private char[] dbKey;
    private DBHelper dbHelper;
    private SQLiteDatabase database;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SQLiteDatabase.loadLibs(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd);

        btnEnter = findViewById(R.id.btnEnter);
        txtPassword = findViewById(R.id.password);

        password = txtPassword.getText().toString();

        btnEnter.setOnClickListener(this);

        dbHelper = new DBHelper(this);
        try {
            dbHelper.createDatabase();
            dbHelper.openDatabase();
            dbHelper.close();
            database = dbHelper.getReadableDatabase(password);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validate() {
        if (TextUtils.isEmpty(txtPassword.getText().toString())) {
            txtPassword.requestFocus();
            txtPassword.setError("Please enter password");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnEnter && validate()) {
            Intent intent = new Intent(PwdActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}