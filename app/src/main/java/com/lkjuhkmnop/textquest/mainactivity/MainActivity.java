package com.lkjuhkmnop.textquest.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lkjuhkmnop.textquest.R;
import com.lkjuhkmnop.textquest.tools.Tools;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView playBtn, addBtn, libBtn, tqLibBtn;
    private Button authBtn, signoutBtn;
    private TextView userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_TextQuest);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playBtn = findViewById(R.id.play_btn);
        addBtn = findViewById(R.id.add_btn);
        libBtn = findViewById(R.id.lib_btn);
        authBtn = findViewById(R.id.auth_button);
        userInfo = findViewById(R.id.user_info);
        signoutBtn = findViewById(R.id.auth_signout_button);
        tqLibBtn = findViewById(R.id.tqlib_button);

        playBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
        libBtn.setOnClickListener(this);
        tqLibBtn.setOnClickListener(this);

        setAuthBtnSignin();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == playBtn.getId()) {
            Tools.startGamesActivity(this, v);
        } else if (v.getId() == addBtn.getId()) {
            Tools.startQuestManageActivityToAddQuest(this, v);
        } else if (v.getId() == libBtn.getId()) {
            Tools.startLibraryActivity(this, v);
        } else if (v.getId() == tqLibBtn.getId()) {
            Tools.startTQLibActivity(this, v);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Tools.AUTH_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                onSignin();
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                if (response != null) {
                    Log.d("AUTH", response.getError().getMessage());
                    userInfo.setText("ERROR");
                }
            }
        }
    }

    private void setAuthBtnSignin() {
        authBtn.setText(R.string.auth_signin_text);
        authBtn.setOnClickListener(v -> {
            List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    Tools.AUTH_REQUEST_CODE);
        });
    }

    private void setAuthBtnAccountManager() {
        authBtn.setText(R.string.auth_account_settings);
        authBtn.setOnClickListener(v -> {
            Tools.startUserManagerActivity(this, authBtn);
        });
    }

    private void onSignin() {
        displaySignedin();
        Tools.cloudManager().checkUserInUsersCollection();

        signoutBtn.setVisibility(View.VISIBLE);
        signoutBtn.setOnClickListener(v -> {
            onSignout();
        });
    }

    private void displaySignedin() {
        FirebaseUser user = Tools.authTools().setUser(FirebaseAuth.getInstance().getCurrentUser());
        String userInfoText = "Uid: " + user.getUid() + "; Prov.id: " + user.getProviderId()
                + "\nEmail: " + user.getEmail() + "; Display name: " + user.getDisplayName();
        Log.d("AUTH", userInfoText);
        userInfo.setText(userInfoText);

        setAuthBtnAccountManager();
    }

    private void onSignout() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                displayNotSignedin();
            }
        });
    }

    private void displayNotSignedin() {
        setAuthBtnSignin();
        signoutBtn.setVisibility(View.INVISIBLE);
        userInfo.setText("User info");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}