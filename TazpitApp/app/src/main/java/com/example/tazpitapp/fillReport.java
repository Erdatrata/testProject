package com.example.tazpitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class fillReport extends AppCompatActivity {
    EditText title;
    EditText description;
    Button submit;
    ImageButton pickMedia;
    CheckBox credit;
    ProgressBar progressBar;


    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private DocumentReference mDocRef;
    private FirebaseAuth mAuth;
    Map<String, Object> dataToSave = new HashMap<>();
    ArrayList<Uri> mediaHolder = new ArrayList<>();
    private static final int SELECT_PHOTO = 100;
   TextView progressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_report);
        title = findViewById(R.id.fill_report_title);
        description = findViewById(R.id.fill_report_description);
        progressBar= findViewById(R.id.progressBar);
        submit = findViewById(R.id.fill_report_send_button);
        pickMedia = findViewById(R.id.fill_report_upload_button);
        credit = findViewById(R.id.fill_report_add_credit);
        progressTextView= findViewById(R.id.progressTextView);
        mAuth = FirebaseAuth.getInstance();
        String scenarioPressed = getIntent().getStringExtra(constants.PRESSED_SCENARIO);
        mDocRef = FirebaseFirestore.getInstance().document(constants.DOC_REF_SCENARIOS+"/" + scenarioPressed+"/"+constants.DOC_REF_FILLED+"/"+mAuth.getCurrentUser().getUid());
        DocumentReference deleteUserFromAccept=FirebaseFirestore.getInstance().document(constants.DOC_REF_SCENARIOS+"/"+scenarioPressed);
        //when pressing the upload media button we go here
//and choose media
        pickMedia.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.choose_media)), SELECT_PHOTO); //SELECT_PICTURES is simply a global int used to check the calling intent in onActivityResult
        });

        submit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(description.getText()) || mediaHolder.size() == 0)
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.all_fields_must), Toast.LENGTH_LONG).show();
            else {
                String getTitle = title.getText().toString();
                String getDescription = description.getText().toString();
                //uploading the media to storage firebase===============================
                for(int i=0; i<mediaHolder.size(); i++) {
                    String path = scenarioPressed+"/" + mAuth.getCurrentUser().getEmail() + "/" + UUID.randomUUID();
                    StorageReference firememeRef = storage.getReference(path);
                    UploadTask uploadTask = firememeRef.putFile(mediaHolder.get(i));

                    int finalI = i;

                    //setting progressbar on each media that uploads to firebase
                    uploadTask.addOnCompleteListener(fillReport.this, task -> {
                        if (task.isSuccessful()) {
                            Log.i("MA", "upload image num " + finalI + "task to storage completed");
                            uploadReportFirestore(uploadTask, firememeRef, finalI,getDescription,getTitle,deleteUserFromAccept, mAuth.getCurrentUser());
                        }
                        else{
                            Log.d("image fail", "failed upload image");
                        }
                    }).addOnProgressListener((com.google.firebase.storage.OnProgressListener<? super UploadTask.TaskSnapshot>) taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressBar.setProgress((int) progress);
                        String progressString = ((int) progress) + getResources().getString(R.string.report_inprocess);
                        progressTextView.setText(progressString);
                    });


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


                        }
                    }
                }
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
    public void uploadReportFirestore(UploadTask uploadTask,StorageReference firememeRef, int numPhoto,String getDescription,String getTitle, DocumentReference docRef,FirebaseUser user) {
        Task<Uri> getDownloadUriTask = uploadTask.continueWithTask(task -> {
           if (!task.isSuccessful()) {
              throw task.getException();
                }
                 return firememeRef.getDownloadUrl();
           //Getting media url to store it in firestore
                       }
        );
        getDownloadUriTask.addOnCompleteListener(fillReport.this, task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
              String returnUrl=downloadUri.toString();
                Log.d("downloaduri", returnUrl);
                if(numPhoto== mediaHolder.size()-1){
                    if (itemClicked(credit))
                        dataToSave.put(constants.CREDIT, true);
                    else
                        dataToSave.put(constants.CREDIT, false);
                    dataToSave.put(constants.DESCRIPTION_KEY, getDescription);
                    dataToSave.put(constants.TITLE_KEY, getTitle);
                    dataToSave.put(constants.MEDIAURL+numPhoto, returnUrl);
                    mDocRef.set(dataToSave).addOnSuccessListener(unused -> {
                        Log.d("InspiritingQuote", "DocumentSnapshot successfully written!");
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.succeed_upload_report), Toast.LENGTH_LONG).show();
                        removeUserFromAccept( docRef,  user);
                        Intent intent = new Intent(fillReport.this, MainActivity.class);
                        startActivity(intent);

                    }).addOnFailureListener(e -> Log.w("InspiritingQuote", "Error writing document", e));
                }
                else
                    dataToSave.put(constants.MEDIAURL+numPhoto, returnUrl);

            }
            else
Log.d("failed to upload report","failed upload report");
        });

    }
    private void removeUserFromAccept(  DocumentReference docRef, FirebaseUser user){
        docRef.collection(constants.DOC_REF_ACCEPTED).document(user.getUid())
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("test55", "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w("test56", "Error deleting document", e));
    }


}