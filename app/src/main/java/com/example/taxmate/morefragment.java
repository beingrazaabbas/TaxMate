package com.example.taxmate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;

public class morefragment extends Fragment {

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView ageTextView;
    private TextView dobTextView;
    private TextView mobileTextView;
    private TextView passwordTextView;
    private Button logoutButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public morefragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.morefragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameTextView = view.findViewById(R.id.name_label);
        emailTextView = view.findViewById(R.id.email_label);
        ageTextView = view.findViewById(R.id.age_label);
        dobTextView = view.findViewById(R.id.dob_label);
        mobileTextView = view.findViewById(R.id.mobile_label);
        passwordTextView = view.findViewById(R.id.password_label);
        logoutButton = view.findViewById(R.id.logout_button);

        fetchUserDetails();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void fetchUserDetails() {
        String currentUserEmail = mAuth.getCurrentUser().getEmail();

        db.collection("users")
                .whereEqualTo("email", currentUserEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String name = document.getString("name");
                        String dob = document.getString("dob");
                        String mobile = document.getString("mobile");
                        String password = document.getString("password");

                        if (name != null) {
                            nameTextView.setText(name);
                        }

                        emailTextView.setText("Email: " + currentUserEmail);

                        if (dob != null && dob.length() == 8) {
                            String formattedDob = dob.substring(0, 2) + "/" + dob.substring(2, 4) + "/" + dob.substring(4);
                            dobTextView.setText("Date of Birth: " + formattedDob);
                        }

                        if (mobile != null) {
                            String formattedMobile = "+91-" + mobile;
                            mobileTextView.setText("Mobile: " + formattedMobile);
                        }

                        if (password != null) {
                            String maskedPassword = "*".repeat(password.length());
                            passwordTextView.setText("Password: " + maskedPassword);
                        }

                        fetchAge(currentUserEmail);

                        TextView contactTextView = requireView().findViewById(R.id.contact);
                        String contactText = "Need Help? Contact Us.";

                        SpannableString spannableString = new SpannableString(contactText);
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                // Handle the click action, e.g., open an email client with the support email address
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:support.taxmate@gmail.com"));
                                intent.putExtra(Intent.EXTRA_SUBJECT, "TaxMate Support");
                                startActivity(intent);
                            }
                        };

                        // Apply underline and clickable span to the contact text
                        spannableString.setSpan(new UnderlineSpan(), 0, contactText.length(), 0);
                        spannableString.setSpan(clickableSpan, 0, contactText.length(), 0);

                        // Set the modified text to the TextView
                        contactTextView.setText(spannableString);
                        contactTextView.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                });
    }


    private void fetchAge(String userEmail) {
        db.collection("taxdata")
                .document(userEmail)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long age = documentSnapshot.getLong("age");
                        if (age != null) {
                            ageTextView.setText("Age: " + age);
                        }
                    }
                });
    }


    private void logoutUser() {
        mAuth.signOut();
        startActivity(new Intent(getActivity(), homepage.class));
        getActivity().finish();
    }
}
