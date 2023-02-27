package com.food.delivery.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.food.delivery.R;
import com.food.delivery.interfaces.OnAddProductListener;

/**
 * A NotificationFragment {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
    private TextInputEditText productNameEt, productDescEt, productPriceEt;
    private Button addProduct;
    private NavController navController;
    private OnAddProductListener onAddProductListener;

    // creating a constant string variable for our
    // product name, description and duration.
    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_PRODUCT_NAME = "EXTRA_PRODUCT_NAME";
    public static final String EXTRA_DESCRIPTION = "EXTRA_PRODUCT_DESCRIPTION";
    public static final String EXTRA_PRICE = "EXTRA_PRODUCT_PRICE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_notification, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        productNameEt = view.findViewById(R.id.product_name);
        productDescEt = view.findViewById(R.id.product_desc);
        productPriceEt = view.findViewById(R.id.product_price);
        addProduct = view.findViewById(R.id.add_product_btn);

        // below line is to get intent as we
        // are getting data via an intent.
        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            // if we get id for our data then we are
            // setting values to our edit text fields.
            productNameEt.setText(intent.getStringExtra(EXTRA_PRODUCT_NAME));
            productDescEt.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            productPriceEt.setText(intent.getStringExtra(EXTRA_PRICE));
        }
        // adding on click listener for our save button.
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getting text value from edittext and validating if
                // the text fields are empty or not.
                String productName = productDescEt.getText().toString();
                String productDesc = productDescEt.getText().toString();
                String productPrice = productPriceEt.getText().toString();
                float price = 0;
                if (!productPrice.isEmpty()) {
                    price = Float.parseFloat(productPrice);
                    if (productName.isEmpty() || productDesc.isEmpty() || TextUtils.isEmpty(productPriceEt.getText())) {
                        Toast.makeText(getActivity(), "Please enter the valid product details.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // calling a method to save our product.
                saveProduct(productName, productDesc, price);
            }
        });
    }

    private void saveProduct(String productName, String productDescription, float productPrice) {
        // inside this method we are passing
        // all the data via an intent.
        Intent data = new Intent();

        // in below line we are passing all our course detail.
        data.putExtra(EXTRA_PRODUCT_NAME, productName);
        data.putExtra(EXTRA_DESCRIPTION, productDescription);
        data.putExtra(EXTRA_PRICE, productPrice);
        int id = getActivity().getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            // in below line we are passing our id.
            data.putExtra(EXTRA_ID, id);
        }

        onAddProductListener = (OnAddProductListener) getActivity();
        onAddProductListener.onClick(productName, productDescription, productPrice);
        // at last we are setting result as data.
        getActivity().setResult(RESULT_OK, data);

        // displaying a toast message after adding the data
        Toast.makeText(getActivity(), "Product has been saved to Room Database. ", Toast.LENGTH_SHORT).show();

        navController.navigate(NotificationFragmentDirections.actionNotificationDestinationToHomeDestination2());
    }

//    private void saveProduct(String productName, String productDescription, float productPrice) {
//        // in below line we are passing our id.
//        onAddProductListener = (OnAddProductListener) getTargetFragment();
//        if (onAddProductListener != null) {
//            onAddProductListener.onClick(productName, productDescription, productPrice);
//        }
//
//        // displaying a toast message after adding the data
//        Toast.makeText(getActivity(), "Product has been saved to Room Database. ", Toast.LENGTH_SHORT).show();
//
//        navController.navigate(NotificationFragmentDirections.actionNotificationDestinationToHomeDestination2());
//    }
}
