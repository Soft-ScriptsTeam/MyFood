package com.example.myfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myfood.module.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    /***************/
    private RelativeLayout rlayout;
    private Animation animation;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView username;
    User user;
    /**************/

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    TextView textt;
    Button logout;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mauthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final User user=new User();
        /********************/


        /*********for menu*********/
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        /**************************/

        /*****navigation drawer menu**********/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        /********************/
        username=findViewById(R.id.user_name);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser CurrentUser = mAuth.getCurrentUser();
        String iduser=CurrentUser.getUid();
        final DocumentReference docRef=db.collection("Utilisateurs").document(iduser);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                        user.setNom("kkk");
                        username.setText(document.getString("nom"));

                } else {
                    Log.d("Data user", "get failed with ", task.getException());
                }
            }
        });




        textt=findViewById(R.id.textt);


    }

    /********Animation*******************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /********menu************/
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                Intent i_home=new Intent(getApplicationContext(),Home.class);
                startActivity(i_home);
                break;
            case R.id.nav_fast_food:
                Intent i=new Intent(getApplicationContext(),FastFood.class);
                startActivity(i);
                break;
            case R.id.nav_sea_food:
                break;
            case R.id.nav_italien_food:
                break;
            case R.id.nav_continental_food:
                break;
            case R.id.nav_chinese_food:
                break;

                //user
            case R.id.nav_profile:
                break;
            case R.id.nav_order_details:
                break;
            case R.id.nav_help:
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent i_logout=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i_logout);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
