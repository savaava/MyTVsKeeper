package com.savaava.mytvskeeper.services;

public interface AccessManager {
    boolean signIn(String email, String password);
    boolean signInOAuth(AccessProvider provider);
    boolean signOut();
    boolean isAuthenticated();
}
