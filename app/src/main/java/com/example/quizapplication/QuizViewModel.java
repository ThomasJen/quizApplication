package com.example.quizapplication;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizViewModel extends AndroidViewModel {
    private final PhotoDAO photoDao; // Henter DAO for å kunne bruke databaseoperasjoner
    private final MutableLiveData<List<PhotoEntity>> photoList = new MutableLiveData<>();   // LiveData  for å observere endringer i listen
    private final ExecutorService executorService;  // ExecutorService for å kjøre databaseoperasjoner i bakgrunnen
    public int currentIndex = 0;    // Indeks for current question
    public int score = 0;           // Score for current round

    public QuizViewModel(Application application) {

        // Henter databasen via Appdatabase og setter opp photoDao og executorService
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        photoDao = db.photoDAO();
        executorService = Executors.newSingleThreadExecutor();

        // Setter inn standardbilder dersom databasen er tom
        initializeDefaultAnimals();

        // Henter alle bilder fra databasen og oppdaterer photoList
        photoDao.getAllPhotos().observeForever(photos -> {
            if(photos != null) {
                photoList.postValue(photos);
            }
            });
        }

    // returnerer LiveData-lista med bilder
    public LiveData<List<PhotoEntity>> getPhotoList() {
        return photoList;
    }

    public void initializeDefaultAnimals () {
        // Legg til standarddyr
        executorService.execute(() -> {
            List<PhotoEntity> existingPhotos = photoDao.getAllPhotosSync();

            if (existingPhotos == null || existingPhotos.isEmpty()) {
                photoDao.insert(new PhotoEntity("Tiger", null, Uri.parse("android.resource://com.example.quizapplication/drawable/tiger").toString()));
                photoDao.insert(new PhotoEntity("Rev", null, Uri.parse("android.resource://com.example.quizapplication/drawable/rev").toString()));
                photoDao.insert(new PhotoEntity("Gorilla", null, Uri.parse("android.resource://com.example.quizapplication/drawable/gorilla").toString()));
                photoDao.insert(new PhotoEntity("Sjiraff", null, Uri.parse("android.resource://com.example.quizapplication/drawable/sjiraff").toString()));
            }
        });
    }

    // Metode for å legge til nye bilder i databasen
    public void addPhoto(String name, Integer imageResID, String imagePath) {
        new Thread(() -> {

            photoDao.insert(new PhotoEntity(name, imageResID, imagePath));

            List<PhotoEntity> updatedList = photoDao.getAllPhotosSync();

            photoList.postValue(updatedList);
        }).start();

    }
    public void deletePhoto(PhotoEntity photo) {
        new Thread(() -> {
            photoDao.delete(photo); // Sletter bildet fra Room-databasen
        }).start();
    }

    // Sortere listen i alfabetisk rekkefølge
    public void sortPhotosAZ() {

        // Henter listen fra LiveData og lager en ny liste med sorterte bilder
        new Thread(() -> {
            List<PhotoEntity> currentList = photoList.getValue();
            if (currentList != null) {
                // Lag en kopi av listen for å sikre at vi har en ny instans
                List<PhotoEntity> newList = new ArrayList<>(currentList);
                Collections.sort(newList, Comparator.comparing(PhotoEntity::getName));
                photoList.postValue(newList);
            }
        }).start();
    }

    public void sortPhotosZA() {

        // Henter listen fra LiveData og lager en ny liste med sorterte bilder
        new Thread(() -> {
            List<PhotoEntity> currentList = photoList.getValue();
            if (currentList != null) {
                List<PhotoEntity> newList = new ArrayList<>(currentList);
                Collections.sort(newList, (p1, p2) -> p2.getName().compareToIgnoreCase(p1.getName()));
                photoList.postValue(newList);
            }
        }).start();
    }

    // Metode for testingen av antall bilder i databasen
    public LiveData<Integer> getPhotoCount() {
        return Transformations.map(photoList, photoEntities ->
                photoEntities != null ? photoEntities.size() : 0
        );
    }
}
