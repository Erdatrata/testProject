package com.example.tazpitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class fillReport extends AppCompatActivity {
    EditText title;
    EditText description;
    Button submit;
    ImageButton pickMedia;
    CheckBox credit;
   // Bitmap bm;
    ArrayList<Bitmap> bm = new ArrayList<Bitmap>();
   // String returnUrl="";
    public static final String TITLE_KEY = "title";
    public static final String DESCRIPTION_KEY = "description";
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DocumentReference mDocRef;
    private FirebaseAuth mAuth;
    Map<String, Object> dataToSave = new HashMap<String, Object>();
    ArrayList<Uri> imagesFromURL = new ArrayList<Uri>();
    private static final int SELECT_PHOTO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_report);
        title = (EditText) findViewById(R.id.fill_report_title);
        description = (EditText) findViewById(R.id.fill_report_description);
        submit = (Button) findViewById(R.id.fill_report_send_button);
        pickMedia = (ImageButton) findViewById(R.id.fill_report_upload_button);
        credit = (CheckBox) findViewById(R.id.fill_report_add_credit);
        mAuth = FirebaseAuth.getInstance();
        String scenarioPressed = getIntent().getStringExtra("pressed scenario");
        mDocRef = FirebaseFirestore.getInstance().document("Scenarios/" + scenarioPressed+"/"+mAuth.getCurrentUser().getEmail()+"/"+mAuth.getCurrentUser().getEmail()+" report:");

        pickMedia.setOnClickListener(new View.OnClickListener() { //when pressing the upload media button we go here
            //and choose media
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO); //SELECT_PICTURES is simply a global int used to check the calling intent in onActivityResult
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(description.getText()) || bm == null)
                    Toast.makeText(getApplicationContext(), "כל השדות הינם חובה", Toast.LENGTH_LONG).show();
                else {
                    String getTitle = title.getText().toString();
                    String getDescription = description.getText().toString();
                    //uploading the media to storage firebase===============================
                    for(int i=0; i<bm.size(); i++) {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bm.get(i).compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        byte[] dataMedia = outputStream.toByteArray();
                        String path = "firememes/" + mAuth.getCurrentUser().getEmail() + "/" + UUID.randomUUID() + ".png";
                        StorageReference firememeRef = storage.getReference(path);
                        StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("caption", "the photo").build();
                        UploadTask uploadTask = firememeRef.putBytes(dataMedia, metadata);

                        int finalI = i;

                        uploadTask.addOnCompleteListener(fillReport.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.i("MA", "upload image num " + finalI + "task to storage completed");
                                    uploadReportFirestore(uploadTask, firememeRef, finalI,getDescription,getTitle);
                                }
                                else{
                                    Log.d("image fail", "failed upload image");
                                }
                            }
                        });


                    }

                }
            }
        });

    }
//this is func that calls automatic by startActivityForResult after choosing media files
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_PHOTO) {
            if(resultCode == Activity.RESULT_OK) {
                if(data.getClipData() != null) {
                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    Uri imageUri;
                    //in the loop we add to arraylist of Bitmap the media. loop runs on size of amount of media that picked
                    for(int i = 0; i < count; i++) {
                        imageUri = data.getClipData().getItemAt(i).getUri();
                        imagesFromURL.add(imageUri);
                        try {
                            bm.add(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri)) ;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            } else if(data.getData() != null) {
                String imagePath = data.getData().getPath();
                //do something with the image (save it to some directory or whatever you need to do with it here)
            }
        }
    }

//function that checks if the user wants to get credit about the report he will upload
    public boolean itemClicked(View v) {
        //code to check if this checkbox is checked!
        boolean indc = false;
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            indc = true;
        }
        return indc;
    }
    //this function pushes media, title, descript and credit to a virable that saves the
    //conetnt we will have in the firebase firestore. when the loop of the media ends we push the virable to firebase
    //and upload the report.
    public void uploadReportFirestore(UploadTask uploadTask,StorageReference firememeRef, int numPhoto,String getDescription,String getTitle) {
        Task<Uri> getDownloadUriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
       @Override
          public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
          if (!task.isSuccessful()) {
             throw task.getException();
               }
                return firememeRef.getDownloadUrl();
          //Getting media url to store it in firestore
                      }
                    }
        );
        getDownloadUriTask.addOnCompleteListener(fillReport.this, new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                  String returnUrl=downloadUri.toString();
                    Log.d("downloaduri", returnUrl);
                    if(numPhoto== bm.size()-1){
                        if (itemClicked(credit) == true)
                            dataToSave.put("credit", true);
                        else
                            dataToSave.put("credit", false);
                        dataToSave.put(DESCRIPTION_KEY, getDescription);
                        dataToSave.put(TITLE_KEY, getTitle);
                        dataToSave.put("media url "+numPhoto, returnUrl);
                        mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("InspiritingQuote", "DocumentSnapshot successfully written!");
                                Toast.makeText(getApplicationContext(), "העלאת האירוע בוצעה בהצלחה", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(fillReport.this, MainActivity.class);
                                startActivity(intent);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Log.w("InspiritingQuote", "Error writing document", e);
                            }
                        });
                    }
                    else
                        dataToSave.put("media url "+numPhoto, returnUrl);

                }
                else
    Log.d("failed to upload report","failed upload report");
            }
        });

    }

}