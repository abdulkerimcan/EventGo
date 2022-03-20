package com.furkankerim.eventgo.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.furkankerim.eventgo.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mdrawerLayout;
    private androidx.appcompat.widget.Toolbar mtoolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNav;
    private BottomNavigationView mBottomNav;
    private NavController navController;
    private FloatingActionButton fbtnAdd;

    private TextView twSign, navBarUsername;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;
    private Dialog dialog;
    private ImageView btnExitYes, btnExitNo, userImg;

    private void init() {
        mdrawerLayout = findViewById(R.id.drawer);
        mtoolbar = findViewById(R.id.main_activity_toolbar);
        twSign = findViewById(R.id.tvSign);
        setSupportActionBar(mtoolbar);
        mNav = findViewById(R.id.main_Navigation);
        mBottomNav = findViewById(R.id.bottom_nav);
        fbtnAdd = findViewById(R.id.fab);
        userImg = findViewById(R.id.userImg);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();

        mDrawerToggle = new ActionBarDrawerToggle(this, mdrawerLayout, mtoolbar, R.string.nav_open, R.string.nav_close);
        mdrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //navigationUI
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph())
                        .setOpenableLayout(mdrawerLayout)
                        .build();
        //NavigationUI.setupWithNavController(mNav, navController);
        NavigationUI.setupWithNavController(mBottomNav, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, mdrawerLayout);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.search || destination.getId() == R.id.favorites || destination.getId() == R.id.chat
                        || destination.getId() == R.id.concerts || destination.getId() == R.id.theater || destination.getId() == R.id.museum
                        || destination.getId() == R.id.settings || destination.getId() == R.id.mapsFragment || destination.getId() == R.id.myTicketsFragment
                || destination.getId() == R.id.myEventsFragment ) {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
                }
            }
        });

        fbtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UploadActivity.class));
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        fbtnAdd.setVisibility(View.GONE);
        if (mUser != null) {
            mFirestore.collection("User").document(mUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(MainActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Map<String, Object> data1 = value.getData();
                    boolean isOrganize = (boolean) data1.get("organizer");
                    if (isOrganize) {
                        fbtnAdd.setVisibility(View.VISIBLE);
                    } else {
                        Menu nav_Menu = mNav.getMenu();
                        nav_Menu.findItem(R.id.myEventsFragment).setVisible(false);
                        fbtnAdd.setVisibility(View.GONE);
                    }

                }
            });
        }
        mNav.setNavigationItemSelectedListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() == null) {
            twSign.setVisibility(View.VISIBLE);
            Menu nav_Menu = mNav.getMenu();
            nav_Menu.findItem(R.id.exit).setVisible(false);
            nav_Menu.findItem(R.id.myTicketsFragment).setVisible(false);
            nav_Menu.findItem(R.id.myEventsFragment).setVisible(false);
            fbtnAdd.setVisibility(View.GONE);
        } else {
            mFirestore.collection("User").document(mUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(MainActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> data = value.getData();
                        if (data != null) {
                            String usernametxt = (String) data.get("username");
                            boolean isOrganiser = (boolean) data.get("organizer");
                            navBarUsername = findViewById(R.id.navbar_username);
                            if (navBarUsername != null) {
                                navBarUsername.setText(usernametxt);
                            }
                            String downloadUrl = (String) data.get("downloadUrl");



                            userImg = findViewById(R.id.userImg);
                            if (isOrganiser) {
                                fbtnAdd.setVisibility(View.VISIBLE);
                            } else {
                                fbtnAdd.setVisibility(View.GONE);
                            }
                            if (downloadUrl.matches("default")) {

                                userImg.setImageResource(R.drawable.ic_action_person);
                            } else {
                                Picasso.get().load(downloadUrl).resize(110, 110).into(userImg);
                            }
                        }

                    }
                }
            });
            twSign.setVisibility(View.INVISIBLE);
            Menu nav_Menu = mNav.getMenu();
            nav_Menu.findItem(R.id.exit).setVisible(true);
        }
    }

    public void signIn(View view) {
        startActivity(new Intent(MainActivity.this, SignUpAndSignInActivity.class));
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavController navControllerr = Navigation.findNavController(this, R.id.nav_host_fragment);
        switch (item.getItemId()) {
            case R.id.exit:
                dialog = new Dialog(MainActivity.this);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.exit_dialog);
                btnExitYes = dialog.findViewById(R.id.btnExitYes);
                btnExitNo = dialog.findViewById(R.id.btnExitNo);
                dialog.show();
                btnExitYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAuth.getCurrentUser() != null) {
                            mAuth.signOut();
                            twSign.setVisibility(View.VISIBLE);
                            item.setVisible(false);
                            dialog.dismiss();
                            navBarUsername = findViewById(R.id.navbar_username);
                            navBarUsername.setText("Username");
                            Intent intent = getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            item.setVisible(true);
                            twSign.setVisibility(View.GONE);
                        }
                    }
                });
                btnExitNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                mdrawerLayout.closeDrawer(GravityCompat.START);
                break;
            default:
                NavigationUI.onNavDestinationSelected(item, navController);

        }
        mdrawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}