package com.example.bookexchange.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookexchange.ExchangeActivity;
import com.example.bookexchange.MainActivity;
import com.example.bookexchange.ProductActivity;
import com.example.bookexchange.R;
import com.example.bookexchange.ui.BookItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

import static com.example.bookexchange.util.constant.MAPVIEW_BUNDLE_KEY;
import static com.example.bookexchange.util.constant.appID;

public class HomeFragment extends Fragment implements OnMapReadyCallback {


    private HomeViewModel homeViewModel;
    MapView mapView;
    GoogleMap mMap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mapView = root.findViewById(R.id.mapView);
        FloatingActionButton addBtn = root.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(v->{
            startActivity(new Intent(getActivity(), ProductActivity.class));
        });

        initGoogleMap(savedInstanceState);

        return root;
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        loadData();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void loadData() {
        App app = new App(new AppConfiguration.Builder(appID).build());
        User user = app.currentUser();
        Document document = new Document();
        MongoClient mongoClient = user.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("ProductData");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("TestData");
        RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(document).iterator();
        findTask.getAsync(this::findTaskFunc);
    }

    private void findTaskFunc(@NotNull App.Result<MongoCursor<Document>> task) {
        if (task.isSuccess()) {
            MongoCursor<Document> cursor = task.get();
            while (cursor.hasNext()) {
                Document result = cursor.next();
                String lat = result.get("latitude").toString();
                String lng = result.get("longitude").toString();
                String bookName = result.get("bookName").toString();
                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng))).title(bookName));
            }
        } else {
            Log.e("userdata", "failed to find documents with: ", task.getError());
        }

    }

}