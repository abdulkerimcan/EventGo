package com.furkankerim.eventgo.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.furkankerim.eventgo.Adapters.CommentAdapter;
import com.furkankerim.eventgo.Adapters.ImagesAdapter;
import com.furkankerim.eventgo.Models.CategoryItem;
import com.furkankerim.eventgo.Models.Comment;
import com.furkankerim.eventgo.Models.Images;
import com.furkankerim.eventgo.Models.Message;
import com.furkankerim.eventgo.Models.User;
import com.furkankerim.eventgo.R;
import com.furkankerim.eventgo.customMapView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.furkankerim.eventgo.Models.Constant.MAPVIEW_BUNDLE_KEY;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback  {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseStorage mStorage;
    private StorageReference mReference;
    private FirebaseFirestore mFirestore;
    private Query query;

    private customMapView mapView;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastLocation;
    private Uri uriImgdata;
    private Bitmap selectedImg;
    private ArrayList<Images> images;
    private ArrayList<Comment> comments;
    private CommentAdapter commentAdapter;
    private RecyclerView recyclerviewComments;
    private ImagesAdapter imagesAdapter;

    private User user,organizer;
    private Spinner fullFareSpinner,studentSpinner,snackSpinner,drinkSpinner,productSpinner,snackCountSpinner,drinkCountSpinner;

    private RecyclerView recyclerviewImg;
    private Button addimgBtn, commentBtn;
    private ImageView imgitem;
    private TextView itemtitle, itemhour, itemdate,detailPricetxt,organizerUsername,tickettxt, productamounttxt,totalAmountTxt;
    private EditText iteminfo, itemLocation;
    private CircleImageView organizerImg;
    private RatingBar ratingBar;
    private Intent intent;
    private CategoryItem item;
    private float avg = 0;
    private int cc = 0;
    private float sum = 0;
    private int studentAmount=0,fullAmount=0,ticketAmont=0,productAmount=0,drinkAmount=0,snackAmount=0,productTotalAmount=0;
    private ImageView msgImg;
    private String channelID = "";
    private AlertDialog newdialog;

    public void init() {

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        mReference = mStorage.getReference();
        mFirestore = FirebaseFirestore.getInstance();


        //Event Information
        itemtitle = findViewById(R.id.detailTitletxt);
        imgitem = findViewById(R.id.itemimg);
        itemhour = findViewById(R.id.detailHourtxt);
        itemdate = findViewById(R.id.detailDatetxt);
        detailPricetxt = findViewById(R.id.detailPricetxt);
        ratingBar = findViewById(R.id.detailRatingBar);
        itemLocation = findViewById(R.id.detaillocationtxt);
        iteminfo = findViewById(R.id.detailinfotxt);
        mapView = findViewById(R.id.details_mapview);

        //Organizer Information
        organizerUsername = findViewById(R.id.organizerUsername);
        organizerImg = findViewById(R.id.organizerImg);
        msgImg = findViewById(R.id.img_toChat);

        //Event Photos
        addimgBtn = findViewById(R.id.imgaddBTN);
        recyclerviewImg = findViewById(R.id.recyclerviewImg);

        //Customer Comments
        recyclerviewComments = findViewById(R.id.recyclerviewComments);
        commentBtn = findViewById(R.id.commentBtn);

        //Ticket Types
        fullFareSpinner = findViewById(R.id.fullFareCountSpinner);
        studentSpinner = findViewById(R.id.studentCountSpinner);
        tickettxt = findViewById(R.id.tickettxt);

        //Products Sold
        snackSpinner = findViewById(R.id.snacksSpinner);
        drinkSpinner = findViewById(R.id.drinkSpinner);
        productSpinner = findViewById(R.id.productSpinner);
        snackCountSpinner = findViewById(R.id.snacksCountSpinner);
        drinkCountSpinner = findViewById(R.id.drinkCountSpinner);
        productamounttxt = findViewById(R.id.productAmountTxt);
        totalAmountTxt = findViewById(R.id.totalAmountTxt);

        //RecyclerView Arrays
        comments = new ArrayList<>();
        images = new ArrayList<>();


        recyclerviewComments.setHasFixedSize(true);
        recyclerviewImg.setHasFixedSize(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        init();



        initGoogleMap(savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerviewImg.setLayoutManager(layoutManager);

        recyclerviewComments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        imagesAdapter = new ImagesAdapter(images, this);
        commentAdapter = new CommentAdapter(this, comments);
        recyclerviewImg.setAdapter(imagesAdapter);
        recyclerviewComments.setAdapter(commentAdapter);
        recyclerviewImg.addItemDecoration(new space(30));

        intent = getIntent();
        item = (CategoryItem) intent.getSerializableExtra("item");
        getCommentsFromDatabase();
        if (mUser == null) {
            msgImg.setVisibility(View.INVISIBLE);
        }else {
            if (item.getOrganizerID().equals(mUser.getUid())) {
                msgImg.setVisibility(View.INVISIBLE);
            }else {
                msgImg.setVisibility(View.VISIBLE);
            }
        }
        getSpinners();


        if (mUser != null) {
            if (item.getOrganizerID().matches(mUser.getUid())) {
                addimgBtn.setVisibility(View.VISIBLE);
            } else
                addimgBtn.setVisibility(View.INVISIBLE);
        } else {
            addimgBtn.setVisibility(View.INVISIBLE);
        }
        if (getCurrentUser() != null) {
            user = getCurrentUser();
        }
        getOrganizer();




        getImages();

        Picasso.get().load(item.getUrl()).into(imgitem);
        itemtitle.setText(item.getTitle());
        itemhour.setText(item.getHour());
        itemdate.setText(item.getDate());
        iteminfo.setText(item.getInfo());
        detailPricetxt.setText(item.getPrice() + " ₺");
        itemLocation.setText(item.getLocation());
        checkThecommentUser();




    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        checkThecommentUser();
        if (mUser == null) {
            msgImg.setVisibility(View.INVISIBLE);
        }else {
            if (item.getOrganizerID().equals(mUser.getUid())) {
                msgImg.setVisibility(View.INVISIBLE);
            }else {
                msgImg.setVisibility(View.VISIBLE);
            }
        }


    }

    //kullanıcı daha önce yorum yapmış mı diye kontrol ediyoruz
    private void checkThecommentUser() {
        if (mUser != null) {
            if (item.getOrganizerID().equals(mUser.getUid())) {
                commentBtn.setVisibility(View.GONE);
            }
            query = mFirestore.collection("Comments").document(item.getPostID()).collection("PostComments");
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(DetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }else {
                        for (DocumentSnapshot ds : value.getDocuments()) {
                            if (mUser.getUid().equals(ds.getId())) {
                                commentBtn.setVisibility(View.GONE);
                                break;
                            }
                        }
                    }
                }
            });
        }

    }

    public void addImg(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery, 2);
        }
    }

    public void makeComment(View view) {

        if (mUser != null) {

            Dialog dialog = new Dialog(this);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.comment_dialog);

            EditText editText = dialog.findViewById(R.id.comment_dialog_edittext);
            Button accept = dialog.findViewById(R.id.comment_dialog_accept);
            Button cancel = dialog.findViewById(R.id.comment_dialog_cancel);
            RatingBar ratingBar = dialog.findViewById(R.id.commentRatingBar);


            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(editText.getText().toString())) {
                        String commenttxt = editText.getText().toString();
                        String rate = String.valueOf(ratingBar.getRating());

                        DateFormat df = new SimpleDateFormat(" dd/MM/yyyy");
                        String date = df.format(Calendar.getInstance().getTime());

                        Comment comment = new Comment(commenttxt, rate, date.toString(), user.getUsername(), user.getDownloadUrl());

                        mFirestore.collection("Comments").document(item.getPostID()).collection("PostComments").document(mUser.getUid())
                                .set(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                commentAdapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DetailsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }else {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sign in");
            builder.setMessage("Please sign in to make a comment");
            builder.setCancelable(false);
            builder.setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(DetailsActivity.this,SignUpAndSignInActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    newdialog.dismiss();
                }
            });

            newdialog = builder.create();
            newdialog.show();
            Button buttonbackground = newdialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            buttonbackground.setTextColor( this.getResources().getColor(R.color.iconSelected,this.getTheme()));

            Button buttonbackground1 = newdialog.getButton(DialogInterface.BUTTON_POSITIVE);
            buttonbackground1.setTextColor( this.getResources().getColor(R.color.iconSelected,this.getTheme()));
        }
    }

    private void getOrganizer() {
        mFirestore.collection("User").document(item.getOrganizerID())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(DetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }else {
                            Map<String, Object> data = value.getData();
                            if (data !=null) {
                                String email = (String) data.get("email");
                                boolean isorganizer = (boolean) data.get("organizer");
                                String usernametxt = (String) data.get("username");
                                String url = (String) data.get("downloadUrl");
                                organizer = new User(email,usernametxt,url,isorganizer);
                                Picasso.get().load(url).resize(130,130).into(organizerImg);
                                organizerUsername.setText(usernametxt);
                            }
                        }

                    }
                });
    }

    private User getCurrentUser() {
        if (mUser != null) {
            mFirestore.collection("User").document(mUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(DetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

    private void getCommentsFromDatabase() {
        mFirestore.collection("Comments").document(item.getPostID())
                .collection("PostComments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(DetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    comments.clear();
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null) {

                            String commenttxt = (String) data.get("comment");
                            String datetxt = (String) data.get("date");
                            String url = (String) data.get("img");
                            String rate = (String) data.get("rate");
                            String usernametxt = (String) data.get("username");
                            Comment comment = new Comment(commenttxt, rate, datetxt, usernametxt, url);
                            comments.add(comment);
                            commentAdapter.notifyDataSetChanged();
                            cc++;
                            sum += Float.valueOf(rate);
                        }
                    }
                    avg = sum/cc;


                    System.out.println(avg);
                    mFirestore.collection("Posts").document(item.getPostID()).update("starCount",String.valueOf(avg))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    item.setStarCount(String.valueOf(avg));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        System.out.println(item.getStarCount());
        ratingBar.setRating(Float.valueOf(item.getStarCount()));

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
                        float latitude = Float.parseFloat(item.getLatitude());
                        float longitude = Float.parseFloat(item.getLongitude());
                        LatLng latLng = new LatLng(latitude,longitude);
                        mMap.addMarker(new MarkerOptions().position(latLng));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                    }

                }
            }
        }
    }

    public void getImages() {

        String newPath = "postImages/" + item.getPostID();
        StorageReference newRef = FirebaseStorage.getInstance().getReference(newPath);
        newRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference sr : listResult.getItems()) {
                    sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Images img = new Images(uri.toString());
                            images.add(img);
                            imagesAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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

                UUID uuid = UUID.randomUUID();
                String path = "postImages/" + item.getPostID() + "/" + uuid + ".jpeg";
                mReference.child(path).putFile(uriImgdata)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                StorageReference newRef = FirebaseStorage.getInstance().getReference(path);
                                newRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Images img = new Images(uri.toString());
                                        images.add(img);
                                        imagesAdapter.notifyDataSetChanged();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    int SnackPrice = 0;
    int drinkPrice = 0;
    int totalAmount = 0;
    private void getSpinners() {


        ArrayAdapter<CharSequence> FullFareSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.Count, android.R.layout.simple_spinner_item);
        FullFareSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fullFareSpinner.setAdapter(FullFareSpinnerAdapter);
        fullFareSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int amount = Integer.parseInt(parent.getItemAtPosition(position).toString());
                int price = Integer.parseInt(item.getPrice());
                fullAmount =  price * amount;
                ticketAmont = fullAmount + studentAmount;
                tickettxt.setText(String.valueOf(ticketAmont) + " ₺");
                totalAmount = ticketAmont + productTotalAmount;
                totalAmountTxt.setText(String.valueOf(totalAmount) +" ₺");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> studentSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.Count, android.R.layout.simple_spinner_item);
        studentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentSpinner.setAdapter(studentSpinnerAdapter);
        studentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int amount = Integer.parseInt(parent.getItemAtPosition(position).toString());
                int price = Integer.parseInt(item.getPrice());
                price = (price - price/20);
                studentAmount = price *amount;
                ticketAmont = fullAmount + studentAmount;
                tickettxt.setText(String.valueOf(ticketAmont) + " ₺");
                totalAmount = ticketAmont + productTotalAmount;
                totalAmountTxt.setText(String.valueOf(totalAmount) + " ₺");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<CharSequence> snackSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.snacks, android.R.layout.simple_spinner_item);
        snackSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        snackSpinner.setAdapter(snackSpinnerAdapter);
        snackSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



                switch (parent.getItemAtPosition(position).toString()){
                    case "Popcorn":
                        SnackPrice = 10;
                        break;
                    case "wafer":
                        SnackPrice = 5;
                        break;
                    case  "nut":
                        SnackPrice = 3;
                        break;
                    case "french fries" :
                        SnackPrice = 5;
                        break;
                    case "biscuit":
                        SnackPrice = 3;
                        break;
                    default:
                        SnackPrice = 0;
                }
                /*
                int degerSnack = Integer.parseInt((String)snackCountSpinner.getSelectedItem());
                int degerDrink = Integer.parseInt((String)drinkCountSpinner.getSelectedItem());
                if (degerSnack != 0 || degerDrink != 0) {
                    snackAmount = SnackPrice * degerSnack;
                    productTotalAmount = productAmount + snackAmount + drinkAmount;
                    producttxt2.setText(String.valueOf(productTotalAmount));
                }
                 */
                int degerSnack = Integer.parseInt((String)snackCountSpinner.getSelectedItem());
                snackAmount = SnackPrice * degerSnack;
                productTotalAmount = productAmount + snackAmount + drinkAmount;
                productamounttxt.setText(String.valueOf(productTotalAmount) + " ₺");
                totalAmount = ticketAmont + productTotalAmount;
                totalAmountTxt.setText(String.valueOf(totalAmount) + " ₺");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> snackCountSpinnerAdapter = ArrayAdapter.createFromResource(DetailsActivity.this,
                R.array.Count, android.R.layout.simple_spinner_item);
        snackCountSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        snackCountSpinner.setAdapter(snackCountSpinnerAdapter);
        snackCountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int count = Integer.parseInt(parent.getItemAtPosition(position).toString());
                snackAmount = SnackPrice * count;
                productTotalAmount = productAmount + snackAmount + drinkAmount;
                productamounttxt.setText(String.valueOf(productTotalAmount) + " ₺");
                totalAmount = ticketAmont + productTotalAmount;
                totalAmountTxt.setText(String.valueOf(totalAmount) + " ₺");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<CharSequence> drinkSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.drinks, android.R.layout.simple_spinner_item);
        drinkSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drinkSpinner.setAdapter(drinkSpinnerAdapter);
        drinkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (parent.getItemAtPosition(position).toString()){
                    case "Coke":
                        drinkPrice = 5;
                        break;
                    case "Fanta":
                        drinkPrice = 4;
                        break;
                    case  "soda water":
                        drinkPrice = 3;
                        break;
                    case "ice tea" :
                        drinkPrice = 6;
                        break;
                    case "lemonade":
                        drinkPrice = 4;
                        break;
                    default:
                        drinkPrice = 0;
                }

                /*
                int deger = Integer.parseInt((String)drinkCountSpinner.getSelectedItem());
                int degerSnack = Integer.parseInt((String)snackCountSpinner.getSelectedItem());
                if(deger != 0 && degerSnack != 0) {
                    drinkAmount = drinkPrice * deger;
                    productTotalAmount = productAmount + snackAmount + drinkAmount;
                    producttxt2.setText(String.valueOf(productTotalAmount));
                }
                 */
                int deger = Integer.parseInt((String)drinkCountSpinner.getSelectedItem());
                drinkAmount = drinkPrice * deger;
                productTotalAmount = productAmount + snackAmount + drinkAmount;
                productamounttxt.setText(String.valueOf(productTotalAmount) + " ₺");
                totalAmount = ticketAmont + productTotalAmount;
                totalAmountTxt.setText(String.valueOf(totalAmount) + " ₺");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        ArrayAdapter<CharSequence> drinkCountSpinnerAdapter = ArrayAdapter.createFromResource(DetailsActivity.this,
                R.array.Count, android.R.layout.simple_spinner_item);
        drinkCountSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drinkCountSpinner.setAdapter(drinkCountSpinnerAdapter);
        drinkCountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int count = Integer.parseInt(parent.getItemAtPosition(position).toString());
                drinkAmount = drinkPrice * count;
                productTotalAmount = productAmount + snackAmount + drinkAmount;
                productamounttxt.setText(String.valueOf(productTotalAmount) + " ₺");
                totalAmount = ticketAmont + productTotalAmount;
                totalAmountTxt.setText(String.valueOf(totalAmount) + " ₺");
            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<CharSequence> productSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.products, android.R.layout.simple_spinner_item);
        productSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(productSpinnerAdapter);
        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (parent.getItemAtPosition(position).toString()){
                    case "tshirt":
                        productAmount = 50;
                        break;
                    case "flar":
                        productAmount = 15;
                        break;
                    case  "sweater":
                        productAmount = 30;
                        break;
                    case "wristband" :
                        productAmount = 10;
                        break;
                    case "hat":
                        productAmount = 20;
                        break;
                    default:
                        productAmount = 0;
                }

                productTotalAmount = productAmount + snackAmount + drinkAmount;
                productamounttxt.setText(String.valueOf(productTotalAmount) + " ₺");
                totalAmount = ticketAmont + productTotalAmount;
                totalAmountTxt.setText(String.valueOf(totalAmount) + " ₺");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    boolean isExists = false;
    int count = 0;
    public void buy(View view) {
        count = 0;
        if (ticketAmont != 0) {
            if (mUser != null) {
                if (!item.getOrganizerID().equals(mUser.getUid())){
                    Query mQuery = mFirestore.collection("Tickets").document(mUser.getUid()).collection("posts");
                    mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            int a = value.getDocuments().size();
                            for (DocumentSnapshot documentSnapshot: value.getDocuments()) {
                                if (documentSnapshot.getId().equals(item.getPostID())){
                                    isExists = true;
                                }
                                count++;
                            }
                            if (isExists && count == a) {
                                Toast.makeText(DetailsActivity.this, "You have already bought it", Toast.LENGTH_SHORT).show();

                            }else if (!isExists && count == value.getDocuments().size()){
                                Map<String,Object> mData = new HashMap<>();
                                 mData.put("eventname",item.getTitle());
                                 mData.put("url",item.getUrl());
                                 mData.put("location",item.getLocation());
                                 mData.put("date",item.getDate());
                                 mData.put("hour",item.getHour());
                                 mData.put("info",item.getInfo());
                                 mData.put("postID",item.getPostID()) ;
                                 mData.put("latitude",item.getLatitude());
                                 mData.put("longitude",item.getLongitude());
                                 mData.put("price",item.getPrice());
                                 mData.put("count",item.getCount());
                                 mData.put("organizerID",item.getOrganizerID());
                                 mData.put("starCount",item.getStarCount());

                                String drinkCount = (String)drinkCountSpinner.getSelectedItem();
                                String snackCount = (String)snackCountSpinner.getSelectedItem();
                                mData.put("fullfare",fullFareSpinner.getSelectedItem());
                                mData.put("student",studentSpinner.getSelectedItem());
                                mData.put("drink",drinkSpinner.getSelectedItem());
                                mData.put("drinkCount",drinkCount);
                                mData.put("snack",snackSpinner.getSelectedItem());
                                mData.put("snackCount",snackCount);
                                mData.put("product",productSpinner.getSelectedItem());

                                mFirestore.collection("Tickets").document(mUser.getUid()).collection("posts").document(item.getPostID())
                                        .set(mData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(DetailsActivity.this, "Satın Alındı", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(DetailsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    });
                }else {
                    Toast.makeText(this, "Post Sahibi Alamaz", Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            Toast.makeText(this, "Bilet Almanız Gerekmektedir", Toast.LENGTH_SHORT).show();
            //dialog
        }

    }

    public void sendMsg(View view) {
        DocumentReference mRef = mFirestore.collection("User").document(mUser.getUid()).collection("Channel").document(item.getOrganizerID());
        mRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    channelID =  (String) documentSnapshot.get("channelID");

                }else {
                    UUID uuid = UUID.randomUUID();
                    channelID = uuid.toString();
                }

                Map<String,Object> mData = new HashMap<>();
                mData.put("userID",item.getOrganizerID());
                mData.put("username",organizer.getUsername());
                mData.put("userImg",organizer.getDownloadUrl());
                mData.put("channelID",channelID);
                mFirestore.collection("User").document(mUser.getUid()).collection("Channel").document(item.getOrganizerID())
                        .set(mData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent toChat = new Intent(DetailsActivity.this,ChatActivity.class);
                        toChat.putExtra("hedefID",item.getOrganizerID());
                        toChat.putExtra("channelID",channelID);
                        toChat.putExtra("hedefUrl",organizer.getDownloadUrl());
                        toChat.putExtra("hedefUsername",organizer.getUsername());
                        startActivity(toChat);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                mData.put("userID",mUser.getUid());
                mData.put("username",user.getUsername());
                mData.put("userImg",user.getDownloadUrl());
                mData.put("channelID",channelID);
                mFirestore.collection("User").document(item.getOrganizerID()).collection("Channel").document(mUser.getUid())
                        .set(mData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });
    }


    class space extends RecyclerView.ItemDecoration {
        private int space;

        public space(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.top = space;
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
        }
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
        locationManager = (LocationManager) DetailsActivity.this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                SharedPreferences sharedPreferences = DetailsActivity.this.getSharedPreferences("com.furkankerim.eventgo.Activities",MODE_PRIVATE);
                boolean track = sharedPreferences.getBoolean("track",false);
                if (!track){
                    float latitude = Float.parseFloat(item.getLatitude());
                    float longitude = Float.parseFloat(item.getLongitude());
                    LatLng latLng = new LatLng(latitude,longitude);
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    sharedPreferences.edit().putBoolean("track",true).apply();

                }
            }
        };

        if (ContextCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DetailsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                float latitude = Float.parseFloat(item.getLatitude());
                float longitude = Float.parseFloat(item.getLongitude());
                LatLng latLng = new LatLng(latitude,longitude);
                mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            }
        }
    }

    public void share(View view) {
        Intent myIntent = new Intent(Intent.ACTION_SEND);
        myIntent.setType("text/plain");
        myIntent.putExtra(Intent.EXTRA_TEXT, "I bought a ticket for " + item.getTitle() + ". You also must buy.");
        startActivity(Intent.createChooser(myIntent,"share using"));
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

}

