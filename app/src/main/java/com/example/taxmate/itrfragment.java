package com.example.taxmate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.core.app.NotificationCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import androidx.lifecycle.ViewModelProvider;


public class itrfragment extends Fragment {

    private static final String CHANNEL_ID = "itr_reminder_channel";
    private static final String CHANNEL_NAME = "ITR Reminder";

    private DatePicker datePicker;
    private TimePicker timePicker;
    private Handler handler;
    private TextView lastReminderDateTextView;

    private SharedViewModel viewModel;


    public itrfragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.itrfragment, container, false);

        // Initialize the DatePicker and TimePicker widgets
        datePicker = rootView.findViewById(R.id.datePicker);
        timePicker = rootView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        // Fetch the last set reminder date from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String lastReminderDate = sharedPreferences.getString("lastReminderDate", "");

        // Display the last set reminder date in a TextView
        lastReminderDateTextView = rootView.findViewById(R.id.lastReminderDateTextView);
        lastReminderDateTextView.setText("Last set reminder date: " + lastReminderDate);

        // Set up the TextView with the hyperlink
        TextView itrLink = rootView.findViewById(R.id.itr_link);
        String url = "https://incometaxindia.gov.in/pages/deadline.aspx";
        String linkText = "<a href='" + url + "'> <u>Check your government tax ITR filing dates here!</u> </a>";
        itrLink.setText(HtmlCompat.fromHtml(linkText, HtmlCompat.FROM_HTML_MODE_LEGACY));
        itrLink.setMovementMethod(LinkMovementMethod.getInstance());


        // Set up the button to set the ITR reminder
        Button setReminderButton = rootView.findViewById(R.id.setReminderButton);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        setReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected date and time from the DatePicker and TimePicker widgets
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int dayOfMonth = datePicker.getDayOfMonth();

                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                // Create a Calendar object with the selected date and time
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                // Get the delay in milliseconds from now until the selected time
                long delayMillis = calendar.getTimeInMillis() - System.currentTimeMillis();

                // Schedule the notification to appear after the delay
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showNotification();
                    }
                }, delayMillis);

                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                viewModel.setDate(selectedDate);

                // Update the last set reminder date in the TextView
                lastReminderDateTextView.setText("Last set reminder date: " + selectedDate);
            }
        });

        return rootView;
    }

    // Create a notification and show it
    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity().getApplicationContext(), CHANNEL_ID)
                .setContentTitle("ITR Reminder")
                .setContentText("Don't forget to file your ITR!")
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true);

        Notification notification = builder.build();

        notificationManager.notify(0, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove any pending notifications when the fragment is destroyed
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public interface ReminderDateListener {

        void onReminderDateSet(String date);

    }
}


