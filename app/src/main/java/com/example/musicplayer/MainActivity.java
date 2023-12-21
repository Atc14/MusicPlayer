package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lista;
    String [] elementos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lista = findViewById(R.id.lisViewCancion);
        permisos();

    }
    public void permisos(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        mostrarCancion();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    public ArrayList<File> encontrarCancion(File f){
        ArrayList<File> array = new ArrayList<>();
        File[] archivos = f.listFiles();
        for (File file : archivos){
            if (file.isDirectory() && !file.isHidden()){
                array.addAll(encontrarCancion(file));
            }
            else{
                if (file.getName().endsWith(".mp3")){
                    array.add(file);
                }
            }
        }
        return array;
    }
    public void mostrarCancion(){
        final ArrayList<File> canciones = encontrarCancion(Environment.getExternalStorageDirectory());
        elementos = new String[canciones.size()];
        for (int i = 0; i<canciones.size(); i++){
            elementos[i] = canciones.get(i).getName().replace(".mp3","");
        }
        customAdapter adapter = new customAdapter();
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String nombreCancion = (String) lista.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(),PlayerActivity.class)
                        .putExtra("Canciones",canciones)
                        .putExtra("Nombre",nombreCancion)
                        .putExtra("pos",i));
            }
        });

    }
    class customAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return elementos.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vista = getLayoutInflater().inflate(R.layout.list_cancion, null);
            TextView txtCancion = vista.findViewById(R.id.txtNombreCancion);
            txtCancion.setSelected(true);
            txtCancion.setText(elementos[position]);
            return vista;
        }
    }
}