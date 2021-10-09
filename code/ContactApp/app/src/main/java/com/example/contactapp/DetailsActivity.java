package com.example.contactapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.contactapp.databinding.ActivityDetailsBinding;

public class DetailsActivity extends AppCompatActivity {
    private ActivityDetailsBinding binding;
    private AppDataBase db;
    private ContactDao dao;
    private Contact currentContact;
    private boolean changedContact;

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if(data!=null && data.hasExtra("edit") && data.getStringExtra("edit").equals("true")){
                            currentContact=dao.get(currentContact.getId());
                            setView();
                            changedContact=true;
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityDetailsBinding.inflate(getLayoutInflater());
        View viewDetailContact=binding.getRoot();
        setContentView(viewDetailContact);
        setSupportActionBar(binding.detailToolbar);
        changedContact=false;

        db=AppDataBase.getInstance(getApplicationContext());
        dao=db.contactDao();

        View logoView = binding.detailToolbar.getChildAt(1);
        logoView.setOnClickListener(v -> {
            if(changedContact){
                Intent intent=new Intent();
                intent.putExtra("changeContact","true");
                setResult(Activity.RESULT_OK,intent);
            }else setResult(Activity.RESULT_CANCELED, null);
            DetailsActivity.this.finish();
        });

        Intent receiveIntent=getIntent();

        if(receiveIntent!=null){
            int data=Integer.parseInt(receiveIntent.getStringExtra("contactId"));
            currentContact= dao.get(data);
            setView();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.mn_detail_edit_contact){
            Intent intent=new Intent(this,AddContactActivity.class);
            intent.putExtra("editContactId",String.valueOf(currentContact.getId()));
            someActivityResultLauncher.launch(intent);
        }
        return true;
    }

    private void setView(){
        if(currentContact==null){
            binding.ivDetailThumbnail.setImageResource(R.drawable.ic_baseline_person_24_white);
            binding.tvDetailContactName.setText("");
            binding.tvDetailContactNumber.setText("");
            binding.tvDetailAboutName.setText(R.string.default_about);
            binding.tvDetailEmail.setText(R.string.default_email);
            binding.tvDetailAddress.setText("Address");
        }else{
            byte[] contactThumbnail=currentContact.getThumbnail();
            if(contactThumbnail!=null){
                Bitmap bitmap= BitmapFactory.decodeByteArray(contactThumbnail,0,contactThumbnail.length);
                binding.ivDetailThumbnail.setImageBitmap(bitmap);
            }else binding.ivDetailThumbnail.setImageResource(R.drawable.ic_baseline_person_24_white);
            binding.tvDetailContactName.setText(currentContact.getName());
            binding.tvDetailContactNumber.setText(currentContact.getMobile());
            binding.tvDetailAboutName.setText(String.format("About %s", currentContact.getName()));
            binding.tvDetailEmail.setText(String.format("Email: %s", currentContact.getEmail()));
            binding.tvDetailAddress.setText(String.format("Address: %s", currentContact.getAddress()));
        }
    }
}