package com.example.bille;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bille.R;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private TextView textX;
    private TextView textY;
    private TextView textZ;
    private TextView levelIndicator;
    private ImageView bille;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupérer les vues
        textX = findViewById(R.id.axeX);
        textY = findViewById(R.id.axeY);
        textZ = findViewById(R.id.axeZ);
        levelIndicator = findViewById(R.id.sensorTextView);
        bille = findViewById(R.id.bille);

        // Initialisation du gestionnaire de capteurs
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Enregistrer les capteurs
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Désenregistrer les capteurs
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null) return;

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                // Accéléromètre : fournit les axes X, Y, Z
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                textX.setText(String.format("Axe X : %.2f", x));
                textY.setText(String.format("Axe Y : %.2f", y));
                textZ.setText(String.format("Axe Z : %.2f", z));

                // Calcul de l'inclinaison
                double inclination = Math.atan2(y, x) * (180 / Math.PI);
                levelIndicator.setText(String.format("Inclinaison : %.2f°", inclination));

                // Déplacer la bille en fonction de l'inclinaison (inversion des axes)
                moveBille(-x, -y); // Inverser les valeurs de x et y pour un mouvement plus naturel
                break;

            case Sensor.TYPE_GYROSCOPE:
                // Utilisation du gyroscope (non nécessaire ici, mais peut être utilisé pour d'autres fonctionnalités)
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // On ignore les changements de précision
    }

    // Fonction pour déplacer la bille
    // Fonction pour déplacer la bille
    private void moveBille(float x, float y) {
        // Limites de l'écran
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Calcul de la position de la bille en fonction de l'inclinaison (inversion des axes)
        float billeX = (x / SensorManager.GRAVITY_EARTH) * (screenWidth / 2) + (screenWidth / 2);
        float billeY = -(y / SensorManager.GRAVITY_EARTH) * (screenHeight / 2) + (screenHeight / 2);

        // Limiter la position de la bille pour éviter qu'elle sorte de l'écran
        float billeWidth = bille.getWidth();
        float billeHeight = bille.getHeight();

        // Limiter billeX à l'écran horizontalement
        float constrainedX = Math.max(0, Math.min(billeX, screenWidth - billeWidth));

        // Limiter billeY pour qu'elle ne dépasse pas le bas de l'écran
        float constrainedY = Math.max(0, Math.min(billeY, screenHeight - billeHeight));

        // Mettre à jour la position de la bille
        bille.animate().x(constrainedX).y(constrainedY).setDuration(0).start();
    }

}
