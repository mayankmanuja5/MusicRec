package com.shahrajat.musicrec;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {
    // Accelerometer
    private float lastAccelerationY;

    // Location
    private float[] locationResults;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private SensorManager mSensorManager;
    private SensorEventListener mEventListenerLight;
    private SensorEventListener mEventListenerAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Playing in background...", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Stop", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar1 = Snackbar.make(view, "Stopped playing...", Snackbar.LENGTH_SHORT);
                                snackbar1.show();
                            }
                        }).show();
            }
        });


        // Accelerometer
        mEventListenerAccelerometer = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float[] values = sensorEvent.values;
                //update UI only when change is significant
                if(lastAccelerationY == 0.0 || Math.abs(lastAccelerationY-values[1]) > 0.5) {
                    lastAccelerationY = values[1];
                    // Perform action accordinly
                    //updateUI(SensorType.ACCELEROMETER);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        populateSongList();
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect(); //location
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mEventListenerAccelerometer, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect(); //location
        super.onStop();
        mSensorManager.unregisterListener(mEventListenerAccelerometer);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);


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

    private List<Song> getSongsFromAnXML(Activity activity)
            throws XmlPullParserException, IOException
    {
        List<Song> songs = new ArrayList<Song>();

        StringBuffer stringBuffer = new StringBuffer();
        Resources res = activity.getResources();
        XmlResourceParser xpp = res.getXml(R.xml.songs);
        xpp.next();

        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("item"))
            {
                Song newSong = new Song();
                xpp.next(); xpp.next();
                newSong.name = xpp.getText();
                xpp.next(); xpp.next();xpp.next();
                newSong.author = xpp.getText();
                xpp.next();xpp.next();xpp.next();
                newSong.year = xpp.getText();
                xpp.next();xpp.next();xpp.next();
                newSong.time = xpp.getText();
                songs.add(newSong);
            }
            eventType = xpp.next();
        }
        return songs;
    }

    // Adds all the songs present in xml/songs.xml to the table
    private void populateSongList() {
        TableLayout songsTbl = (TableLayout) findViewById(R.id.song_list);
        int textColor = Color.BLACK;
        float textSize = 18;
        try {
            List<Song> songs = getSongsFromAnXML(this);
            for(Song s: songs) {
                TableRow tbrow = new TableRow(this);
                tbrow.setPadding(0, 70, 0, 70);
                tbrow.setBackgroundResource(R.drawable.cell_shape);

                TextView tv1 = new TextView(this);
                tv1.setTextSize(textSize);
                tv1.setText(s.name);
                tv1.setTextColor(textColor);
                tv1.setGravity(Gravity.LEFT);
                tbrow.addView(tv1);

                TextView t2v = new TextView(this);
                t2v.setText(s.author);
                t2v.setTextColor(textColor);
                tbrow.addView(t2v);

                TextView t3v = new TextView(this);
                t3v.setText(s.year);
                t3v.setTextColor(textColor);
                tbrow.addView(t3v);

                TextView t4v = new TextView(this);
                t4v.setText(s.time);
                t4v.setTextColor(textColor);
                t4v.setGravity(Gravity.RIGHT);
                tbrow.addView(t4v);
                songsTbl.addView(tbrow);

            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        songsTbl.requestLayout();
    }
}
