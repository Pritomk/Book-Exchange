package com.example.bookexchange;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

import static com.example.bookexchange.util.constant.*;

public class ExchangeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Uri selectedImage;
    ImageView imageView;
    EditText bookName,authorName,bookEdition,quality,price,wantedBook,wantedBookAuthor,number;
    TextView locationText;
    Button exchangeButton,locationBtn;
    FirebaseStorage storage;
    StorageReference reference;
    App app;
    User user;
    FusedLocationProviderClient fusedLocationProviderClient;
    double latitude, longitude;
    Spinner exSpinner;
    ArrayAdapter<String> arrayAdapter;
    String selectedItem;
    boolean fromProduct;
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> mongoCollection;
    String objID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        Intent intent = getIntent();
        fromProduct = intent.getBooleanExtra("boolean",false);
        objID = intent.getStringExtra("_id");

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
        exSpinner = findViewById(R.id.genreExSpinner);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        storage = FirebaseStorage.getInstance();

        app = new App(new AppConfiguration.Builder(appID).build());
        user = app.currentUser();
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("ProductData");
        mongoCollection = mongoDatabase.getCollection("TestData");

        locationBtn.setOnClickListener(v->getLastLocation());
        imageView.setOnClickListener(v->clickpic());
        if (!fromProduct) {
            exchangeButton.setOnClickListener(v->exchangeFunc());
        } else {
            loadData();
            exchangeButton.setOnClickListener(v->updateFunc());
        }

        setGenreSpinner();

    }

    private void updateFunc() {
        String bookNameStr = bookName.getText().toString();
        String authorNameStr = authorName.getText().toString();
        String bookEditionStr = bookEdition.getText().toString();
        String qualityStr = quality.getText().toString();
        String priceStr = price.getText().toString();
        String wantedBookStr = wantedBook.getText().toString();
        String wantedBookAuthorStr = wantedBookAuthor.getText().toString();
        String locationTextStr = locationText.getText().toString();
        String numberStr = number.getText().toString();

        Document queryFilter = new Document("userid",user.getId());
        Document document = new Document();
        document.put("userid",user.getId());
        document.put("bookName",bookNameStr);
        document.put("authorName",authorNameStr);
        document.put("bookEdition",bookEditionStr);
        document.put("quality",qualityStr);
        document.put("price",priceStr);
        document.put("wantedBook",wantedBookStr);
        document.put("wantedBookAuthor",wantedBookAuthorStr);
        document.put("locationText",locationTextStr);
        document.put("latitude", String.valueOf(latitude));
        document.put("longitude", String.valueOf(longitude));
        document.put("number",numberStr);
        document.put("genre",selectedItem);

        mongoCollection.updateOne(queryFilter,document).getAsync(result -> {
            if (result.isSuccess()) {
                Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show();
                Log.e("userdata","success");
            } else {
                Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show();
                Log.e("userdata","success");
            }
        });
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
        document.append("genre",selectedItem);

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

    private void loadData() {
        Document queryFilter  = new Document("_id",new ObjectId(objID));
        RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(queryFilter).iterator();

        findTask.getAsync(this::findTaskFunc);
    }

    private void findTaskFunc(App.Result<MongoCursor<Document>> result) {
        if (result.isSuccess()) {
            Log.e("newdata", "execute");
            MongoCursor<Document> cursor = result.get();
            if (cursor.hasNext()) {
                Document document = cursor.next();
                setTextView(document);
                Log.e("newdata",document.toString()+"  "+objID);
            }
        }
    }

    private void setTextView(Document document) {
        bookName.setText(document.get("bookName").toString());
        authorName.setText(document.get("authorName").toString());
        bookEdition.setText(document.get("bookEdition").toString());
        quality.setText(document.get("quality").toString());
        wantedBook.setText(document.get("wantedBook").toString());
        wantedBookAuthor.setText(document.get("wantedBookAuthor").toString());
        price.setText(document.get("price").toString());
        locationText.setText(document.get("locationText").toString());
        number.setText(document.get("number").toString());

        exchangeButton.setText("Update");
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
            locationText.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setGenreSpinner() {
        exSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) ExchangeActivity.this);
        arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                genres);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        exSpinner.setAdapter(arrayAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedItem = genres[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}