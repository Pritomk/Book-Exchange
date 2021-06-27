package com.example.bookexchange.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bookexchange.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
import static com.example.bookexchange.util.constant.genres;

public class HomeFragment extends Fragment implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    String selectedItem;
    MapView mapView;
    GoogleMap mMap;
    Spinner homeGenreSpinner;
    Button applyButton;
    Document document = new Document();
    ArrayAdapter<String> arrayAdapter;
    List<Marker> allMarkers = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mapView = root.findViewById(R.id.mapView);
        homeGenreSpinner = root.findViewById(R.id.spinnerID);
        applyButton = root.findViewById(R.id.applyBtn);
        document = new Document();
        setGenreSpinnerFunc();
        initGoogleMap(savedInstanceState);

        applyButton.setOnClickListener(v->applyGenreFunc());

        return root;
    }

    private void applyGenreFunc() {
        removeAllMarkers();
        document.clear();
        if (selectedItem.equals("All")) {
            document = new Document();
        } else {
            document.put("genre",selectedItem);
        }
        loadData();
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
                Marker mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng))).title(bookName));
                allMarkers.add(mMarker);
            }
        } else {
            Log.e("userdata", "failed to find documents with: ", task.getError());
        }

    }

    private void removeAllMarkers() {
        for (Marker marker : allMarkers) {
            marker.remove();
        }
        allMarkers.clear();
    }

    private void setGenreSpinnerFunc() {
        homeGenreSpinner.setOnItemSelectedListener(this);
        arrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,genres);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        homeGenreSpinner.setAdapter(arrayAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedItem = genres[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}