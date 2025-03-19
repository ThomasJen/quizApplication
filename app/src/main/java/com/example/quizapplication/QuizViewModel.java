package com.example.quizapplication;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizViewModel extends AndroidViewModel {
    private final PhotoDAO photoDao;
    private final MutableLiveData<List<PhotoEntity>> photoList = new MutableLiveData<>();
    private final ExecutorService executorService;

    public int currentIndex = 0;
    public int score = 0;

    private final MutableLiveData<List<PhotoEntity>> sortedPhotos = new MutableLiveData<>();

    public QuizViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        photoDao = db.photoDAO();
        executorService = Executors.newSingleThreadExecutor();

        initializeDefaultAnimals();

        photoDao.getAllPhotos().observeForever(photos -> {
            if(photos != null) {
                photoList.postValue(photos);
            }
            });
        }


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

    public void addPhoto(String name, Integer imageResID, String imagePath) {
        new Thread(() -> {

            photoDao.insert(new PhotoEntity(name, imageResID, imagePath));

            List<PhotoEntity> updatedList = photoDao.getAllPhotosSync();

            photoList.postValue(updatedList);
        }).start();

    }
    public void deletePhoto(PhotoEntity photo) {
        new Thread(() -> {
            photoDao.delete(photo); // Deletes the photo from Room database
        }).start();
    }

    public void sortPhotosAZ() {
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
        new Thread(() -> {
            List<PhotoEntity> currentList = photoList.getValue();
            if (currentList != null) {
                List<PhotoEntity> newList = new ArrayList<>(currentList);
                Collections.sort(newList, (p1, p2) -> p2.getName().compareToIgnoreCase(p1.getName()));
                photoList.postValue(newList);
            }
        }).start();
    }

    public String getCorrectAnswers() {
        return photoList.getValue().get(currentIndex).getName();
    }
}
