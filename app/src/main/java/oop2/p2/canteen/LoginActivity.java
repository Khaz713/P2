package oop2.p2.canteen;


import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Starting class of the app, responsible for logging users into the app and redirecting them to password reset and creating new account.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Contains instance of FirebaseAuth class.
     *
     * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth">FirebaseAuth</a>
     */
    public static FirebaseAuth mAuth;
    /**
     * Contains information on currently logged user based on FirebaseUser class.
     *
     * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser">FirebaseUser</a>
     */
    public static FirebaseUser user;
    /**
     * Boolean used to store if current user is an app administrator.
     */
    public static boolean isAdmin;
    /**
     * String used to store current user's nickname.
     */
    public static String nickname;
    /**
     * Boolean used to store the current setting of WiFi required to download images.
     */
    public static boolean settingsWifi;
    /**
     * Used to access shared preferences in the app.
     *
     * @see <a href="https://developer.android.com/reference/android/content/SharedPreferences.html">SharedPreferences</a>
     */
    private SharedPreferences sharedPreferences;
    /**
     * Layout element used to enter an email address for signing in.
     *
     * @see <a href="https://developer.android.com/reference/android/support/design/widget/TextInputLayout">TextInputLayout</a>
     */
    private TextInputLayout mEmailLayout;
    /**
     * Layout element used to enter a password for signing in.
     *
     * @see <a href="https://developer.android.com/reference/android/support/design/widget/TextInputLayout">TextInputLayout</a>
     */
    private TextInputLayout mPasswordLayout;
    /**
     * Layout element used to show an error in case of error during signing in.
     *
     * @see <a href="https://developer.android.com/reference/android/widget/TextView">TextView</a>
     */
    private TextView mSignInError;
    /**
     * Layout element used to show a progress bar while signing in.
     *
     * @see <a href='https://developer.android.com/reference/android/widget/ProgressBar'>ProgressBar</a>
     */
    private ProgressBar loading;
    /**
     * Layout element used to confirming email and password and signing in.
     *
     * @see <a href="https://developer.android.com/reference/android/widget/Button">Button</a>
     */
    private Button mSignIn;


    /**
     * Overridden method from Activity class. Its function is to update values of mAuth and user objects and start updateUI method afterwards.
     *
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#onStart()">onStart()</a>
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html">Activity</a>
     * @see #updateUI(FirebaseUser)
     * @see #user
     * @see #mAuth
     */
    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        updateUI(user);


    }

    /**
     * Overridden method from Activity class to disable an option to go back to previous activity by pressing back key.
     *
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html">Activity</a>
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#onBackPressed()">onBackPressed()</a>
     */
    @Override
    public void onBackPressed() {
        return;
    }


    /**
     * Overridden method from Activity class. Changed to also load ids of used layouts.
     *
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#onCreate()">onCreate()</a>
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html">Activity</a>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loading = findViewById(R.id.progressBar);
        loading.setVisibility(View.GONE);

        mSignInError = findViewById(R.id.sign_in_error);

        mEmailLayout = findViewById(R.id.email_layout);

        mPasswordLayout = findViewById(R.id.password_layout);

        mSignIn = findViewById(R.id.sing_in_button);
    }


    /**
     * Method used to update information about current user to sharedPreferences and start ChooseMenuActivity but only if any user is signed in.
     *
     * @param user {@link #user}
     * @see #sharedPreferences
     * @see <a href="https://developer.android.com/reference/android/content/Intent">Intent</a>
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            sharedPreferences = LoginActivity.this.getSharedPreferences("Pref", Context.MODE_PRIVATE);
            isAdmin = sharedPreferences.getBoolean(getString(R.string.is_admin), false);
            nickname = sharedPreferences.getString(getString(R.string.nickname), " ");
            settingsWifi = sharedPreferences.getBoolean(getString(R.string.settings_wifi), false);
            Intent intent = new Intent(LoginActivity.this, ChooseMenuActivity.class);
            startActivity(intent);
            //go to next activity
        }
    }


    /**
     * Method used after {@link #mSignIn} button is pressed. Its function is to check if both text fields are filled in and tries to sign in using this data.
     * If entered data is correct user will be signed in, if not error message will show up.
     *
     * @param view from View class
     * @see <a href='https://developer.android.com/reference/android/view/View'>View</a>
     */
    public void signIn(View view) {
        String email = mEmailLayout.getEditText().getText().toString();
        String password = mPasswordLayout.getEditText().getText().toString();
        mEmailLayout.setError(null);
        mPasswordLayout.setError(null);
        mSignInError.setText(null);
        if (!email.isEmpty() && !password.isEmpty()) {
            loading.setVisibility(View.VISIBLE);
            mSignIn.setVisibility(View.INVISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        user = mAuth.getCurrentUser();
                        Log.d("Sign in", "Sign in successful, email verified: " + user.isEmailVerified());
                        if (user.isEmailVerified()) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot document = task.getResult();
                                    isAdmin = (Boolean) document.get("isAdmin");
                                    nickname = (String) document.get("nickname");
                                    sharedPreferences = LoginActivity.this.getSharedPreferences("Pref", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(getString(R.string.is_admin), isAdmin);
                                    editor.putString(getString(R.string.nickname), nickname);
                                    editor.apply();
                                    loading.setVisibility(View.GONE);
                                    updateUI(user);


                                }
                            });

                        } else {
                            loading.setVisibility(View.GONE);
                            mSignIn.setVisibility(View.VISIBLE);
                            mSignInError.setText(R.string.email_not_verified);
                        }
                    } else {
                        loading.setVisibility(View.GONE);
                        mSignIn.setVisibility(View.VISIBLE);
                        Log.w("Sign in", "Sign in failure", task.getException());
                        mSignInError.setText(R.string.sign_in_error);
                    }
                }
            });
        } else {
            if (email.isEmpty()) {
                mEmailLayout.setError(getString(R.string.email_empty));
            }
            if (password.isEmpty()) {
                mPasswordLayout.setError(getString(R.string.password_empty));
            }
        }
    }

    /**
     * Method used to start RegisterActivity.
     *
     * @param view from View class
     * @see RegisterActivity
     * @see <a href='https://developer.android.com/reference/android/view/View'>View</a>
     * @see <a href="https://developer.android.com/reference/android/content/Intent">Intent</a>
     */
    public void signUp(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Method used to start ForgotPassActivity.
     *
     * @param view from View class
     * @see ForgotPassActivity
     * @see <a href='https://developer.android.com/reference/android/view/View'>View</a>
     * @see <a href="https://developer.android.com/reference/android/content/Intent">Intent</a>
     */
    public void passForgot(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgotPassActivity.class);
        startActivity(intent);
    }
}
