package com.example.taxmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class regno_page extends AppCompatActivity {

    private static final String TAG = "regno_page";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regno_page);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Button b = (Button) findViewById(R.id.btn);

        EditText nameInput = findViewById(R.id.editTextTextPersonName1);
        EditText emailInput = findViewById(R.id.editTextTextEmailAddress1);
        EditText passwordInput = findViewById(R.id.editTextTextPassword1);
        EditText passwordConfirmInput = findViewById(R.id.editTextTextPassword);
        EditText mobileInput = findViewById(R.id.editTextPhone);
        EditText dobInput = findViewById(R.id.editTextDate1);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = nameInput.getText().toString();
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                String passwordConfirm = passwordConfirmInput.getText().toString();
                String mobile = mobileInput.getText().toString();
                String dob = dobInput.getText().toString();

                if(name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || mobile.isEmpty() || dob.isEmpty()) {
                    Toast.makeText(regno_page.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!password.equals(passwordConfirm)) {
                    Toast.makeText(regno_page.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(regno_page.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("name", name);
                                    userMap.put("email", email);
                                    userMap.put("password", password);
                                    userMap.put("mobile", mobile);
                                    userMap.put("dob", dob);

                                    db.collection("users")
                                            .add(userMap)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(regno_page.this, "Registered Successfully! Welcome!", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(view.getContext(), homepage.class);
                                                startActivity(intent);
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(regno_page.this, "Error adding user", Toast.LENGTH_SHORT).show());
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(regno_page.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
