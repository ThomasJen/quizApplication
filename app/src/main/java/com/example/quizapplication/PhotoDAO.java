package com.example.quizapplication;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PhotoDAO {

    // Metode for å sette inn en PhotoEntity i databasen
    @Insert
    void insert(PhotoEntity photo);

    @Insert
    void insertAll(List<PhotoEntity> photos);

    // Spørring for å hente alle PhotoEntity-ene fra databasen
    @Query("SELECT * FROM photos")
    List<PhotoEntity> getAllPhotosSync();

    // LiveData-metode for å observere endringer i PhotoEntity-ene i databasen
    @Query("SELECT * FROM photos")
    LiveData<List<PhotoEntity>> getAllPhotos();

    @Query("SELECT * FROM photos WHERE name = :name LIMIT 1")
    PhotoEntity getPhotoByName(String name);

    @Query("DELETE FROM photos")
    void deleteAllPhotos();

    // Metode for å fjerne en PhotoEntity fra databasen
    @Delete
    void delete(PhotoEntity photo);

}
