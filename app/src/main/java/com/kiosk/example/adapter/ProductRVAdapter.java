package com.kiosk.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kiosk.example.R;
import com.kiosk.example.db.ProductModal;

import java.util.Objects;

public class ProductRVAdapter extends ListAdapter<ProductModal, ProductRVAdapter.ViewHolder> {

    // creating a variable for on item click listener.
    private OnItemClickListener listener;

    // creating a constructor class for our adapter class.
    public ProductRVAdapter() {
        super(DIFF_CALLBACK);
    }

    // creating a call back for item of recycler view.
    private static final DiffUtil.ItemCallback<ProductModal> DIFF_CALLBACK = new DiffUtil.ItemCallback<ProductModal>() {
        @Override
        public boolean areItemsTheSame(ProductModal oldItem, ProductModal newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(ProductModal oldItem, ProductModal newItem) {
            // below line is to check the course name, description and course duration.
            return oldItem.getProductName().equals(newItem.getProductName()) &&
                    oldItem.getProductDescription().equals(newItem.getProductDescription()) &&
                    Objects.equals(oldItem.getProductPrice(), newItem.getProductPrice());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // below line is use to inflate our layout
        // file for each item of our recycler view.
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // below line of code is use to set data to
        // each item of our recycler view.
        ProductModal model = getProductAt(position);
        holder.productNameTV.setText(model.getProductName());
        holder.productDescTV.setText(model.getProductDescription());
        float price = (float) model.getProductPrice();
        String productPrice = Float.toString(price);
        holder.productPriceTV.setText(productPrice);
    }

    // creating a method to get course modal for a specific position.
    public ProductModal getProductAt(int position) {
        return getItem(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // view holder class to create a variable for each view.
        TextView productNameTV, productDescTV, productPriceTV;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing each view of our recycler view.
            productNameTV = itemView.findViewById(R.id.idTVProductName);
            productDescTV = itemView.findViewById(R.id.idTVProductDescription);
            productPriceTV = itemView.findViewById(R.id.idProductPrice);

            // adding on click listener for each item of recycler view.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // inside on click listener we are passing
                    // position to our item of recycler view.
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ProductModal model);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
