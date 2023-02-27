package com.food.delivery.fragments;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.food.delivery.R;
import com.food.delivery.adapter.ProductRVAdapter;
import com.food.delivery.db.ProductModal;
import com.food.delivery.interfaces.OnAddProductListener;
import com.food.delivery.viewmodal.ViewModal;

import java.util.List;

/**
 * A HomeFragment {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnAddProductListener {
    // creating a variables for our recycler view.
    private RecyclerView productsRV;
    private static final int ADD_PRODUCT_REQUEST = 1;
    private static final int EDIT_PRODUCT_REQUEST = 2;
    private ViewModal viewmodal;
    private AndroidViewModelFactory viewModelFactory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productsRV = view.findViewById(R.id.recyclerView);
        productsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        productsRV.setHasFixedSize(true);

        final ProductRVAdapter adapter = new ProductRVAdapter();

        productsRV.setAdapter(adapter);

        viewModelFactory = new AndroidViewModelFactory(requireActivity().getApplication());

        viewmodal = new ViewModelProvider(requireActivity(), viewModelFactory).get(ViewModal.class);

        // below line is use to get all the Products from view modal.
        viewmodal.getAllProducts().observe(this, new Observer<List<ProductModal>>() {
            @Override
            public void onChanged(List<ProductModal> models) {
                // when the data is changed in our models we are
                // adding that list to our adapter class.
                adapter.submitList(models);
            }
        });

        // below method is use to add swipe to delete method for item of recycler view.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // on recycler view item swiped then we are deleting the item of our recycler view.
                viewmodal.delete(adapter.getProductAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getContext(), "Product deleted", Toast.LENGTH_SHORT).show();
            }
        }).
                // below line is use to attach this to recycler view.
                        attachToRecyclerView(productsRV);
        // below line is use to set item click listener for our item of recycler view.
        adapter.setOnItemClickListener(new ProductRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ProductModal model) {
                // after clicking on item of recycler view
                // we are opening a new activity and passing
                // a data to our activity.
                Intent intent = new Intent(getContext(), NotificationFragment.class);
                intent.putExtra(NotificationFragment.EXTRA_ID, model.getId());
                intent.putExtra(NotificationFragment.EXTRA_PRODUCT_NAME, model.getProductName());
                intent.putExtra(NotificationFragment.EXTRA_DESCRIPTION, model.getProductDescription());
                intent.putExtra(NotificationFragment.EXTRA_PRICE, model.getProductPrice());

                // below line is to start a new activity and
                // adding a edit Product constant.
                startActivityForResult(intent, EDIT_PRODUCT_REQUEST);
            }
        });
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == ADD_PRODUCT_REQUEST && resultCode == RESULT_OK) {
//            String productName = data.getStringExtra(NotificationFragment.EXTRA_PRODUCT_NAME);
//            String productDescription = data.getStringExtra(NotificationFragment.EXTRA_DESCRIPTION);
//            float productPrice = Float.parseFloat(data.getStringExtra(NotificationFragment.EXTRA_PRICE));
//            ProductModal model = new ProductModal(productName, productDescription, productPrice);
//            viewmodal.insert(model);
//            Toast.makeText(getContext(), "Product saved", Toast.LENGTH_SHORT).show();
//        } else if (requestCode == EDIT_PRODUCT_REQUEST && resultCode == RESULT_OK) {
//            int id = data.getIntExtra(NotificationFragment.EXTRA_ID, -1);
//            if (id == -1) {
//                Toast.makeText(getContext(), "Product can't be updated", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            String productName = data.getStringExtra(NotificationFragment.EXTRA_PRODUCT_NAME);
//            String productDescription = data.getStringExtra(NotificationFragment.EXTRA_DESCRIPTION);
//            float productPrice = Float.parseFloat(data.getStringExtra(NotificationFragment.EXTRA_PRICE));
//            ProductModal model = new ProductModal(productName, productDescription, productPrice);
//            model.setId(id);
//            viewmodal.update(model);
//            Toast.makeText(getContext(), "Product updated", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getContext(), "Product not saved", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public void onClick(String productName, String productDesc, float productPrice) {
        ProductModal model = new ProductModal(productName, productDesc, productPrice);
        viewmodal.insert(model);
        Toast.makeText(getContext(), "Product saved", Toast.LENGTH_SHORT).show();
    }
}
