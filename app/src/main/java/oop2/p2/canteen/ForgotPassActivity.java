package oop2.p2.canteen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Class responsible for sending request to reset password.
 */
public class ForgotPassActivity extends AppCompatActivity {
    /**
     * Layout element used to enter an email address for password reset.
     *
     * @see <a href="https://developer.android.com/reference/android/support/design/widget/TextInputLayout">TextInputLayout</a>
     */
    private TextInputLayout mEmail;
    /**
     * Contains instance of FirebaseAuth class.
     *
     * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth">FirebaseAuth</a>
     */
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    /**
     * Overridden method from Activity class. Changed to also load ids of used layouts.
     *
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#onCreate()">onCreate()</a>
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html">Activity</a>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        mEmail = findViewById(R.id.email_layout);
    }


    /**
     * Method used to check if the email is entered into the field and to send a request for password reset for provided email.
     * In case entered email is incorrect an error message will show up, otherwise password reset email will be sent.
     *
     * @param view from View class.
     * @see <a href='https://developer.android.com/reference/android/view/View'>View</a>
     */
    public void forgotPass(View view) {
        String email = mEmail.getEditText().getText().toString().trim();
        mEmail.setError(null);
        if (email.isEmpty()) {
            mEmail.setError(getString(R.string.email_empty));
        } else {
            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), R.string.password_email_sent, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ForgotPassActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        mEmail.setError(getString(R.string.password_email_error));
                    }
                }
            });
        }
    }
}
