package oop2.p2.canteen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.Calendar;


/**
 * Class responsible for loading menu based on selected canteen.
 */
public class MenuActivity extends AppCompatActivity {
    /**
     * Key for menuSelection variable.
     *
     * @see #menuSelection
     */
    private static final String EXTRA_MENU_SELECTION = "oop2.p2.canteen.menu_selection";
    /**
     * Key for uniSelection variable.
     *
     * @see #uniSelection
     */
    private static final String EXTRA_UNI_SELECTION = "oop2.p2.canteen.uni_selection";
    /**
     * String used to store currently selected university.
     */
    private String menuSelection;
    /**
     * String used to store currently selected type of menu; Breakfast or Lunch.
     */
    private String uniSelection;
    /**
     * Integer used to store currently selected day.
     */
    private int day;
    /**
     * String array storing names of every working day of the week
     */
    private final String[] calendar = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    /**
     * String array storing all current menu items names.
     */
    private String[] menuList;
    /**
     * Adapter used to properly display list of items in menu.
     *
     * @see #listView
     */
    private ArrayAdapter adapter;
    /**
     * Contains instance of FirebaseFirestore.
     *
     * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/package-summary">FirebaseFirestore</a>
     */
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    /**
     * Layout element used to display the toolbar.
     *
     * @see <a href="https://developer.android.com/reference/android/support/v7/widget/Toolbar">Toolbar</a>
     */
    private Toolbar toolbar;
    /**
     * Layout element used to show a progress bar while loading a menu.
     *
     * @see <a href='https://developer.android.com/reference/android/widget/ProgressBar'>ProgressBar</a>
     */
    private ProgressBar loading;
    /**
     * Layout element used to display a list of items in current menu.
     *
     * @see <a href="https://developer.android.com/reference/android/widget/ListView'>ListView</a>
     */
    private ListView listView;
    /**
     * Layout element used to change current menu to Lunch version.
     *
     * @see <a href="https://developer.android.com/reference/android/widget/Button">Button</a>
     */
    private Button lunchButton;
    /**
     * Layout element used to change current menu to Breakfast version.
     *
     * @see <a href="https://developer.android.com/reference/android/widget/Button">Button</a>
     */
    private Button breakfastButton;
    /**
     * Layout element used to change day of menu to one day back.
     *
     * @see <a href="https://developer.android.com/reference/android/widget/ImageButton'>ImageButton</a>
     */
    private ImageButton leftButton;
    /**
     * Layout element used to change day of menu to one day back.
     *
     * @see <a href="https://developer.android.com/reference/android/widget/ImageButton'>ImageButton</a>
     */
    private ImageButton rightButton;
    /**
     * Layout element used to search the current menu for specific meal.
     *
     * @see <a href="https://developer.android.com/reference/android/widget/EditText">EditText</a>
     */
    private EditText searchBar;

    /**
     * Method used to create an intent to transfer data from ChooseMenuActivity.
     *
     * @param menuSelection {@link #menuSelection}
     * @param uniSelection  {@link #uniSelection}
     * @return the intent
     * @see ChooseMenuActivity
     * @see <a href="https://developer.android.com/reference/android/content/Intent">Intent</a>
     */
    public static Intent newIntent(Context packageContext, String menuSelection, String uniSelection) {
        Intent intent = new Intent(packageContext, MenuActivity.class);
        intent.putExtra(EXTRA_MENU_SELECTION, menuSelection);
        intent.putExtra(EXTRA_UNI_SELECTION, uniSelection);
        return intent;
    }

