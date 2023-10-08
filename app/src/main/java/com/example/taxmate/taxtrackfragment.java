package com.example.taxmate;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class taxtrackfragment extends Fragment {

    private FirebaseAuth mAuth;  // Firebase Auth instance
    private FirebaseFirestore db; // Firebase Firestore instance

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public taxtrackfragment() {
        // Required empty public constructor
    }

    public static taxtrackfragment newInstance(String param1, String param2) {
        taxtrackfragment fragment = new taxtrackfragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.taxtrackfragment, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button calculateButton = view.findViewById(R.id.calculate_button);
        EditText ageText = view.findViewById(R.id.age_edit_text);
        EditText salarytext = view.findViewById(R.id.salary_edit_text);
        EditText hratext = view.findViewById(R.id.hra);
        EditText ltatext = view.findViewById(R.id.lta);
        EditText savingsDepositInteresttext = view.findViewById(R.id.savings);
        EditText otherAllowancestext = view.findViewById(R.id.other_edit_text);
        EditText sec80text = view.findViewById(R.id.sec80c_edit_text);
        EditText medInsurancetext = view.findViewById(R.id.medic_edit_text);
        EditText eduLoanInteresttext = view.findViewById(R.id.edloan_edit_text);
        EditText homeLoanInteresttext = view.findViewById(R.id.houseloan_edit_text);
        EditText evLoanInteresttext = view.findViewById(R.id.evloan_edit_text);
        EditText charityDonationstext = view.findViewById(R.id.donation_edit_text);
        EditText npsContributionstext = view.findViewById(R.id.nps);
        EditText otherDeductionstext = view.findViewById(R.id.otherdeduct_edit_text);
        TextView resultEditText = view.findViewById(R.id.recom);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Calculation Successful!", Toast.LENGTH_SHORT).show();

                int age = Integer.parseInt(ageText.getText().toString());
                int salary = Integer.parseInt(salarytext.getText().toString());
                int hra = Integer.parseInt(hratext.getText().toString());
                int lta = Integer.parseInt(ltatext.getText().toString());
                int savingsDepositInterest = Integer.parseInt(savingsDepositInteresttext.getText().toString());
                int otherAllowances = Integer.parseInt(otherAllowancestext.getText().toString());
                int sec80c = Integer.parseInt(sec80text.getText().toString());
                int medInsurance = Integer.parseInt(medInsurancetext.getText().toString());
                int eduLoanInterest = Integer.parseInt(eduLoanInteresttext.getText().toString());
                int homeLoanInterest = Integer.parseInt(homeLoanInteresttext.getText().toString());
                int evLoanInterest = Integer.parseInt(evLoanInteresttext.getText().toString());
                int charityDonations = Integer.parseInt(charityDonationstext.getText().toString());
                int npsContributions = Integer.parseInt(npsContributionstext.getText().toString());
                int otherDeductions = Integer.parseInt(otherDeductionstext.getText().toString());

                TextView taxResultTextView = view.findViewById(R.id.res);
                TextView tax2ResultTextView = view.findViewById(R.id.res2);

                double taxLiabilityFY22 = calculateTaxLiabilityFY22(age, salary, sec80c, medInsurance, homeLoanInterest, eduLoanInterest, evLoanInterest, charityDonations, npsContributions, otherDeductions);
                double taxLiabilityFY23 = calculateTaxLiabilityFY23(salary, hra, lta, savingsDepositInterest, otherAllowances, npsContributions);

                String result1Text = String.format("Tax Liability as per Old Regime (FY22-23):  ₹%.2f", taxLiabilityFY22);
                String result2Text = String.format("Tax Liability as per New Regime (FY23-24):  ₹%.2f", taxLiabilityFY23);


                String recommendedRegime = (taxLiabilityFY22 <= taxLiabilityFY23) ? " Old " : " New ";

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Set the result to the EditText after a 3-second delay
                        resultEditText.setText("TaxMate's analysis recommends you to choose the" + recommendedRegime + "Tax Regime");

                        // Change the background to the box background
                        resultEditText.setBackgroundResource(R.drawable.box);
                    }
                }, 2000); // 3000 milliseconds = 3 seconds

                taxResultTextView.setText(result1Text);
                tax2ResultTextView.setText(result2Text);

                saveDataToFirestore(age, salary, hra, lta, savingsDepositInterest, otherAllowances, sec80c, medInsurance, homeLoanInterest, eduLoanInterest, evLoanInterest, charityDonations, npsContributions, otherDeductions, taxLiabilityFY22, taxLiabilityFY23);
                Intent intent = new Intent(getActivity(), dashboardfragment.class);
                intent.putExtra("taxLiabilityFY22", taxLiabilityFY22);
                intent.putExtra("taxLiabilityFY23", taxLiabilityFY23);


            }

        });
    }

    private void saveDataToFirestore(int age, int salary, int hra, int lta, int savingsDepositInterest, int otherAllowances,
                          int sec80c, int medInsurance, int homeLoanInterest, int eduLoanInterest, int evLoanInterest,
                          int charityDonations, int npsContributions, int otherDeductions,
                          double taxLiabilityFY22, double taxLiabilityFY23) {

        Map<String, Object> user = new HashMap<>();
        user.put("age", age);
        user.put("salary", salary);
        user.put("hra", hra);
        user.put("lta", lta);
        user.put("savingsDepositInterest", savingsDepositInterest);
        user.put("otherAllowances", otherAllowances);
        user.put("sec80c", sec80c);
        user.put("medInsurance", medInsurance);
        user.put("homeLoanInterest", homeLoanInterest);
        user.put("eduLoanInterest", eduLoanInterest);
        user.put("evLoanInterest", evLoanInterest);
        user.put("charityDonations", charityDonations);
        user.put("npsContributions", npsContributions);
        user.put("otherDeductions", otherDeductions);
        user.put("taxLiabilityFY22", taxLiabilityFY22);
        user.put("taxLiabilityFY23", taxLiabilityFY23);

        if (mAuth.getCurrentUser() != null) {
            String email = mAuth.getCurrentUser().getEmail();
            db.collection("taxdata").document(email).set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(),  "Tax data successfully saved!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),
                                    "Error saving tax data.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }




    private double calculateTaxLiabilityFY22(int age, int salary,
                                             int sec80c, int medInsurance, int homeLoanInterest, int eduLoanInterest,
                                             int evLoanInterest, int charityDonations, int npsContributions, int otherDeductions) {
        // Calculate total income (only salary considered)
        int totalIncome = salary;

        // Subtract standard deduction
        int standardDeduction = 50000;

        // Calculate taxable income
        int taxableIncome = totalIncome - standardDeduction;

        // Calculate total exemptions
        int totalExemptions = sec80c + medInsurance + homeLoanInterest + eduLoanInterest + evLoanInterest
                + charityDonations + npsContributions + otherDeductions;

        // Apply the exemption limit based on age
        int exemptionLimit;
        if (age < 60) {
            exemptionLimit = 250000;
        } else if (age >= 60 && age < 80) {
            exemptionLimit = 300000;
        } else {
            exemptionLimit = 500000;
        }
        totalExemptions = Math.min(totalExemptions, exemptionLimit);



        // Check if the income is at least 5 lakhs to apply exemptions
        if (totalIncome >= 500000) {
            taxableIncome -= totalExemptions;
        }

        // Calculate tax liability based on the tax slabs and age
        double taxLiability = 0;
        if (age < 60) {
            if (taxableIncome > 1000000) {
                taxLiability += (taxableIncome - 1000000) * 0.3;
                taxableIncome = 1000000;
            }

            if (taxableIncome > 500000) {
                taxLiability += (taxableIncome - 500000) * 0.2;
                taxableIncome = 500000;
            }

            if (taxableIncome > 250000) {
                taxLiability += (taxableIncome - 250000) * 0.1;
            }
        }
        else {
            if (taxableIncome > 1000000) {
                taxLiability += (taxableIncome - 1000000) * 0.3;
                taxableIncome = 1000000;
            }

            if (taxableIncome > 500000) {
                taxLiability += (taxableIncome - 500000) * 0.2;
                taxableIncome = 500000;
            }

            if (taxableIncome > 300000) {
                taxLiability += (taxableIncome - 300000) * 0.05;
            }
        }

        // Apply tax rebate under section 87A for individuals with net taxable income less than or equal to Rs 5 lakh
        if (taxLiability <= 12500 && totalIncome <= 500000) {
            taxLiability = 0;
        }


        return taxLiability;
    }


    private double calculateTaxLiabilityFY23(int salary, int hra, int lta, int savingsDepositInterest, int otherAllowances,
                                             int npsContributions) {
        // Calculate total income
        int totalIncome = salary + hra + lta + savingsDepositInterest + otherAllowances;

        // Subtract standard deduction
        int standardDeduction = 50000;

        // Calculate taxable income
        int taxableIncome = totalIncome - standardDeduction;

        // Calculate total exemptions
        int totalExemptions = npsContributions;

        // Apply the exemption limit
        int exemptionLimit = 250000;
        totalExemptions = Math.min(totalExemptions, exemptionLimit);

        // Calculate taxable income
        taxableIncome = totalIncome;

        // Check if the income is at least 7 lakhs to apply exemptions
        if (totalIncome >= 700000) {
            taxableIncome -= totalExemptions;
        }

        // Calculate tax liability based on the tax slabs
        double taxLiability = 0;

        if (taxableIncome > 1500000) {
            taxLiability += (taxableIncome - 1500000) * 0.3;
            taxableIncome = 1500000;
        }

        if (taxableIncome > 1250000) {
            taxLiability += (taxableIncome - 1250000) * 0.2;
            taxableIncome = 1250000;
        }

        if (taxableIncome > 1200000) {
            taxLiability += (taxableIncome - 1200000) * 0.2;
            taxableIncome = 1200000;
        }

        if (taxableIncome > 1000000) {
            taxLiability += (taxableIncome - 1000000) * 0.15;
            taxableIncome = 1000000;
        }

        if (taxableIncome > 900000) {
            taxLiability += (taxableIncome - 900000) * 0.15;
            taxableIncome = 900000;
        }

        if (taxableIncome > 750000) {
            taxLiability += (taxableIncome - 750000) * 0.1;
            taxableIncome = 750000;
        }

        if (taxableIncome > 600000) {
            taxLiability += (taxableIncome - 600000) * 0.1;
            taxableIncome = 600000;
        }

        if (taxableIncome > 500000) {
            taxLiability += (taxableIncome - 500000) * 0.05;
            taxableIncome = 500000;
        }

        if (taxableIncome > 300000) {
            taxLiability += (taxableIncome - 300000) * 0.05;
            taxableIncome = 300000;
        }

        // Apply tax rebate under section 87A for individuals with net taxable income less than or equal to Rs 7 lakh
        if (taxLiability <= 15000 && totalIncome <= 700000) {
            taxLiability = 0;
        }


        return taxLiability;
    }

}