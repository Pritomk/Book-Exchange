package com.example.bookexchange.ui.list;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.bookexchange.MainActivity;
import com.example.bookexchange.R;

import org.bson.Document;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

import static com.example.bookexchange.util.constant.appID;

public class ListFragment extends Fragment {

    Button btn;
    View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_list, container, false);

        btn = root.findViewById(R.id.insertBtn);
        btn.setOnClickListener(v->func());
        return root;
    }

    private void func() {

        App app = new App(new AppConfiguration.Builder(appID).build());
        User user = app.currentUser();
        MongoClient mongoClient = user.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("ProductData");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("TestData");

        mongoCollection.insertOne(new Document("userid",user.getId()).append("data","Data One")).getAsync(result -> {
            if(result.isSuccess())
            {
                Log.v("Data","Data Inserted Successfully");
            }
            else
            {
                Log.v("Data","Error:"+result.getError().toString());
            }
        });

    }
}