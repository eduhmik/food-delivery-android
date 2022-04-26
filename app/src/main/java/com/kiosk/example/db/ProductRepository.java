package com.kiosk.example.db;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class ProductRepository {
    // below line is the create a variable 
    // for dao and list for all Products.
    private Dao dao;
    private LiveData<List<ProductModal>> allProducts;

    // creating a constructor for our variables
    // and passing the variables to it.
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ProductRepository(Application application) throws IllegalBlockSizeException, BadPaddingException {
        ProductDatabase database = ProductDatabase.getInstance(application);
        dao = database.Dao();
        allProducts = dao.getAllProducts();
    }

    // creating a method to insert the data to our database.
    public void insert(ProductModal model) {
        new InsertProductsAsyncTask(dao).execute(model);
    }

    // creating a method to update data in database.
    public void update(ProductModal model) {
        new UpdateProductsAsyncTask(dao).execute(model);
    }

    // creating a method to delete the data in our database.
    public void delete(ProductModal model) {
        new DeleteProductsAsyncTask(dao).execute(model);
    }

    // below is the method to delete all the Products.
    public void deleteAllProducts() {
        new DeleteAllProductsAsyncTask(dao).execute();
    }

    // below method is to read all the Products.
    public LiveData<List<ProductModal>> getAllProducts() {
        return allProducts;
    }

    // we are creating a async task method to insert new Products.
    private static class InsertProductsAsyncTask extends AsyncTask<ProductModal, Void, Void> {
        private Dao dao;

        private InsertProductsAsyncTask(Dao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(ProductModal... model) {
            // below line is use to insert our modal in dao.
            dao.insert(model[0]);
            return null;
        }
    }

    // we are creating a async task method to update our Products.
    private static class UpdateProductsAsyncTask extends AsyncTask<ProductModal, Void, Void> {
        private Dao dao;

        private UpdateProductsAsyncTask(Dao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(ProductModal... models) {
            // below line is use to update
            // our modal in dao.
            dao.update(models[0]);
            return null;
        }
    }

    // we are creating a async task method to delete Products.
    private static class DeleteProductsAsyncTask extends AsyncTask<ProductModal, Void, Void> {
        private Dao dao;

        private DeleteProductsAsyncTask(Dao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(ProductModal... models) {
            // below line is use to delete 
            // our Products modal in dao.
            dao.delete(models[0]);
            return null;
        }
    }

    // we are creating a async task method to delete all Products.
    private static class DeleteAllProductsAsyncTask extends AsyncTask<Void, Void, Void> {
        private Dao dao;
        private DeleteAllProductsAsyncTask(Dao dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            // on below line calling method
            // to delete all Products.
            dao.deleteAllProducts();
            return null;
        }
    }
}
