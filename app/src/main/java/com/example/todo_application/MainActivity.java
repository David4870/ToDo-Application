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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;
    private static final String TODO_FILE_NAME = "todo_items.txt";

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

        // Initialize the items list by loading items from a text file
        items = loadItemsFromFile();

        itemsAdapter = new CustomArrayAdapter(this, R.layout.custom_list_item, items);
        listView.setAdapter(itemsAdapter);

        setUpListViewListener();

        // Events
        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
    }

    private void setUpListViewListener() {

        // Events
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Check for camera permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent(position);
                } else {
                    // Request camera permission
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteItem(position);
                return true; // To consume the long click event
            }
        });
    }

    private void deleteItem(int position) {
        if (position >= 0 && position < items.size()) {
            TodoItem deletedItem = items.remove(position);
            itemsAdapter.notifyDataSetChanged();
            saveItemsToFile(); // Save the updated list to the file after deletion

            String deletedItemName = deletedItem.getItemName();
            Toast.makeText(this, "Item '" + deletedItemName + "' deleted", Toast.LENGTH_SHORT).show();
        }
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
            TodoItem todoItem = items.get(items.size() - 1);
            todoItem.setImagePath(currentPhotoPath);
            itemsAdapter.notifyDataSetChanged();
            // After updating the item with the image path, save the updated list to the file
            saveItemsToFile();
        }
    }

    private void addItem() {
        EditText input = findViewById(R.id.editItemName);
        String itemText = input.getText().toString();

        if (!itemText.isEmpty()) {
            TodoItem newItem = new TodoItem(itemText, "");
            itemsAdapter.add(newItem);
            input.setText("");
            // After adding an item, save the updated list to the file
            saveItemsToFile();
        } else {
            Toast.makeText(getApplicationContext(), "Item has no name", Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<TodoItem> loadItemsFromFile() {
        ArrayList<TodoItem> items = new ArrayList<>();
        File file = new File(getFilesDir(), TODO_FILE_NAME);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        String itemName = parts[0];
                        String imagePath = parts[1];
                        items.add(new TodoItem(itemName, imagePath));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return items;
    }

    private void saveItemsToFile() {
        File file = new File(getFilesDir(), TODO_FILE_NAME);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (TodoItem item : items) {
                String line = item.getItemName() + "," + item.getImagePath();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
