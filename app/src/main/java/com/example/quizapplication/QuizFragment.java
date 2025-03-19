package com.example.quizapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.os.Handler;

public class QuizFragment extends Fragment {

    private List<PhotoEntity> photoList;
    private QuizViewModel quizViewModel;
    private int score = 0;
    private TextView scoreText;
    private ImageView imageView;
    private Button button1, button2, button3, buttonNext, btnBackToMain;


    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        QuizViewModel quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find Views
        scoreText = view.findViewById(R.id.scoreText);
        imageView = view.findViewById(R.id.imageview);
        button1 = view.findViewById(R.id.button1);
        button2 = view.findViewById(R.id.button2);
        button3 = view.findViewById(R.id.button3);
        buttonNext = view.findViewById(R.id.buttonNext);
        btnBackToMain = view.findViewById(R.id.btnBackToMain);

        quizViewModel = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);


        quizViewModel.getPhotoList().observe(getViewLifecycleOwner(), photos -> {
            if (photos != null && !photos.isEmpty()) {
                photoList = photos;
                displayNextQuestion();
            } else {
                Toast.makeText(getContext(), "Ingen tilgjengelig spørsmål", Toast.LENGTH_SHORT).show();
            }
        });

        buttonNext.setOnClickListener(v -> {
                if(photoList != null && !photoList.isEmpty()) {
                   displayNextQuestion();
                }

        });
        btnBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
    }
    private void displayNextQuestion() {
        List<PhotoEntity> currentList = quizViewModel.getPhotoList().getValue();
        if (currentList == null || currentList.isEmpty()) {
            Toast.makeText(getContext(), "Ingen tilgjengelig spørsmål", Toast.LENGTH_SHORT).show();

            // Alle spørsmål er besvart – gå tilbake til hovedmenyen
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
            return;
        }

        if(quizViewModel.currentIndex >= currentList.size()) {
            quizViewModel.currentIndex = 0;
            Collections.shuffle(currentList);
        }
        // Hent neste spørsmål fra snapshot-listen
        PhotoEntity currentPhoto = photoList.get(quizViewModel.currentIndex);
        ((QuizActivity2) requireActivity()).setCurrentCorrectAnswer(currentPhoto.getName());
        quizViewModel.currentIndex++; // Øk indeksen for neste spørsmål

        // Vis bildet
        if (currentPhoto.getImagePath() != null && !currentPhoto.getImagePath().isEmpty()) {
            imageView.setImageURI(Uri.parse(currentPhoto.getImagePath()));
        } else {
            imageView.setImageResource(R.drawable.gorilla); // fallback om nødvendig
        }
        // Sett opp svaralternativer
        randomizeButtons(currentPhoto);
    }

    public void randomizeButtons(PhotoEntity correctPhoto) {
        List<PhotoEntity> currentList = quizViewModel.getPhotoList().getValue();
        List<String> alternativeNames = new ArrayList<>();

        if (currentList != null) {
            for (PhotoEntity photo : currentList) {
                alternativeNames.add(photo.getName());
            }
        }

        // Fjern det korrekte svaret for å unngå duplikat
        alternativeNames.remove(correctPhoto.getName());
        Collections.shuffle(alternativeNames);

        // Bygg en liste med tre alternativer: korrekt svar + to falske svar
        List<String> options = new ArrayList<>();
        options.add(correctPhoto.getName());
        if (alternativeNames.size() >= 2) {
            options.add(alternativeNames.get(0));
            options.add(alternativeNames.get(1));
        } else {
            // Bruk fallback-liste hvis nødvendig
            List<String> defaultFakes = new ArrayList<>(Arrays.asList("Rev", "Katt", "Ku", "Okse"));
            defaultFakes.remove(correctPhoto.getName());
            Collections.shuffle(defaultFakes);
            while (options.size() < 3 && !defaultFakes.isEmpty()) {
                options.add(defaultFakes.remove(0));
            }
        }
        Collections.shuffle(options);

        // Sett knappetekstene
        button1.setText(options.get(0));
        button2.setText(options.get(1));
        button3.setText(options.get(2));

        // Sett opp onClickListeners
        button1.setOnClickListener(v -> handleAnswer(options.get(0), correctPhoto.getName()));
        button2.setOnClickListener(v -> handleAnswer(options.get(1), correctPhoto.getName()));
        button3.setOnClickListener(v -> handleAnswer(options.get(2), correctPhoto.getName()));
    }

    private void handleAnswer(String selectedAnswer, String correctAnswer) {
            boolean isCorrect = selectedAnswer.equals(correctAnswer);
            if (isCorrect) {
                score++;
                scoreText.setText("Score: " + score);
                Toast.makeText(getContext(), "Correct!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Wrong!", Toast.LENGTH_SHORT).show();
            }

        new Handler(Looper.getMainLooper()).postDelayed(this::displayNextQuestion, 800);

    }
}
