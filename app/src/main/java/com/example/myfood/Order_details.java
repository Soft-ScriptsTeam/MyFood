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
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfood.Adapter.CustomAdapter;
import com.example.myfood.module.Food;
import com.example.myfood.module.Sale_line;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class Order_details extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /***************/
    private RelativeLayout rlayout;
    private Animation animation;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    /**************/

    Boolean Exist;
    private ArrayList<String> identifiants = new ArrayList<>();
    private String MySale_id;
    private Food f_sale_line = new Food();
    private Sale_line my_sale_line = new Sale_line();
    TextView username;
    Button logout;
    FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore db;
    private FirebaseAuth.AuthStateListener mauthStateListener;

    /********set data********/
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private CustomAdapter adapter;
    private List<Sale_line> data_list;

    /****************/

    public interface VolleyCallBack {
        void onSuccess();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

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
        navigationView.setCheckedItem(R.id.nav_order_details);
        /********************/


        username = findViewById(R.id.user_name);

        mFirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser CurrentUser = mFirebaseAuth.getCurrentUser();
        String iduser = CurrentUser.getUid();
        final DocumentReference docRef = db.collection("Utilisateurs").document(iduser);

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

        data_list=new ArrayList<>();

           data_list=hghg();
        Toast.makeText(getApplicationContext(), String.valueOf(data_list.get(0).getQte()), Toast.LENGTH_SHORT).show();
    }

    public List<Sale_line> hghg() {
        final List<Sale_line> arra = new ArrayList<>();
        getSaleID(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                getIDS(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {



                        for (String element : identifiants) {
                            getSales_line(element, new VolleyCallBack() {
                                @Override
                                public void onSuccess() {
                                    arra.add(my_sale_line);


                                }
                            });
                        }



                    }

                });

            }
        });
        return arra;


    }

    /*******get IDs**********/
    public void getIDS(final Order_details.VolleyCallBack callBack) {

        final DocumentReference docRef2 = db.collection("Sales").document(MySale_id);
        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    ArrayList<String> Sale_lines = new ArrayList<>();
                    Sale_lines = (ArrayList<String>) document.get("Sale_lines");

                    identifiants = Sale_lines;
                    callBack.onSuccess();

                } else {
                    Log.d("Data user", "get failed with ", task.getException());
                }
            }
        });


    }

    /*************get Food*************/
    public void getSale_Sales_line(final Order_details.VolleyCallBack callBack) {

        final List<Food> FoodList = new ArrayList<Food>();

        getSaleID(new Order_details.VolleyCallBack() {
            @Override
            public void onSuccess() {
                //Toast.makeText(getApplicationContext(),MySale_id,Toast.LENGTH_SHORT).show();
                final DocumentReference docRef2 = db.collection("Sales").document(MySale_id);
                docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            final ArrayList<Sale_line> myArray = new ArrayList<>();
                            ArrayList<String> Sale_lines = new ArrayList<>();
                            Sale_lines = (ArrayList<String>) document.get("Sale_lines");

                            for (String element : Sale_lines) {
                                getSales_line(element, new Order_details.VolleyCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        myArray.add(my_sale_line);
                                    }
                                });
                            }
                            data_list = myArray;
                            callBack.onSuccess();

                        } else {
                            Log.d("Data user", "get failed with ", task.getException());
                        }
                    }
                });
            }
        });

    }

    /*************get Food*************/
    public void getMyFood(String id_food, final Order_details.VolleyCallBack callBack) {


        final DocumentReference docRef99 = db.collection("Food").document(id_food);
        docRef99.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    Food f11 = new Food();
                    f11.setCategory(document.getString("Category"));
                    f11.setName(document.getString("Nom"));
                    f11.setPrice(document.getDouble("Price"));
                    f11.setUrl_picture(document.getString("Picture"));

                    f_sale_line = f11;
                    callBack.onSuccess();

                } else {
                    Log.d("Data user", "get failed with ", task.getException());
                }
            }
        });

    }

    /*************get Food*************/
    public void getSales_line(String sale_lineID, final Order_details.VolleyCallBack callBack) {

        final List<Food> FoodList = new ArrayList<Food>();

        final DocumentReference docRef2 = db.collection("sale_line").document(sale_lineID);
        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    final Sale_line s11 = new Sale_line();
                    s11.setQte(document.getLong("qte"));

                    getMyFood(document.getString("id_food"), new Order_details.VolleyCallBack() {
                        @Override
                        public void onSuccess() {
                            s11.setMy_food(f_sale_line);
                            my_sale_line = s11;
                            callBack.onSuccess();
                        }
                    });
                } else {
                    Log.d("Data user", "get failed with ", task.getException());
                }
            }
        });

    }

    /*****get Sale ID****/
    public void getSaleID(final Order_details.VolleyCallBack callBack) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Sales")
                .whereEqualTo("id_user", "Utilisateurs/" + getUserID())
                .whereEqualTo("ValidatedAt", "0000-00-00 00:00:00:00")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MySale_id = document.getId();
                                callBack.onSuccess();
                            }
                        } else {

                        }
                    }
                });


    }

    /*******get User ID*******/
    public String getUserID() {
        mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser CurrentUser = mFirebaseAuth.getCurrentUser();
        final String iduser = CurrentUser.getUid();

        return iduser;
    }

    /********Animation*******************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
                Intent i_home = new Intent(getApplicationContext(), Home.class);
                startActivity(i_home);
                break;
            case R.id.nav_fast_food:
                Intent i = new Intent(getApplicationContext(), FastFood.class);
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
                Intent i_order = new Intent(getApplicationContext(), Order_details.class);
                startActivity(i_order);
                break;
            case R.id.nav_help:
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent i_logout = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i_logout);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
