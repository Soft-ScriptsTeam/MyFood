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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button login_btn;
    TextView register_btn;
    EditText email_et;
    EditText password_et ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        email_et = (EditText) findViewById(R.id.email);
        password_et = (EditText)  findViewById(R.id.password);
        login_btn = (Button) findViewById(R.id.login);
        register_btn = findViewById(R.id.register);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){Sign_In();}});

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register_intent = new Intent(MainActivity.this, Register.class);
                startActivity(register_intent);
            }
        });
    }

    @Override
    protected  void  onStart() {
        super.onStart();

        FirebaseUser CurrentUser = mAuth.getCurrentUser();
        if (CurrentUser != null)
        {
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
            finish();
        }
    }

    private void Sign_In() {

        String email = email_et.getText().toString().trim();
        String password = password_et.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {

            Task<AuthResult> authResultTask = mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                                Intent intent = new Intent(MainActivity.this, Home.class);
                                startActivity(intent);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            }

                            // ...
                        }
                    });
        }
    }
}