    /**
     * Overridden method from Activity class. Changed to also load ids of used layouts, create drawer, gets day from getDay() method and starts getMenu() method.
     *
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#onCreate()">onCreate()</a>
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html">Activity</a>
     * @see #getDay()
     * @see #getMenu()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        if (savedInstanceState != null) {
            menuSelection = savedInstanceState.getString("menu");
            uniSelection = savedInstanceState.getString("uni");
        } else {
            menuSelection = getIntent().getStringExtra(EXTRA_MENU_SELECTION);
            uniSelection = getIntent().getStringExtra(EXTRA_UNI_SELECTION);
        }

        toolbar = findViewById(R.id.toolbar);

        loading = findViewById(R.id.loading);

        lunchButton = findViewById(R.id.time_button);
        breakfastButton = findViewById(R.id.time_button2);

        leftButton = findViewById(R.id.back_button);
        rightButton = findViewById(R.id.forward_button);

        searchBar = findViewById(R.id.search);

        listView = findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("List clicked", listView.getItemAtPosition(position).toString());
                Intent intent = new Intent(getApplicationContext(), ShowingMeal.class);
                intent.putExtra("EXTRA_MNAME", listView.getItemAtPosition(position).toString());
                startActivity(intent);

            }
        });


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


        day = getDay();
        if (day == 0) {
            leftButton.setVisibility(View.GONE);
        }
        if (day == 4) {
            rightButton.setVisibility(View.GONE);
        }
        getMenu();
    }

    /**
     * Overridden method from Activity class. Changed to create a listener for {@link #searchBar}, because of error while changing an orientation of the device.
     *
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#onPostCreate()">onPostCreate()</a>
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html">Activity</a>
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Method used to get current day from Calendar, or in case if current day is a weekend it will return Monday.
     *
     * @return Day of the week from 0 to 4, 0 being Monday and 4 being Friday.
     */
    private int getDay() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 7 || day == 1) {
            day = 0;
        } else {
            day -= 2;
        }
        return day;
    }

    /**
     * Method used to get the menu from database based on currently selected university, day of the week and time of the day(Breakfast/Lunch).
     */
    private void getMenu() {
        loading.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        DocumentReference menuRef = db.collection("Menu").document(uniSelection).collection(calendar[day]).document(menuSelection);
        menuRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    int size = Integer.valueOf(document.get("size").toString());
                    menuList = new String[size];
                    for (int i = 0; i < size; i++) {
                        menuList[i] = document.get(String.valueOf(i + 1)).toString();
                    }
                    toolbar.setTitle(calendar[day]);
                    if (menuSelection.equals("Lunch")) {
                        lunchButton.setBackgroundColor(Color.parseColor("#3F9933"));
                        lunchButton.setEnabled(false);
                        breakfastButton.setBackgroundColor(Color.parseColor("#B0BBAF"));
                        breakfastButton.setEnabled(true);

                    } else {
                        lunchButton.setBackgroundColor(Color.parseColor("#B0BBAF"));
                        lunchButton.setEnabled(true);
                        breakfastButton.setEnabled(false);
                        breakfastButton.setBackgroundColor(Color.parseColor("#3F9933"));
                    }

                    loading.setVisibility(View.GONE);
                    adapter = new ArrayAdapter<>(MenuActivity.this, R.layout.activity_listview, menuList);
                    listView.setAdapter(adapter);
                    listView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Method used to change menu to Lunch version and call {@link #getMenu()} method.
     *
     * @param view from View class
     * @see <a href='https://developer.android.com/reference/android/view/View'>View</a>
     */
    public void lunchMealButton(View view) {
        menuSelection = "Lunch";
        getMenu();
    }

    /**
     * Method used to change menu to Breakfast version and call {@link #getMenu()} method.
     *
     * @param view from View class
     * @see <a href='https://developer.android.com/reference/android/view/View'>View</a>
     */
    public void breakfastMealButton(View view) {
        menuSelection = "Breakfast";
        getMenu();
    }

    /**
     * Method used to change day backwards and call {@link #getMenu()} method.
     *
     * @param view from View class
     * @see <a href='https://developer.android.com/reference/android/view/View'>View</a>
     */
    public void changeDayBack(View view) {
        day--;
        if (day == 0) {
            leftButton.setVisibility(View.GONE);
        }
        if (day == 3) {
            rightButton.setVisibility(View.VISIBLE);
        }
        getMenu();
    }

    /**
     * Method used to change day forward and call {@link #getMenu()} method.
     *
     * @param view from View class
     * @see <a href='https://developer.android.com/reference/android/view/View'>View</a>
     */
    public void changeDayForward(View view) {
        day++;
        if (day == 4) {
            rightButton.setVisibility(View.GONE);
        }
        if (day == 1) {
            leftButton.setVisibility(View.VISIBLE);
        }
        getMenu();
    }

    /**
     * Overridden method from Activity class. Changed to save the value of {@link #menuSelection} and {@link #uniSelection}.
     *
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#onSaveInstanceState()">onSaveInstanceState()</a>
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html">Activity</a>
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("menu", menuSelection);
        savedInstanceState.putString("uni", uniSelection);
    }
}
