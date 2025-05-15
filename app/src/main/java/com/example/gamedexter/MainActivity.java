package com.example.gamedexter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MainActivity extends AppCompatActivity {
    Button btn;
    Button lbtn;
    Button mapbtn;
    Button quizBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn1);
        lbtn = findViewById(R.id.button);
        mapbtn = findViewById(R.id.btnmap);
        quizBtn = findViewById(R.id.btnquiz);

        lbtn.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
            @SuppressLint("InflateParams") View bottomsheetView = getLayoutInflater().inflate(R.layout.menu_bottom_sheet, null);

            bottomSheetDialog.setContentView(bottomsheetView);
            bottomSheetDialog.show();

            TextView settings = bottomsheetView.findViewById(R.id.settings);
            TextView about = bottomsheetView.findViewById(R.id.about);
            TextView privacy = bottomsheetView.findViewById(R.id.privacy);
            TextView help = bottomsheetView.findViewById(R.id.help);
            TextView profile = bottomsheetView.findViewById(R.id.profile);
            TextView contact = bottomsheetView.findViewById(R.id.contact);
            TextView favourites = bottomsheetView.findViewById(R.id.favourites);

            settings.setOnClickListener(view -> {
                Toast.makeText(MainActivity.this, "Settings clicked", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            });

            about.setOnClickListener(view -> {
                Toast.makeText(MainActivity.this, "about clicked", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            });

            profile.setOnClickListener(view -> {
                Toast.makeText(MainActivity.this, "profile clicked", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            });

            contact.setOnClickListener(view -> {
                Toast.makeText(MainActivity.this, "contact us clicked", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            });

            privacy.setOnClickListener(view -> {
                Toast.makeText(MainActivity.this, "privacy policy clicked", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            });

            favourites.setOnClickListener(view -> {
                Toast.makeText(MainActivity.this, "favourites clicked", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            });

            help.setOnClickListener(view -> {
                Toast.makeText(MainActivity.this, "help clicked", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            });
        });


        btn = findViewById(R.id.btn1);
        mapbtn = findViewById(R.id.btnmap);
        quizBtn = findViewById(R.id.btnquiz);

        View.OnClickListener commonListener = v -> {
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_click));
            v.playSoundEffect(SoundEffectConstants.CLICK);

            int id = v.getId();
            if (id == R.id.btn1) {
                Toast.makeText(MainActivity.this, "Opening Guide...", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(this, GuideActivity.class));
            } else if (id == R.id.btnmap) {
                Toast.makeText(MainActivity.this, "Opening Map...", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(this, MapActivity.class));
            } else if (id == R.id.btnquiz) {
                Toast.makeText(MainActivity.this, "Opening Quizzes...", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(this, QuizActivity.class));
            }
        };

        btn.setOnClickListener(commonListener);
        mapbtn.setOnClickListener(commonListener);
        quizBtn.setOnClickListener(commonListener);

    }
}
