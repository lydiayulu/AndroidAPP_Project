package project.coen268.scu.dogplaydate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.parse.Parse;
import com.parse.ParseInstallation;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseObject;

// reference
// http://javapapers.com/android/find-places-nearby-in-google-maps-using-google-places-apiandroid-app/

// Server Key: AIzaSyDVGQDiBMRR0pXxAOrdWPwHaPiQXJMQc08
// Browser Key: AIzaSyAHkSb33zot8zfyDca3TmYO09_C1PXlYB8

/**
 * wenyi
 */
public class CreatePlayDate extends FragmentActivity implements
        OnMapReadyCallback,
        LocationListener{
    private  final LatLng LOCATION_UNIV = new LatLng(37.349642, -121.938987);
    private  final LatLng LOCATION_BUILDING = new LatLng(37.348190, -121.937975);
    double latitude = LOCATION_UNIV.latitude;
    double longitude = LOCATION_UNIV.longitude;
    private GoogleMap googleMap;
    private int PROXIMITY_RADIUS = 2000;
    private Button buttonCreate;
    private EditText editTextSearchPlace;
    private DatePickerDialog datePickerDialogStart;
    private DatePickerDialog datePickerDialogEnd;
    private TimePickerDialog timePickerDialogStart;
    private TimePickerDialog timePickerDialogEnd;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private EditText editTextStartTime;
    private EditText editTextEndTime;
    private Calendar newSetDateStart;
    private Calendar newSetDateEnd;
    private Calendar compareDateBaseline;
    private String userName;
    private String userID;
    private String dogName;
    private String dogID;
    private String place;
    private String status; // 1.created 2.received invitation 3.reject invitation 4.accepted invitation
    private String pointLat;
    private String pointLng;
    private boolean isValid;
    private Random rand;
    private SimpleDateFormat format;
    private static final String TABLENAME_PLAYDATELIST  = "playDatesListsTable";
    private TextView textViewChosenPlace;
    private LatLng latLng;
    private HashMap<String, Marker> markerHashMap;
    //private ArrayList<Marker> markerArrayList;
    //public List<HashMap<String, String>> listHashMap;

    // NOTE: server key is recommend, though it seems that server key or browser key both work fine.
    private static final String GOOGLE_API_KEY = "AIzaSyDVGQDiBMRR0pXxAOrdWPwHaPiQXJMQc08"; //server key
    //private static final String GOOGLE_API_KEY =  "AIzaSyAHkSb33zot8zfyDca3TmYO09_C1PXlYB8"; // browser key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_play_date);
        Parse.initialize(this, "DgaXmRWHs3HaCC2buvdgC1ji2LPlItoxgCol7DcJ", "8a4PcTnqh14fJC5ekKmgxR7pDWgMTl27w2eKZEqK");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        editTextSearchPlace =  (EditText) findViewById(R.id.editTextSearchPlace);
        textViewChosenPlace = (TextView) findViewById(R.id.textViewChosenPlace);
        googleMap = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
//        if (location != null) {
//            onLocationChanged(location);
//        }
//        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);

        onMapReady(googleMap);

        initDefaultDateTime();

        rand = new Random();

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                latLng = marker.getPosition();
                place = marker.getTitle();
                pointLat = Double.toString(latLng.latitude);
                pointLng = Double.toString(latLng.longitude);
                textViewChosenPlace.setText("Loc: "  + place + "," + latLng.toString());
                System.out.println("FF_MARKER_2 : " + marker.getTitle() + "," + marker.getSnippet() + marker.getPosition().toString());
                return true;
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMyLocationEnabled(true);
        LatLng currentLatLng = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14));
        map.addMarker(new MarkerOptions()
                .title("Welcome!")
                .snippet("Let's play with the dog.")
                .position(currentLatLng));
                //.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.dog_icon));
    }



    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
    }

