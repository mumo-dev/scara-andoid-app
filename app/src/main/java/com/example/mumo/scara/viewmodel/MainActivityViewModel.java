package com.example.mumo.scara.viewmodel;

import android.arch.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    private boolean isSigningIn;

    public MainActivityViewModel(){
        isSigningIn = false;
    }

    public boolean isSigningIn() {
        return isSigningIn;
    }

    public void setSigningIn(boolean signingIn) {
        isSigningIn = signingIn;
    }
}
