package com.example.todo_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;

    private ArrayList<TodoItem> items;
    private CustomArrayAdapter itemsAdapter;
    private ListView listView;
    private Button buttonAddItem;
    private ImageView capturedImageView;
    private String currentPhotoPath;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        buttonAddItem = findViewById(R.id.buttonAddItem);
        capturedImageView = findViewById(R.id.capturedImageView);

        items = new ArrayList<>();
        itemsAdapter = new CustomArrayAdapter(this, R.layout.custom_list_item, items);
        listView.setAdapter(itemsAdapter);

        setUpListViewListener();

        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
    }

    private void setUpListViewListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Check for camera permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted, open the camera
                    dispatchTakePictureIntent(position);
                } else {
                    // Request camera permission
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }
            }
        });
    }

    private void dispatchTakePictureIntent(int position) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    currentPhotoPath = photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = getExternalFilesDir("images");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // The photo was taken successfully
            // You can handle the captured image here
            TodoItem todoItem = items.get(items.size() - 1);
            todoItem.setImagePath(currentPhotoPath);
            itemsAdapter.notifyDataSetChanged();
        }
    }

    private void addItem() {
        EditText input = findViewById(R.id.editItemName);
        String itemText = input.getText().toString();

        if (!itemText.isEmpty()) {
            TodoItem newItem = new TodoItem(itemText, "");
            itemsAdapter.add(newItem);
            input.setText("");
        } else {
            Toast.makeText(getApplicationContext(), "Item has no name", Toast.LENGTH_LONG).show();
        }
    }

    // Storing and managing items in a text file

}
