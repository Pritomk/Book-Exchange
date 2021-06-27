package com.example.bookexchange.ui.list;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookexchange.ProductActivity;
import com.example.bookexchange.R;
import com.example.bookexchange.ui.BookAdapter;
import com.example.bookexchange.ui.BookItem;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

import static com.example.bookexchange.util.constant.appID;
import static com.example.bookexchange.util.constant.genres;

public class ListFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    RecyclerView recyclerView;
    View view;
    BookAdapter bookAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<BookItem> bookItems = new ArrayList<>();
    Spinner genreSpinnerList;
    App app;
    Button applyBtnList;
    ArrayAdapter<String> arrayAdapter;
    String selectedItem;
    Document document = new Document();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_list, container, false);


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        genreSpinnerList = view.findViewById(R.id.genreSpinnerList);
        applyBtnList = view.findViewById(R.id.applyBtnList);

        //Implement genre spinner
        setGenreSpinnerFunc();
        //Load all data in bookItems ArrayList
        loadData();
        //call Implemented the recyclerView
        recyclerViewFunc();
        //call Implemented apply button function
        applyBtnList.setOnClickListener(v->applyGenreFunc());
    }

    private void setGenreSpinnerFunc() {
        genreSpinnerList.setOnItemSelectedListener(this);
        arrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,genres);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genreSpinnerList.setAdapter(arrayAdapter);
    }

    private void applyGenreFunc() {
        document.clear();
        if (!selectedItem.equals("All")) {
            document.put("genre",selectedItem);
        } else {
            document = new Document();
        }
        loadData();
    }

    private void loadData() {
        bookItems.clear();
        bookItems.add(new BookItem("null","null","null"));

        app = new App(new AppConfiguration.Builder(appID).build());
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
                BookItem bookItem = new BookItem(
                        result.get("_id").toString(),
                        result.get("bookName").toString(),
                        result.get("price").toString());

                bookItems.add(bookItem);
            }
            bookItems.remove(0);
            bookAdapter.notifyDataSetChanged();
        } else {
            Log.e("userdata", "failed to find documents with: ", task.getError());
        }

    }

    private void recyclerViewFunc() {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        if (bookItems.size() > 0) {
            bookAdapter = new BookAdapter(getContext(),bookItems);
            recyclerView.setAdapter(bookAdapter);
            bookAdapter.setOnItemClickListener(position -> {
                Intent intent = new Intent(getActivity(), ProductActivity.class);
                intent.putExtra("_id",bookItems.get(position).get_id());
                startActivity(intent);
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedItem = genres[position];
        Toast.makeText(getActivity(),selectedItem,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}