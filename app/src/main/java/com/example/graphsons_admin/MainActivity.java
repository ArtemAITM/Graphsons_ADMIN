package com.example.graphsons_admin;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graphsons_admin.databinding.ActivityMainBinding;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "VisitStatsActivity";
    private static final String VISITS_REFERENCE = "visits";
    private String[] values = new String[4];
    private int admin_year;
    private int admin_month;
    private int admin_day;
    private int admin_hour;
    public static String ip;
    private final String Password = "t38a4bF#";
    public ArrayList<BarEntry> entries = new ArrayList<>();
    private ActivityMainBinding binding;

    private DatabaseReference visitsRef;
    private DatabaseReference security;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        visitsRef = database.getReference(VISITS_REFERENCE);
        security = database.getReference("security");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        Date admin_date = new Date();

        admin_year = (1900 + admin_date.getYear());
        admin_month = admin_date.getMonth() + 1;
        admin_day = admin_date.getDate();
        admin_hour = zonedDateTime.getHour();
        System.out.println(admin_year);

        System.out.println(zonedDateTime.getHour());
        IntegerAxisValueFormatter xAxisFormatter = new IntegerAxisValueFormatter();
        binding.chart.getXAxis().setValueFormatter(xAxisFormatter);
        binding.imageButton.setOnClickListener(this::showMenu);
    }
    private void showMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.inflate(R.menu.type);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.ItemYear) {
                    System.out.println("YearData");
                    LoadYearData();
                } else if (item.getItemId() == R.id.ItemMonth) {
                    System.out.println("MonthData");
                    LoadMonthData();
                } else if (item.getItemId() == R.id.ItemDay) {
                    System.out.println("DayData");
                    LoadDayData();
                } else if (item.getItemId() == R.id.ItemHour) {
                    System.out.println("HourData");
                    LoadHourData();
                }
                return false;
            }
        });
        popupMenu.show();
    }
    private void LoadYearData(){
        binding.indeterminateBar.setVisibility(View.VISIBLE);
        visitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                entries = new ArrayList<>();
                int count;
                for (int i = 0; i < 4; i++) {
                    count = 0;
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String text = (String) dataSnapshot.getValue();
                        assert text != null;
                        int year = Integer.parseInt(text.split(":")[0]);
                        if (year == admin_year - i){
                            count++;
                        }
                    }
                    entries.add(new BarEntry(admin_year - i, count));
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(entries);
                BarDataSet dataSet = new BarDataSet(entries, "Годовая статистика");
                BarData data = new BarData(dataSet);
                binding.indeterminateBar.setVisibility(View.GONE);
                binding.chart.setData(data);
                binding.chart.centerViewTo(admin_year, 0, YAxis.AxisDependency.LEFT);
                binding.chart.invalidate();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Не удалось");
            }
        });

    }
    private void LoadMonthData(){
        binding.indeterminateBar.setVisibility(View.VISIBLE);
        visitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                entries = new ArrayList<>();
                int count;
                for (int i = 0; i < 4; i++) {
                    count = 0;
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String text = (String) dataSnapshot.getValue();
                        assert text != null;
                        int month = Integer.parseInt(text.split(":")[1]);
                        int year = Integer.parseInt(text.split(":")[0]);
                        if (year == admin_year && month == admin_month - i && admin_month - i >= 1){
                            count++;
                        }
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    entries.add(new BarEntry(admin_month - i, count));
                    System.out.println(entries);
                    BarDataSet dataSet = new BarDataSet(entries, "Месячная статистика");
                    BarData data = new BarData(dataSet);
                    binding.indeterminateBar.setVisibility(View.GONE);
                    binding.chart.setData(data);
                    binding.chart.centerViewTo(admin_month, 0, YAxis.AxisDependency.LEFT);
                    binding.chart.invalidate();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Не удалось");
            }
        });
    }
    private void LoadDayData(){
        binding.indeterminateBar.setVisibility(View.VISIBLE);
        visitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                entries = new ArrayList<>();
                int count;
                for (int i = 0; i < 4; i++) {
                    count = 0;
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String text = (String) dataSnapshot.getValue();
                        assert text != null;

                        int day =  Integer.parseInt(text.split(":")[2]);
                        int month = Integer.parseInt(text.split(":")[1]);
                        int year = Integer.parseInt(text.split(":")[0]);
                        if (year == admin_year && month == admin_month && admin_day - i >= 1 && admin_day - i == day){
                            count++;
                        }
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    entries.add(new BarEntry(admin_month - i, count));
                    System.out.println(entries);
                    BarDataSet dataSet = new BarDataSet(entries, "Дневная статистика");
                    BarData data = new BarData(dataSet);
                    binding.indeterminateBar.setVisibility(View.GONE);
                    binding.chart.setData(data);
                    binding.chart.centerViewTo(admin_day, 0, YAxis.AxisDependency.LEFT);
                    binding.chart.invalidate();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Не удалось");
            }
        });
    }
    private void LoadHourData(){
        binding.indeterminateBar.setVisibility(View.VISIBLE);
        visitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                entries = new ArrayList<>();
                int count;
                for (int i = 0; i < 4; i++) {
                    count = 0;
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String text = (String) dataSnapshot.getValue();
                        assert text != null;
                        int hour = Integer.parseInt(text.split(":")[3]);
                        int day = Integer.parseInt(text.split(":")[2]);
                        int month = Integer.parseInt(text.split(":")[1]);
                        int year = Integer.parseInt(text.split(":")[0]);
                        if (year == admin_year && month == admin_month && day == admin_day && hour == admin_hour - i && admin_hour - i >= 1){
                            count++;
                        }
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    entries.add(new BarEntry(admin_month - i, count));
                    System.out.println(admin_hour);
                    System.out.println(entries);
                    BarDataSet dataSet = new BarDataSet(entries, "Почасовая статистика");
                    BarData data = new BarData(dataSet);
                    binding.indeterminateBar.setVisibility(View.GONE);
                    binding.chart.setData(data);
                    binding.chart.centerViewTo(admin_hour, 0, YAxis.AxisDependency.LEFT);
                    binding.chart.invalidate();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Не удалось");
            }
        });
    }
}