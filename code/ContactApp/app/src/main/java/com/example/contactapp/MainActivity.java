package com.example.contactapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.contactapp.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ContactAdapter.ClickListener{

    private ActivityMainBinding binding;
    private AppDataBase db;
    private ContactDao dao;
    private ContactAdapter adapter;

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if(data!=null){
                            if((data.hasExtra("addNewContact") && data.getStringExtra("addNewContact").equals("true"))
                                ||(data.hasExtra("changeContact") && data.getStringExtra("changeContact").equals("true"))
                            )
                            {
                                adapter.setContactList((ArrayList<Contact>) dao.getAll());
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());

        View viewRoot=binding.getRoot();
        setContentView(viewRoot);
        setSupportActionBar(binding.mainToolbar);

        db=AppDataBase.getInstance(getApplicationContext());
        dao=db.contactDao();
        adapter=new ContactAdapter((ArrayList<Contact>) dao.getAll());
        adapter.setClickListener(this);
        binding.rvListContact.setAdapter(adapter);
        binding.rvListContact.setLayoutManager(new LinearLayoutManager(this){});
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        MenuItem menuItem=menu.findItem(R.id.mn_search_main);
        SearchView searchView=(SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.mn_add_new_contact_main){
            Intent intent=new Intent(MainActivity.this, AddContactActivity.class);
            someActivityResultLauncher.launch(intent);
        }
        return true;
    }

    @Override
    public void itemClicked(Contact contact) {
        Intent intent=new Intent(this,DetailsActivity.class);
        intent.putExtra("contactId",String.valueOf(contact.getId()));
        someActivityResultLauncher.launch(intent);
    }
}