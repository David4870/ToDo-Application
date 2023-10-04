package com.example.todo_application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class CustomArrayAdapter extends ArrayAdapter<TodoItem> {

    public CustomArrayAdapter(Context context, int resource, ArrayList<TodoItem> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item, parent, false);
        }

        TodoItem item = getItem(position);

        TextView itemNameTextView = convertView.findViewById(R.id.itemNameTextView);
        ImageView capturedImageView = convertView.findViewById(R.id.capturedImageView);

        if (item != null) {
            itemNameTextView.setText(item.getItemName());

            // Check if there is an image path associated with the item
            String imagePath = item.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                capturedImageView.setVisibility(View.VISIBLE);

                // Load and set the image to the ImageView
                Bitmap bitmap = decodeSampledBitmapFromFile(imagePath, 48, 48); // Specify desired image dimensions
                capturedImageView.setImageBitmap(bitmap);
            } else {
                capturedImageView.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
