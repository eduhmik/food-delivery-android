package com.kiosk.example;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class ProductActivity extends AppCompatActivity {
    private TextInputEditText productNameEt, productDescEt, productPriceEt;
    private Button addProduct;

    // creating a constant string variable for our
    // product name, description and duration.
    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_PRODUCT_NAME = "EXTRA_PRODUCT_NAME";
    public static final String EXTRA_DESCRIPTION = "EXTRA_PRODUCT_DESCRIPTION";
    public static final String EXTRA_PRICE = "EXTRA_PRICE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        productNameEt = findViewById(R.id.product_name);
        productDescEt = findViewById(R.id.product_desc);
        productPriceEt = findViewById(R.id.product_price);
        addProduct = findViewById(R.id.add_product_btn);

        // below line is to get intent as we
        // are getting data via an intent.
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            // if we get id for our data then we are
            // setting values to our edit text fields.
            productNameEt.setText(intent.getStringExtra(EXTRA_PRODUCT_NAME));
            productDescEt.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            productPriceEt.setText(String.valueOf(intent.getFloatExtra(EXTRA_PRICE, 0.0F)));
        }
        // adding on click listener for our save button.
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getting text value from edittext and validating if
                // the text fields are empty or not.
                String productName = productNameEt.getText().toString();
                String productDesc = productDescEt.getText().toString();
                String productPrice = productPriceEt.getText().toString();
                float price = 0;
                if (!productPrice.isEmpty()) {
                    price = Float.parseFloat(productPrice);
                    if (productName.isEmpty() || productDesc.isEmpty() || TextUtils.isEmpty(productPriceEt.getText())) {
                        Toast.makeText(ProductActivity.this, "Please enter the valid product details.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // calling a method to save our product.
                saveProduct(productName, productDesc, price);
            }

            private void saveProduct(String productName, String productDescription, float productPrice) {
                // inside this method we are passing
                // all the data via an intent.
                Intent data = new Intent();

                // in below line we are passing all our course detail.
                data.putExtra(EXTRA_PRODUCT_NAME, productName);
                data.putExtra(EXTRA_DESCRIPTION, productDescription);
                data.putExtra(EXTRA_PRICE, productPrice);
                int id = getIntent().getIntExtra(EXTRA_ID, -1);
                if (id != -1) {
                    // in below line we are passing our id.
                    data.putExtra(EXTRA_ID, id);
                }

                // at last we are setting result as data.
                setResult(RESULT_OK, data);

                // displaying a toast message after adding the data
                Toast.makeText(ProductActivity.this, "Product has been saved to Room Database. ", Toast.LENGTH_SHORT).show();
//                startActivity(data);
                finish();
            }
        });
    }
}