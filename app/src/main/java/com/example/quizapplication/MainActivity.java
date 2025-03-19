package com.example.quizapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quizapplication.PhotoEntity;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomAdapter.OnPhotoClickListener {

    private QuizViewModel quizViewModel;
    private CustomAdapter customAdapter;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize ViewModel
        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        /** Button to start the quiz **/
        Button button = findViewById(R.id.btnStartQuiz);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuizActivity2.class);
            startActivity(intent);
        });

        /** Button to continue the quiz **/
        Button button2 = findViewById(R.id.btnContinueQuiz);
        button2.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("quiz_prefs", MODE_PRIVATE);
            int savedIndex = prefs.getInt("savedIndex", 0);
            int savedScore = prefs.getInt("savedScore", 0);

            Intent intent = new Intent(MainActivity.this, QuizActivity2.class);
            intent.putExtra("savedIndex", savedIndex);
            intent.putExtra("savedScore", savedScore);
            intent.putExtra("continue", true);
            startActivity(intent);
        });


        // RecyclerView setup
        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter without data, LiveData will populate it
        customAdapter = new CustomAdapter(this, Collections.emptyList());
        recyclerView.setAdapter(customAdapter);

        // Observe LiveData from ViewModel
        quizViewModel.getPhotoList().observe(this, photoEntities ->  {
                customAdapter.updateData(photoEntities); // Update RecyclerView when data changes
        });

        /* text input from user */
        EditText editText = findViewById(R.id.edit_text);
        Button buttonSubmit = findViewById(R.id.button_submit);

        /* image input from user */
        Button pickImage = findViewById(R.id.imageUpload);
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                            getContentResolver().takePersistableUriPermission(uri, takeFlags);
                            selectedImage = uri;
                        }
                    }
                }
        );

        pickImage.setOnClickListener(view -> imagePickerLauncher.launch("image/*"));

        buttonSubmit.setOnClickListener(v -> {
            String userInput = editText.getText().toString().trim();
            if (selectedImage != null && !userInput.isEmpty()) {
                String cap = userInput.substring(0, 1).toUpperCase() + userInput.substring(1);
                quizViewModel.addPhoto(cap, null, selectedImage.toString());
                Toast.makeText(this, "Image added!", Toast.LENGTH_SHORT).show();
            }
        });

        /* sort the list in RecyclerView */
        Button buttonSortAZ = findViewById(R.id.btnSortAZ);
        Button buttonSortZA = findViewById(R.id.btnSortZA);

        buttonSortAZ.setOnClickListener(v -> quizViewModel.sortPhotosAZ());
        buttonSortZA.setOnClickListener(v -> quizViewModel.sortPhotosZA());
    }

    /**
     * Handles click on image and deletes the image from the Room database
     */

    @Override
    public void onPhotoClick(PhotoEntity photo) {
        Toast.makeText(this, "Clicked: " + photo.getName(), Toast.LENGTH_SHORT).show();
        QuizViewModel quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        quizViewModel.deletePhoto(photo);
    }

    public void insertTestPhoto() {
        quizViewModel.addPhoto("Gorilla", null, "android.resource://com.example.quizapplication/drawable/gorilla");
    }
}