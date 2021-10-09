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
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.contactapp.databinding.ActivityAddContactBinding;

import java.io.ByteArrayOutputStream;

public class AddContactActivity extends AppCompatActivity {

    private ActivityAddContactBinding binding;
    private Uri imageUri;
    private boolean changeImage;
    private Contact currentContact;
    private AppDataBase db;
    private ContactDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAddContactBinding.inflate(getLayoutInflater());
        View viewAddContact=binding.getRoot();
        setContentView(viewAddContact);
        setSupportActionBar(binding.addToolbar);

        Spinner spinner = findViewById(R.id.sp_list_device);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.device, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        db=AppDataBase.getInstance(getApplicationContext());
        dao=db.contactDao();
        changeImage=false;

        Intent receiveIntent=getIntent();
        if(receiveIntent!=null && receiveIntent.hasExtra("editContactId")){
            int data=Integer.parseInt(String.valueOf(receiveIntent.getStringExtra("editContactId")));
            currentContact=dao.get(data);
            binding.addToolbar.setTitle("Edit contact");
            setView();
        }

        binding.btnAddImage.setOnClickListener(view -> openGalleryIntent());
        binding.btnDeleteImage.setOnClickListener(view -> {
           imageUri=null;
           binding.ivThumbnail.setImageResource(R.drawable.ic_baseline_person_24_white);
           binding.ivThumbnail.setBackgroundColor(Color.rgb(136,136,136));
           binding.btnAddImage.setImageResource(R.drawable.ic_baseline_photo_camera_24);
           binding.btnDeleteImage.setVisibility(View.INVISIBLE);
           changeImage=true;
       });

        View logoView = binding.addToolbar.getChildAt(1);
        logoView.setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED, null);
            AddContactActivity.this.finish();
        });

    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent i=result.getData();
                        if(i!=null && i.getData()!=null){
                            imageUri=i.getData();
                            binding.ivThumbnail.setImageURI(imageUri);
                            binding.ivThumbnail.setBackgroundColor(0);
                            binding.btnAddImage.setImageResource(R.drawable.ic_camera_24);
                            binding.btnDeleteImage.setVisibility(View.VISIBLE);
                            changeImage=true;
                            return;
                        }
                    }
                    Toast.makeText(AddContactActivity.this,"No image chosen",Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_contact,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.mn_save_new_contact){
            if(checkValidInput()){
               String fullName=binding.etFullName.getText().toString();
               String phone=binding.etPhone.getText().toString();
               String mail=binding.etEmail.getText().toString();
                String address=binding.etAddress.getText().toString();
               Intent intent=new Intent();
               if(currentContact==null){
                   Contact contact;
                   if(imageUri!=null){
                       contact=new Contact(fullName,phone,mail,address,imageViewToByte(binding.ivThumbnail));
                   }else contact=new Contact(fullName,phone,mail,address);
                   dao.insert(contact);
                   intent.putExtra("addNewContact","true");
               }else{
                   int currentId=currentContact.getId();
                   if(changeImage){
                       if(imageUri!=null)
                           currentContact=new Contact(fullName,phone,mail,address,imageViewToByte(binding.ivThumbnail));
                       else currentContact=new Contact(fullName,phone,mail,address);
                   }else currentContact=new Contact(fullName,phone,mail,address,currentContact.getThumbnail());
                   currentContact.setId(currentId);
                   dao.update(currentContact);
                   intent.putExtra("edit","true");
               }
               setResult(Activity.RESULT_OK,intent);
               this.finish();
           }else binding.etFullName.setFocusable(true);
        }
        return true;
    }

    private void openGalleryIntent(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intent);
    }

    private byte[] imageViewToByte (ImageView image){
        Bitmap bitmap=((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        return stream.toByteArray();
    }

    private boolean checkValidInput(){
        if(binding.etFullName.getText().toString().equals("")
                && binding.etPhone.getText().toString().equals("")){
            Toast toast=Toast.makeText(AddContactActivity.this,"Can not save contact if both full name and phone number are empty!",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,0);
            toast.show();
            return false;
        }
        return true;
    }

    private void setView(){
        if(currentContact==null){
            binding.ivThumbnail.setImageResource(R.drawable.ic_baseline_person_24_white);
            binding.etFullName.setText("");
            binding.etPhone.setText("");
            binding.etEmail.setText("");
            binding.etAddress.setText("");
        }else{
            byte[] contactThumbnail=currentContact.getThumbnail();
            if(contactThumbnail!=null){
                Bitmap bitmap= BitmapFactory.decodeByteArray(contactThumbnail,0,contactThumbnail.length);
                binding.ivThumbnail.setImageBitmap(bitmap);
                binding.ivThumbnail.setBackgroundColor(0);
                binding.btnAddImage.setImageResource(R.drawable.ic_camera_24);
                binding.btnDeleteImage.setVisibility(View.VISIBLE);
            }else binding.ivThumbnail.setImageResource(R.drawable.ic_baseline_person_24_white);
            binding.etFullName.setText(currentContact.getName());
            binding.etPhone.setText(currentContact.getMobile());
            binding.etEmail.setText(currentContact.getEmail());
            binding.etAddress.setText(currentContact.getAddress());
        }
    }
}