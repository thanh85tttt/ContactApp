package com.example.contactapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("Select * from Contact where id=:contactId")
    Contact get(int contactId);
    @Query("Select * from Contact ORDER BY fullName COLLATE NOCASE")
    List<Contact> getAll();
    @Insert
    void insert(Contact... contact); //list contacts: contact 1,2,3
    @Delete
    void delete(Contact contact);
    @Update
    void update(Contact contact);
}
