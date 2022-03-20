package com.furkankerim.eventgo.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.furkankerim.eventgo.Activities.DetailsActivity;
import com.furkankerim.eventgo.Models.CategoryItem;
import com.furkankerim.eventgo.Models.InfoWindowData;
import com.furkankerim.eventgo.Models.User;
import com.furkankerim.eventgo.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class MapsFragment extends Fragment {
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private FirebaseFirestore mFirestore;
    private DocumentReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private CategoryItem item;
    private User user;
    private float latitude, longitude;
    private String category, eventname, url, title, date;
    private View v;
    private ArrayList<MyInfoWindowAdapter> windowAdapters;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {

                }
            };

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    LatLng ltlng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(ltlng).icon(bitmapDescriptorfromVector(getContext(), R.drawable.ic_person)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltlng, 10));
                }
            }


            mFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        Map<String, Object> data = documentSnapshot.getData();


                        latitude = Float.parseFloat((String) Objects.requireNonNull(data.get("latitude")));
                        longitude = Float.parseFloat((String) Objects.requireNonNull(data.get("longitude")));

                        category = (String) data.get("category");
                        title = (String) data.get("eventname");
                        url = (String) data.get("url");
                        date = (String) data.get("date");
                        String location = (String) data.get("location");
                        String  info = (String) data.get("info");
                        String  hour = (String) data.get("hour");
                        String  postID = (String) data.get("postID");
                        String  price = (String) data.get("price");
                        String  count = (String) data.get("count");
                        String  organizerID = (String) data.get("organizerID");
                        String  starCount = (String) data.get("starCount");
                        String  lat = (String) data.get("latitude");
                        String  lng = (String) data.get("longitude");

                        item = new CategoryItem(title,location,date,url,info,hour,postID,lat,lng,price,count,organizerID,starCount);
                        LatLng eventLog = new LatLng(latitude, longitude);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(eventLog).title(title).snippet(date);
                        MyInfoWindowAdapter myInfoWindowAdapter = new MyInfoWindowAdapter();

                        mMap.setInfoWindowAdapter(myInfoWindowAdapter);


                        if (category.matches("Concerts")) {
                            markerOptions.icon(bitmapDescriptorfromVector(getContext(), R.drawable.ic_action_konser));
                            Marker m = mMap.addMarker(markerOptions);
                            m.setTag(item);

                        }
                        if (category.matches("Theaters")) {
                            markerOptions.icon(bitmapDescriptorfromVector(getContext(), R.drawable.ic_action_tiyatro));
                            Marker m = mMap.addMarker(markerOptions);
                            m.setTag(item);
                        }


                        if (category.matches("Museum")) {
                            markerOptions.icon(bitmapDescriptorfromVector(getContext(), R.drawable.ic_action_muze));
                            Marker m = mMap.addMarker(markerOptions);
                            m.setTag(item);
                        }

                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(@NonNull Marker marker) {
                                CategoryItem mitem = (CategoryItem) marker.getTag();
                                if (mitem != null) {
                                    Intent intent = new Intent(getContext(), DetailsActivity.class);
                                    intent.putExtra("item",mitem);
                                    getContext().startActivity(intent);
                                }
                            }
                        });
                    }

                }
            });


        }
    };

    private BitmapDescriptor bitmapDescriptorfromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (requestCode == 1) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, locationListener);

                    Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_maps, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        windowAdapters = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        user = getCurrentUser();
        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View view;
        private String title, date, url;


        MyInfoWindowAdapter() {
            view = getLayoutInflater().inflate(R.layout.infowindow_layout,
                    null);
        }


        @Override
        public View getInfoContents(Marker marker) {

            ImageView icon = (ImageView) view.findViewById(R.id.infoImg);
            TextView txtdate = view.findViewById(R.id.infoDate);
            TextView txtTitle = view.findViewById(R.id.infoTitle);


            CategoryItem newitem = (CategoryItem) marker.getTag();
            txtTitle.setText(marker.getTitle());
            txtdate.setText(marker.getSnippet());

            if (newitem!= null) {
                Picasso.get().load(newitem.getUrl()).into(icon);
            }
            return view;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }
    }
    private User getCurrentUser() {
        if (mUser != null) {
            mFirestore.collection("User").document(mUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> data = value.getData();
                        if (data != null) {
                            String usernametxt = (String) data.get("username");
                            String email = (String) data.get("email");
                            String url = (String) data.get("downloadUrl");
                            boolean organizer = (boolean) data.get("organizer");

                            user = new User(email, usernametxt, url, organizer);
                        }
                    }
                }
            });
            return user;
        }
        return null;
    }

}