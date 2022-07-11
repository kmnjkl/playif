package com.lkjuhkmnop.textquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.type.TimeOfDayOrBuilder;
import com.lkjuhkmnop.textquest.tools.Tools;

public class UserManagerActivity extends AppCompatActivity {
    TextView uEmail, uDisplayName;
    EditText newPass, repeatNewPass;
    Button changePassBtn, finishBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);

        uEmail = findViewById(R.id.user_email);
        uDisplayName = findViewById(R.id.user_display_name);
        newPass = findViewById(R.id.user_new_pass);
        repeatNewPass = findViewById(R.id.user_repeat_new_pass);
        changePassBtn = findViewById(R.id.user_change_pass_button);
        finishBtn = findViewById(R.id.user_finish_button);

//        Set user's Email and Display Name
        uEmail.setText(Tools.authTools().getUser().getEmail());
        uDisplayName.setText(Tools.authTools().getUser().getDisplayName());

//        Set on click listener for the "finish" button (to close the activity)
        finishBtn.setOnClickListener(v -> {
            finish();
        });

//        Password change
        changePassBtn.setOnClickListener(v -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(changePassBtn.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            changePassBtn.setClickable(false);
            if (newPass.getText().toString().isEmpty()) {
                Snackbar.make(v, "EMPTY  " + getText(R.string.user_enter_new_pass_warning), BaseTransientBottomBar.LENGTH_LONG).show();
                changePassBtn.setClickable(true);
            } else if (!newPass.getText().toString().equals(repeatNewPass.getText().toString())) {
                Snackbar.make(v, "NOT EQUAL  " + getText(R.string.user_enter_new_pass_warning), BaseTransientBottomBar.LENGTH_LONG).show();
                changePassBtn.setClickable(true);
            } else if (newPass.getText().length() < 6) {
                Snackbar.make(v, "6 CHARS. AT LEAST  " + getText(R.string.user_enter_new_pass_warning), BaseTransientBottomBar.LENGTH_LONG).show();
                changePassBtn.setClickable(true);
            } else {
                Tools.authTools().getUser().updatePassword(newPass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Snackbar.make(v, getText(R.string.user_pass_changed_successfully), BaseTransientBottomBar.LENGTH_SHORT).show();
                                    changePassBtn.setClickable(true);
                                } else {
                                    Snackbar.make(v, "ERROR OCCURRED: " + task.getException(), BaseTransientBottomBar.LENGTH_SHORT).show();
                                    Log.d("LKJD", "USER CHANGE PASSWORD: ERROR: " + task.getException());
                                }
                            }
                        });
            }
        });
    }
}