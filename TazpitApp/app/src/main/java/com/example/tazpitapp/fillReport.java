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
    Bitmap bm;
    String returnUrl="";
    public static final String TITLE_KEY = "title";
    public static final String DESCRIPTION_KEY = "description";
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DocumentReference mDocRef;
    private FirebaseAuth mAuth;
    Map<String, Object> dataToSave = new HashMap<String, Object>();
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
        mAuth = FirebaseAuth.getInstance();
        mDocRef = FirebaseFirestore.getInstance().document("users reports/" + mAuth.getCurrentUser().getEmail());

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
                if (TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(description.getText()) || bm == null)
                    Toast.makeText(getApplicationContext(), "כל השדות הינם חובה", Toast.LENGTH_LONG).show();
                else {
                    String getTitle = title.getText().toString();
                    String getDescription = description.getText().toString();

                    //==============================================================

                    //uploading the image to storage firebase===============================
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    byte[] data = outputStream.toByteArray();
                    String path = "firememes/" + mAuth.getCurrentUser().getEmail() + "/" + UUID.randomUUID() + ".png";
                    StorageReference firememeRef = storage.getReference(path);
                    StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("caption", "the photo").build();
                    UploadTask uploadTask = firememeRef.putBytes(data, metadata);

                    uploadTask.addOnCompleteListener(fillReport.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()) {
                                Log.i("MA", "upload image task to storage completed");
                            }
                        }
                    });

                    uploadReportToFireStore(uploadTask, firememeRef, getDescription, getTitle);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO) {
            Log.d("after pick media", "succced pick media");
            Uri imgUri = data.getData();
            try {
                bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

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

    public void uploadReportToFireStore(UploadTask uploadTask,StorageReference firememeRef,String getDescription, String getTitle) {
        Task<Uri> getDownloadUriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
       @Override
          public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
          if (!task.isSuccessful()) {
             throw task.getException();
               }
                return firememeRef.getDownloadUrl();
                      }
                    }
        );
        getDownloadUriTask.addOnCompleteListener(fillReport.this, new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    if (itemClicked(credit) == true)
                        dataToSave.put("credit", true);
                    else
                        dataToSave.put("credit", false);
                    dataToSave.put(DESCRIPTION_KEY, getDescription);
                    dataToSave.put(TITLE_KEY, getTitle);
                    Uri downloadUri = task.getResult();
                    returnUrl+=downloadUri.toString();
                    Log.d("downloaduri", returnUrl);
                    dataToSave.put("media url", returnUrl);
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
            }
        });

    }

}