package oop2.p2.canteen;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static oop2.p2.canteen.LoginActivity.mAuth;

/**
 * Class responsible for creating new accounts.
 */
public class RegisterActivity extends AppCompatActivity {
    /**
     * Layout element used to enter a nickname for new account.
     *
     * @see <a href="https://developer.android.com/reference/android/support/design/widget/TextInputLayout">TextInputLayout</a>
     */
    private TextInputLayout mNicknameText;
    /**
     * Layout element used to enter an email address for new account.
     *
     * @see <a href="https://developer.android.com/reference/android/support/design/widget/TextInputLayout">TextInputLayout</a>
     */
    private TextInputLayout mEmailText;
    /**
     * Layout element used to enter a password for new account.
     *
     * @see <a href="https://developer.android.com/reference/android/support/design/widget/TextInputLayout">TextInputLayout</a>
     */
    private TextInputLayout mPasswordText;
    /**
     * Contains instance of FirebaseFirestore.
     *
     * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/package-summary">FirebaseFirestore</a>
     */
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Overridden method from Activity class. Changed to also load ids of used layouts.
     *
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#onCreate()">onCreate()</a>
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html">Activity</a>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();
        db.setFirestoreSettings(settings);

        mNicknameText = findViewById(R.id.nickname_layout);

        mEmailText = findViewById(R.id.email_layout);

        mPasswordText = findViewById(R.id.password_layout);

    }

    /**
     * Method used to check if every field is filled in and is filled in correctly.
     * If it is it will create new user and create new entry for it in database. If any of the fields is incorrectly filled in it will show an error message in those fields.
     *
     * @param view from View class.
     * @see <a href='https://developer.android.com/reference/android/view/View'>View</a>
     */
    public void signUp(View view) {

        final String nickname = mNicknameText.getEditText().getText().toString().trim();
        mNicknameText.setError(null);
        String email = mEmailText.getEditText().getText().toString().trim();
        mEmailText.setError(null);
        String password = mPasswordText.getEditText().getText().toString().trim();
        mPasswordText.setError(null);

        if (nickname.isEmpty() || email.isEmpty() || password.isEmpty() ||
                !email.matches("^[a-z0-9]+@+[a-z0-9]+.aau.dk$") ||
                !password.matches("^(?=.*[0-9]+.*)(?=.*[a-zA-Z]+.*)[0-9a-zA-Z]{6,}$")) {
            Log.w("Sign up", "Empty fields");
            if (nickname.isEmpty()) {
                mNicknameText.setError(getString(R.string.nickname_empty));
            }
            if (email.isEmpty()) {
                mEmailText.setError(getString(R.string.email_empty));
            }else if(!email.matches("^[a-z0-9]+@+[a-z0-9]+.aau.dk$")){
                Log.w("Sign up", "Wrong email domain");
                mEmailText.setError(getString(R.string.incorrect_email));
            }
            if (password.isEmpty()) {
                mPasswordText.setError(getString(R.string.password_empty));
            } else if (!password.matches("^(?=.*[0-9]+.*)(?=.*[a-zA-Z]+.*)[0-9a-zA-Z]{6,}$")) {
                Log.w("Sign up", "Password doesn't match specifications");
                mPasswordText.setError(getString(R.string.password_wrong));
            }
        } else {

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("Sign up", "Account created");
                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Sign up", "Email sent");
                                }
                            }
                        });

                        Map<String, Object> newUser = new HashMap<>();
                        newUser.put("nickname", nickname);
                        newUser.put("isAdmin", false);

                        db.collection("Users").document(mAuth.getCurrentUser().getUid()).set(newUser);

                        Toast.makeText(RegisterActivity.this, "Account created! \n Please confirm your email", Toast.LENGTH_LONG).show();
                        final Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3500);
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } catch (Exception ignored) {

                                }
                            }
                        };
                        thread.start();
                    } else {
                        Log.w("Sing up", "Create user failure: ", task.getException());
                    }

                }
            });
        }
    }
}
