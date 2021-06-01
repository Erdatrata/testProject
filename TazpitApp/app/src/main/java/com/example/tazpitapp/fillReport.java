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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.OnProgressListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.sql.Struct;
import java.text.BreakIterator;
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
    ProgressBar progressBar;
   // Bitmap bm;
   // ArrayList<Bitmap> bm = new ArrayList<Bitmap>();
   // String returnUrl="";
    public static final String TITLE_KEY = "title";
    public static final String DESCRIPTION_KEY = "description";
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DocumentReference mDocRef;
    private FirebaseAuth mAuth;
    Map<String, Object> dataToSave = new HashMap<String, Object>();
    ArrayList<Uri> mediaHolder = new ArrayList<Uri>();
    private static final int SELECT_PHOTO = 100;
   TextView progressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_report);
        title = (EditText) findViewById(R.id.fill_report_title);
        description = (EditText) findViewById(R.id.fill_report_description);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        submit = (Button) findViewById(R.id.fill_report_send_button);
        pickMedia = (ImageButton) findViewById(R.id.fill_report_upload_button);
        credit = (CheckBox) findViewById(R.id.fill_report_add_credit);
        progressTextView=(TextView) findViewById(R.id.progressTextView);
        mAuth = FirebaseAuth.getInstance();
        String scenarioPressed = getIntent().getStringExtra("pressed scenario");
        mDocRef = FirebaseFirestore.getInstance().document("Scenarios/" + scenarioPressed+"/"+"filled/"+mAuth.getCurrentUser().getUid() +" report:");
        DocumentReference deleteUserFromAccept=FirebaseFirestore.getInstance().document("Scenarios/"+scenarioPressed);
        pickMedia.setOnClickListener(new View.OnClickListener() { //when pressing the upload media button we go here
            //and choose media
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "בחר מדיה"), SELECT_PHOTO); //SELECT_PICTURES is simply a global int used to check the calling intent in onActivityResult
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(description.getText()) || mediaHolder == null)
                    Toast.makeText(getApplicationContext(), "כל השדות הינם חובה", Toast.LENGTH_LONG).show();
                else {
                    String getTitle = title.getText().toString();
                    String getDescription = description.getText().toString();
                    //uploading the media to storage firebase===============================
                    for(int i=0; i<mediaHolder.size(); i++) {
                        String path = scenarioPressed+"/" + mAuth.getCurrentUser().getEmail() + "/" + UUID.randomUUID();
                        StorageReference firememeRef = storage.getReference(path);
                        UploadTask uploadTask = firememeRef.putFile(mediaHolder.get(i));

                        int finalI = i;

                        uploadTask.addOnCompleteListener(fillReport.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.i("MA", "upload image num " + finalI + "task to storage completed");
                                    uploadReportFirestore(uploadTask, firememeRef, finalI,getDescription,getTitle,deleteUserFromAccept, mAuth.getCurrentUser());
                                }
                                else{
                                    Log.d("image fail", "failed upload image");
                                }
                            }
                            //setting progressbar on each media that uploads to firebase
                        }).addOnProgressListener((com.google.firebase.storage.OnProgressListener<? super UploadTask.TaskSnapshot>) taskSnapshot -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressBar.setProgress((int) progress);
                            String progressString = ((int) progress) + " % העלאת הדוח בתהליכים";
                            progressTextView.setText(progressString);
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
                if(data!=null) {
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                        Uri imageUri;
                        //in the loop we add to arraylist of Bitmap the media. loop runs on size of amount of media that picked
                        for (int i = 0; i < count; i++) {
                            imageUri = data.getClipData().getItemAt(i).getUri();
                            mediaHolder.add(imageUri);
//                        try {
//                            System.out.println("the media is: "+imageUri.toString());
//                            bm.add(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri)) ;
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }

                        }
                    }
                }
            }
//            else if(data.getData() != null) {
//                String imagePath = data.getData().getPath();
//                //do something with the image (save it to some directory or whatever you need to do with it here)
//            }
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
    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image"); }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");}

    //this function pushes media, title, descript and credit to a virable that saves the
    //conetnt we will have in the firebase firestore. when the loop of the media ends we push the virable to firebase
    //and upload the report.
    public void uploadReportFirestore(UploadTask uploadTask,StorageReference firememeRef, int numPhoto,String getDescription,String getTitle, DocumentReference docRef,FirebaseUser user) {
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
                    if(numPhoto== mediaHolder.size()-1){
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
                                removeUserFromAccept( docRef,  user);
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
    private void removeUserFromAccept(  DocumentReference docRef, FirebaseUser user){
        docRef.collection("accepted").document(user.getUid())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("test55", "DocumentSnapshot successfully deleted!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("test56", "Error deleting document", e);
                    }
                });
    }


}