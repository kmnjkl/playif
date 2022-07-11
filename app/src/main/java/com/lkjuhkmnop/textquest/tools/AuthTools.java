package com.lkjuhkmnop.textquest.tools;

import com.google.firebase.auth.FirebaseUser;

public class AuthTools {
    private FirebaseUser user;
    public FirebaseUser getUser() {
        return user;
    }
    public FirebaseUser setUser(FirebaseUser user) {
        this.user = user;
        return user;
    }
}
