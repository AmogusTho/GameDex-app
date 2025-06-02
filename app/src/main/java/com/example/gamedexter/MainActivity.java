package com.example.gamedexter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btn, lbtn, mapbtn, quizBtn, pfpBtn, musbtn;
    MediaPlayer mediaPlayer;
    private String currentlyPlayingTrack = null;
    private MusicAdapter musicAdapterRef; // Сохраняем ссылку на адаптер

    static final int REQUEST_AUDIO = 1001;
    ArrayList<TrackItem> trackList = new ArrayList<>();

    // Запуск выбора файла
    private void openAudioFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_AUDIO);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация кнопок
        btn = findViewById(R.id.btn1);
        lbtn = findViewById(R.id.button);
        mapbtn = findViewById(R.id.btnmap);
        quizBtn = findViewById(R.id.btnquiz);
        pfpBtn = findViewById(R.id.buttonpfp);
        musbtn = findViewById(R.id.MusicBtn);

        lbtn.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
            View bottomsheetView = getLayoutInflater().inflate(R.layout.menu_bottom_sheet, null);

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

        // Стартовый трек
        trackList.add(new TrackItem("That's the way it is", R.raw.thatsthewayitis));

        loadCustomTracks();

        View.OnClickListener commonListener = v -> {
            v.playSoundEffect(SoundEffectConstants.CLICK);
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_click));

            int id = v.getId();
            if (id == R.id.btnmap) {
                startActivity(new Intent(this, MapActivity.class));
            } else if (id == R.id.buttonpfp) {
                showProfileDialog(v);
            } else if (id == R.id.MusicBtn) {
                showMusicBottomSheet();
            }
        };

        btn.setOnClickListener(commonListener);
        mapbtn.setOnClickListener(commonListener);
        quizBtn.setOnClickListener(commonListener);
        pfpBtn.setOnClickListener(commonListener);
        musbtn.setOnClickListener(commonListener);
    }

    public void showProfileDialog(View view) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.profile_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    @SuppressLint("MissingInflatedId")
    public void showMusicBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_music, null);
        bottomSheetDialog.setContentView(view);

        SeekBar volumeSeekBar = view.findViewById(R.id.volumeSeekBar);
        RecyclerView tracksRecyclerView = view.findViewById(R.id.tracksRecyclerView);

        SharedPreferences prefs = getSharedPreferences("music_prefs", MODE_PRIVATE);
        int savedVolume = prefs.getInt("volume_level", 100);
        String savedTrack = prefs.getString("playing_track", null);

        // Восстановление состояния play/pause
        for (TrackItem t : trackList) {
            if (savedTrack != null && t.getTitle().equals(savedTrack) && t.isSelected()) {
                t.setPlaying(true);
                currentlyPlayingTrack = savedTrack;
            } else {
                t.setPlaying(false);
            }
        }

        // SeekBar громкости
        volumeSeekBar.setMax(100);
        volumeSeekBar.setProgress(savedVolume);
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(volume, volume);
                }
                prefs.edit().putInt("volume_level", progress).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Первоначальная сортировка
        sortTracksAtStart();

        // 1) LayoutManager
        tracksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2) Адаптер
        musicAdapterRef = new MusicAdapter(this, trackList, new MusicAdapter.OnTrackActionListener() {
            @Override
            public void onPlayClicked(TrackItem track) {
                if (track.isPlaying()) {
                    // --- ПОЛЬЗОВАТЕЛЬ НАЖАЛ «PAUSE» ---
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    track.setPlaying(false);

                    // Важно: удаляем сохранённый playing_track, чтобы при следующем
                    // открытии BottomSheet этот трек не считался «играющим»
                    prefs.edit().remove("playing_track").apply();

                } else {
                    // --- ПОЛЬЗОВАТЕЛЬ НАЖАЛ «PLAY» ---
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    // Сбрасываем флаг isPlaying у всех остальных треков
                    for (TrackItem t : trackList) {
                        t.setPlaying(false);
                    }
                    // Устанавливаем текущему треку isPlaying = true
                    track.setPlaying(true);
                    currentlyPlayingTrack = track.getTitle();

                    // Сохраняем название этого трека в prefs
                    prefs.edit().putString("playing_track", track.getTitle()).apply();

                    try {
                        if (track.isFromRaw()) {
                            mediaPlayer = MediaPlayer.create(getApplicationContext(), track.getResId());
                        } else {
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(getApplicationContext(), track.getFileUri());
                            mediaPlayer.prepare();
                        }
                        float vol = volumeSeekBar.getProgress() / 100f;
                        mediaPlayer.setVolume(vol, vol);
                        mediaPlayer.setLooping(false);
                        mediaPlayer.start();

                        mediaPlayer.setOnCompletionListener(mp -> {
                            track.setPlaying(false);

                            int idx = -1;
                            for (int i = 0; i < trackList.size(); i++) {
                                if (trackList.get(i).getTitle().equals(track.getTitle())) {
                                    idx = i;
                                    break;
                                }
                            }

                            int selCount = musicAdapterRef.getSelectedCount();
                            if (idx >= 0 && selCount > 0) {
                                int nextIdx = (idx + 1) % selCount;
                                TrackItem nextTrack = trackList.get(nextIdx);
                                if (nextTrack.isSelected()) {
                                    nextTrack.setPlaying(true);
                                    currentlyPlayingTrack = nextTrack.getTitle();
                                    prefs.edit().putString("playing_track", nextTrack.getTitle()).apply();
                                    try {
                                        if (nextTrack.isFromRaw()) {
                                            mediaPlayer = MediaPlayer.create(getApplicationContext(), nextTrack.getResId());
                                        } else {
                                            mediaPlayer = new MediaPlayer();
                                            mediaPlayer.setDataSource(getApplicationContext(), nextTrack.getFileUri());
                                            mediaPlayer.prepare();
                                        }
                                        mediaPlayer.setVolume(vol, vol);
                                        mediaPlayer.start();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            musicAdapterRef.notifyDataSetChanged();
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Unable to play this track", Toast.LENGTH_SHORT).show();
                    }
                }

                // Обновляем RecyclerView, чтобы иконка сменилась сразу
                musicAdapterRef.notifyDataSetChanged();
            }

            @Override
            public void onAddMusicClicked() {
                openAudioFilePicker();
            }

            @Override
            public void onCheckboxChanged(TrackItem track, boolean isChecked) {
                // сортировка внутри адаптера, просто перерисуем позже
            }

            @Override
            public void onStopTrack(TrackItem track) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                track.setPlaying(false);
                currentlyPlayingTrack = null;
                prefs.edit().remove("playing_track").apply();
                musicAdapterRef.notifyDataSetChanged();
            }

            @Override
            public void onDeleteTrack(TrackItem track, int position) {
                if (track.isFromRaw()) {
                    Toast.makeText(MainActivity.this,
                            "Cannot delete default track",
                            Toast.LENGTH_SHORT).show();
                    musicAdapterRef.notifyItemChanged(position);
                    return;
                }

                Toast.makeText(MainActivity.this,
                        "Track deleted",
                        Toast.LENGTH_SHORT).show();

                if (track.isPlaying()) {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    currentlyPlayingTrack = null;
                    prefs.edit().remove("playing_track").apply();
                }

                trackList.remove(position);
                saveCustomTracks();
                musicAdapterRef.notifyItemRemoved(position);
            }
        });

        // 3) Назначаем адаптер
        tracksRecyclerView.setAdapter(musicAdapterRef);

        // 4) ItemTouchHelper для drag + swipe
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(musicAdapterRef);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(tracksRecyclerView);

        bottomSheetDialog.show();
    }

    // Запуск выбора аудиофайла

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_AUDIO && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri == null) {
                Toast.makeText(this, "Error picking file", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean alreadyExists = false;
            for (TrackItem existing : trackList) {
                if (!existing.isFromRaw() && existing.getFileUri() != null) {
                    if (existing.getFileUri().toString().equals(uri.toString())) {
                        alreadyExists = true;
                        break;
                    }
                }
            }
            if (alreadyExists) {
                Toast.makeText(this, "This track is already added", Toast.LENGTH_SHORT).show();
                return;
            }

            int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
            try {
                getContentResolver().takePersistableUriPermission(uri, takeFlags);
            } catch (SecurityException | IllegalArgumentException e) {
                e.printStackTrace();
            }

            String title = getFileNameFromUri(uri);
            if (!isSupportedAudioFile(title)) {
                Toast.makeText(this, "Unsupported file format", Toast.LENGTH_SHORT).show();
                return;
            }

            TrackItem newTrack = new TrackItem(title, uri);
            trackList.add(newTrack);
            saveCustomTracks();

            if (musicAdapterRef != null) {
                musicAdapterRef.sortTracksAfterCheckbox();
                int insertedPos = trackList.indexOf(newTrack);
                musicAdapterRef.notifyItemInserted(insertedPos);
            }
        }
    }

    // Вспомогательный метод: сортировать перед показом
    @SuppressLint("NewApi")
    private void sortTracksAtStart() {
        trackList.sort((a, b) -> {
            if (a.isSelected() && !b.isSelected()) return -1;
            if (!a.isSelected() && b.isSelected()) return 1;
            return 0;
        });
    }


    private String getFileNameFromUri(Uri uri) {
        String result = "Unknown";
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        } else if (uri.getScheme().equals("file")) {
            result = new File(uri.getPath()).getName();
        }
        return result;
    }

    private boolean isSupportedAudioFile(String filename) {
        String[] supported = {".mp3", ".wav", ".ogg", ".m4a", ".flac"};
        filename = filename.toLowerCase();
        for (String ext : supported) {
            if (filename.endsWith(ext)) return true;
        }
        return false;
    }

    private void saveCustomTracks() {
        JSONArray jsonArray = new JSONArray();
        for (TrackItem track : trackList) {
            if (track.isFromRaw()) continue;
            JSONObject json = new JSONObject();
            try {
                json.put("title", track.getTitle());
                Uri uri = track.getFileUri();
                if (uri != null) {
                    json.put("uri", uri.toString());
                    jsonArray.put(json);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        prefs.edit().putString("custom_tracks", jsonArray.toString()).apply();
    }


    private void loadCustomTracks() {
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String json = prefs.getString("custom_tracks", null);
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String title = obj.getString("title");
                    Uri uri = Uri.parse(obj.getString("uri"));
                    trackList.add(new TrackItem(title, uri));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }
}
