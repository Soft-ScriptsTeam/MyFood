package com.example.myfood.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfood.FastFood;
import com.example.myfood.R;
import com.example.myfood.module.Food;
import com.example.myfood.module.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private int quantity = 0;
    private String sale_line_id;
    private String MySale_id;
    private Boolean Exist;
    private Context context;
    private List<Food> my_data;
    private OnItemClickListener mListener;
    FirebaseAuth mFirebaseAuth;

    public interface VolleyCallBack {
        void onSuccess();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public CustomAdapter(Context context, List<Food> my_data, Boolean Exist) {
        this.context = context;
        this.my_data = my_data;
        this.Exist = Exist;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView food_name, price,Tvquantity;
        public ImageView picture_food;
        public Button Bplus, Bmoins, BAdd_to_cart;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            food_name = itemView.findViewById(R.id.food_name);
            price = itemView.findViewById(R.id.price);
            picture_food = itemView.findViewById(R.id.picture_food);
            Bplus = itemView.findViewById(R.id.plus);
            Bmoins = itemView.findViewById(R.id.moins);
            BAdd_to_cart = itemView.findViewById(R.id.Add_to_cart);
            Tvquantity=itemView.findViewById(R.id.quantity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);

        return new ViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.food_name.setText(my_data.get(position).getName());
        holder.price.setText(String.valueOf(my_data.get(position).getPrice()));
        Picasso.get().load(my_data.get(position).getUrl_picture()).into(holder.picture_food);

        holder.Bplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_data.get(position).setQuantity(my_data.get(position).getQuantity() + 1);
                holder.Tvquantity.setText(String.valueOf(my_data.get(position).getQuantity()));
            }
        });

        holder.Bmoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (my_data.get(position).getQuantity() > 0)
                    my_data.get(position).setQuantity(my_data.get(position).getQuantity() - 1);
                else my_data.get(position).setQuantity(0);
                holder.Tvquantity.setText(String.valueOf(my_data.get(position).getQuantity()));
            }
        });

        holder.BAdd_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Exist) {
                    CreateSale_Line(my_data.get(position).getID(), my_data.get(position).getQuantity(), new VolleyCallBack() {
                        @Override
                        public void onSuccess() {
                            CreateSale(getUserID(), sale_line_id);
                        }

                    });
                } else {

                   getSaleID(getUserID(),new VolleyCallBack() {
                       @Override
                       public void onSuccess() {
                           CreateSale_Line(my_data.get(position).getID(), my_data.get(position).getQuantity(), new VolleyCallBack() {
                               @Override
                               public void onSuccess() {
                                   FirebaseFirestore db=FirebaseFirestore.getInstance();
                                   DocumentReference washingtonRef = db.collection("Sales").document(MySale_id);
                                   washingtonRef.update("Sale_lines", FieldValue.arrayUnion("sale_line/"+sale_line_id));
                               }
                           });
                       }
                   });
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    //DocumentReference docRef = db.collection("cities").document("SF");

                    /*db.collection("Sales")

                            .whereArrayContains("Sale_lines","sale_line/cUpLbZqWyc0FMoSzk2uu")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context,"yeeeeeeeeees",Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context,"Noooooooo",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });*/
                    /*db.document("Sales/TCecMKmUJ3NYUsCIQ9mN").collection("Sales")
                            .whereArrayContains("Sale_lines","sale_line/fXFhbndaLO8hbqWMpoph")
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                    if (queryDocumentSnapshots.size() == 0) {
                                        Toast.makeText(context,"Noooooooo",Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(context,"yeeeeeeeeees",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });*/


                }


            }
        });


    }

    public void ISExist(String iduser, final CustomAdapter.VolleyCallBack callBack) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Sales").whereEqualTo("id_user", "Utilisateurs/" + iduser)
                .whereEqualTo("FinishedAt", "0000-00-00 00:00:00:00")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (queryDocumentSnapshots.size() == 0) {
                            Exist = false;
                            callBack.onSuccess();
                            /*
                            CreateSale_Line(db, my_data.get(position).getID(), my_data.get(position).getQuantity(), new VolleyCallBack() {
                                @Override
                                public void onSuccess() {
                                    CreateSale(db, iduser, sale_line_id);
                                }

                            });*/
                        } else {
                            Exist = true;
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

    public void CreateSale(String id_user, String id_Sale_line) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> Sale = new HashMap<>();

        ArrayList<String> Sale_lines = new ArrayList<>();
        Sale_lines.add("sale_line/" + id_Sale_line);

        Sale.put("id_user", "Utilisateurs/" + id_user);
        Sale.put("CreatedAt", String.valueOf(getDate()));
        Sale.put("ValidatedAt", "0000-00-00 00:00:00:00");
        Sale.put("FinishedAt", "0000-00-00 00:00:00:00");
        Sale.put("Sale_lines", Sale_lines);

        db.collection("Sales")
                .add(Sale)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        sale_line_id = String.valueOf(documentReference.getId());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void CreateSale_Line(String id_food, int qte, final CustomAdapter.VolleyCallBack callBack) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> Sale = new HashMap<>();

        Sale.put("id_food", "Food/" + id_food);
        Sale.put("qte", qte);

        db.collection("sale_line")
                .add(Sale)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        sale_line_id = String.valueOf(documentReference.getId());
                        callBack.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void getSaleID(String iduser, final VolleyCallBack callBack) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("Sales")
                .whereEqualTo("id_user", "Utilisateurs/" + iduser)
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

    public String getUserID() {
        mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser CurrentUser = mFirebaseAuth.getCurrentUser();
        final String iduser = CurrentUser.getUid();

        return iduser;
    }

    /********get date*********/
    public String getDate() {

        Date currentTime = Calendar.getInstance().getTime();

        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        return currentDateTimeString;
    }

    @Override
    public int getItemCount() {
        return my_data.size();
    }
}
