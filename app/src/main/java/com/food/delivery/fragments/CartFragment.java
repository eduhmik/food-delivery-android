package com.food.delivery.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.food.delivery.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_cart, container, false);
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Button buttonViewProduct = view.findViewById(R.id.buttonViewProduct);
    buttonViewProduct.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        CartFragmentDirections.NextAction nextAction =
            CartFragmentDirections.nextAction("This is a sample product");
        nextAction.setAmount(100.00f);
        nextAction.setItemCount(2);

        NavController navController = Navigation.findNavController(view);
        navController.navigate(nextAction);
      }
    });
  }
}
