package com.example.tazpitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class fillReport extends AppCompatActivity {
    EditText title;
    EditText description;
    Button submit;
    ImageButton pickMedia;
    CheckBox credit;
    //ImageView checkUploadImage;
    Bitmap bm;
    Uri download_url;
    public static final String TITLE_KEY="title";
    public static final String DESCRIPTION_KEY="description";
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DocumentReference mDocRef= FirebaseFirestore.getInstance().document("users reports/"+UUID.randomUUID());

    private static final int SELECT_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_report);
        title = (EditText) findViewById(R.id.fill_report_title);
        description = (EditText) findViewById(R.id.fill_report_description);
        submit = (Button) findViewById(R.id.fill_report_send_button);
        pickMedia = (ImageButton) findViewById(R.id.fill_report_upload_button);
        credit = (CheckBox) findViewById(R.id.fill_report_add_credit);
        // checkUploadImage=(ImageView)findViewById(R.id.check_upload_phto);
        // checkUploadImage = new ImageView(this);
        pickMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("*/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(description.getText()) || bm==null)
                    Toast.makeText(getApplicationContext(), "כל השדות הינם חובה", Toast.LENGTH_LONG).show();
                else {
                    String getTitle = title.getText().toString();
                    String getDescription = description.getText().toString();
//                    if (itemClicked(credit) == true)
//                        Toast.makeText(getApplicationContext(), "title: " + getTitle + "\n desc: " + getDescription + "\n chekbox chosen ", Toast.LENGTH_SHORT).show();
//                    else
//                        Toast.makeText(getApplicationContext(), "title: " + getTitle + "\n desc: " + getDescription + "\n chekbox not chosen ", Toast.LENGTH_SHORT).show();
                        //uploading title and description to firestore firebase============
                    Map<String,Object> dataToSave=new HashMap<String,Object>();
                    dataToSave.put(TITLE_KEY,getTitle);
                    dataToSave.put(DESCRIPTION_KEY,getDescription);
                    //dataToSave.put("ImageUrl", "https://firebasestorage.googleapis.com/v0/b/tazpit-testingandprototype.appspot.com/o/firememes%2F%D7%A0%D7%99%D7%A1%D7%95%D7%99%20%D7%A0%D7%99%D7%A1%D7%95%D7%99%2F25d8f449-70ba-448d-a56d-d59128b2ab38.png?alt=media&token=827aa828-cb4c-49a5-b3b3-f5d1c2e2c1fa");
                    mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("InspiritingQuote", "DocumentSnapshot successfully written!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.w("InspiritingQuote", "Error writing document", e);
                        }
                    });

                    //==============================================================

                    //uploading the image to storage firebase===========================
                    ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    byte [] data=outputStream.toByteArray();
                    String path = "firememes/" + "ניסוי ניסוי" +"/"+UUID.randomUUID()+ ".png";
                    StorageReference firememeRef = storage.getReference(path);
                    StorageMetadata metadata=new StorageMetadata.Builder().setCustomMetadata("caption", "the photo").build();
                    UploadTask uploadTask=firememeRef.putBytes(data, metadata);

                    //if(uploadTask.isSuccessful())
                    Toast.makeText(getApplicationContext(), "image upload succeed ", Toast.LENGTH_LONG).show();



                    //==========================================

                }

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO) {
            Toast.makeText(getApplicationContext(), "after pick", Toast.LENGTH_LONG).show();
            //below checks i can get the media and present in on ImageView
            Uri imgUri = data.getData();
            try {
                bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
//                        try {
//                 bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
//                checkUploadImage.setImageBitmap(bm);} catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    public boolean itemClicked(View v) {
        //code to check if this checkbox is checked!
        boolean indc = false;
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            indc = true;
        }
        return indc;
    }

}