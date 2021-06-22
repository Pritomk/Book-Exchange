package com.example.bookexchange;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;

import static com.example.bookexchange.util.constant.appID;

public class LoginActivity extends AppCompatActivity {

    private EditText userEmail,userPassword;
    private Button loginBtn,regBtn;
    private App app;
    private final String TAG = "userdata";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        loginBtn = findViewById(R.id.loginBtn);
        regBtn = findViewById(R.id.regBtn);
        app = new App(new AppConfiguration.Builder(appID).build());

        loginBtn.setOnClickListener(v->loginFunc());
        regBtn.setOnClickListener(v->regfunc());
    }

    private void regfunc() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        Credentials credentials = Credentials.emailPassword(email,password);


        if (!password.isEmpty()) {
            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                app.getEmailPassword().registerUserAsync(email,password, result -> {
                    if (result.isSuccess()) {
                        Toast.makeText(getApplicationContext(),"Successfully registered",Toast.LENGTH_SHORT).show();
                        app.loginAsync(credentials, it -> {
                            if (result.isSuccess()) {
                                Toast.makeText(getApplicationContext(),"Successfully logged in",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this,MainActivity.class));
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Login failed",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Registered failed",Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                userEmail.setError("Enter valid email");
            }
        } else {
            userPassword.setError("Enter password");
        }

    }

    private void loginFunc() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        Credentials credentials = Credentials.emailPassword(email,password);

        if (!password.isEmpty()) {
            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                app.loginAsync(credentials, result -> {
                    if (result.isSuccess()) {
                        Toast.makeText(getApplicationContext(),"Successfully logged in",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this,MainActivity.class));
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Login failed",Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                userEmail.setError("Enter valid email");
            }
        } else {
            userPassword.setError("Enter password");
        }



    }
}