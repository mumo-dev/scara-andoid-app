package com.example.mumo.scara;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mumo.scara.adapter.FirestorePagingQuetionAdapter;
import com.example.mumo.scara.model.Question;
import com.example.mumo.scara.viewmodel.MainActivityViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Collections;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = MainActivity.class.getSimpleName();
    private MainActivityViewModel mViewModel;

    private FirebaseFirestore mFirestore;

    private RecyclerView mQuestionRecyclerView;
    private TextView mErrorTextView;
    private ImageButton mRetryButton;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AskActivity.class);
                Log.i(TAG, "This button was clicked");
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        mFirestore = FirebaseFirestore.getInstance();

        final Query query = mFirestore.collection("questions")
                .orderBy("createdAt", Query.Direction.DESCENDING)
               .limit(50);

        final PagedList.Config  config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        final FirestorePagingOptions<Question> options = new FirestorePagingOptions.Builder <Question>()
                .setLifecycleOwner(this)
                .setQuery(query,config, Question.class)
                .build();

        mQuestionRecyclerView = findViewById(R.id.question_recylerview);
        mErrorTextView = findViewById(R.id.tv_error_loading);
        mLoadingIndicator = findViewById(R.id.pg_loading_indicator);
        mRetryButton = findViewById(R.id.retry_button);

        mQuestionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        final FirestorePagingQuetionAdapter adapter = new FirestorePagingQuetionAdapter(options, this){
            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                        // The initial load has begun
                        // ...
                        mLoadingIndicator.setVisibility(View.VISIBLE);
                        mErrorTextView.setVisibility(View.GONE);
                        mRetryButton.setVisibility(View.GONE);
                        mQuestionRecyclerView.setVisibility(View.GONE);
                    case LOADING_MORE:
                        // The adapter has started to load an additional page
                        // ...
                        mLoadingIndicator.setVisibility(View.VISIBLE);
                        mErrorTextView.setVisibility(View.GONE);
                        mRetryButton.setVisibility(View.GONE);
                        mQuestionRecyclerView.setVisibility(View.VISIBLE);
                    case LOADED:
                        // The previous load (either initial or additional) completed
                        // ...
                        mLoadingIndicator.setVisibility(View.GONE);
                        mQuestionRecyclerView.setVisibility(View.VISIBLE);
                        mLoadingIndicator.setVisibility(View.GONE);
                        mErrorTextView.setVisibility(View.GONE);
                        mRetryButton.setVisibility(View.GONE);

                    case ERROR:
                        // The previous load (either initial or additional) failed. Call
                        // the retry() method in order to retry the load operation.
                        // ...
//                        mLoadingIndicator.setVisibility(View.GONE);
//                        mErrorTextView.setVisibility(View.VISIBLE);
//                        mRetryButton.setVisibility(View.VISIBLE);
//                        mQuestionRecyclerView.setVisibility(View.VISIBLE);
//                        this.retry();

                }
            }
        };
        mQuestionRecyclerView.setAdapter(adapter);

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }
    }

    private boolean shouldStartSignIn() {
        return (!mViewModel.isSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    private void startSignIn() {
        //sign in with firebase ui
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(true)
                .build();
        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setSigningIn(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mViewModel.setSigningIn(false);
            if (resultCode != RESULT_OK && shouldStartSignIn()) {
                startSignIn();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.
                START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_logout:
                AuthUI.getInstance().signOut(this);
                startSignIn();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
