package ue.edu.co.recursospermisos;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private Activity activity;
    //Version Android
    private TextView versionAndroid;
    private int versionSDK;
    //Bateria
    private ProgressBar pbLevelBattery;

    private TextView tvLevelBattery;
    //Pregunta examen
    private IntentFilter batteryFilter;
    //Conexión
    private TextView tvConexion;
    private ConnectivityManager connexManager;
    //Linterna
    private CameraManager cameraManager;
    private String cameraId;
    private Button onFlash;
    private Button offFlash;
    //File
    private EditText nameFile;
    //private ClFile clFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initObjects();
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.onFlash.setOnClickListener(this::startFlash);
        this.offFlash.setOnClickListener(this::stopFlash);
        registerReceiver(broadcastReceiver, batteryFilter);
    }
    // 6.Bateria
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            pbLevelBattery.setProgress(level);
            tvLevelBattery.setText("Battery level: "+level+ "%");

        }
    };
    //5. Apagar linterna
    private void stopFlash(View view){
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (Exception e) {
            Log.i("FLASH", "stopFlash: ");
            throw new RuntimeException(e);
        }
    }

    //4.Encender la linterna
    private void startFlash(View view){
        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if(cameraManager != null){
                cameraId = cameraManager.getCameraIdList()[0];
                if (!cameraId.isEmpty()){
                    cameraManager.setTorchMode(cameraId, true);
                    Toast.makeText(context, "Prendiendo linterna", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Linterna no disponible", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.i("FLASH", "Error con el flash");
            throw new RuntimeException(e);
        }
    }


    //3.Chequeo de conexión
    private void chekingConexionRed(){
        try { //Manejo de excepciones en tiempo de ejecuccion
            this.connexManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connexManager != null){
                Network network = connexManager.getActiveNetwork();
                if (network != null){
                    //
                    NetworkCapabilities capabilities = connexManager.getNetworkCapabilities(network);
                    boolean hasInternet = capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                    if(hasInternet) tvConexion.setText("Hay internet o conexion a red");
                    else tvConexion.setText("No hay internet o conexion a red");
                }
            }
        } catch (Exception e) {
            Log.i("Conexcion", "chekingConexionRed: "+ e.getMessage());
            throw new RuntimeException(e);
        }
    }
    //2.Implementacion del hook onResume
    @Override
    protected void onResume() {
        super.onResume();
        //Invocacion del metodo
        getVersionAndroid();
        chekingConexionRed();
    }

    //1.Version android
    //Metodo de procedimiento por retornar void
    private void getVersionAndroid(){
        //Variable local
        String systemOperating = Build.VERSION.RELEASE;
        this.versionSDK = Build.VERSION.SDK_INT;
        //Concatenacion y asignacion
        systemOperating += " /SDK "+this.versionSDK;
        this.versionAndroid.setText(systemOperating);
    }

    private void initObjects(){
        //Inicializacion de de los objetos declardados anteriormente
        this.context = getApplicationContext();
        this.activity = this;
        this.versionSDK = -1;
        this.cameraId ="";
        this.versionAndroid = findViewById(R.id.tvVersionAndroid);
        this.pbLevelBattery = findViewById(R.id.pbLevelBattery);
        this.tvLevelBattery = findViewById(R.id.tvLevelBattery);
        this.tvConexion = findViewById(R.id.tvState);
        this.nameFile = findViewById(R.id.etNameFile);
        this.onFlash = findViewById(R.id.btnOn);
        this.offFlash = findViewById(R.id.btnOff);
    }

}