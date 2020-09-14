package oop2.p2.canteen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static oop2.p2.canteen.ShowingMeal.mMealName;


/**
 * Class responsible for making a Breakfast or Lunch menu for a specific day at a specific AAU campus
 */
public class MakeMenuActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    FirebaseFirestore db;

    private Spinner spinner;
    private Spinner spinnerUni;
    private RadioGroup radioGroup;
    private RadioButton lunch, bfast;
    private Button confirm;

    private ProgressBar loading;

    final Handler handler = new Handler();


    int mSelectedItem = 0;
    MyRecyclerViewAdapter adapter;
    RecyclerView recyclerView;

    ArrayList<String> menuList = new ArrayList<>();
    final ArrayList<String> animalNames = new ArrayList<>();
    String selectedUni = "Aalborg";
    String selectedDay = "Monday";
    String selectedCategory = "Breakfast";

    Context context = this;

    /**
     * onCreate method is responsible for building the activity. All the views, initializations , parameters etc get created here
     *
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#onCreate()">onCreate()</a>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_menu);

        db = FirebaseFirestore.getInstance();


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //    databaseQuerying();
        Log.d("Method", "Before Q");
        db.collection("Meals").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("Success", "Success");
                    for (DocumentSnapshot document : task.getResult()) {
                        String mealName = document.getId();
                        //   System.out.println(mealName);
                        animalNames.add(mealName);
                        Log.d("Name", mealName);

                    }
                    Log.d("Success", "finish loop");

                }
                Log.d("Success", "finish IF");
                adapter = new MyRecyclerViewAdapter(getApplicationContext(), animalNames);
                adapter.setClickListener((MyRecyclerViewAdapter.ItemClickListener) context);
                recyclerView.setAdapter(adapter);
            }
        });
        Log.d("Method", "After Q");


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


        loading = (ProgressBar) findViewById(R.id.loading3);
        loading.setVisibility(View.GONE);

        confirm = findViewById(R.id.button_confirm_menu);
        confirm.setVisibility(View.VISIBLE);

        radioGroup = (RadioGroup) findViewById(R.id.radio_group1);

        lunch = (RadioButton) findViewById(R.id.radioButtonLunch);
        bfast = (RadioButton) findViewById(R.id.radioButtonBf);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonBf) {
                    selectedCategory = "Breakfast";
                }

                if (checkedId == R.id.radioButtonLunch) {
                    selectedCategory = "Lunch";
                }
            }
        });


        spinner = (Spinner) findViewById(R.id.days_spinner);
        ArrayAdapter<CharSequence> adapterS = ArrayAdapter.createFromResource(this, R.array.spinner_days, android.R.layout.simple_spinner_item);
        adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterS);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDay = parent.getSelectedItem().toString();
                Log.d("Spinner", selectedDay);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerUni = (Spinner) findViewById(R.id.uni_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_uni, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUni.setAdapter(adapter);

        spinnerUni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUni = parent.getSelectedItem().toString();
                Log.d("Spinner", selectedUni);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void databaseQuerying() {
        Log.d("Method", "Before Q");
        db.collection("Meals").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("Success", "Success");
                    for (DocumentSnapshot document : task.getResult()) {
                        String mealName = document.getId();
                        //   System.out.println(mealName);
                        Log.d("Name", mealName);

                    }
                    Log.d("Success", "finish loop");


                }
                Log.d("Success", "finish IF");
            }
        });
        Log.d("Method", "After Q");


    }

    /**
     * A method that is called when users click on an item of list. It would add or remove the item
     * from the menuList which is the list of names that would be sent to database at the end of this activity
     *
     * @param view     the view of the text that user has clicked on
     * @param position the position of that name in the list
     */
    @Override
    public void onItemClick(View view, int position) {
        // Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        System.out.println("You clicked " + adapter.getItem(position) + " on row number " + position);
        String nameOfItem = adapter.getItem(position);
        int k[] = view.getDrawableState();
        boolean toogled = false;
        for (int i : k) {
            if (i == android.R.attr.state_activated) {
                view.setActivated(false);
                menuList.remove(nameOfItem);
                toogled = true;
            }
        }
        if (!toogled) {
            view.setActivated(true);
            menuList.add(nameOfItem);
        }

    }

    /**
     * When user clicks on confirm button this method is called which makes a map object filled with the data of the menu
     * and then import it to the database. The process of writing to the database is similar to the AddingMeal Activity
     *
     * @param view
     */


    public void ConfirmMenu(View view) {
        loading.setVisibility(View.VISIBLE);
        confirm.setVisibility(View.INVISIBLE);
        String s = selectedUni;
        System.out.println("Confirmation");
        System.out.println("Category " + selectedCategory);
        System.out.println("Day : " + selectedDay);
        for (String i : menuList) {
            System.out.println("      " + i);
        }

        final Map<String, Object> newMenu = new HashMap<>();

        int cnt = 0;
        for (String i : menuList) {
            cnt++;
            newMenu.put(Integer.toString(cnt), i);
        }
        newMenu.put("size", cnt);

        db.collection("Menu").document(selectedUni).collection(selectedDay).document(selectedCategory).set(newMenu)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        loading.setVisibility(View.GONE);
                        Toast toast = Toast.makeText(MakeMenuActivity.this, "Added new menu", Toast.LENGTH_SHORT);


                        toast.show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                recreate();
                            }
                        }, 2000);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Error", e.getMessage());
                    }
                });


    }
}
