package com.cea.cares;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class WeekActivity extends AppCompatActivity {

    User user;
    String id,weekNo,currentDate,selectedDate;
    TextView textView;
    private Calendar calendar;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_activity);

        EditText week = findViewById(R.id.mtrl_calendar_days_of_week);
        Button buttonWeek = findViewById(R.id.buttonweek);
        user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        weekNo  = week.getText().toString().trim();
        id = String.valueOf(user.getId());
        textView = findViewById(R.id.textView3);

        currentDate = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault()).format(new Date());

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        buttonWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Objects.equals(weekNo, "")){
                    Date date1;
                    Date date2;
                    SimpleDateFormat dates = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        date1 = dates.parse(currentDate);
                        date2 = dates.parse(selectedDate);
                        long difference =  Math.abs(date1.getTime() - date2.getTime());
                        long differenceDates = difference / (24 * 60 * 60 * 1000);
                        weekNo = String.valueOf(differenceDates / 7);


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                else if (Objects.equals(currentDate,selectedDate) && Objects.equals(weekNo, ""))
                    weekNo = "1";
                Toast.makeText(getApplicationContext(),weekNo, Toast.LENGTH_LONG).show();
                updateWeek();
            }
        });


    }

    public void updateWeek(){

        class UpdateWeek extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("id",id);
                params.put("week",weekNo);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_WEEK,params);
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new user object
                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getInt("target"),
                                userJson.getString("username"),
                                userJson.getString("email"),
                                userJson.getInt("week")
                        );

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        //starting the profile activity
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    } else {
                        Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
        //executing the async task
        UpdateWeek uw = new UpdateWeek();
        uw.execute();
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                        Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private final DatePickerDialog.OnDateSetListener myDateListener = this::onDateSet;

    private void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
        /*
         TODO Auto-generated method stub
         arg1 = year
         arg2 = month
         arg3 = day
        */
        showDate(arg1, arg2 + 1, arg3);
    }

    private void showDate(int year, int month, int day) {

        selectedDate = day + "/" + month + "/" + year;
        textView.setText(selectedDate);

    }
}
