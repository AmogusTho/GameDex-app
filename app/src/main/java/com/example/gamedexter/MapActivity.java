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

    private static final float MARKER_SIZE_DP =64f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        photoView = findViewById(R.id.rdr2map);
        mapContainer = findViewById(R.id.map_container);

        photoView.setImageResource(R.drawable.mapmapmap);
        photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        photoView.setMinimumScale(1.0f);
        photoView.setMaximumScale(7.0f);
        photoView.setMediumScale(3.5f);

        addMarker(0.7f, 0.6f, R.drawable.legfish11, "Legendary baliq", "Legendary Fish", "Fish that Maxar eats every day in ATYRAAAAU!!!!!");
        addMarker(0.797f, 0.446f, R.drawable.vosclicat, "Pleasance", "Point of Interest", "");
        //addMarker(0.5f, 0.5f, R.drawable.diamond);

        photoView.setOnMatrixChangeListener(rect -> updateMarkerPositions());

        // Обновим позиции после полной инициализации PhotoView
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

        // 👇 Устанавливаем клик по метке
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
