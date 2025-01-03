package com.example.lotto649.Views.Fragments;

import static java.lang.Math.abs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.fragment.app.Fragment;

import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * A Fragment that displays a map with coordinates from Firestore sign-ups.
 * It updates markers on the map based on user sign-up locations and adjusts the map's zoom and center.
 */
public class MapFragment extends Fragment {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private FirebaseFirestore db;
    private CollectionReference signUpsRef;
    private ExtendedFloatingActionButton backButton;
    private ObservableArrayList<GeoPoint> coordsList;
    private View view;

    // when points changed in list, update the map
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

    /**
     * Updates the map with markers based on the coordinates in the coordsList.
     * It also adjusts the map's zoom and center to fit all the markers.
     */
    private void updateMapMarkings() {
        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.getOverlays().clear();
        IMapController mapController = map.getController();
        mapController.setZoom(9.5);
        // default zoom to Edmonton area
        GeoPoint startPoint = new GeoPoint(53.526640, -113.530590);
        mapController.setCenter(startPoint);

        // must have map showing on screen to be able to make changes - return otherwise
        if (getActivity() == null || getActivity().isDestroyed()) {
            Log.e("JASON MAP", "Map is null - return");
            return;
        }

        // get bounding box points to set zoom
        Double northPoint = -90.0;
        Double southPoint = 90.0;
        Double eastPoint = -180.0;
        Double westPoint = 180.0;

        // Good resource for changing location on emulator: https://stackoverflow.com/questions/2279647/how-to-emulate-gps-location-in-the-android-emulator (second answer)
        for (GeoPoint coord : coordsList) {
            // find new bounding points
            if (coord.getLatitude() > northPoint) northPoint = coord.getLatitude();
            if (coord.getLatitude() < southPoint) southPoint = coord.getLatitude();
            if (coord.getLongitude() > eastPoint) eastPoint = coord.getLongitude();
            if (coord.getLongitude() < westPoint) westPoint = coord.getLongitude();
            // create marker for each coordinate on the map
            Marker marker = new Marker(map);
            marker.setPosition(coord);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            Log.e("JASON MAP", "Creating marker!!");
            map.getOverlays().add(marker);
        }
        if (coordsList.size() > 0) {
            // zoom to bounding box, with a little wiggle room to show all points nicely
            if (abs(northPoint - southPoint) <= 1 || abs(eastPoint - westPoint) <= 1) {
                map.zoomToBoundingBox(new BoundingBox(northPoint + 0.5, eastPoint + 0.5, southPoint - 0.5, westPoint - 0.5), false);
            } else {
                map.zoomToBoundingBox(new BoundingBox(northPoint + 1, eastPoint + 1, southPoint - 1, westPoint - 1), false);
            }
        }
    }

    /**
     * Called to create the fragment's view.
     * Initializes the map, sets up the back button, and fetches sign-up data from Firestore.
     *
     * @param inflater           The LayoutInflater to inflate the layout.
     * @param container          The container in which the view will be placed.
     * @param savedInstanceState A bundle containing saved instance state (if any).
     * @return The fragment's root view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_layout, container, false);

        String eventId = getArguments().getString("eventId");
        Log.e("JASON MAP", "Event id from bundle: " + eventId);
        coordsList = new ObservableArrayList<GeoPoint>();
        coordsList.addOnListChangedCallback(listChangedCallback);

        backButton = view.findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.getInstance().popFragment();
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
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String eventIdStr = doc.getString("eventId");
                        if (!eventIdStr.equals(eventId)) {
                            continue;
                        }
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

        updateMapMarkings();

        return view;
    }
}
