package com.example.quizapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import android.net.Uri;

@Entity(tableName = "photos")
public class PhotoEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
     public String name;;
    public Integer imageResId;

    public String imagePath;

    public PhotoEntity(String name, Integer imageResId, String imagePath){
        this.name = name;
        this.imageResId = imageResId;
        this.imagePath = imagePath;
    }

    public int getId(){ return id;}
    public void setId(int id){ this.id = id;}

    public String getName(){ return name;}
    public String getImagePath() {
        return imagePath;
    }

    public Object getImageUri() {
        return (imageResId != null && imageResId != 0) ? imageResId : imagePath;
    }

}
