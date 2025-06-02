package com.example.gamedexter;

import android.graphics.Matrix;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private PhotoView photoView;
    private FrameLayout mapContainer;
    private List<MapMarker> mapMarkers = new ArrayList<>();

    private static final float MARKER_SIZE_DP =50f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        photoView = findViewById(R.id.rdr2map);
        mapContainer = findViewById(R.id.map_container);

        photoView.setImageResource(R.drawable.mapmapmap);
        photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        photoView.setMinimumScale(1.0f);
        photoView.setMaximumScale(11.0f);
        photoView.setMediumScale(5.5f);

        addMarker(0.701f, 0.58f, R.drawable.legfish11, "Legendary Bluegill", "Legendary Fish", "<b>Lure:</b> Special Lake Lure" +
                "<br><br><b>Requirements:</b> Available on a fishing trip with Kieran in Chapter 3<br><br><b>Advanced Reeling-In Technique:</b> When the fish is tired out, tilt Left Stick downward to pull your rod up and quickly reel it in. Repeat after a second or two - pull up and reel in. Repeat as often as possible until the fish starts struggling again.");
        addMarker(0.797f, 0.446f, R.drawable.vosclicat, "Pleasance", "Point of Interest", "");
        addMarker(0.749f, 0.562f, R.drawable.diamond, "Gold Bar", "Treasure", "A prisoner chained to the rocks outside of Crawdad Willies Shack will give you the location of this hidden treasure.<br><br><b>Location:</b> In the middle of 3 large rocks by a tree.");
        addMarker(0.717f, 0.633f, R.drawable.vosclicat, "Braithwaites' Secret", "Point of Interest", "A physically deformed and mentally ill woman can be found locked inside this outhouse.");
        addMarker(0.772f, 0.615f, R.drawable.diamond, "Landmarks of Riches Treasure Hunt - Map 4", "Treasure", "Inside the tree on the hill at the center of the Bolger Glade civil war battlefield.");
        addMarker(0.773f, 0.617f, R.drawable.vosclicat, "Bolger Glade", "Point of Interest", "Approach the cannons in the middle of the battlefield to add this POI to your journal.");
        addMarker(0.7805f, 0.6177f, R.drawable.vosclicat, "Abandoned Church", "Point of Interest", "");
        addMarker(0.709f, 0.568f, R.drawable.stranger, "To The Ends of the Earth (IV)", "Stranger", "<b>Quest Giver:</b> William\n" +
                "<br><br><b>Requirements:</b> Chapter 3 onwards");
        addMarker(0.7465f, 0.551f, R.drawable.stranger, "The Iniquities of History", "Stranger", "<b>Quest Giver:</b> Jeremiah Compson\n" +
                "<br><br><b>Region:</b>Rhodes<br><br><b>Requirements:</b> Chapter 3");
        addMarker(0.752f, 0.563f, R.drawable.stranger, "The Ties that Bind Us", "Stranger", "A prisoner chained to the rocks outside of Crawdad Willies Shack will give you the location of this hidden treasure.<br><br><b>Location:</b> In the middle of 3 large rocks by a tree.");
        addMarker(0.7595f, 0.556f, R.drawable.stranger, "No Good Deed", "Stranger", "<b>Quest Giver:</b> Dr. Alphonse Renaud" +
                "                \"<br><br><b>Region:</b> Rhodes<br><br><b>Requirements:</b> Chapter 3 after finishing The Course of True Love <br><br><b>Reward:</b> Special Health Cure Pamphlet");
        addMarker(0.797f, 0.56f, R.drawable.vosclicat,
                "Gray's Secret", "Point of Interest", "On the front porch. Only appears after completing the Chapter 3 mission \"Horse Flesh for Dinner\"");
        addMarker(0.831f, 0.585f, R.drawable.legfish11, "Legendary Lake Sturgeon", "Legendary Fish", "<b>Lure:</b> Special River Lure" +
                "<br><br><b>Advanced Reeling-In Technique:</b> When the fish is tired out, tilt Left Stick downward to pull your rod up and quickly reel it in. Repeat after a second or two - pull up and reel in. Repeat as often as possible until the fish starts struggling again.");
        addMarker(0.8393f, 0.482f, R.drawable.legfish11, "Legendary Longnose Gar", "Legendary Fish", "<b>Lure:</b> Special Swamp Lure" +
                "<br><br><b>Advanced Reeling-In Technique:</b> When the fish is tired out, tilt Left Stick downward to pull your rod up and quickly reel it in. Repeat after a second or two - pull up and reel in. Repeat as often as possible until the fish starts struggling again.");
        addMarker(0.925f, 0.4835f, R.drawable.legfish11, "Legendary Bullhead Catfish", "Legendary Fish", "<b>Lure:</b> Special Swamp Lure" +
                "<br><br><b>Advanced Reeling-In Technique:</b> When the fish is tired out, tilt Left Stick downward to pull your rod up and quickly reel it in. Repeat after a second or two - pull up and reel in. Repeat as often as possible until the fish starts struggling again.");
        addMarker(0.915f, 0.35f, R.drawable.legfish11, "Legendary Muskie", "Legendary Fish", "<b>Lure:</b> Special River Lure" +
                "<br><br><b>Advanced Reeling-In Technique:</b> When the fish is tired out, tilt Left Stick downward to pull your rod up and quickly reel it in. Repeat after a second or two - pull up and reel in. Repeat as often as possible until the fish starts struggling again.");




        photoView.setOnMatrixChangeListener(rect -> updateMarkerPositions());

        photoView.post(this::updateMarkerPositions);
    }

    private void addMarker(float relativeX, float relativeY, int drawableRes,
                           String title, String type, String description) {

        ImageView markerView = new ImageView(MapActivity.this);
        markerView.setImageDrawable(ContextCompat.getDrawable(MapActivity.this, drawableRes));

        int sizePx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                MARKER_SIZE_DP,
                getResources().getDisplayMetrics()
        );

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(sizePx, sizePx);
        mapContainer.addView(markerView, params);

        MapMarker marker = new MapMarker(relativeX, relativeY, markerView, title, type, description);
        mapMarkers.add(marker);

        //  Устанавливаем клик по метке
        markerView.setOnClickListener(v -> {
            showBottomSheet(marker);
        });
    }

    private void updateMarkerPositions() {
        Matrix matrix = new Matrix();
        photoView.getDisplayMatrix(matrix);
        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);

        float scale = matrixValues[Matrix.MSCALE_X];
        float transX = matrixValues[Matrix.MTRANS_X];
        float transY = matrixValues[Matrix.MTRANS_Y];

        int drawableWidth = photoView.getDrawable().getIntrinsicWidth();
        int drawableHeight = photoView.getDrawable().getIntrinsicHeight();

        for (MapMarker marker : mapMarkers) {
            float x = marker.MapX * drawableWidth * scale + transX;
            float y = marker.MapY * drawableHeight * scale + transY;

            marker.view.setX(x - marker.view.getWidth() / 2f);
            marker.view.setY(y - marker.view.getHeight() / 2f);
        }
    }

    private void showBottomSheet(MapMarker marker) {
        MarkerBottomSheetDialogFragment fragment = MarkerBottomSheetDialogFragment.newInstance(
                marker.title,
                marker.type,
                marker.description
        );
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }
}
