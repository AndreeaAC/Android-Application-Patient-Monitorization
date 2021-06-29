package com.example.patientmonitorization;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity{

    private EditText Nume, Prenume, Telefon, Email, Parola;
    Button Register;
    DatabaseReference databaseReference;
    FirebaseAuth Auth;
    FirebaseAuth.AuthStateListener firebaseAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        databaseReference = FirebaseDatabase.getInstance().getReference();

        Nume = (EditText) findViewById(R.id.nume);
        Prenume = (EditText) findViewById(R.id.prenume);
        Email = (EditText) findViewById(R.id.email);
        Parola = (EditText) findViewById(R.id.parola);
        Telefon = (EditText) findViewById(R.id.telefon);
        Register = (Button) findViewById(R.id.register);

        Auth = FirebaseAuth.getInstance();

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EmailText = Email.getText().toString();
                String ParolaText = Parola.getText().toString();
                String NumeText = Nume.getText().toString();
                String PrenumeText = Prenume.getText().toString();
                final String TelefonText = Telefon.getText().toString();

                if(NumeText.matches("") || PrenumeText.matches("") || EmailText.matches("")
                        || ParolaText.matches("") || NumeText.matches("") || TelefonText.matches("")){
                    Toast.makeText(RegisterActivity.this, "Date de înregistrare incomplete!", Toast.LENGTH_SHORT).show();
                }else {
                    final String registerEmail = Email.getText().toString();
                    final String registerParola = Parola.getText().toString();

                    Auth.createUserWithEmailAndPassword(registerEmail, registerParola).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Eroare de înregistrare!", Toast.LENGTH_SHORT).show();
                            } else {
                                String user_id = Auth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Doctori").child(user_id);

                                String dbNume = Nume.getText().toString();
                                String dbPrenume = Prenume.getText().toString();
                                String dbEmail = Email.getText().toString();
                                String dbTelefon = Telefon.getText().toString();

                                Map newPost = new HashMap();
                                newPost.put("Nume", dbNume);
                                newPost.put("Prenume", dbPrenume);
                                newPost.put("Email", dbEmail);
                                newPost.put("Telefon", dbTelefon);
                                current_user_db.setValue(newPost);
                            }
                        }
                    });
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        Auth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Auth.removeAuthStateListener(firebaseAuthListener);
    }

    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}


