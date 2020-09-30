package com.myblog.blogapplication.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myblog.blogapplication.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText firstName,lastName,email,password;
    private Button createAccountBtn;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    private ImageButton profilePic;
    private final static int GALLERY_CODE=1;
    private Uri resultUri=null;
    private StorageReference mFirebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference=mDatabase.getReference().child("MUsers");

        mAuth=FirebaseAuth.getInstance();
        mFirebaseStorage=FirebaseStorage.getInstance().getReference().child("MBlog_Profile_Pics");
        mProgressDialog=new ProgressDialog(this);

        firstName=(EditText)findViewById(R.id.firstNameId);
        lastName=(EditText)findViewById(R.id.lastNameId);
        email=(EditText)findViewById(R.id.emailId);
        password=(EditText)findViewById(R.id.passwordId);
        createAccountBtn=(Button) findViewById(R.id.createAccountId);
        profilePic=(ImageButton)findViewById(R.id.profilePicId);


        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });


        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);
            }
        });

    }

    private void createNewAccount() {
        final String fName=firstName.getText().toString().trim();
        final String lName=lastName.getText().toString().trim();
        String em=email.getText().toString().trim();
        String pwd=password.getText().toString().trim();

        if(!TextUtils.isEmpty(fName) && !TextUtils.isEmpty(lName) && !TextUtils.isEmpty(em) && !TextUtils.isEmpty(pwd)){
            mProgressDialog.setMessage("Creating Account...");
            mProgressDialog.show();

            mAuth.createUserWithEmailAndPassword(em,pwd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if(authResult!=null){

                        StorageReference imagePath=mFirebaseStorage.child("MBlog_Profile_Pics").child(resultUri.getLastPathSegment());

                        imagePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String userId=mAuth.getCurrentUser().getUid();
                                DatabaseReference currentUserDb=mDatabaseReference.child(userId);
                                currentUserDb.child("firstname").setValue(fName);
                                currentUserDb.child("lastname").setValue(lName);
                                currentUserDb.child("image").setValue(resultUri.toString());
                                currentUserDb.child("somethingnew").setValue("none");

                                mProgressDialog.dismiss();

                                //send users to postList

                                Intent intent=new Intent(CreateAccountActivity.this,PostListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });


                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_CODE && resultCode==RESULT_OK){
            Uri mImageUri=data.getData();
            CropImage.activity(mImageUri)
                    .setAspectRatio(1,1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();

                profilePic.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}