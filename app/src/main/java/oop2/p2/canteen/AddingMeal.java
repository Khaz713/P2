package oop2.p2.canteen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.tomer.fadingtextview.FadingTextView;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This activity is for adding meal to the database.
 * Each meal has specific details that should be entered by user. Name, Description, price and image.
 * <p>
 * This activity connects to the database for writing inputted data into database.
 * <p>
 * This activity also opens intents related to opening camera and also choosing picture from gallery
 */

public class AddingMeal extends AppCompatActivity {


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private StorageReference mStorageRef;

    int PICK_IMAGE_REQUEST = 2; // Used for uploading from gallery
    static final int REQUEST_IMAGE_CAPTURE = 1; // Used for uploading from camera


    private TextInputLayout mNameTI;
    private TextInputLayout mPriceTI;
    private TextInputLayout mDesTI; // Description Text Input

    final Handler handler = new Handler();


    private TextInputEditText mNameET;
    private TextInputEditText mPriceET;
    private TextInputEditText mDesET;


    private ProgressBar loading;
    private TextView wall;

    FadingTextView fadingTextView;


    private Button mBChoose; // Choose a picture from gallery

    private Button mBCamera; // Take a picture with the device's camera
    private Button mBUpload; // Confirm Button which would upload the information entered by user into the database.

    ImageView mIView = null;

    /**
     * Stores the Uri of the image that is chosen from gallery
     * (In case that user don't use gallery its value is null)
     */
    Uri mImageUri = null;
    /**
     * Bitmap of the image that is taken by camera
     * (In case that we don't use camera its value is null)
     */
    Bitmap inImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_meal);


        mStorageRef = FirebaseStorage.getInstance().getReference("meal_images");

        mNameTI = findViewById(R.id.textInputLayout2);
        mPriceTI = findViewById(R.id.textInputLayout1);
        mDesTI = findViewById(R.id.textInputLayout);

        mNameET = findViewById(R.id.mealNameE);
        mPriceET = findViewById(R.id.mealPriceE);
        mDesET = findViewById(R.id.mealDesE);

        mBChoose = findViewById(R.id.ChooseButton);
        mBCamera = findViewById(R.id.cameraButton);

        mBUpload = findViewById(R.id.uploadButton);

        mIView = findViewById(R.id.imageViewUpload);

        fadingTextView = (FadingTextView) findViewById(R.id.fade1);

        loading = findViewById(R.id.loading3);
        wall = findViewById(R.id.wall);


        loading.setVisibility(View.GONE);
        wall.setVisibility(View.GONE);


