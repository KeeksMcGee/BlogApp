package com.kiarra.blogapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kiarra.blogapp.Model.Blog;
import com.kiarra.blogapp.R;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {
    private ImageButton mPostImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitButton;
    private DatabaseReference mPostDatabase;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog mProgress;
    private Uri mImageUri;
    private static final int GALLERY_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mProgress = new ProgressDialog(this);

        mPostImage = (ImageButton)findViewById(R.id.imageButton);
        mPostTitle = (EditText)findViewById(R.id.postTitleEt);
        mPostDesc = (EditText)findViewById(R.id.postDescEt);
        mSubmitButton = (Button)findViewById(R.id.submitPost);



        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("MBlog");
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Posting the info to the database
                startPosting();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            mPostImage.setImageURI(mImageUri);

        }
    }

    private void startPosting() {
        mProgress.setMessage("Posting to blog...");
        mProgress.show();

        final String titleVal = mPostTitle.getText().toString().trim();
        final String descVal = mPostDesc.getText().toString().trim();

        if (!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) && mImageUri != null){
            final StorageReference filepath = mStorage.child("MBlog_images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();

                                DatabaseReference newPost = mPostDatabase.push();
                                Map<String, String> dataToSave = new HashMap<>();
                    dataToSave.put("title", titleVal);
                    dataToSave.put("desc", descVal);
                    dataToSave.put("image", downloadUrl);
                    dataToSave.put("timeStamp", String.valueOf(java.lang.System.currentTimeMillis()));
                    dataToSave.put("userId", mUser.getUid());

                    newPost.setValue(dataToSave);
                    mProgress.dismiss();

                    startActivity(new Intent(AddPostActivity.this, PostListActivity.class));
                    finish();
                            }
                        });
                    }
                }
            });
        }
    }

//    private void startPosting() {
//        mProgress.setMessage("Posting to blog...");
//        mProgress.show();
//
//        final String titleVal = mPostTitle.getText().toString().trim();
//        final String descVal = mPostDesc.getText().toString().trim();
//
//        if (!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) && mImageUri != null) {
//            //Start the uploading of the post.
//
//            final StorageReference filePath = mStorage.child("MBlog_images").child(mImageUri.getLastPathSegment());
//            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();
//
//                    DatabaseReference newPost = mPostDatabase.push();
//
//                    Map<String, String> dataToSave = new HashMap<>();
//                    dataToSave.put("title", titleVal);
//                    dataToSave.put("desc", descVal);
//                    dataToSave.put("image", downloadUrl.toString());
//                    dataToSave.put("timeStamp", String.valueOf(java.lang.System.currentTimeMillis()));
//                    dataToSave.put("userId", mUser.getUid());
//
//                    newPost.setValue(dataToSave);
//                    mProgress.dismiss();
//
//                    startActivity(new Intent(AddPostActivity.this, PostListActivity.class));
//                    finish();
//
//                }
//            });
//
//
//        }
//    }
}
