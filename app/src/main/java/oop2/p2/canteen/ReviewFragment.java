package oop2.p2.canteen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;


import static androidx.constraintlayout.widget.Constraints.TAG;
import static oop2.p2.canteen.ShowingMeal.justAdded;
import static oop2.p2.canteen.ShowingMeal.mMealName;

/**
 * The fragment that pops up after the user clicks on "write review" in ShowingMeal activity
 * It is called a dialog window because it is floating on top of the activity
 *
 * @see <a href="https://developer.android.com/reference/android/app/Fragment">Fragment</a>
 */

public class ReviewFragment extends AppCompatDialogFragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RatingBar ratingBar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.addreview_layout, null);


        // Create button listener
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;

                    case DialogInterface.BUTTON_NEUTRAL:
                        EditText review = getDialog().findViewById(R.id.search);
                        String typedReview = review.getText().toString();
                        ratingBar = getDialog().findViewById(R.id.ratingBar);
                        String rate = String.valueOf(ratingBar.getRating());

                        //typedReview += " Rate : " + rate;
                        //    String activeUserName = LoginActivity.user.getUid().toString();

                        createReviewBox(LoginActivity.nickname, typedReview, ratingBar.getRating());

                        break;

                }
                Log.i(TAG, "You Clicked the dialog button");
                justAdded = true;
                // getActivity().recreate();
            }
        };
        // Build the dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle("")
                .setView(v)
                .setNeutralButton(R.string.ar, listener)
                .setNegativeButton(android.R.string.cancel, listener)
                .create();


    }

    /**
     * When the dialog is destroying (due to user closing it)
     * We call recreate method of the activity so the added review could be shown in the new ( updated )list
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "Dialog destroyed");
        getActivity().recreate();

    }

    /**
     * The method is called when user clicks on "Post review" after finishing his / her review.
     * here the review together with the name of the user and the rate they gave to the meal would be written to the database in a shape of an
     * Map object.
     * <p>
     * Pretty similar to the other database writing processes
     *
     * @param userName
     * @param typed_review
     * @param rateValue
     */

    public void createReviewBox(final String userName, final String typed_review, float rateValue) {


        // It checks if the review already exists in the dataset. we did it only for testing purposes and it wont have effect on the app
        db.collection("Meals").document(mMealName).collection("Reviews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean been = false;
                    float sumOfRates = 0;
                    int previousID = 0;

                    for (DocumentSnapshot document : task.getResult()) {


                        String review = document.getString("review");
                        String writer = document.getId();

                        if (writer.equals(userName)) {
                            System.out.println("It has been there");
                            been = true;
                            break;
                        }

                    }
                    if (!been) {
                        System.out.println("It is new ! ");
                    }
                }
            }
        });


        final Map<String, Object> newReview = new HashMap<>();
        newReview.put("review", typed_review);
        newReview.put("rate", rateValue);
        newReview.put("nickname", userName);

        db.collection("Meals").document(mMealName).collection("Reviews").document(LoginActivity.user.getUid()).set(newReview)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        System.out.println("Added new Review");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Error", e.getMessage());
                    }
                });


        justAdded = true;

    }
}
