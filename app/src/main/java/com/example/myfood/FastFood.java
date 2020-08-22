package com.example.myfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfood.Adapter.CustomAdapter;
import com.example.myfood.module.Food;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class FastFood extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    /***************/
    private RelativeLayout rlayout;
    private Animation animation;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    /**************/

    Boolean Exist;
    TextView username;
    Button logout;
    FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore db;
    private FirebaseAuth.AuthStateListener mauthStateListener;

    /********set data********/
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private CustomAdapter adapter;
    private List<Food> data_list;
    /****************/

    public interface VolleyCallBack {
        void onSuccess();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_food);

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
        navigationView.setCheckedItem(R.id.nav_fast_food);
        /********************/


        username=findViewById(R.id.user_name);

        mFirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser CurrentUser = mFirebaseAuth.getCurrentUser();
        String iduser=CurrentUser.getUid();
        final DocumentReference docRef=db.collection("Utilisateurs").document(iduser);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    username.setText(document.getString("nom"));

                } else {
                    Log.d("Data user", "get failed with ", task.getException());
                }
            }
        });

        /********set Data************************************/
        recyclerView = findViewById(R.id.recycler_view);
        data_list = new ArrayList<>();
        getFood(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                ISExist(getUserID(), new CustomAdapter.VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        adapter = new CustomAdapter(getApplicationContext(), data_list,Exist);
                        recyclerView.setAdapter(adapter);

                        adapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                Toast.makeText(getApplicationContext(),"yeeeeeeeeees",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        });

        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);







    }

    /*************get Food*************/
    public void getFood(final FastFood.VolleyCallBack callBack) {

        final List<Food> FoodList = new ArrayList<Food>();

        FirebaseFirestore dbb=FirebaseFirestore.getInstance();
        db.collection("Food").whereEqualTo("Category","Fast Food").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentSnapshot snapshot : queryDocumentSnapshots){
                    Food f=new Food();
                    f.setID(snapshot.getId());
                    f.setName(snapshot.getString("Nom"));
                    f.setPrice(snapshot.getDouble("Price"));
                    f.setCategory(snapshot.getString("Category"));
                    f.setUrl_picture(snapshot.getString("Picture"));
                    f.setQuantity(0);
                    FoodList.add(f);
                }
                data_list=FoodList;
                callBack.onSuccess();
            }
        });

    }

    public void ISExist(String iduser,final CustomAdapter.VolleyCallBack callBack){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("Sales").whereEqualTo("id_user","Utilisateurs/"+iduser)
                .whereEqualTo("FinishedAt","0000-00-00 00:00:00:00")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if(queryDocumentSnapshots.size()==0){
                            Exist=false;
                            callBack.onSuccess();
                            /*
                            CreateSale_Line(db, my_data.get(position).getID(), my_data.get(position).getQuantity(), new VolleyCallBack() {
                                @Override
                                public void onSuccess() {
                                    CreateSale(db, iduser, sale_line_id);
                                }

                            });*/
                        }else{
                            Exist=true;
                            callBack.onSuccess();
                            /*getSaleID(db, new VolleyCallBack() {
                                @Override
                                public void onSuccess() {
                                    CreateSale_Line(db, my_data.get(position).getID(), my_data.get(position).getQuantity(), new VolleyCallBack() {
                                        @Override
                                        public void onSuccess() {
                                            DocumentReference washingtonRef = db.collection("Sales").document(MySale_id);
                                            washingtonRef.update("Sale_lines", FieldValue.arrayUnion("sale_line/"+sale_line_id));
                                        }
                                    });
                                    Toast.makeText(context,String.valueOf(MySale_id),Toast.LENGTH_SHORT).show();
                                }
                            });*/

                        }
                    }
                });
    }

    public String getUserID(){
        mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser CurrentUser = mFirebaseAuth.getCurrentUser();
        final String iduser=CurrentUser.getUid();

        return iduser;
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
                Intent i_order=new Intent(getApplicationContext(),Order_details.class);
                startActivity(i_order);
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
