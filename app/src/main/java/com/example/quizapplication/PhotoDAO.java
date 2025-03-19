package com.example.quizapplication;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PhotoDAO {
    @Insert
    void insert(PhotoEntity photo);

    @Insert
    void insertAll(List<PhotoEntity> photos);

    @Query("SELECT * FROM photos")
    List<PhotoEntity> getAllPhotosSync();


    @Query("SELECT * FROM photos")
    LiveData<List<PhotoEntity>> getAllPhotos();

    @Query("SELECT * FROM photos WHERE name = :name LIMIT 1")
    PhotoEntity getPhotoByName(String name);

    @Query("DELETE FROM photos")
    void deleteAllPhotos();

    @Delete
    void delete(PhotoEntity photo);

}
