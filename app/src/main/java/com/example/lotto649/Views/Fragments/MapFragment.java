package com.example.lotto649.Views.Fragments;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.A;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private FirebaseFirestore db;
    private CollectionReference signUpsRef;
    private ExtendedFloatingActionButton backButton;
    private ObservableArrayList<GeoPoint> coordsList;

    ObservableList.OnListChangedCallback<ObservableArrayList> listChangedCallback = new ObservableList.OnListChangedCallback<ObservableArrayList>() {
        @Override
        public void onChanged(ObservableArrayList sender) {
            updateMapMarkings();
        }

        @Override
        public void onItemRangeChanged(ObservableArrayList sender, int positionStart, int itemCount) {
            updateMapMarkings();
        }

        @Override
        public void onItemRangeInserted(ObservableArrayList sender, int positionStart, int itemCount) {
            updateMapMarkings();
        }

        @Override
        public void onItemRangeMoved(ObservableArrayList sender, int fromPosition, int toPosition, int itemCount) {
            updateMapMarkings();
        }

        @Override
        public void onItemRangeRemoved(ObservableArrayList sender, int positionStart, int itemCount) {
            updateMapMarkings();
        }
    };

    private void updateMapMarkings() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(53.526640, -113.530590);
        mapController.setCenter(startPoint);

        // Good resource for changing location on emulator: https://stackoverflow.com/questions/2279647/how-to-emulate-gps-location-in-the-android-emulator (second answer)
        for (GeoPoint coord: coordsList) {
            Marker marker = new Marker(map);
            marker.setPosition(coord);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            Log.e("JASON MAP", "Creating marker!!");
            map.getOverlays().add(marker);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_layout, container, false);

        String eventId = getArguments().getString("eventId");
        Log.e("JASON MAP", "Event id from bundle: " + eventId);
        coordsList = new ObservableArrayList<GeoPoint>();
        coordsList.addOnListChangedCallback(listChangedCallback);

        backButton = view.findViewById(R.id.back_button);

        // TODO: replace this with Isaac's stack replace code
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.getInstance().replaceFragment(new HomeFragment());
            }
        });

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        signUpsRef = db.collection("signUps");

        signUpsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (querySnapshots != null) {
                    Log.e("JASON MAP", "Getting signup information");
                    coordsList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String eventIdStr = doc.getString("eventId");
                        if (!eventIdStr.equals(eventId)) {
                            continue;
                        }
                        String userId = doc.getString("userId");
                        String latStr = doc.getString("latitude");
                        String longStr = doc.getString("longitude");
                        if (latStr != null && longStr != null && !latStr.isEmpty() && !longStr.isEmpty()) {
                            Log.e("JASON MAP", "Adding coord to coordsList");
                            coordsList.add(new GeoPoint(Double.parseDouble(latStr), Double.parseDouble(longStr)));
                        }
                    }
                }
            }
        });

        map = (MapView) view.findViewById(R.id.map);
        updateMapMarkings();

        return view;
    }
}
