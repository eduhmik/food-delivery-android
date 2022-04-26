package com.kiosk.example.viewmodal;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kiosk.example.db.ProductModal;
import com.kiosk.example.db.ProductRepository;

import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class ViewModal extends AndroidViewModel {
    // creating a new variable for course repository.
    private ProductRepository repository;

    // below line is to create a variable for live 
    // data where all the products are present.
    private LiveData<List<ProductModal>> allProducts;

    // constructor for our view modal.
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ViewModal(@NonNull Application application) throws IllegalBlockSizeException, BadPaddingException {
        super(application);
        repository = new ProductRepository(application);
        allProducts = repository.getAllProducts();
    }

    // below method is use to insert the data to our repository.
    public void insert(ProductModal model) {
        repository.insert(model);
    }

    // below line is to update data in our repository.
    public void update(ProductModal model) {
        repository.update(model);
    }

    // below line is to delete the data in our repository.
    public void delete(ProductModal model) {
        repository.delete(model);
    }

    // below method is to delete all the products in our list.
    public void deleteAllProducts() {
        repository.deleteAllProducts();
    }

    // below method is to get all the products in our list.
    public LiveData<List<ProductModal>> getAllProducts() {
        return allProducts;
    }
}
