package com.example.mymeetings;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.mymeetings.AttendanceActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Admin_home extends AppCompatActivity {
    Button btn_create,btn_meeting_list,btn_meeting_close;
    EditText et_meeting_id;
    String meetingloc;
    Intent intent;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String location;
    Switch statusswitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_admin_home);
        btn_create=findViewById(R.id.btn_create);
        et_meeting_id=findViewById(R.id.idmeeting);
        intent = getIntent();
        meetingloc = intent.getStringExtra("loc123");
        btn_meeting_list =(Button)findViewById(R.id.btn_meeting_list);
       // btn_meeting_close = (Button)findViewById(R.id.btn_meeting_close);
        //btn_meeting_close.setVisibility(View.INVISIBLE);
        btn_meeting_list.setVisibility(View.INVISIBLE);
        statusswitch = (Switch) findViewById(R.id.switch_status);
        statusswitch.setVisibility(View.INVISIBLE);

        //Creating an object of the maps activity(AttendanceActivity to access the map lccation directly without using the intent.
        //final AttendanceActivity attendanceActivity = new AttendanceActivity();
       // location=attendanceActivity.addressStr;



        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //hiding the soft keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btn_create.getWindowToken(), 0);

                final String meeting_id = et_meeting_id.getText().toString();
                // Write a message to the database
                 database = FirebaseDatabase.getInstance();
                 myRef = database.getReference();
                if (meeting_id.equals("")) {
                    Toast.makeText(Admin_home.this, "Create a meeting id before proceeding", Toast.LENGTH_LONG).show();
                } else {

                    myRef.child("Details").child(meeting_id.trim()).child("location").setValue(meetingloc).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Admin_home.this, ""+ meeting_id+" has been created", Toast.LENGTH_SHORT).show();
                                btn_create.setEnabled(false);
                                btn_create.setTextColor(Color.GRAY);
                                et_meeting_id.setText(meeting_id);
                                et_meeting_id.setFocusable(false);
                               // btn_meeting_close.setVisibility(View.VISIBLE);
                                btn_meeting_list.setVisibility(View.VISIBLE);
                                statusswitch.setVisibility(View.VISIBLE);
                            }
                            else
                            {Toast.makeText(Admin_home.this, "Error,Please try again", Toast.LENGTH_SHORT).show();}
                        }
                    });
                    myRef.child("Details").child(meeting_id.trim()).child("status").setValue("open");
                    myRef.child("Global").child(meeting_id);



                }

//

            }
        });

        btn_meeting_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Attendance_confirm.class);
                intent.putExtra("meetingidfromedittext",et_meeting_id.getText().toString());
                startActivity(intent);
            }
        });

        statusswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    statusswitch.setText("Meeting open");
                    myRef.child("Details").child(et_meeting_id.getText().toString()).child("status").setValue("open");
                }
                else{
                    statusswitch.setText("Meeting closed");
                    myRef.child("Details").child(et_meeting_id.getText().toString()).child("status").setValue("closed").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Admin_home.this, "This meeting has been closed", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        });



    }
}
