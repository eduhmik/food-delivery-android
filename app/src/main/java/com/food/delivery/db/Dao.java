package com.food.delivery.db;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@androidx.room.Dao
public interface Dao {
    // below method is use to
    // add data to database.
    @Insert
    void insert(ProductModal model);

    // below method is use to update
    // the data in our database.
    @Update
    void update(ProductModal model);

    // below line is use to delete a
    // specific course in our database.
    @Delete
    void delete(ProductModal model);

    // on below line we are making query to
    // delete all courses from our database.
    @Query("DELETE FROM product_table")
    void deleteAllProducts();

    // below line is to read all the courses from our database.
    // in this we are ordering our courses in ascending order
    // with our course name.
    @Query("SELECT * FROM product_table ORDER BY productName ASC")
    LiveData<List<ProductModal>> getAllProducts();
}
