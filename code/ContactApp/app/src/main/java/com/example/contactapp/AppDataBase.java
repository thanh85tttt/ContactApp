package com.example.contactapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Contact.class},version = 1)
public abstract class AppDataBase extends RoomDatabase {

    private static final String DATABASE_NAME="ContactDB";
    private static AppDataBase instance;

    public static synchronized AppDataBase getInstance(Context context){
        if(instance==null){
            instance= Room.databaseBuilder(context, AppDataBase.class,DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract ContactDao contactDao();
}