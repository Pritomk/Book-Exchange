package com.example.bookexchange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.bookexchange.ui.AdAdapter;
import com.example.bookexchange.util.BookItem;

import org.bson.Document;
import org.bson.types.ObjectId;
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

public class MyAdActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AdAdapter adAdapter;
    ArrayList<BookItem> adItems = new ArrayList<>();
    MongoCollection<Document> mongoCollection;
    Document document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ad);

        recyclerView = findViewById(R.id.myAdRecyclerView);

        App app = new App(new AppConfiguration.Builder(appID).build());
        User user = app.currentUser();
        Log.e("userid",user.getId()+"");
        document = new Document("userid",String.valueOf(user.getId()));
        MongoClient mongoClient = user.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("ProductData");
        mongoCollection = mongoDatabase.getCollection("TestData");

        loadData();
        recyclerViewFunc();
    }

    private void loadData() {
        adItems.clear();
        adItems.add(new BookItem("null","null","null"));

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
                        result.get("bookName").toString());
                adItems.add(bookItem);
            }
            adItems.remove(0);
            adAdapter.notifyDataSetChanged();
        } else {
            Log.e("userdata", "failed to find documents with: ", task.getError());
        }
    }
    private void recyclerViewFunc() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (adItems.size() > 0) {
            adAdapter = new AdAdapter(this,adItems);
            recyclerView.setAdapter(adAdapter);
            adAdapter.setOnItemClickListener(position -> {
                Intent intent = new Intent(this, ProductActivity.class);
                intent.putExtra("_id",adItems.get(position).get_id());
                intent.putExtra("boolean","true");
                startActivity(intent);
            });
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case 0 :
                deleteAd(item.getGroupId());
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void deleteAd(int position) {
        Log.e("position",position+" "+adItems.size());
        Document queryFilter = new Document("_id",new ObjectId(adItems.get(position).get_id()));
        mongoCollection.deleteOne(queryFilter).getAsync(task->{
            if (task.isSuccess()){
                adItems.remove(position);
                adAdapter.notifyDataSetChanged();
                Toast.makeText(this,"Successfully deleted",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,"Failed to deleted",Toast.LENGTH_SHORT).show();
            }
        });
    }
}