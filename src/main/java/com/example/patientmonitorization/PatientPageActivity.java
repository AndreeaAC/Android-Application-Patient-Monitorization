package com.example.patientmonitorization;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PatientPageActivity extends AppCompatActivity {

    private Button graphicPage;
    DatabaseReference dbReference;
    DatabaseReference notifRef, pulsRef;
    final String LOG = "intrare";
    String user_id, cnpNotif;
    String notificationKey;
    ListView patientInfo;
    String notifName, notifPren;
    int countMin = 0;
    int countMax = 0;
    private ArrayList<String> listPatients = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_page);
        String CNP = getIntent().getStringExtra("CNP");
        String ID = getIntent().getStringExtra("ID");
        cnpNotif = getIntent().getStringExtra("CNP");
        user_id = getIntent().getStringExtra("ID");
        Log.d(LOG,CNP);
        dbReference = FirebaseDatabase.getInstance().getReference().child("Doctori").child(ID).child("Pacienți").child(CNP);

        patientInfo = (ListView) findViewById(R.id.listview);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listPatients);
        patientInfo.setAdapter(arrayAdapter);

        dbReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String value = dataSnapshot.getValue(String.class);
                listPatients.add(value);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        graphicPage = (Button) findViewById(R.id.grafic);

        graphicPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientPageActivity.this, PlotActivity.class);
                startActivity(intent);
                finish();
            }
        });

        notifRef = FirebaseDatabase.getInstance().getReference().child("fall");
        notifRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(LOG,"merge");
                DatabaseReference dbRef1 = FirebaseDatabase.getInstance().getReference().child("Doctori").child(user_id);
                dbRef1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        notificationKey = dataSnapshot.child("notificationKey").getValue().toString();
                        Log.d(LOG,"aaa");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //Log.d(LOG,notificationKey);
                DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Doctori").child(user_id).child("Pacienți").child(cnpNotif);
                dbRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        notifName = dataSnapshot.child("Nume").getValue().toString();
                        notifPren = dataSnapshot.child("Prenume").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                new SendNotification("Pacientul " + notifName + " " + notifPren + " a avut o pierdere a echilibrului!", "Cădere!", notificationKey);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



        pulsRef = FirebaseDatabase.getInstance().getReference().child("puls");

        pulsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String valoarePuls = dataSnapshot.child("valPuls").getValue().toString();
                if(Integer.parseInt(valoarePuls) < 55 && countMin < 5) {
                    countMin++;
                    Log.d(LOG, "" + countMin);
                    Log.d(LOG, "scade");
                }
                if(countMin == 5){
                    DatabaseReference dbRef1 = FirebaseDatabase.getInstance().getReference().child("Doctori").child(user_id);
                    dbRef1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            notificationKey = dataSnapshot.child("notificationKey").getValue().toString();
                            Log.d(LOG,"aaa");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Doctori").child(user_id).child("Pacienți").child(cnpNotif);
                    dbRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            notifName = dataSnapshot.child("Nume").getValue().toString();
                            notifPren = dataSnapshot.child("Prenume").getValue().toString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    new SendNotification("Pacientul " + notifName + " " + notifPren + " prezintă o valoare scăzută a pulsului!", "Puls scăzut!", notificationKey);
                    countMin = 0;
                    }

                    if(Integer.parseInt(valoarePuls) > 100 && countMax < 5){
                        countMax ++;
                        Log.d(LOG, ""+countMax);
                        Log.d(LOG,"creste");
                    }
                    if(countMax == 5){
                        DatabaseReference dbRef1 = FirebaseDatabase.getInstance().getReference().child("Doctori").child(user_id);
                        dbRef1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                notificationKey = dataSnapshot.child("notificationKey").getValue().toString();
                                Log.d(LOG,"aaa");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Doctori").child(user_id).child("Pacienți").child(cnpNotif);
                        dbRef2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                notifName = dataSnapshot.child("Nume").getValue().toString();
                                notifPren = dataSnapshot.child("Prenume").getValue().toString();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        new SendNotification("Pacientul " + notifName + " " + notifPren + " prezintă o valoare ridicată a pulsului!", "Puls ridicat!", notificationKey);
                        countMax = 0;
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void onBackPressed() {
        Intent intent = new Intent(PatientPageActivity.this, MonitorActivity.class);
        startActivity(intent);
        finish();
    }
}
