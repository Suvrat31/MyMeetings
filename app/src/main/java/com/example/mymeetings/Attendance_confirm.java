package com.example.mymeetings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import com.example.mymeetings;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Attendance_confirm extends AppCompatActivity {
    DatabaseReference databaseReference;
    Intent intent;
    String meetingidfromedittext;
    RecyclerView recyclerView;
    ArrayList list;
    MeetingListAdapter meetingListAdapter;
    TextView totalpresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_confirm);
        intent = getIntent();
        meetingidfromedittext=intent.getStringExtra("meetingidfromedittext");
        recyclerView = findViewById(R.id.meetings_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList();
        totalpresent =(TextView) findViewById(R.id.totalpresent_tv);



        databaseReference = FirebaseDatabase.getInstance().getReference().child("Global").child(meetingidfromedittext);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                   MeetingListModel m = dataSnapshot1.getValue(MeetingListModel.class);
                   list.add(m);
               }

                meetingListAdapter= new MeetingListAdapter(list,Attendance_confirm.this);
               recyclerView.setAdapter(meetingListAdapter);
               totalpresent.setText(meetingListAdapter.getItemCount() + " people attended this meeting");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Attendance_confirm.this, "Aw,snap..an Error occured", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
