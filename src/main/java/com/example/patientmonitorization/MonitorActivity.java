package com.example.patientmonitorization;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

public class MonitorActivity extends AppCompatActivity {
    Button Add, Patient;
    DatabaseReference databaseReference;
    EditText editTextCNP;
    String iCNP;
    private Spinner selectedPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        OneSignal.startInit(this).init();
        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                FirebaseDatabase.getInstance().getReference().child("Doctori").child(FirebaseAuth.getInstance().getUid()).child("notificationKey").setValue((userId));
            }
        });
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        Add = (Button) findViewById(R.id.adaugare);
        Patient = (Button) findViewById(R.id.pacient);

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonitorActivity.this, AddPatientActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //

        editTextCNP = (EditText) findViewById(R.id.cnp);

        Patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iCNP = editTextCNP.getText().toString();
                if(iCNP.matches("")){
                    Toast.makeText(MonitorActivity.this, "Introduceți CNP-ul pacientului!", Toast.LENGTH_SHORT).show();
                } else{
                    String user_id1 = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Doctori").child(user_id1).child("Pacienți");
                    dbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(iCNP)){
                                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                Intent intent = new Intent(getBaseContext(), PatientPageActivity.class);
                                intent.putExtra("CNP", iCNP);
                                intent.putExtra("ID", user_id);
                                startActivity(intent);
                            } else{
                                Toast.makeText(MonitorActivity.this, "Pacientul nu a fost găsit!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
    }

    public void onBackPressed() {
        OneSignal.setSubscription(false);
        Intent intent = new Intent(MonitorActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
