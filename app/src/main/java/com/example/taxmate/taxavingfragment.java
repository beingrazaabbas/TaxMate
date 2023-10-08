package com.example.taxmate;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class taxavingfragment extends Fragment {

    private TextView deductionsListTextView;
    private TextView savingsTextView;

    private SharedViewModel viewModel;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public taxavingfragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.taxavingfragment, container, false);
        deductionsListTextView = view.findViewById(R.id.deductionsList);
        savingsTextView = view.findViewById(R.id.savings);

        fetchTaxData();

        return view;
    }

    private void fetchTaxData() {
        String currentUserEmail = auth.getCurrentUser().getEmail();
        if (currentUserEmail != null) {
            db.collection("taxdata")
                    .document(currentUserEmail)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            double sec80c = documentSnapshot.getDouble("sec80c");
                            double otherDeductions = documentSnapshot.getDouble("otherDeductions");
                            double npsContributions = documentSnapshot.getDouble("npsContributions");
                            double medInsurance = documentSnapshot.getDouble("medInsurance");
                            double homeLoanInterest = documentSnapshot.getDouble("homeLoanInterest");
                            double evLoanInterest = documentSnapshot.getDouble("evLoanInterest");
                            double eduLoanInterest = documentSnapshot.getDouble("eduLoanInterest");
                            double charityDonations = documentSnapshot.getDouble("charityDonations");

                            StringBuilder deductionsList = new StringBuilder();
                            deductionsList.append("\u2022 ");

                            if (sec80c > 0)
                                deductionsList.append("Section 80C\n").append("\u2022 ");
                            if (otherDeductions > 0)
                                deductionsList.append("Other Deductions\n").append("\u2022 ");
                            if (npsContributions > 0)
                                deductionsList.append("NPS Contributions\n").append("\u2022 ");
                            if (medInsurance > 0)
                                deductionsList.append("Medical Insurance\n").append("\u2022 ");
                            if (homeLoanInterest > 0)
                                deductionsList.append("Home Loan Interest\n").append("\u2022 ");
                            if (evLoanInterest > 0)
                                deductionsList.append("EV Loan Interest\n").append("\u2022 ");
                            if (eduLoanInterest > 0)
                                deductionsList.append("Education Loan Interest\n").append("\u2022 ");
                            if (charityDonations > 0)
                                deductionsList.append("Charity Donations\n");

                            deductionsListTextView.setText(deductionsList.toString());
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                    });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String taxLiability = viewModel.getTax();
        if (taxLiability != null && !taxLiability.isEmpty()) {
            double tax = Double.parseDouble(taxLiability.replace("₹", "").trim());
            double savings = tax * 0.15;

            String savingsText = "Tax you can still save: ";
            String savingsValueText = "₹" + savings;

            StringBuilder deductionsBuilder = new StringBuilder();
            deductionsBuilder.append("\n\nBy availing these deductions:\n");
            List<String> zeroValueDeductions = new ArrayList<>();

            String currentUserEmail = auth.getCurrentUser().getEmail();
            if (currentUserEmail != null) {
                db.collection("taxdata")
                        .document(currentUserEmail)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                double sec80c = documentSnapshot.getDouble("sec80c");
                                double otherDeductions = documentSnapshot.getDouble("otherDeductions");
                                double npsContributions = documentSnapshot.getDouble("npsContributions");
                                double medInsurance = documentSnapshot.getDouble("medInsurance");
                                double homeLoanInterest = documentSnapshot.getDouble("homeLoanInterest");
                                double evLoanInterest = documentSnapshot.getDouble("evLoanInterest");
                                double eduLoanInterest = documentSnapshot.getDouble("eduLoanInterest");
                                double charityDonations = documentSnapshot.getDouble("charityDonations");

                                // Check deductions with zero values and add them to the list
                                if (sec80c == 0) {
                                    zeroValueDeductions.add("Section 80C");
                                }
                                if (otherDeductions == 0) {
                                    zeroValueDeductions.add("Other Deductions");
                                }
                                if (npsContributions == 0) {
                                    zeroValueDeductions.add("NPS Contributions");
                                }
                                if (medInsurance == 0) {
                                    zeroValueDeductions.add("Medical Insurance");
                                }
                                if (homeLoanInterest == 0) {
                                    zeroValueDeductions.add("Home Loan");
                                }
                                if (evLoanInterest == 0) {
                                    zeroValueDeductions.add("EV loan");
                                }
                                if (eduLoanInterest == 0) {
                                    zeroValueDeductions.add("Education Loan");
                                }
                                if (charityDonations == 0) {
                                    zeroValueDeductions.add("Charity Donations");
                                }

                                // Add more deductions as needed

                                // Append the deductions with zero values as bullets
                                for (String deduction : zeroValueDeductions) {
                                    deductionsBuilder.append("\u2022 ").append(deduction).append("\n");
                                }

                                // Set the text and formatting
                                SpannableString spannableString = new SpannableString(savingsText + savingsValueText + deductionsBuilder.toString());
                                spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, savingsText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannableString.setSpan(new ForegroundColorSpan(Color.RED), savingsText.length(), savingsText.length() + savingsValueText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                // Set the text size for the deductions part
                                spannableString.setSpan(new AbsoluteSizeSpan(16, true), savingsText.length() + savingsValueText.length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                savingsTextView.setText(spannableString);
                            }
                        });
            }
        }
    }
}
