package com.food.delivery.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.food.delivery.R;

/**
 * ProductFragment  {@link Fragment} subclass.
 */
public class ProductFragment extends Fragment {

  public ProductFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_product, container, false);
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    TextView tvInfo = view.findViewById(R.id.tv_product_info);

    ProductFragmentArgs args = ProductFragmentArgs.fromBundle(getArguments());
    //String name = args.getProductName();
    //Float amount = args.getAmount();
    //Integer itemCount = args.getItemCount();

    tvInfo.setText(getString(R.string.name_lable)
        + args.getProductName()
        + "\n" +
        " Count "
        + args.getItemCount()
        + "\n" +
        " Amount "
        + args.getAmount());
  }
}
