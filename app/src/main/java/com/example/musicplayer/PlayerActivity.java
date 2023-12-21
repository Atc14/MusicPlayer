package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    ImageButton btnplay, btnpause, btnnext, btnprevious;
    TextView txtCancion, txtInicio, txtFinal;
    ImageView imageView;
    SeekBar reproductor;
    Thread actualizarReproductor;
    String nombreCancion;
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> canciones;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            getOnBackPressedDispatcher();
        }
        return  super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnprevious = findViewById(R.id.btnPrevious);
        btnnext = findViewById(R.id.btnNext);
        btnplay = findViewById(R.id.btnPlay);
        txtCancion = findViewById(R.id.txtCancion);
        txtInicio = findViewById(R.id.txtInicio);
        txtFinal = findViewById(R.id.txtFinal);
        reproductor = findViewById(R.id.reproductor);
        imageView = findViewById(R.id.imageView);
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        canciones = (ArrayList) bundle.getParcelableArrayList("Canciones");
        String nombre = i.getStringExtra("Nombre");
        position = bundle.getInt("pos",0);
        txtCancion.setSelected(true);
        Uri uri = Uri.parse(canciones.get(position).toString());
        nombreCancion = canciones.get(position).getName();
        txtCancion.setText(nombreCancion);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

        actualizarReproductor = new Thread(){
            @Override
            public void run() {
                int duracion = mediaPlayer.getDuration();
                int seg = 0;

                while(seg<duracion){
                    try {
                        sleep(100);
                        seg = mediaPlayer.getCurrentPosition();
                        reproductor.setProgress(seg);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        reproductor.setMax(mediaPlayer.getDuration());
        actualizarReproductor.start();

        reproductor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(reproductor.getProgress());
            }
        });
        String tiempoFinal = actualizarTiempo(mediaPlayer.getDuration());
        txtFinal.setText(tiempoFinal);
        final Handler  handler = new Handler();
        final int delay = 100;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String tiempoActual = actualizarTiempo(mediaPlayer.getCurrentPosition());
                txtInicio.setText(tiempoActual);
                handler.postDelayed(this,delay);

            }
        }, delay);
        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    btnplay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                    Toast.makeText(PlayerActivity.this, "Pause", Toast.LENGTH_SHORT).show();
                }
                else{
                    btnplay.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                    Toast.makeText(PlayerActivity.this, "Play", Toast.LENGTH_SHORT).show();

                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnnext.performClick();
            }
        });
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%canciones.size());
                Uri u = Uri.parse(canciones.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                nombreCancion = canciones.get(position).getName();
                txtCancion.setText(nombreCancion);
                mediaPlayer.start();
                btnplay.setBackgroundResource(R.drawable.ic_pause);
                animacion(view);
            }
        });

        btnprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)<0)?(canciones.size()-1):(position-1);
                Uri u = Uri.parse(canciones.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                nombreCancion = canciones.get(position).getName();
                txtCancion.setText(nombreCancion);
                mediaPlayer.start();
                btnplay.setBackgroundResource(R.drawable.ic_pause);
                animacion(view);
            }
        });
    }
    public void animacion(View vista){
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);

        animator.start();
    }

    public String actualizarTiempo(int duracion){
        String tiempo = "";
        int min = duracion/1000/60;
        int sec = duracion/1000%60;

        tiempo += min+":";

        if(sec<10){
            tiempo += "0";
        }
        tiempo += sec;

        return tiempo;
    }
}