package com.example.bookexchange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

import static com.example.bookexchange.util.constant.appID;

public class ExchangeActivity extends AppCompatActivity {

    Uri selectedImage;
    ImageView imageView;
    public int CAMERA_PERMISSION_CODE = 100;
    public int READ_PERMISSION_CODE = 101;
    public int LOCATION_PERMISSION_CODE = 102;
    EditText bookName,authorName,bookEdition,quality,price,wantedBook,wantedBookAuthor,number;
    TextView locationText;
    Button exchangeButton,locationBtn;
    FirebaseStorage storage;
    StorageReference reference;
    App app;
    User user;
    FusedLocationProviderClient fusedLocationProviderClient;
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        imageView = findViewById(R.id.imageView);
        bookName = findViewById(R.id.bookName);
        authorName = findViewById(R.id.authorName);
        bookEdition = findViewById(R.id.bookEdition);
        quality = findViewById(R.id.quality);
        price = findViewById(R.id.price);
        wantedBook = findViewById(R.id.wantedBook);
        wantedBookAuthor = findViewById(R.id.wantedBookAuthor);
        exchangeButton = findViewById(R.id.exchangeButton);
        number = findViewById(R.id.number);
        locationText = findViewById(R.id.locationText);
        locationBtn = findViewById(R.id.locationBtn);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        storage = FirebaseStorage.getInstance();

        locationBtn.setOnClickListener(v->getLastLocation());
        imageView.setOnClickListener(v->clickpic());

        exchangeButton.setOnClickListener(v->exchangeFunc());

    }

    private void exchangeFunc() {
        String bookNameStr = bookName.getText().toString();
        String authorNameStr = authorName.getText().toString();
        String bookEditionStr = bookEdition.getText().toString();
        String qualityStr = quality.getText().toString();
        String priceStr = price.getText().toString();
        String wantedBookStr = wantedBook.getText().toString();
        String wantedBookAuthorStr = wantedBookAuthor.getText().toString();
        String locationTextStr = locationText.getText().toString();
        String numberStr = number.getText().toString();

        app = new App(new AppConfiguration.Builder(appID).build());
        user = app.currentUser();
        MongoClient mongoClient = user.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("ProductData");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("TestData");

        Document document = new Document("userid",user.getId());
        document.append("bookName",bookNameStr);
        document.append("authorName",authorNameStr);
        document.append("bookEdition",bookEditionStr);
        document.append("quality",qualityStr);
        document.append("price",priceStr);
        document.append("wantedBook",wantedBookStr);
        document.append("wantedBookAuthor",wantedBookAuthorStr);
        document.append("locationText",locationTextStr);
        document.append("latitude",latitude);
        document.append("longitude",longitude);
        document.append("number",numberStr);

        mongoCollection.insertOne(document).getAsync(result -> {
            if(result.isSuccess())
            {
                Log.v("Datainsert","Data Inserted Successfully");
            }
            else
            {
                Log.v("Datainsert","Error:"+result.getError().toString());
            }
        });

        uploadImage();
    }

    private void clickpic() {
        checkPermission(Manifest.permission.CAMERA,CAMERA_PERMISSION_CODE);
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_PERMISSION_CODE);

        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == RESULT_OK) {
            selectedImage = data.getData(); //The uri with the location of the file
        }
        Log.e("filepath",selectedImage.getPath());
        Glide.with(this)
                .load(selectedImage)
                .into(imageView);
    }
    private void uploadImage() {
        if (selectedImage != null) {
            String path = user.getId()+bookName.getText().toString()+"";
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading....");
            progressDialog.show();
            reference = storage.getReference().child("images/"+ path);
            reference.putFile(selectedImage)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(ExchangeActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(ExchangeActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    });

        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,LOCATION_PERMISSION_CODE);
        checkLocationPermission();
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location == null) {
                requestNewLocationData();
            } else {
                getAddress(location.getLatitude(),location.getLongitude());
            }
        });
    }

    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(ExchangeActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(ExchangeActivity.this, new String[] { permission }, requestCode);
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            else {
                Toast.makeText(ExchangeActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
        if (requestCode == READ_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            else {
                Toast.makeText(ExchangeActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            else {
                Toast.makeText(ExchangeActivity.this, "Location Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
    }

    private void checkLocationPermission() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5);
        locationRequest.setFastestInterval(0);
        locationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location lastLocation = locationResult.getLastLocation();
            getAddress(lastLocation.getLatitude(),lastLocation.getLongitude());
        }
    };

    private void getAddress(double lat,double lon) {
        latitude = lat;
        longitude = lon;
        Geocoder myLocation = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> list = myLocation.getFromLocation(lat,lon,1);
            String address = "";
            address += list.get(0).getAddressLine(0);
            Log.e("address",list+"");
//            address += list.get(1);
            locationText.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}