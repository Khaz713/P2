package oop2.p2.canteen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.view.Window;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;


/**
 * Class responsible for choosing menu based on manual input or gps location.
 */
public class ChooseMenuActivity extends AppCompatActivity {
    /**
     * Key for menuSelection variable.
     *
     * @see #menuSelection
     */
    private static final String KEY_MENU_SELECTION = "menu_selection";
    /**
     * Key for selectedUni variable.
     *
     * @see #selectedUni
     */
    private static final String KEY_UNI_SELECTION = "uni_selection";
    /**
     * Layout element used to to select the value for selectedUni variable.
     *
     * @see #selectedUni
     * @see <a href='https://developer.android.com/reference/android/widget/Spinner'>Spinner</a>
     */
    private Spinner spinner;
    /**
     * String used to store currently selected university.
     */
    private static String selectedUni = "Aalborg";
    /**
     * String used to store currently selected type of menu; Breakfast or Lunch.
     */
    private String menuSelection;
    /**
     * Object used to access system's location services.
     *
     * @see <a href='https://developer.android.com/reference/android/location/LocationManager'>LocationManager</a>
     */
    private LocationManager locationManager;
    /**
     * Object used to specify criteria used to access location.
     *
     * @see <a href='https://developer.android.com/reference/android/location/Criteria'>Criteria</a>
     */
    private Criteria criteria;
    /**
     * Object used to receive information from LocationManager when device's location changes.
     *
     * @see <a href='https://developer.android.com/reference/android/location/LocationManager'>LocationManager</a>
     * @see <a href='https://developer.android.com/reference/android/location/LocationListener'>LocationListener</a>
     */
    private LocationListener locationListener;
    /**
     * Variable used to store latitude of the device.
     */
    private double latitude;
    /**
     * Variable used to store longitude of the device.
     */
    private double longitude;


    /**
     * Overridden method from Activity class. Changed to also load ids of used layouts, create drawer, set criteria for gps, starts the LocationListener and fills in the spinner with available options.
     *
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#onCreate()">onCreate()</a>
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html">Activity</a>
     * @see #locationListener
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_menu);

        if (savedInstanceState != null) {
            menuSelection = savedInstanceState.getString(KEY_MENU_SELECTION);
        }


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


        //criteria of how gps will be used, saves battery life
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        //permission to use gps
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.d("Location changes", latitude + " " + longitude);


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //filling in the spinner
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_uni, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    /**
     * Method used after requesting to select the closest canteen. In case the app does not have a permission to access the gps service it will first ask for it.
     * In case the permission is granted the closest canteen to the device will be selected, as long as, the battery level is over 10%, otherwise error message will be shown.
     *
     * @param view from View class
     * @see <a href='https://developer.android.com/reference/android/view/View'>View</a>
     */
    public void gpsButton(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int MY_PERMISSION_ACCESS_COURSE_LOCATION = 0;
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COURSE_LOCATION);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                gpsButton(view);
            }
        } else {
            BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
            int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            if (batLevel > 10) {
                latitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
                longitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
                Log.d("Location changes", latitude + " " + longitude);
                locationManager.requestSingleUpdate(criteria, locationListener, null);

                //check nearest canteen
                double distanceCopenhagen = Math.sqrt(Math.pow(latitude - 55.650426, 2) + Math.pow(longitude - 12.543229, 2));
                double distanceAalborg = Math.sqrt(Math.pow(latitude - 57.014640, 2) + Math.pow(longitude - 9.982166, 2));
                double distanceEsbjerg = Math.sqrt(Math.pow(latitude - 55.491323, 2) + Math.pow(longitude - 8.446421, 2));

                if (distanceCopenhagen < distanceAalborg && distanceCopenhagen < distanceEsbjerg) {
                    spinner.setSelection(1);
                } else if (distanceAalborg < distanceCopenhagen && distanceAalborg < distanceEsbjerg) {
                    spinner.setSelection(0);
                } else {
                    spinner.setSelection(2);
                }

            } else {
                Toast.makeText(ChooseMenuActivity.this, R.string.battery_too_low, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Method used to start MenuActivity.
     *
     * @param view from View class
     * @see MenuActivity
     * @see <a href='https://developer.android.com/reference/android/view/View'>View</a>
     * @see <a href="https://developer.android.com/reference/android/content/Intent">Intent</a>
     */
    public void breakfastButton(View view) {
        menuSelection = "Breakfast";
        Intent intent = MenuActivity.newIntent(ChooseMenuActivity.this, menuSelection, selectedUni);
        startActivity(intent);
    }

    /**
     * Method used to start MenuActivity.
     *
     * @param view from View class
     * @see MenuActivity
     * @see <a href='https://developer.android.com/reference/android/view/View'>View</a>
     * @see <a href="https://developer.android.com/reference/android/content/Intent">Intent</a>
     */
    public void lunchButton(View view) {
        menuSelection = "Lunch";
        Intent intent = MenuActivity.newIntent(ChooseMenuActivity.this, menuSelection, selectedUni);
        startActivity(intent);
    }
    /**
     * Overridden method from Activity class. Changed to save the value of {@link #menuSelection} and {@link #selectedUni}.
     *
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#onSaveInstanceState()">onSaveInstanceState()</a>
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html">Activity</a>
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(KEY_MENU_SELECTION, menuSelection);
        savedInstanceState.putString(KEY_UNI_SELECTION, selectedUni);
    }
}
