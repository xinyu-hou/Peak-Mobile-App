package edu.northeastern.numad22fa_team15.activities.peakActivities.piggySavings;

import static edu.northeastern.numad22fa_team15.utils.CommonUtils.closeKeyboard;
import static edu.northeastern.numad22fa_team15.utils.CommonUtils.displayMessageInSnackbar;
import static edu.northeastern.numad22fa_team15.utils.CommonUtils.nullOrEmptyInputChecker;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.TimeUnit;

import edu.northeastern.numad22fa_team15.R;
import edu.northeastern.numad22fa_team15.activities.peakActivities.addTransaction.AddTransactionActivity;
import edu.northeastern.numad22fa_team15.activities.peakActivities.graph.GraphActivity;
import edu.northeastern.numad22fa_team15.activities.peakActivities.homePage.PeakHomePage;
import edu.northeastern.numad22fa_team15.activities.peakActivities.profilePage.ProfileActivity;
import edu.northeastern.numad22fa_team15.models.databaseModels.SavingModel;
import edu.northeastern.numad22fa_team15.utils.DBHelper;
import edu.northeastern.numad22fa_team15.utils.IDBHelper;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Angle;
import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.Spread;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;
import static nl.dionsegijn.konfetti.core.Position.Relative;

public class SavingsActivity extends AppCompatActivity {

    private static final String TAG = "SavingsActivity___";

    private NavigationBarView navigationBarView;
    private EditText savingGoal_et;
    private EditText goalDescription_et;
    private Button confirmGoal_btn;
    private TextView goal_tv;
    private TextView goalDescription_tv;
    private TextView savedAmount_tv;
    private TextView remainingAmount_tv;
    private IDBHelper dbHelper;

    private KonfettiView konfettiView = null;
    private Shape.DrawableShape drawableShape = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peak_piggybank);

        dbHelper = new DBHelper(SavingsActivity.this);

        goal_tv = findViewById(R.id.tv_goal_amount);
        goalDescription_tv = findViewById(R.id.tv_goal_description);
        savedAmount_tv = findViewById(R.id.tv_saved_amount);
        remainingAmount_tv = findViewById(R.id.tv_remaining_amount);

        final Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_heart);
        drawableShape = new Shape.DrawableShape(drawable, true);
        konfettiView = findViewById(R.id.konfettiView);
        EmitterConfig emitterConfig = new Emitter(5L, TimeUnit.SECONDS).perSecond(50);

        // show current saving info
        SavingModel saving = dbHelper.retrieveLatestSavingTableSaving();
        goal_tv.setText("$ " + saving.getSavingGoal());
        goalDescription_tv.setText("Goal: " + saving.getGoalDescription());
        savedAmount_tv.setText("$ " + saving.getSavingSoFar());
        float remainingAmount = saving.getSavingGoal() - saving.getSavingSoFar();
        remainingAmount_tv.setText("$ " + remainingAmount);

        // if reach saving goal, play animation
        if (saving.getSavingSoFar() >= saving.getSavingGoal() && saving.getSavingGoal() != 0) {
            // TODO: goal resets everytime app is reopened
            explode();
            float remainingSaving = saving.getSavingSoFar() - saving.getSavingGoal();
            boolean resetSaving = dbHelper.resetSavingTableSaving(remainingSaving);
            String resetMessage = "Failed to reset saving";
            if (resetSaving) {
                resetMessage = "Successfully reset saving";
            }
            Log.d(TAG, resetMessage);
        }

        // set up navigation bar
        navigationBarView = findViewById(R.id.bottom_navigation_id);
        navigationBarView.setSelectedItemId(R.id.nav_savings);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), PeakHomePage.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_graph:
                        startActivity(new Intent(getApplicationContext(), GraphActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_savings:
                        startActivity(new Intent(getApplicationContext(), SavingsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    public void explode() {
        EmitterConfig emitterConfig = new Emitter(100L, TimeUnit.MILLISECONDS).max(100);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .spread(360)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE, drawableShape))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(0f, 30f)
                        .position(new Relative(0.5, 0.3))
                        .build()
        );
    }

    // Bottom Navigation Bar -- add transaction
    public void addTransactionFAB(View view) {
        Log.v(TAG, "Trying to add a new transaction");
        Intent intent = new Intent(SavingsActivity.this, AddTransactionActivity.class);
        startActivity(intent);
        finish();
    }

    public void editGoalDialog(View view) {
        Log.d(TAG, "Edit piggy bank goal button was clicked");

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_edit_piggybank_goal, null);
        b.setView(dialogView);
        AlertDialog alert = b.create();
        savingGoal_et = dialogView.findViewById(R.id.et_edit_goal_amount);
        goalDescription_et = dialogView.findViewById(R.id.et_edit_goal_description);
        SavingModel saving = dbHelper.retrieveLatestSavingTableSaving();
        savingGoal_et.setHint("" + saving.getSavingGoal());
        goalDescription_et.setHint(saving.getGoalDescription());
        confirmGoal_btn = dialogView.findViewById(R.id.btn_confirm_edit_goal);
        confirmGoal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard(view.getContext(), view);
                String newGoal_str = savingGoal_et.getText().toString();
                String newGoalDescription = goalDescription_et.getText().toString();
                if (nullOrEmptyInputChecker(newGoal_str, newGoalDescription)) {
                    String message = "All fields are required.";
                    displayMessageInSnackbar(view, message, Snackbar.LENGTH_SHORT);
                    return;
                }
                int newGoal = Integer.parseInt(newGoal_str);
                boolean updateSaving = dbHelper.updateLatestSavingTableSaving(newGoal, newGoalDescription);

                String savingMessage = "Fail to update saving goal";
                if (updateSaving) {
                    savingMessage = "Successfully updated saving goal";
                    alert.dismiss();
                }
                Toast.makeText(getApplicationContext(), savingMessage, Toast.LENGTH_SHORT).show();

                // show current saving info
                SavingModel saving = dbHelper.retrieveLatestSavingTableSaving();
                goal_tv.setText("$ " + saving.getSavingGoal());
                goalDescription_tv.setText("Goal: " + saving.getGoalDescription());
                savedAmount_tv.setText("$ " + saving.getSavingSoFar());
                float remainingAmount = saving.getSavingGoal() - saving.getSavingSoFar();
                remainingAmount_tv.setText("$ " + remainingAmount);
            }
        });
        alert.show();
    }

}