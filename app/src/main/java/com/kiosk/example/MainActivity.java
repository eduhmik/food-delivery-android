package com.kiosk.example;

import static com.kiosk.example.ProductActivity.EXTRA_DESCRIPTION;
import static com.kiosk.example.ProductActivity.EXTRA_ID;
import static com.kiosk.example.ProductActivity.EXTRA_PRICE;
import static com.kiosk.example.ProductActivity.EXTRA_PRODUCT_NAME;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kiosk.example.adapter.ProductRVAdapter;
import com.kiosk.example.db.ProductModal;
import com.kiosk.example.viewmodal.ViewModal;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // creating a variables for our recycler view.
    private RecyclerView productsRV;
    private static final int ADD_PRODUCT_REQUEST = 1;
    private static final int EDIT_PRODUCT_REQUEST = 2;
    private ViewModal viewmodal;
    private ViewModelProvider.AndroidViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productsRV = findViewById(R.id.recyclerView);
        FloatingActionButton fab = findViewById(R.id.idFABAdd);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // starting a new activity for adding a new course
                // and passing a constant value in it.
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                startActivityForResult(intent, ADD_PRODUCT_REQUEST);
            }
        });

        productsRV.setLayoutManager(new LinearLayoutManager(this));
        productsRV.setHasFixedSize(true);

        final ProductRVAdapter adapter = new ProductRVAdapter();

        productsRV.setAdapter(adapter);

        viewModelFactory = new ViewModelProvider.AndroidViewModelFactory((Application) getApplicationContext());

        viewmodal = new ViewModelProvider(this, viewModelFactory).get(ViewModal.class);

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
                Toast.makeText(MainActivity.this, "Product deleted", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                intent.putExtra(EXTRA_ID, model.getId());
                intent.putExtra(EXTRA_PRODUCT_NAME, model.getProductName());
                intent.putExtra(EXTRA_DESCRIPTION, model.getProductDescription());
                intent.putExtra(EXTRA_PRICE, model.getProductPrice());

                // below line is to start a new activity and
                // adding a edit Product constant.
                startActivityForResult(intent, EDIT_PRODUCT_REQUEST);
            }
        });

        String dbname = "product_database.db";
        File dbpath = getDatabasePath(dbname);

//        launchShareFileIntent(Uri.fromFile(dbpath));
    }

    private void launchShareFileIntent(Uri uri) {
        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setType("*/*")
                .setStream(uri)
                .setChooserTitle("Select application to share file")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_PRODUCT_REQUEST && resultCode == RESULT_OK) {
            String productName = data.getStringExtra(EXTRA_PRODUCT_NAME);
            String productDescription = data.getStringExtra(EXTRA_DESCRIPTION);
            float productPrice = data.getFloatExtra(EXTRA_PRICE, 0.0F);
            ProductModal model = new ProductModal(productName, productDescription, productPrice);
            viewmodal.insert(model);
            Toast.makeText(MainActivity.this, "Product saved", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_PRODUCT_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(MainActivity.this, "Product can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String productName = data.getStringExtra(EXTRA_PRODUCT_NAME);
            String productDescription = data.getStringExtra(EXTRA_DESCRIPTION);
            float productPrice = data.getFloatExtra(EXTRA_PRICE, 0.0F);
            ProductModal model = new ProductModal(productName, productDescription, productPrice);
            model.setId(id);
            viewmodal.update(model);
            Toast.makeText(MainActivity.this, "Product updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Product not saved", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return true;
    }
}
