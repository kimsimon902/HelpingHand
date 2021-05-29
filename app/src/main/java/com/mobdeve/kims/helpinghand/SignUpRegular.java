package com.mobdeve.kims.helpinghand;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUpRegular extends AppCompatActivity {

    private String currentPhotoPath;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    private EditText signupRegNameEt, signupRegEmailEt, signupRegPassEt, signupRegConfirmEt, signupRegDescEt;
    private Button signupRegConfirmBtn, signupRegCamBtn, signupRegGalBtn;
    private ImageView signupRegSelectedImageIv;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    private StorageReference storageReference;

    //FOR image name and uri
    private String image_name;
    private Uri localUri;
    private String firebaseUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_regular);

        mAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        signupRegNameEt = findViewById(R.id.addPostNameEt);
        signupRegEmailEt = findViewById(R.id.signupRegEmailEt);
        signupRegPassEt = findViewById(R.id.signupRegPassEt);
        signupRegConfirmEt = findViewById(R.id.signupRegConfirmEt);
        signupRegDescEt = findViewById(R.id.addPostDescEt);

        signupRegConfirmBtn = findViewById(R.id.addPostCreateBtn);
        signupRegCamBtn = findViewById(R.id.addPostCamBtn);
        signupRegGalBtn = findViewById(R.id.addPostGalBtn);

        signupRegSelectedImageIv = findViewById(R.id.addPostSelectedImageIv);
        signupRegSelectedImageIv.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressBar);


        signupRegConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();

            }
        });

        signupRegCamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });

        signupRegGalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });

    }


    private void askCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else{
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else{
                Toast.makeText(getBaseContext(), "Camera Permission Required!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera(){
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                File f = new File(currentPhotoPath);
                signupRegSelectedImageIv.setImageURI(Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                image_name = f.getName();
                localUri = contentUri;
                signupRegSelectedImageIv.setVisibility(View.VISIBLE);
//                uploadImageToFirebase(f.getName(), contentUri);
            }
        }

        if(requestCode == GALLERY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                Uri contentUri = data.getData();
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timestamp + "." + getFileExt(contentUri);
                signupRegSelectedImageIv.setImageURI(contentUri);

                image_name = imageFileName;
                localUri = contentUri;

                signupRegSelectedImageIv.setVisibility(View.VISIBLE);
//                uploadImageToFirebase(imageFileName, contentUri);
            }
        }

    }


    private void uploadImageToFirebase(String name, Uri contentUri){
        StorageReference image = storageReference.child("images/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image URL is " + uri.toString());
                        //                        Picasso.get().load(uri).into(signupBusSelectedImageIv);

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpRegular.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExt(Uri contentUri){
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private File createImageFile() throws IOException {
        //Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* Directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
//        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }catch (IOException ex){

            }

            // Continue only if the File was successfully created
            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.google.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
//        }
    }

    private void registerUser(){
        String name = signupRegNameEt.getText().toString();
        String email = signupRegEmailEt.getText().toString().trim();
        String pass = signupRegPassEt.getText().toString().trim();
        String confirm = signupRegConfirmEt.getText().toString().trim();
        String desc = signupRegDescEt.getText().toString();

        if(name.isEmpty()){
            signupRegNameEt.setError("Name is required!");
            signupRegNameEt.requestFocus();
            return;
        }

        if(email.isEmpty()){
            signupRegEmailEt.setError("Email is required!");
            signupRegEmailEt.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signupRegEmailEt.setError("Invalid email format!");
            signupRegEmailEt.requestFocus();
            return;
        }

        if(pass.isEmpty()){
            signupRegPassEt.setError("Password is required!");
            signupRegPassEt.requestFocus();
            return;
        }

        if(pass.length() < 6){
            signupRegPassEt.setError("Minimum password length should be 6 characters!");
            signupRegPassEt.requestFocus();
            return;
        }

        if(confirm.isEmpty()){
            signupRegConfirmEt.setError("Please confirm your password");
            signupRegConfirmEt.requestFocus();
            return;
        }

        if(!pass.equals(confirm)){
            signupRegPassEt.setError("Passwords do not match!");
            signupRegConfirmEt.setError("Passwords do not match!");
            signupRegConfirmEt.requestFocus();
            signupRegPassEt.requestFocus();
            return;
        }

        if(desc.isEmpty()){
            signupRegDescEt.setError("Description is required!");
            signupRegDescEt.requestFocus();
            return;
        }

        if(image_name == null && localUri == null) {
            Toast.makeText(SignUpRegular.this, "Image is required!", Toast.LENGTH_LONG).show();
            return;
        }

        uploadImageToFirebase(image_name, localUri);
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(name, email, desc, false, image_name);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(SignUpRegular.this, "User has been registered!", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(SignUpRegular.this, Login.class);
                                        i.putExtra("signUp", "true");
                                        startActivity(i);

                                        //redirect to login next
                                    }else{
                                        Toast.makeText(SignUpRegular.this, "Failed to register. Try again.", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });

    }

}
