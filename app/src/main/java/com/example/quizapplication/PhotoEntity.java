package com.example.quizapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "photos")
public class PhotoEntity {

    // Definerer primærnøkkelen for PhotoEntity
    @PrimaryKey(autoGenerate = true)
    public int id;                  // Sikrer at hver rad har egen ID
    public String name;             // Lagrer navnet på bildet
    public Integer imageResId;      // Lagrer en resource ID for bildet
    public String imagePath;        // Lagrer en path til bildet


    // Konstruktør for PhotoEntity
    public PhotoEntity(String name, Integer imageResId, String imagePath){
        this.name = name;
        this.imageResId = imageResId;
        this.imagePath = imagePath;
    }

    // Getter og setter for ID så vi kan hente og oppdatere PK om nødvendig
    public int getId(){ return id;}
    public void setId(int id){ this.id = id;}

    // Gir tilgang til navnet på bildet
    public String getName(){ return name;}

    // For å hente path til bildet
    public String getImagePath() {
        return imagePath;
    }

}
