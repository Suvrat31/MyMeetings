package com.example.mymeetings;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Attendance_home extends AppCompatActivity {

    Button btn_confirm;
    String currentTime,meetinglocation;
    EditText name,meetingid,sapid;
    String namevalue,meetingidvalue,sapidvalue,fetchloc;
    FirebaseDatabase database ;
    DatabaseReference myref;
    GoogleApiClient googleApiClient;
    ArrayList mmMeetingList;
    Intent intent;
    TextView lastmeetid;
    String lastmeetingid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_home);
        database =FirebaseDatabase.getInstance();
        myref=database.getReference();
        myref.keepSynced(true);
        mmMeetingList = new ArrayList<>();
        lastmeetid=(TextView) findViewById(R.id.lastMeetingAttended_tv);


        currentTime = new SimpleDateFormat("hh mm ss", Locale.getDefault()).format(new Date());
        name = (EditText)findViewById(R.id.et_name);
        meetingid = (EditText)findViewById(R.id.et_mettingid);
        sapid = (EditText)findViewById(R.id.et_sapid);

        meetingidvalue=meetingid.getText().toString();
        sapidvalue = sapid.getText().toString();
         intent= getIntent();
        meetinglocation=intent.getStringExtra("fetchedLocation");

        //Shared preferences fetching previous value
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MEETINGID",0);
        lastmeetingid=sharedPreferences.getString("LastMeetingid","No meetings attended yet");
        lastmeetid.setText(lastmeetingid);


        //On clicking the confirm button
        btn_confirm = findViewById(R.id.btn_confirm);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namevalue=name.getText().toString();

               //hiding the keyboard on button click
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btn_confirm.getWindowToken(), 0);


                //Checking the location code
//                myref.child(meetingid.getText().toString()).child("Location").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        String location = (String) dataSnapshot.getValue();
//                        if(meetinglocation!=location){
//                            Intent i = new Intent(getApplicationContext(),MainActivity.class);
//                            startActivity(i);
//                            Toast.makeText(Attendance_home.this, "You are not present at the meeting location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });



                if (name.getText().toString().isEmpty() || meetingid.getText().toString().isEmpty()) {
                    Toast.makeText(Attendance_home.this, "Please enter all the details", Toast.LENGTH_LONG).show();
                }
                //if the edittexts are not empty then


                else {
                    myref.child("Details").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Reading last meeting id the user marked attendance in , from Shared Pref





                            if(dataSnapshot.hasChild(meetingid.getText().toString().trim())){
                                //Checking the location code

                                myref.child("Details").child(meetingid.getText().toString().trim()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String location = (String) dataSnapshot.child("location").getValue();
                                        String status = (String)dataSnapshot.child("status").getValue();
                                        if(!meetinglocation.equals(location)){

                                            Toast.makeText(Attendance_home.this, "You are not present at the meeting location", Toast.LENGTH_SHORT).show();
                                        }

                                        else{
                                            if(status.equals("closed")){
                                                Toast.makeText(Attendance_home.this, "This meeting has been closed", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                if(meetingid.getText().toString().equals(lastmeetingid) || meetingid.getText().toString().equals(lastmeetid.getText().toString())){
                                                    Toast.makeText(Attendance_home.this, "You have already marked attendance for " + meetingid.getText().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                myref.child("Global").child(meetingid.getText().toString().trim()).child(sapid.getText().toString()).child("sapid").setValue(sapid.getText().toString());
                                                myref.child("Global").child(meetingid.getText().toString()).child(sapid.getText().toString()).child("name").setValue(name.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(Attendance_home.this, "Your attendance has been marked", Toast.LENGTH_SHORT).show();
                                                            mmMeetingList.add(meetingid.getText().toString());

                                                           //write to shared pref
                                                            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("MEETINGID",0);
                                                            SharedPreferences.Editor editor = sharedPref.edit();
                                                            editor.putString("LastMeetingid",meetingid.getText().toString());
                                                            editor.commit();
                                                            lastmeetid.setText(meetingid.getText().toString());

                                                            //NOTIFICATION OF ATTENDANCE MARKED
                                                            createNotificationChannel();

                                                        }
                                                    }
                                                });}



                                            }





                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(Attendance_home.this, "Error,Please try again", Toast.LENGTH_SHORT).show();

                                    }
                                });


                            }
                            else
                                Toast.makeText(Attendance_home.this, "Enter the correct meeting id", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


//                    Toast toast = Toast.makeText(getApplicationContext(), "" + currentTime, Toast.LENGTH_LONG);
//                    toast.show();


                }

            }
        });
//end of the confirm button tasks

//        fetchloc = getIntent().getStringExtra("fetchedLocation");
//        Toast.makeText(this, ""+fetchloc, Toast.LENGTH_SHORT).show();

    }



    private void createNotificationChannel() {
        String CHANNEL_ID = "example_channel_id";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(Attendance_home.this,CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("Attendance Marked")
                        .setContentText("You have been marked present at the meeting");
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }



//    private Boolean getMatchMeetingid() {
//
//        try {
//
//        }
//        catch (Exception e){
//
//            Toast.makeText(this, "Error,try again", Toast.LENGTH_SHORT).show();
//        }
////        try{
//////            myref=database.getReference(meetingid.getText().toString());
//////            myref.child("name").setValue(name.getText().toString());
////
////            Log.d("","e"+ myref);
////        }
////            catch (Exception exception){
////                Toast.makeText(this, "The meeting id has not been created", Toast.LENGTH_SHORT).show();
////            }
//
//return  true;
//    }
}
