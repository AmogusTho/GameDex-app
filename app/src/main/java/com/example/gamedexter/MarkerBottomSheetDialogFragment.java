package com.example.gamedexter;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MarkerBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private String type, description, title;

    // Пустой конструктор обязателен
    public MarkerBottomSheetDialogFragment() {}

    public static MarkerBottomSheetDialogFragment newInstance(String title, String type, String description) {
        MarkerBottomSheetDialogFragment fragment = new MarkerBottomSheetDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("type", type);
        args.putString("description", description);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Получаем дефолтный BottomSheetDialog
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bsDialog = (BottomSheetDialog) dialogInterface;
            View bottomSheet = bsDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(bottomSheet);
                // Размер видимой части в пикселях (можешь подправить)
                int peek = (int) (getResources().getDisplayMetrics().density * 200);
                behavior.setPeekHeight(peek, true);
                behavior.setHideable(false);          // не даём скрыться полностью
                behavior.setFitToContents(false);     // позволяет оставаться между collapsed/expanded
                behavior.setExpandedOffset(peek);     // offset при fully expanded
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marker_bottom_sheet, container, false);

        TextView titleView = view.findViewById(R.id.titleText);
        TextView typeView = view.findViewById(R.id.typeText);
        TextView descView = view.findViewById(R.id.descriptionText);
        Button foundButton = view.findViewById(R.id.foundButton);

        Bundle args = getArguments();
        if (args != null) {
            title = args.getString("title");
            type = args.getString("type");
            description = args.getString("description");
            titleView.setText(title);
            typeView.setText(type);
            descView.setText(description);
        }

        foundButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), title + " marked as found!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            View parent = (View) view.getParent();
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);

            // Делаем его растягиваемым
            behavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            // Устанавливаем максимальную высоту (до почти всего экрана)
            parent.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            parent.requestLayout();
        }
    }
}