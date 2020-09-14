package oop2.p2.canteen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static android.os.Build.ID;


/**
 * This class/Activity is responsible for showing a single meal. it also shows the reviews for that meal and the average rate
 */
public class ShowingMeal extends AppCompatActivity {


    FirebaseFirestore db;


    public static String mMealName;
    public static int lastBox;
    public static boolean justAdded;

    private String mDescription;
    private String mPrice;
    private String mUrl;

    ImageView menuImg;
    TextView menuName;
    TextView menuPrice;
    TextView menuDes;
    ProgressBar loading;

    TextView menuRate;


    public static boolean add_review = false;


    /**
     * onCreate method is responsible for building the activity. All the views, initializations , parameters etc get created here
     *
     * @param savedInstanceState
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#onCreate()">onCreate()</a>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showing_meal);


        db = FirebaseFirestore.getInstance();

        mMealName = getIntent().getStringExtra("EXTRA_MNAME");

        menuImg = findViewById(R.id.menu_img);
        menuName = findViewById(R.id.menu_name);
        menuPrice = findViewById(R.id.menu_price);
        menuDes = findViewById(R.id.menu_des);
        menuRate = findViewById(R.id.menu_rate);
        loading = findViewById(R.id.progressBar);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(mMealName);

        //hamburger menu
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_item_menu);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.drawer_item_add_meal);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.drawer_item_make_menu);
        SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(5).withName(R.string.drawer_item_settings);
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName(R.string.drawer_item_sign_out);


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

        readSingleContact(mMealName);


        createReview();
        setUpReview();
    }


    private void setUpReview() {

        TextView writeReview = findViewById(R.id.click_write_review);
        writeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                justAdded = false;
                FragmentManager manager = getSupportFragmentManager();
                ReviewFragment dialog = new ReviewFragment();
                dialog.show(manager, "ReviewDialog");
                Log.i("TAG", "Just showed the dialog");

            }
        });
    }


    /**
     * Part of a OnCreate method which for simplicity has brought into a separate method,
     * <p>
     * Which is responsible for creating the review list by reading the reviews that already are stored in Database
     */

    public void createReview() {

        final ConstraintLayout constraintLayout = findViewById(R.id.cons_lay);
        final ConstraintSet set = new ConstraintSet();


        db.collection("Meals").document(mMealName).collection("Reviews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int cnt = 0;
                    float sumOfRates = 0;
                    int previousID = 0;
                    for (DocumentSnapshot document : task.getResult()) {


                        cnt++;
                        String review = document.getString("review");
                        String writer = document.getString("nickname");

                        System.out.println("Review : " + document.getString("review"));
                        System.out.println("ID : " + document.getId());
                        double rate = document.getDouble("rate");
                        TextView valueTV = new TextView(getApplicationContext());

                        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                        valueTV.setLayoutParams(params);
                        valueTV.setBackgroundResource(R.drawable.reviewtext_14);

                        sumOfRates += rate;

                        valueTV.setText(writer + "         Rate : " + rate + ": \n" + "        " + review);
                        valueTV.setTextSize(20);
                        valueTV.setPadding(25, 25, 0, 25);

                        int g_id = writer.hashCode();

                        //    int k_id = 122;
                        //    k_id += cnt;
                        valueTV.setId(g_id);

                        System.out.println("g_id " + g_id);
                        constraintLayout.addView(valueTV, 0);
                        set.clone(constraintLayout);

                        if (cnt == 1) {
                            set.connect(valueTV.getId(), ConstraintSet.TOP, R.id.menu_des, ConstraintSet.BOTTOM, 60);
                        } else {
                            set.connect(valueTV.getId(), ConstraintSet.TOP, previousID, ConstraintSet.BOTTOM, 60);
                            lastBox = valueTV.getId();
                        }
                        set.applyTo(constraintLayout);
                        previousID = valueTV.getId();

                    }

                    sumOfRates /= cnt;

                    sumOfRates = (float) (Math.round(sumOfRates * 10.0) / 10.0);
                    System.out.println("Average rate is " + sumOfRates);
                    menuRate.setText(String.valueOf(sumOfRates));
                }
            }
        });


    }

    /**
     * This method is called at the last step of onCreate methid
     *
     * @param mealName
     */

    private void readSingleContact(String mealName) {
        DocumentReference contact = db.collection("Meals").document(mealName);
        contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    mDescription = doc.getString("description");
                    mPrice = doc.getString("price");
                    mUrl = doc.getString("url");

                    menuName.setText(mMealName);
                    menuPrice.setText(mPrice);
                    menuDes.setText(mDescription);

                    ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                    Log.d("Image loading", LoginActivity.settingsWifi + " " + mWifi.isConnected());
                    if (LoginActivity.settingsWifi && !mWifi.isConnected()) {
                        Picasso.get().load(R.drawable.wifi).into(menuImg);
                        loading.setVisibility(View.INVISIBLE);
                    } else {
                        Picasso.get().load(mUrl).into(menuImg);
                    }

                }
            }
        });
    }
}
