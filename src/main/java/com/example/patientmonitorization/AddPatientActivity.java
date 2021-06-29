package com.example.patientmonitorization;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class AddPatientActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText CNP, Nume, Prenume, Telefon, Adresa, Varsta;
    private String choosedSex;
    Button Register;
    DatabaseReference databaseReference;
    FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        Spinner Sex;
        Sex = (Spinner) findViewById(R.id.sex);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.sex,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sex.setAdapter(adapter);
        Sex.setOnItemSelectedListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        CNP = (EditText) findViewById(R.id.cnp);
        Nume = (EditText) findViewById(R.id.nume);
        Prenume = (EditText) findViewById(R.id.prenume);
        Adresa = (EditText) findViewById(R.id.adresa);
        Varsta = (EditText) findViewById(R.id.varsta);
        Telefon = (EditText) findViewById(R.id.telefon);
        Register = (Button) findViewById(R.id.adaugare);

        Auth = FirebaseAuth.getInstance();

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CNPText = CNP.getText().toString();
                String PrenumeText = Prenume.getText().toString();
                String AdresaText = Adresa.getText().toString();
                String VarstaText = Varsta.getText().toString();
                String NumeText = Nume.getText().toString();
                String TelefonText = Telefon.getText().toString();

                if(CNPText.matches("") || choosedSex.equals("Sex") || NumeText.matches("") || PrenumeText.matches("") || AdresaText.matches("")
                        || VarstaText.matches("") || NumeText.matches("") || TelefonText.matches("")){
                    Toast.makeText(AddPatientActivity.this, "Datele pacientului sunt incomplete!", Toast.LENGTH_SHORT).show();
                }else {
                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference patient = FirebaseDatabase.getInstance().getReference().child("Doctori").child(user_id).child("Pacienți").child(CNPText);

                    Map newPost = new HashMap();
                    newPost.put("Nume", NumeText);
                    newPost.put("Prenume", PrenumeText);
                    newPost.put("Vârstă", VarstaText);
                    newPost.put("Adresă", AdresaText);
                    newPost.put("Telefon", TelefonText);
                    newPost.put("Sex", choosedSex);
                    patient.setValue(newPost);

                    Intent intent = new Intent(AddPatientActivity.this, MonitorActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getItemAtPosition(position).equals("Sex")){
            //do nothing
        }
        choosedSex = parent.getItemAtPosition(position).toString();
        //Toast.makeText(parent.getContext(),text,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void onBackPressed() {
        Intent intent = new Intent(AddPatientActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
