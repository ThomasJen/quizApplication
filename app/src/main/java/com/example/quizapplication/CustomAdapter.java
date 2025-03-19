package com.example.quizapplication;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/*this class extends the RecyclerView.Adapter and defines how items are displayed and interacted with*/
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<PhotoEntity> photoList;
    private OnPhotoClickListener listener;

    public void updateData(List<PhotoEntity> photoEntities) {
        this.photoList = photoEntities;
        notifyDataSetChanged();
    }

    /**
     * defines a method that the activity will implement to handle image clicks
     * allows separation of UI handling and user interaction*/
    public interface OnPhotoClickListener {
        void onPhotoClick(PhotoEntity photo);
    }

    /*constructor. initializes the adapter with data and listene
     * added to allow dynamic data interaction handling*/
    public CustomAdapter(OnPhotoClickListener listener, List<PhotoEntity> photoList) {
        this.photoList = photoList;
        this.listener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    /**
     * android dev template method
     * holds reference to the views in each item layout*/
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            imageView = view.findViewById(R.id.animal_image);
            textView = view.findViewById(R.id.textView);
        }

        public void bind(PhotoEntity photo) {
            imageView.setImageResource(Integer.parseInt(photo.getImagePath()));
            textView.setText(photo.getName());
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        PhotoEntity photo = photoList.get(position);
       viewHolder.textView.setText(photo.getName());

        if (photo.getImagePath() != null) {
            Uri imageUri = Uri.parse(photo.getImagePath());
            // Load image from URI using Glide
            viewHolder.imageView.setImageURI(imageUri);
        }else {
            viewHolder.imageView.setImageResource(R.drawable.gorilla);
        }

        viewHolder.imageView.setOnClickListener(v -> {
            listener.onPhotoClick(photo);
        });
        //viewHolder.bind(animalist.get(position));
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }
}