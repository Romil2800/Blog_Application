package com.myblog.blogapplication.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.myblog.blogapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private ImageButton mPostImage;
    private EditText mPostTitle,mPostDesc;
    private Button mSubmitButton;
    private DatabaseReference mPostDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog mProgress;
    private static final int GALLARY_CODE=1;
    private StorageReference mStorage;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mProgress=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        mStorage= FirebaseStorage.getInstance().getReference();

        mPostDatabase= FirebaseDatabase.getInstance().getReference().child("MBlog");

        mPostImage=(ImageButton) findViewById(R.id.imageButton);
        mPostTitle=(EditText)findViewById(R.id.postTitle);
        mPostDesc=(EditText)findViewById(R.id.postDescription);
        mSubmitButton=(Button)findViewById(R.id.submitPost);


        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallaryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent,GALLARY_CODE);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLARY_CODE&& resultCode==RESULT_OK){
            mImageUri=data.getData();
            mPostImage.setImageURI(mImageUri);
        }
    }

    private void startPosting() {
        mProgress.setMessage("Posting to blog...");
        mProgress.show();

        final String titleVal=mPostTitle.getText().toString().trim();
        final String descVal=mPostDesc.getText().toString().trim();

        if(!TextUtils.isEmpty(titleVal)&&!TextUtils.isEmpty(descVal)&&mImageUri!=null){

//            Blog blog=new Blog("Title","Description","imageurl","timestamp","userid");
//
//            mPostDatabase.setValue(blog).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Toast.makeText(getApplicationContext(),"Item Added",Toast.LENGTH_LONG).show();
//                }
//            });
            StorageReference filepath=mStorage.child("MBlog_images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadURL=taskSnapshot.getUploadSessionUri();
                    DatabaseReference newPost=mPostDatabase.push();

                    Map<String,String> dataToSave=new HashMap<>();
                    dataToSave.put("title",titleVal);
                    dataToSave.put("desc",descVal);
                    dataToSave.put("image",downloadURL.toString());
                    dataToSave.put("timestamp",String.valueOf(java.lang.System.currentTimeMillis()));
                    dataToSave.put("userid",mUser.getUid());

                    newPost.setValue(dataToSave);
                    mProgress.dismiss();

                    startActivity(new Intent(AddPostActivity.this,PostListActivity.class));
                    finish();
                }
            });
        }
    }

}