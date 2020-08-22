package com.example.myfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    Button register_btn;
    TextView login_btn;
    EditText nom_et;
    EditText prenom_et;
    EditText tel_et;
    EditText email_et;
    EditText pwd_et;
    EditText re_pwd_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        register_btn = findViewById(R.id.btn_register);
        login_btn = findViewById(R.id.login_text);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!FormIsFilled()) return;
                if (pwd_et.getText().toString().equals(re_pwd_et.getText().toString()))
                    SignInUser(email_et.getText().toString(),pwd_et.getText().toString());
            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }


    public void SignInUser(String userEmail, String userPassword)
    {
        mAuth.createUserWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "New user registration: " + task.isSuccessful());

                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithEmail:success");
                            SaveUserData();
                            Register.this.startActivity(new Intent(Register.this, MainActivity.class));
                            Register.this.finish();
                        } else {
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Sign In failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void SaveUserData()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userID = currentUser.getUid();

        Map<String, String> utilisateur = new HashMap<>();
        utilisateur.put("nom", nom_et.getText().toString());
        utilisateur.put("prenom", nom_et.getText().toString());
        utilisateur.put("telephone", tel_et.getText().toString());

        db.collection("Utilisateurs").document(userID)
                .set(utilisateur)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d("TAG", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                    }
                });
    }

    public boolean FormIsFilled()
    {
        nom_et = (EditText) findViewById(R.id.nom);
        tel_et = (EditText) findViewById(R.id.telephone);
        email_et = (EditText) findViewById(R.id.email);
        pwd_et = (EditText) findViewById(R.id.password);
        re_pwd_et = (EditText) findViewById(R.id.password2);

        if (nom_et.getText().toString().matches(""))
        {
            Toast.makeText(this, "Vous n'avez pas entré votre nom", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (nom_et.getText().toString().matches(""))
        {
            Toast.makeText(this, "Vous n'avez pas entré votre prenom", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (tel_et.getText().toString().matches(""))
        {
            Toast.makeText(this, "Vous n'avez pas entré votre numero de Telephone", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (email_et.getText().toString().matches(""))
        {
            Toast.makeText(this, "Vous n'avez pas entré votre Email", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (pwd_et.getText().toString().matches(""))
        {
            Toast.makeText(this, "Vous n'avez pas entré votre Mot de Pass", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (re_pwd_et.getText().toString().matches(""))
        {
            Toast.makeText(this, "Vous n'avez pas validé votre Mot de Pass", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;

    }
}
