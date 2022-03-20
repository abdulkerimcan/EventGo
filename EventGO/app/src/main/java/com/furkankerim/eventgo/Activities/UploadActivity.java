package com.furkankerim.eventgo.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.furkankerim.eventgo.R;
import com.furkankerim.eventgo.customMapView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.furkankerim.eventgo.Models.Constant.MAPVIEW_BUNDLE_KEY;

public class UploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private ImageView imageView;
    private TextView tvdate, tvtime;
    private EditText etAdress, etInfo, etTitle, etTicketPrice;
    private Spinner categorySpinner, ticketCountSpinner;
    private Uri uriImgdata;
    private Bitmap selectedImg;
    private LinearLayout mainLayout;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private StorageReference mReference;

    private customMapView mapView;
    private NestedScrollView scrollView;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastLocation;
    private String adress,latiude,longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        tvdate = findViewById(R.id.tvDate);
        tvtime = findViewById(R.id.tvTime);
        categorySpinner = findViewById(R.id.categorySpinner);
        ticketCountSpinner = findViewById(R.id.ticketCountSpinner);
        imageView = findViewById(R.id.upload_img);
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mReference = mStorage.getReference();
        etAdress = findViewById(R.id.etAdress);
        etInfo = findViewById(R.id.etInfo);
        etTicketPrice = findViewById(R.id.etTicketPrice);
        etTitle = findViewById(R.id.etTitle);
        mainLayout = findViewById(R.id.upload_Layout);
        mapView = findViewById(R.id.mapView);
        scrollView = findViewById(R.id.scrollviewUpload);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        initGoogleMap(savedInstanceState);



        tvdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(tvdate);
            }
        });

        tvtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(tvtime);
            }
        });

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> ticketCountAdapter = ArrayAdapter.createFromResource(this,
                R.array.ticketCount, android.R.layout.simple_spinner_item);
        ticketCountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ticketCountSpinner.setAdapter(ticketCountAdapter);
        ticketCountSpinner.setOnItemSelectedListener(this);
    }

    private void initGoogleMap(Bundle savedInstanceState){
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);
    }

    private void showTimeDialog(final TextView tvTime) {
        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                tvTime.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        new TimePickerDialog(UploadActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void showDateDialog(final TextView tvDate) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, +2);
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                tvDate.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(UploadActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void selectImg(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery, 2);
            }
        }

        if (grantResults.length > 0) {
            if (requestCode == 2) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                     lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastLocation != null) {
                        LatLng ltlng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(ltlng).icon(bitmapDescriptorfromVector(UploadActivity.this, R.drawable.ic_person)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltlng, 10));
                    }

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            uriImgdata = data.getData();

            try {
                if (Build.VERSION.SDK_INT >= 28) {
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), uriImgdata);
                    selectedImg = ImageDecoder.decodeBitmap(source);
                } else {
                    selectedImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriImgdata);
                }
                imageView.setImageBitmap(selectedImg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void upload(View view) {

        if (uriImgdata != null && !etTitle.getText().toString().equals("") && !tvdate.getText().toString().equals("Date") &&
                !tvtime.getText().toString().equals("Time") && categorySpinner.getSelectedItemId() != 0 &&
                ticketCountSpinner.getSelectedItemId() != 0 && !etAdress.getText().toString().equals("") &&
                !etInfo.getText().toString().equals("") && !etTicketPrice.getText().toString().equals("")) {
            ProgressDialog progressDialog = new ProgressDialog(UploadActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            UUID uuid = UUID.randomUUID();

            String path = "images/" + uuid + ".jpeg";
            mReference.child(path).putFile(uriImgdata)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            StorageReference newRef = FirebaseStorage.getInstance().getReference(path);
                            newRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    UUID postUID = UUID.randomUUID();
                                    String downloadUri = uri.toString();
                                    String eventName = etTitle.getText().toString();
                                    String location = etAdress.getText().toString();
                                    String info = etInfo.getText().toString();
                                    String date = tvdate.getText().toString();
                                    String hour = tvtime.getText().toString();
                                    System.out.println("uuid = " +uuid);

                                    String ticketPrice=etTicketPrice.getText().toString();
                                    String count = ticketCountSpinner.getSelectedItem().toString();

                                    HashMap<String, Object> mData = new HashMap<>();
                                    mData.put("eventname", eventName.toUpperCase());
                                    mData.put("location", location);
                                    mData.put("ticketprice",ticketPrice);
                                    mData.put("ticketcount",ticketCountSpinner.getSelectedItem().toString());
                                    mData.put("category", categorySpinner.getSelectedItem().toString());
                                    mData.put("postID",postUID.toString());
                                    String inDate = date + " " + hour + ":00";
                                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                    Date date1 = null;
                                    try {
                                        date1 = df.parse(inDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    Timestamp timestamp = new Timestamp(date1.getTime());
                                    mData.put("info", info);
                                    mData.put("date", date);
                                    mData.put("timestamp", timestamp);
                                    mData.put("hour", hour);
                                    mData.put("url", downloadUri);
                                    mData.put("latitude",latiude);
                                    mData.put("longitude",longitude);
                                    mData.put("price",ticketPrice);
                                    mData.put("count",count);
                                    String  a = "0.0";
                                    mData.put("starCount",a);
                                    mData.put("organizerID",mUser.getUid());

                                    mFirestore.collection("Posts").document(postUID.toString()).set(mData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            progressDialog.dismiss();
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        } else {
            if (uriImgdata != null) {
                if (!etTitle.getText().toString().equals("")) {
                    if (!tvdate.getText().toString().equals("Date")) {
                        if (!tvtime.getText().toString().equals("Time")) {
                            if (categorySpinner.getSelectedItemId() != 0) {
                                if (ticketCountSpinner.getSelectedItemId() != 0) {
                                    if (!etTicketPrice.getText().toString().equals("")) {
                                        if (!etAdress.getText().toString().equals("")) {
                                            if (!etInfo.getText().toString().equals("")) {
                                                System.out.println("Bu kısım çalışmayacak üst kısım çalışırsa");
                                            } else {
                                                Snackbar.make(mainLayout, "Event Information cannot be empty.", Snackbar.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Snackbar.make(mainLayout, "Event Address cannot be empty.", Snackbar.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Snackbar.make(mainLayout, "Event Ticket Price cannot be empty.", Snackbar.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Snackbar.make(mainLayout, "Event's ticket count cannot be empty.", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(mainLayout, "Event Category cannot be empty.", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(mainLayout, "Event Time cannot be empty.", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(mainLayout, "Event Date cannot be empty.", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(mainLayout, "Event Name cannot be empty.", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(mainLayout, "Event Picture cannot be empty.", Snackbar.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        locationManager = (LocationManager) UploadActivity.this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                SharedPreferences sharedPreferences = UploadActivity.this.getSharedPreferences("com.furkankerim.eventgo.Activities",MODE_PRIVATE);
                boolean track = sharedPreferences.getBoolean("track",false);
                if (!track){
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    sharedPreferences.edit().putBoolean("track",true).apply();

                }
            }
        };

        if (ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UploadActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                LatLng ltlng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(ltlng).icon(bitmapDescriptorfromVector(UploadActivity.this,R.drawable.ic_person)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltlng, 10));
            }
        }

    }

    private BitmapDescriptor bitmapDescriptorfromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context,vectorResId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        adress = "";
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (addressList != null && addressList.size() > 0) {
                if (addressList.get(0).getThoroughfare() != null) {
                    adress += addressList.get(0).getThoroughfare();

                    if (addressList.get(0).getSubThoroughfare() != null) {
                        adress += " ";
                        adress += addressList.get(0).getSubThoroughfare();
                        if (addressList.get(0).getSubAdminArea() != null) {
                            adress += " ";
                            adress += addressList.get(0).getSubAdminArea();
                            if (addressList.get(0).getAdminArea() != null) {
                                adress += "/";
                                adress +=addressList.get(0).getAdminArea();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.clear();

        if (ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UploadActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                LatLng ltlng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(ltlng).icon(bitmapDescriptorfromVector(UploadActivity.this,R.drawable.ic_person)));
            }
        }

        latiude = "";
        latiude = String.valueOf(latLng.latitude);
        longitude = String.valueOf(latLng.longitude);

        etAdress.setText(adress);

        mMap.addMarker(new MarkerOptions().title(adress).position(latLng));

    }
}