package com.example.mymeetings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    Button btn_attendance,btn_meeting_admin;
    String address;
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myref;
    List<MeetingListModel> list;
    MeetingListAdapter adapter;
    ProgressBar progressBar;
    Button admin_login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
//        recyclerView= (RecyclerView)findViewById(R.id.meetings_rv);
//        firebaseDatabase = FirebaseDatabase.getInstance();
//         myref = firebaseDatabase.getReference();
//         myref.addValueEventListener(new ValueEventListener() {
//             @Override
//             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                 list= new ArrayList<MeetingListModel>();
//                 for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
//                     MeetingListModel value = dataSnapshot1.getValue(MeetingListModel.class);
//                     MeetingListModel meetingListModel = new MeetingListModel();
//                     String meetingname = value.getMeetingname();
//                     meetingListModel.setMeetingname(meetingname);
//                     list.add(meetingListModel);
//                 }
//             }
//
//             @Override
//             public void onCancelled(@NonNull DatabaseError databaseError) {
//
//             }
//         });
//         adapter = new MeetingListAdapter(list,getApplicationContext());
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setAdapter(adapter);
//
//
//



        Intent intent = getIntent();
        address = intent.getStringExtra("fetchedLocation");
        btn_attendance = findViewById(R.id.btn_attendance);
        btn_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,Attendance_home.class);
                intent.putExtra("ml",address);
                startActivity(intent);
            }
        });

        btn_meeting_admin=findViewById(R.id.btn_meeting_admin);
        btn_meeting_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);


                firebaseDatabase = FirebaseDatabase.getInstance();
                myref=firebaseDatabase.getReference();
                myref.child("admin").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       final String db_id = (String) dataSnapshot.child("id").getValue();
                        final String db_password = (String) dataSnapshot.child("password").getValue();
                        //Custom admin login dialog-------
                        AlertDialog.Builder mbuilder = new AlertDialog.Builder(MainActivity.this);
                       final View mView = getLayoutInflater().inflate(R.layout.dialog_admin_login,null);

                       admin_login_button = (Button) mView.findViewById(R.id.admin_login_button);
                        admin_login_button.setOnClickListener(new View.OnClickListener() {


                            @Override
                            public void onClick(View v) {

                                InputMethodManager imm = (InputMethodManager)getSystemService(
                                        Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(admin_login_button.getWindowToken(), 0);

                                EditText id = (EditText) mView.findViewById(R.id.admin_id);
                                EditText password = (EditText)mView.findViewById(R.id.admin_password);
                                if (id.getText().toString().equals("")&& password.toString().equals("")){
                                    Toast.makeText(MainActivity.this, "Fill all the entries", Toast.LENGTH_SHORT).show();
                                }
                                else if(id.getText().toString().equals(db_id)&& password.getText().toString().equals(db_password)){
                                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this,Admin_home.class);
                                    intent.putExtra("meetinglocation",address);
                                    startActivity(intent);
                                }
                                else
                                    Toast.makeText(MainActivity.this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();




                            }
                        });
                        mbuilder.setView(mView);
                        AlertDialog dialog = mbuilder.create();
                        progressBar.setVisibility(View.INVISIBLE);
                        dialog.show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                //--------------------------------

            }
        });
    }
}
