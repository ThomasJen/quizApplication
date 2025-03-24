package com.example.quizapplication;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Database;

// Definerer en Room-database, spesifiserer at det blir brukt en entitet, PhotoEntity
@Database(entities = {PhotoEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    // Oppretter en statisk volatile variabel som lagrer den enkeltinstansierte databasen. Må være "volatile" for å sikre at endringer i variabelen er synlig for alle tråder
    private static volatile AppDatabase INSTANCE;

    // Abstract metode som gir tilgang til DAO-objektet for PhotoEntity. Room genererer implementasjonen så vi kan kall metoder for databaseoperasjoner
    public abstract PhotoDAO photoDAO();

    // Statisk metode som returnerer en instans av databasen
    public static AppDatabase getDatabase(final Context context) {
        // Sjekker om databasen allerede er initialisert
        if (INSTANCE == null) {
            // Hindrer at flere tråder kan opprette databasen samtidig
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // Oppretter en ny instans av databasen ved hjelp av Room.databaseBuilder()
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "photo_database")
                            .fallbackToDestructiveMigration()   // Konfigurerer databasen til å ødelegge og gjenopprette den dersom en migrasjon ikke er definert.
                            .build();   // Bygger databaseinstansen
                }
            }
        }
        return INSTANCE;
    }
}
