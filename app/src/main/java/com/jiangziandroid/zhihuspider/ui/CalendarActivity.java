package com.jiangziandroid.zhihuspider.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.utils.ZhihuAPI;
import com.squareup.timessquare.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CalendarActivity extends Activity {

    @InjectView(R.id.calendar_view)  CalendarPickerView mCalendarPickerView;
    @InjectView(R.id.BackImageView) ImageView mBackImageView;
    @InjectView(R.id.goButton)  Button mGoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.inject(this);
        //Init the Calendar
        initTheCalendar();
        //set AppBar OnClickListener
        setAppBarOnClickListener();
    }



    private void initTheCalendar() {
        GregorianCalendar todayCalendar = new GregorianCalendar();
        GregorianCalendar ribaoBirthdayCalendar = new GregorianCalendar(2013,4,19);
        Date today = new Date(todayCalendar.getTimeInMillis() + 86400*1000);
        mCalendarPickerView.init(ribaoBirthdayCalendar.getTime(), today)
                .withSelectedDate(todayCalendar.getTime());
    }

    private void setAppBarOnClickListener() {
        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(CalendarActivity.this, mCalendarPickerView.getSelectedDate().toString(),Toast.LENGTH_LONG).show();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                String stringSelectedDateUrl = formatter.format(mCalendarPickerView.getSelectedDate());
                String stringCurrentDate = formatter.format(new GregorianCalendar().getTime());
                if(stringSelectedDateUrl.equals(stringCurrentDate)){
                    Toast.makeText(CalendarActivity.this, "It's today! "+ stringSelectedDateUrl, Toast.LENGTH_LONG).show();
                    //Get latest news!
                    Intent intent = new Intent(CalendarActivity.this, GridStoriesActivity.class);
                    intent.putExtra("NewsAPI", ZhihuAPI.API_LATEST_NEWS);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(CalendarActivity.this, "It's not today! "+ stringSelectedDateUrl, Toast.LENGTH_LONG).show();
                    //Get history news!
                    long longDate = Long.parseLong(stringSelectedDateUrl)+1;
                    Intent intent = new Intent(CalendarActivity.this, GridStoriesActivity.class);
                    intent.putExtra("NewsAPI", ZhihuAPI.API_HISTORY_NEWS + longDate);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
