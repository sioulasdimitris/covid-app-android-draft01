package com.dimitrissioulas.covidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {
    // views
    private FirebaseDatabase database; //database object
    private FirebaseAuth mAuth;
    private EditText emailEditText,passwordEditText;
    private Button signInButton,signOutButton, signUpButton,bookButton, showMapButton;
    private TextView userTextView,usersLocationTextView,adminReporTextView,closestVaccinationCenterLabelTextView,apointmentsDateTextView,usersIdTextView;
    private DatePicker datePicker1;
    private ListView allAppointmentsListView;
    // variables
    ArrayAdapter<String> listAdapter;
    private String email;
    private boolean isAdmin = false;
    private ArrayList<VaccinationCenter> availableVaccinationCenters = new ArrayList<VaccinationCenter>();
    private ArrayList<String> myLocations = new ArrayList<String>();
    private VaccinationCenter closestCenter;
    private Location usersLocation;
    private String uid;
    // location
    static final int REQ_FINE_LOC = 5432;
    LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instatiate
        instatiate();

        //instatiate views
        instatiateViews();

        //check if the user is logged in
        checkUserLoggedInSession();

    }//onCreate

    private void checkUserLoggedInSession(){
        if(mAuth.getInstance().getCurrentUser()!= null){
            uid = mAuth.getUid();
            signedInAndSignedUpActions();
        } else {
            signedOutActions();
        }
    }

    private void instatiate(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        manager = (LocationManager)getSystemService(LOCATION_SERVICE);
    }//instatiate

    private void instatiateViews(){
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signInButton = findViewById(R.id.signInButton);
        signOutButton = findViewById(R.id.signOutButton);
        signUpButton = findViewById(R.id.signUpButton);
        userTextView = findViewById(R.id.userTextView);
        showMapButton = findViewById(R.id.showMapButton);
        usersLocationTextView = findViewById(R.id.usersLocationTextView);
        closestVaccinationCenterLabelTextView = findViewById(R.id.closestVaccinationCenterLabelTextView);
        bookButton = findViewById(R.id.bookButton);
        datePicker1 = findViewById(R.id.datePicker1);
        adminReporTextView = findViewById(R.id.adminReporTextView);
        allAppointmentsListView = findViewById(R.id.allAppointmentsListView);
        apointmentsDateTextView  = findViewById(R.id.apointmentsDateTextView);
        //usersIdTextView = findViewById(R.id.usersIdTextView);
    }//instatiateViews

    private void readDbCheckAvailableVaccinationCenters(){
        DatabaseReference vaccinationCentersRef = database.getReference("vaccination_centers");
        vaccinationCentersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    VaccinationCenter center = postSnapshot.getValue(VaccinationCenter.class);
                    availableVaccinationCenters.add(new VaccinationCenter(center.getDescription(),center.getId(),center.getLatitude(),center.getLongitude(),center.getName()));
                    myLocations.add(center.getLatitude()+","+center.getLongitude()+","+center.getName());
                }
                displayClosestCenter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }//readDbCheckVaccinationCenters

    private void displayClosestCenter(){
        float minimumDistance = availableVaccinationCenters.get(0).getDistance(usersLocation);
        closestCenter = availableVaccinationCenters.get(0);
        for (VaccinationCenter center : availableVaccinationCenters) {
            if(minimumDistance > center.getDistance(usersLocation)){
                minimumDistance = center.getDistance(usersLocation);
                closestCenter = center;
            }
        }
        closestVaccinationCenterLabelTextView.setVisibility(View.VISIBLE);
        closestVaccinationCenterLabelTextView.setText("Closest Vaccination Center: "+"\n"+closestCenter.getName());
        bookButton.setVisibility(View.VISIBLE);
        datePicker1.setVisibility(View.VISIBLE);
    }

    private void readDbCheckUser(){
        DatabaseReference usersRef = database.getReference("admins");
        Query query = usersRef.orderByChild("id").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    isAdmin = true;
                    userTextView.setText("Logged in as Admin");

                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        Admin admin = postSnapshot.getValue(Admin.class);
                        Log.e("xaxa",""+admin.getEmail());
                        adminActions(admin.getCenter_id());
                    }

                } else {
                    isAdmin = false;
                    userTextView.setText("Logged in as User");
                    userActions();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }//readDb

    public void showMapMethod(View view){
        Intent intent= new Intent(this,MapsActivity.class);
        intent.putStringArrayListExtra("myLocations",myLocations);
        startActivity(intent);
    }

    public void signUpMethod(View view){
        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(),passwordEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    email = emailEditText.getText().toString(); //get user's email
                    signedInAndSignedUpActions();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }//signUpMethod

    public void signInMethod(View view){
        mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(),passwordEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    email = emailEditText.getText().toString(); //get user's email
                    signedInAndSignedUpActions();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }//signUpMethod

    public void signOutMethod(View view){
        mAuth.signOut();
        signedOutActions();
    }//signUpMethod

    public void bookAppointmentMethod(View view) throws ParseException {
        DatabaseReference studentlistRef = database.getReference("appointments");
        studentlistRef.push().setValue( new Appointment(uid,closestCenter.getId(),closestCenter.getName(),""+datePicker1.getDayOfMonth()+"-"+ (datePicker1.getMonth() + 1)+"-"+datePicker1.getYear())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "BOOKED!!!",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().toString(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }//bookAppointmentMethod

    private void signedOutActions(){
        signInButton.setVisibility(View.VISIBLE);
        signUpButton.setVisibility(View.VISIBLE);
        signOutButton.setVisibility(View.INVISIBLE);
        emailEditText.setVisibility(View.VISIBLE);
        passwordEditText.setVisibility(View.VISIBLE);
        userTextView.setVisibility(View.INVISIBLE);
        usersLocationTextView.setVisibility(View.INVISIBLE);
        closestVaccinationCenterLabelTextView.setVisibility(View.INVISIBLE);
        showMapButton.setVisibility(View.INVISIBLE);
        bookButton.setVisibility(View.INVISIBLE);
        datePicker1.setVisibility(View.INVISIBLE);
        adminReporTextView.setVisibility(View.INVISIBLE);
        allAppointmentsListView.setVisibility(View.INVISIBLE);
        manager.removeUpdates(this); //close it
    }//signedOutActions

    private void signedInAndSignedUpActions(){
        uid = mAuth.getUid();
        signInButton.setVisibility(View.INVISIBLE);
        signUpButton.setVisibility(View.INVISIBLE);
        signOutButton.setVisibility(View.VISIBLE);
        emailEditText.setVisibility(View.INVISIBLE);
        passwordEditText.setVisibility(View.INVISIBLE);
        userTextView.setVisibility(View.VISIBLE);
        readDbCheckUser();
    }//signedInActions

    private void adminActions(String centerId){
        usersLocationTextView.setVisibility(View.INVISIBLE);
        closestVaccinationCenterLabelTextView.setVisibility(View.INVISIBLE);
        adminReporTextView.setVisibility(View.VISIBLE);
        //getAllAppointments();
        getAppointmentsForParticularCenterByDay(centerId);
    }

    private void getAllAppointments(){ //not used
        DatabaseReference appointmentsRef = database.getReference("appointments");
        Query query = appointmentsRef.orderByChild("centerId");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){//check if there are booked appointments
                    adminReporTextView.setText("All Appointments: "+"\n"+snapshot.getValue().toString()+"\n");
                } else {
                    adminReporTextView.setText("All Appointments: "+"\n"+"No Appointments yet...");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }//getAllAppointments

    private void getAppointmentsForParticularCenter(String centerId){ //not used  //for each admin, the appointments of his center will be displayed
        DatabaseReference appointmentsRef = database.getReference("appointments");
        Query query = appointmentsRef.orderByChild("centerId").equalTo(centerId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){//check if there are booked appointments
                    adminReporTextView.setText("All Appointments: "+"\n"+snapshot.getValue().toString()+"\n");
                } else {
                    adminReporTextView.setText("All Appointments: "+"\n"+"No Appointments yet...");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }//getAllAppointments

    private void getAppointmentsForParticularCenterByDay(String centerId){ //for each admin, the appointments of his center will be displayed
        List<String> listItem = new ArrayList<String>();
        DatabaseReference appointmentsRef = database.getReference("appointments");
        Query query = appointmentsRef.orderByChild("centerId").equalTo(centerId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){//check if there are booked appointments
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        Appointment appointment = postSnapshot.getValue(Appointment.class);
                        listItem.add(appointment.getDate()+" "+appointment.getUid());
                        listItem.add(appointment.getDate2()+" "+appointment.getUid());
                    }

                    listAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.appointments_list, R.id.apointmentsDateTextView, listItem);
                    allAppointmentsListView.setVisibility(View.VISIBLE);
                    allAppointmentsListView.setAdapter(listAdapter);

                } else {
                    adminReporTextView.setText("All Appointments: "+"\n"+"No Appointments yet...");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                adminReporTextView.setText("All Appointments: "+"\n"+"No Appointments yet...");
            }
        });
    }//getAllAppointments

    private void userActions(){
        usersLocationTextView.setVisibility(View.VISIBLE);
        showMapButton.setVisibility(View.VISIBLE);
        gpsActions();
    }

    //location methods
    public void gpsActions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQ_FINE_LOC);
        }else {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
            //manager.removeUpdates(this); //close it
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLocationChanged(Location location) {
        usersLocationTextView.setText("My Location"+"\n"+String.valueOf(location.getLatitude())+"\n"+String.valueOf(location.getLongitude()));
        readDbCheckAvailableVaccinationCenters();
        usersLocation = new Location("My Current Location");
        usersLocation.setLatitude(location.getLatitude());
        usersLocation.setLongitude(location.getLongitude());
        manager.removeUpdates(this); //close it
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}