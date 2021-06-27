   package com.example.bookexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.bson.Document;
import org.bson.types.ObjectId;

import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

import static com.example.bookexchange.util.constant.appID;

public class ProductActivity extends AppCompatActivity {

    Intent intent;
    String objID;
    App app;
    ImageView imageViewList;
    TextView bookNameList,authorNameList,bookEditionList,qualityList,genreSellList,
        wantedBookList,wantedBookAuthorList,priceList,locationTextList,numberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        intent = getIntent();
        objID = intent.getStringExtra("_id");

        imageViewList = findViewById(R.id.imageViewList);
        bookNameList = findViewById(R.id.bookNameList);
        authorNameList = findViewById(R.id.authorNameList);
        bookEditionList = findViewById(R.id.bookEditionList);
        qualityList = findViewById(R.id.qualityList);
        genreSellList = findViewById(R.id.genreSellList);
        wantedBookList = findViewById(R.id.wantedBookList);
        wantedBookAuthorList = findViewById(R.id.wantedBookAuthorList);
        priceList = findViewById(R.id.priceList);
        locationTextList = findViewById(R.id.locationTextList);
        numberList = findViewById(R.id.numberList);

        app = new App(new AppConfiguration.Builder(appID).build());
        User user = app.currentUser();
        MongoClient mongoClient = user.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("ProductData");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("TestData");
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
        bookNameList.setText(document.get("bookName").toString());
        authorNameList.setText(document.get("authorName").toString());
        bookEditionList.setText(document.get("bookEdition").toString());
        qualityList.setText(document.get("quality").toString());
        genreSellList.setText(document.get("genre").toString());
        wantedBookList.setText(document.get("wantedBook").toString());
        wantedBookAuthorList.setText(document.get("wantedBookAuthor").toString());
        priceList.setText(document.get("price").toString());
        locationTextList.setText(document.get("locationText").toString());
        numberList.setText(document.get("number").toString());
    }
}