//hamburger menu
        //hamburger menu
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_item_menu);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.drawer_item_add_meal);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.drawer_item_make_menu);
        SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(5).withName(R.string.drawer_item_settings);
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName(R.string.drawer_item_sign_out);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);


        DrawerBuilder drawerBuilder;
        if (LoginActivity.isAdmin) {
            drawerBuilder = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withTranslucentNavigationBar(true)
                    .withActionBarDrawerToggle(true)
                    .addDrawerItems(item1, item2, item4, new DividerDrawerItem(), item5, item3)
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            Intent intent;
                            switch (position) {
                                case 0:
                                    intent = new Intent(getApplicationContext(), ChooseMenuActivity.class);
                                    startActivity(intent);
                                    break;
                                case 1:
                                    intent = new Intent(getApplicationContext(), AddingMeal.class);
                                    startActivity(intent);
                                    break;
                                case 2:
                                    intent = new Intent(getApplicationContext(), MakeMenuActivity.class);
                                    startActivity(intent);
                                    break;
                                case 4:
                                    intent = new Intent(getApplicationContext(), SettingsActivity.class);
                                    startActivity(intent);
                                    break;
                                case 5:
                                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    LoginActivity.mAuth.signOut();
                                    startActivity(intent);
                                    finish();
                                    break;
                            }
                            return false;
                        }
                    });

        } else {
            drawerBuilder = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withTranslucentNavigationBar(true)
                    .withActionBarDrawerToggle(true)
                    .addDrawerItems(item1, new DividerDrawerItem(), item5, item3)
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            Intent intent;
                            switch (position) {
                                case 0:
                                    intent = new Intent(getApplicationContext(), ChooseMenuActivity.class);
                                    startActivity(intent);
                                    break;
                                case 2:
                                    intent = new Intent(getApplicationContext(), SettingsActivity.class);
                                    startActivity(intent);
                                    break;
                                case 3:
                                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    LoginActivity.mAuth.signOut();
                                    startActivity(intent);
                                    finish();
                                    break;
                            }
                            return false;
                        }
                    });

        }

        Drawer drawer = drawerBuilder.build();
        drawer.setSelection(-1);


        mBChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mBCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }

        });
        mBUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validName() && validDes() && validPrice()) {

                    wall.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.VISIBLE);

                    if (mImageUri != null)
                        uploadGalleryPicture();
                    else {
                        uploadCameraPicture();
                    }
                }

            }
        });


    }

    /**
     * Checks weather the inputted name is valid or not
     * (If the field was empty name is invalid)
     *
     * @return boolean True : valid, False : invalid
     */
    private boolean validName() {
        String nameMeal = mNameTI.getEditText().getText().toString().trim();
        if (nameMeal.isEmpty()) {
            mNameTI.setError("Cannot be empty");
            return false;
        } else {
            mNameTI.setError(null);
            return true;
        }

    }

    /**
     * Checks weather the inputted price is valid or not
     * (If the field was empty price is invalid)
     *
     * @return boolean True : valid, False : invalid
     */

    private boolean validPrice() {
        String priceMeal = mPriceTI.getEditText().getText().toString().trim();
        if (priceMeal.isEmpty()) {
            mPriceTI.setError("Cannot be empty");
            return false;
        } else {
            mPriceTI.setError(null);
            return true;
        }

    }

    /**
     * Checks weather the inputted description is valid or not
     * (If the field was empty description is invalid)
     *
     * @return boolean True : valid, False : invalid
     */
    private boolean validDes() {
        String desMeal = mDesTI.getEditText().getText().toString().trim();
        if (desMeal.isEmpty()) {
            mDesTI.setError("Cannot be empty");
            return false;
        } else {
            mDesTI.setError(null);
            return true;
        }

    }


    /**
     * This method is called when user clicks on "Camera" for uploading the image
     * <p>
     * It simply opens another activity which is the camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    /**
     * If the user had taken a photo by the camera and then
     * he / she confirmed the inputted data, this method is called.
     * (After checking the validity of text fields)
     * <p>
     * First it checks if there is a picture chosen by user. (By checking that inImage is null or not)
     * <p>
     * After we store the EditTexAts' texts in Strings and create a Map object for storing these data
     * This object later would be written to the database
     * <p>
     * Also we make a file reference in Storage and write the image bites into that file
     * And upload it into the Storage, if it is successfully uploaded then we extract the Download url of that image
     * and store it into the map object we created earlier.
     * This object is now ready to be written in the meal collection of the database as a document.
     * name of this document is the name of the meal
     */
    public void uploadCameraPicture() {
        if (inImage != null) {
            final String nameMeal = mNameTI.getEditText().getText().toString().trim();
            String desMeal = mDesTI.getEditText().getText().toString().trim();
            String priceMeal = mPriceTI.getEditText().getText().toString().trim();

            final Map<String, Object> newMeal = new HashMap<>();
            newMeal.put("description", desMeal);
            newMeal.put("price", priceMeal);

            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + ".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = fileReference.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
                            System.out.println("download URL " + downloadUrl);
                            newMeal.put("url", downloadUrl.toString());

                            db.collection("Meals").document(nameMeal).set(newMeal)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mNameET.getText().clear();
                                            mPriceET.getText().clear();
                                            mDesET.getText().clear();

                                            loading.setVisibility(View.GONE);
                                            String[] example = {"Added to database"};
                                            fadingTextView.setTexts(example);
                                            fadingTextView.setTimeout(4000, TimeUnit.MILLISECONDS);
                                            fadingTextView.forceRefresh();

                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                    recreate();
                                                }
                                            }, 3000);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Error", e.getMessage());
                                        }
                                    });


                        }
                    });
                }
            });


        }
    }

    /**
     * If the user had picked an image from the device's gallery
     * and then he / she confirmed the inputted data, this method is called.
     * (After checking the validity of text fields)
     * <p>
     * First it checks if there is a picture chosen by user. (By checking that mImageUri is null or not)
     * <p>
     * After we store the EditTexts' texts in Strings and create a Map object for storing these data
     * This object later would be written to the database
     * <p>
     * Also we make a file reference in Storage and write the image bites into that file
     * And upload it into the Storage, if it is successfully uploaded then we extract the Download url of that image
     * and store it into the map object we created earlier.
     * This object is now ready to be written in the meal collection of the database as a document.
     * name of this document is the name of the meal
     */

    public void uploadGalleryPicture() {


        if (mImageUri != null) {
            final String nameMeal = mNameTI.getEditText().getText().toString().trim();
            String desMeal = mDesTI.getEditText().getText().toString().trim();
            String priceMeal = mPriceTI.getEditText().getText().toString().trim();

            final Map<String, Object> newMeal = new HashMap<>();
            newMeal.put("description", desMeal);
            newMeal.put("price", priceMeal);


            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
//                            Upload upload = new Upload("noname1",downloadUrl.toString());
                            newMeal.put("url", downloadUrl.toString());

                            db.collection("Meals").document(nameMeal).set(newMeal)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mNameET.getText().clear();
                                            mPriceET.getText().clear();
                                            mDesET.getText().clear();

                                            loading.setVisibility(View.GONE);
                                            String[] example = {"Added to database"};
                                            fadingTextView.setTexts(example);

                                            fadingTextView.setTimeout(4000, TimeUnit.MILLISECONDS);
                                            fadingTextView.forceRefresh();

                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                    recreate();
                                                }
                                            }, 3000);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Error", e.getMessage());
                                        }
                                    });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddingMeal.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            mIView.setImageURI(mImageUri);
            inImage = null;
        }


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            inImage = (Bitmap) extras.get("data");

            mIView.setImageBitmap(inImage);
            mImageUri = null;
        }
    }

}
