package com.example.taxmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.lifecycle.ViewModelProvider;


public class dashboardfragment extends Fragment {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private TextView welcomeText;
    private TextView totalIncomeText;
    private TextView totalTaxLiabilityText;
    private TextView itrDateText;
    private TextView regimeChoiceText;
    private PieChart pieChart;

    private SharedViewModel viewModel;


    public dashboardfragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dashboardfragment, container, false);

        welcomeText = rootView.findViewById(R.id.welcome_text);
        totalIncomeText = rootView.findViewById(R.id.total_income);
        totalTaxLiabilityText = rootView.findViewById(R.id.total_tax);
        itrDateText = rootView.findViewById(R.id.itr_date);
        regimeChoiceText = rootView.findViewById(R.id.regime_choice);
        pieChart = rootView.findViewById(R.id.pie_chart);

        fetchUserData();
        fetchTaxData();
        ImageView imageView = rootView.findViewById(R.id.logo);
        PieChart pieChart = rootView.findViewById(R.id.pie_chart);

        // Bring the ImageView to the front
        imageView.bringToFront();

        // Invalidate the layout to reflect the changes
        rootView.invalidate();


        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);


        return rootView;
    }




    private void fetchUserData() {
        String userEmail = auth.getCurrentUser().getEmail();
        firestore.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userName = document.getString("name");
                            if (userName != null) {
                                // Capitalize the first letter
                                String capitalizedUserName = capitalizeFirstLetter(userName);
                                // Get the first word
                                String firstName = getFirstWord(capitalizedUserName);
                                welcomeText.setText("Welcome Back, " + firstName + "!");
                            }
                        }
                    } else {
                        Log.d("dashboardfragment", "Error getting documents: ", task.getException());
                    }
                });
    }

    // Helper method to capitalize the first letter of a string
    private String capitalizeFirstLetter(String input) {
        if (input != null && !input.isEmpty()) {
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        }
        return input;
    }

    // Helper method to get the first word from a string
    private String getFirstWord(String input) {
        if (input != null && !input.isEmpty()) {
            String[] words = input.split(" ");
            if (words.length > 0) {
                return words[0];
            }
        }
        return input;
    }


    private void fetchTaxData() {
        String userEmail = auth.getCurrentUser().getEmail();
        firestore.collection("taxdata").document(userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            double salary = document.getDouble("salary");
                            double hra = document.getDouble("hra");
                            double lta = document.getDouble("lta");
                            double savingsDepositInterest = document.getDouble("savingsDepositInterest");
                            double otherAllowances = document.getDouble("otherAllowances");
                            double charityDonations = document.getDouble("charityDonations");
                            double sec80c = document.getDouble("sec80c");
                            double otherDeductions = document.getDouble("otherDeductions");
                            double npsContributions = document.getDouble("npsContributions");
                            double medInsurance = document.getDouble("medInsurance");
                            double homeLoanInterest = document.getDouble("homeLoanInterest");
                            double evLoanInterest = document.getDouble("evLoanInterest");
                            double eduLoanInterest = document.getDouble("eduLoanInterest");
                            double taxLiabilityFY22 = document.getDouble("taxLiabilityFY22");
                            double taxLiabilityFY23 = document.getDouble("taxLiabilityFY23");

                            // Calculate total income
                            double totalIncome = salary + hra + lta + savingsDepositInterest + otherAllowances;
                            totalIncomeText.setText("Total Income: ₹" + formatAmount(totalIncome));

                            // Determine total tax liability
                            double totalTaxLiability = Math.min(taxLiabilityFY22, taxLiabilityFY23);
                            totalTaxLiabilityText.setText("Total Tax Liability: ₹" + formatAmount(totalTaxLiability));
                            String Tax= Double.toString(totalTaxLiability);
                            viewModel.setTax(Tax);

                            // Set date for ITR filing

                            String itrDate = viewModel.getDate();

                            if (itrDate == null) {
                                itrDate = " Set date reminders in ITR Manager! ";
                            }
                            // Use the string somewhere in your UI, for example:

                            itrDateText.setText("ITR Filing date:" + itrDate);

                            // Determine recommended regime
                            String recommendedRegime = (totalTaxLiability == taxLiabilityFY22) ? "Old Regime (FY 2022/23)" : "New Regime (FY 2023/24)";
                            regimeChoiceText.setText("Recommended regime: " + recommendedRegime);

                            // Calculate total taxable income
                            double totalTaxableIncome = salary + hra + lta + savingsDepositInterest + otherAllowances;

// Create pie chart data
                            List<PieEntry> entries = new ArrayList<>();
                            entries.add(new PieEntry((float) salary, "Salary"));
                            entries.add(new PieEntry((float) hra, "HRA"));
                            entries.add(new PieEntry((float) lta, "LTA"));
                            entries.add(new PieEntry((float) savingsDepositInterest, "Savings' Interest"));
                            entries.add(new PieEntry((float) otherAllowances, "Other Allowances"));

                            PieDataSet dataSet = new PieDataSet(entries, "Total Taxable Income");
                            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                            dataSet.setValueTextSize(12f);
                            dataSet.setValueFormatter(new ValueFormatter() {
                                @Override
                                public String getFormattedValue(float value) {
                                    return String.format(Locale.getDefault(), "%.0f", value);
                                }
                            });
                            PieData pieData = new PieData(dataSet);

// Configure pie chart
                            pieChart.setData(pieData);
                            Description description = new Description();
                            description.setText("");
                            pieChart.setDescription(description);
                            pieChart.setDrawEntryLabels(false);
                            pieChart.getLegend().setEnabled(true);
                            pieChart.invalidate();
                        }
                    }
                });
    }

                            private String formatAmount(double amount) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##,###.##");
        return decimalFormat.format(amount);
    }
}