//    public void onClickSearch(View v) {
//
//        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_BUILDING, 17);
//        mGoogleMap.animateCamera(update);
//    }


    public void onClickSearch(View v) {
        markerHashMap = new HashMap<String, Marker>();
        //markerArrayList = new ArrayList<Marker> ();

        searchNearbyParks(v, markerHashMap);
        searchEventedParks(v);
        //test
        //Iterator it = markerHashMap.entrySet().iterator();
        for (HashMap.Entry<String, Marker> entry : markerHashMap.entrySet()) {
            System.out.println("mhashMapFFFF" + entry.getValue().getTitle());
        }
//        for (int i = 0; i < markerArrayList.size(); ++i) {
//            Log.i("FFFF", markerArrayList.get(i));
//        }
    }


    public void searchNearbyParks(View v, HashMap<String, Marker> markerArrList) {
        // NOTE: the TYPE keyword should be lower case letters
        String type = editTextSearchPlace.getText().toString().toLowerCase();
        if (type == null || type.isEmpty()) {
            type = "park";
        }
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);
        Toast.makeText(getApplicationContext(), googlePlacesUrl.toString(), Toast.LENGTH_SHORT).show();
        System.out.println(googlePlacesUrl.toString());
        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
//        Object[] toPass = new Object[2];
//        toPass[0] = googleMap;
//        toPass[1] = googlePlacesUrl.toString();
        Object[] toPass = new Object[10];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
        markerHashMap = new HashMap<String, Marker>();
        toPass[2] = markerHashMap;
        //googlePlacesReadTask.execute(toPass);
        //Object toReceive = new Object();
        //toRecieve[0] = listHashMap;
        //Integer i = new Integer(1);
        //HashMap<String, Marker> markerHashMap;
        //toRecieve[0]= markerHashMap;
        System.out.println("inputObj.length3 length length length length length = "  + toPass.length + toPass[2]);
        googlePlacesReadTask.execute(toPass);
        //googlePlacesReadTask.execute(toPass, null, null);  //这是错误的！！！
        //markerHashMap = (HashMap<String, Marker>) toReceive;

    }


    public void searchEventedParks(View v) {
        return;
    }

    public void onClickStartDate(View v) {
        datePickerDialogStart.show();
    }

    public void onClickEndDate(View v) {
        datePickerDialogEnd.show();
    }

    public void onClickStartTime(View v) {
        timePickerDialogStart.show();
    }

    public void onClickEndTime(View v) {
        timePickerDialogEnd.show();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                break;
            case R.id.create_play_date:
                break;
            case R.id.send_message:
                Intent intent = new Intent(CreatePlayDate.this, SendMessage.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

//    @Override
//    public boolean onMarkerClick (Marker marker) {
//        System.out.println("FF_MARKER" + marker.getTitle() +"," + marker.getSnippet());
//        Toast.makeText(getApplicationContext(), marker.getTitle() +"," + marker.getSnippet(), Toast.LENGTH_SHORT).show();
//        return true;
//    }


    public void prepareParseSendingData(ParseObject parseObject){

        //userID = new Integer(rand.nextInt(100)).toString();
        int tempUserID = rand.nextInt(100);
        userID = Integer.toString(tempUserID);
        userName = ((tempUserID % 2) == 0 ? "Tracey" : "Maggie Q");
        Date startTime = newSetDateStart.getTime();
        Date endTime = newSetDateEnd.getTime();
        dogName = ((tempUserID % 2) == 0 ? "Jerry" : "Tom");
        int tempDogID = rand.nextInt(100);
        dogID = Integer.toString(tempDogID);
        status = "0";
        isValid = true;
        //place =((tempDogID % 2) == 0 ? "Mission Park" : "Alumni Park");
        parseObject.put("userName", userName);
        parseObject.put("userID", userID);
        parseObject.put("dogName", dogName);
        parseObject.put("dogID", dogID);
        parseObject.put("place", place);
        parseObject.put("startTime", newSetDateStart.getTime()); // para2: Date
        parseObject.put("endTime", newSetDateEnd.getTime()); // para2: Date
        parseObject.put("status", status);
        parseObject.put("isValid",true );
        parseObject.put("pointLat", pointLat);
        parseObject.put("pointLng",pointLng);
    }

    public void onClickCreate (View v) {
        if ( ! validateStartEndDates() ) {
            initDefaultDateTime();
            Toast.makeText(this, "please chooser a valid start and time", Toast.LENGTH_SHORT).show();
            return;
        }
        ParseObject playDatesListsTable = new ParseObject(TABLENAME_PLAYDATELIST);
        prepareParseSendingData(playDatesListsTable );
        playDatesListsTable.saveInBackground();
        Intent intentViewDatesList = new Intent (CreatePlayDate.this, ViewDatesList.class);
        Toast.makeText(this, newSetDateStart.toString() + "\n" + newSetDateEnd.toString(), Toast.LENGTH_SHORT).show();
        startActivity(intentViewDatesList);//要把startActivity写在onClickCreate的最后一行，否则系统会时不时crash
    }


    private void initDefaultDateTime() {
        editTextStartDate = (EditText) findViewById(R.id.editTextStartDate);
        editTextEndDate = (EditText) findViewById(R.id.editTextEndDate);
        editTextStartTime = (EditText) findViewById(R.id.editTextStartTime);
        editTextEndTime = (EditText) findViewById(R.id.editTextEndTime);
        buttonCreate = (Button) findViewById(R.id.btnCreateRecord);

        // in xml file, to hide keyboard,  android:focusableInTouchMode="false"
        compareDateBaseline = Calendar.getInstance();
        newSetDateStart = Calendar.getInstance();
        newSetDateEnd = Calendar.getInstance();
        format = new SimpleDateFormat("MMM dd, yyyy");
        editTextStartDate.setText(format.format(newSetDateStart.getTime()));
        format = new SimpleDateFormat("hh:mm a");
        editTextStartTime.setText(format.format(newSetDateStart.getTime()));
        newSetDateEnd.add(Calendar.HOUR, 1);
        editTextEndTime.setText(format.format(newSetDateEnd.getTime()));
        format = new SimpleDateFormat("MMM dd, yyyy");
        editTextEndDate.setText(format.format(newSetDateEnd.getTime()));
        prepareDatePickerDialog();
        prepareTimePickerDialog();
        return;
    }

    private boolean validateStartEndDates () {
        System.out.println(newSetDateStart.toString());
        System.out.println(compareDateBaseline.toString());
        System.out.println(newSetDateEnd.toString());
        return (!newSetDateStart.before(compareDateBaseline)) && newSetDateStart.before(newSetDateEnd);
    }

    void prepareDatePickerDialog() {
        Calendar newCalendar = Calendar.getInstance(); // use today's date as default
        datePickerDialogStart = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                format = new SimpleDateFormat("MMM dd, yyyy");
                newSetDateStart.set(year, monthOfYear, dayOfMonth);
                editTextStartDate.setText(format.format(newSetDateStart.getTime()));
                // set default for end date as the same with the start date
                newSetDateEnd.set(year, monthOfYear, dayOfMonth);
                editTextEndDate.setText(format.format(newSetDateEnd.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialogEnd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                format = new SimpleDateFormat("MMM dd, yyyy");
                newSetDateEnd.set(year, monthOfYear, dayOfMonth);
                editTextEndDate.setText(format.format(newSetDateEnd.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    void prepareTimePickerDialog() {
        Calendar newCalendar = Calendar.getInstance();
        timePickerDialogStart = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener(){
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                format = new SimpleDateFormat("hh:mm a");
                newSetDateStart.set(newSetDateStart.get(Calendar.YEAR), newSetDateStart.get(Calendar.MONTH), newSetDateStart.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                editTextStartTime.setText(format.format(newSetDateStart.getTime()));
                // set default for end time as 1 hour later than the start time
                newSetDateEnd.set(newSetDateEnd.get(Calendar.YEAR), newSetDateEnd.get(Calendar.MONTH), newSetDateEnd.get(Calendar.DAY_OF_MONTH), hourOfDay + 1, minute);
                editTextEndTime.setText(format.format(newSetDateEnd.getTime()));
            }
        }, newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE),false );

        timePickerDialogEnd= new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener(){
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                format = new SimpleDateFormat("hh:mm a");
                newSetDateEnd.set(newSetDateEnd.get(Calendar.YEAR), newSetDateEnd.get(Calendar.MONTH), newSetDateEnd.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                editTextEndTime.setText(format.format(newSetDateEnd.getTime()));
            }
        }, newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE),false );
    }
}