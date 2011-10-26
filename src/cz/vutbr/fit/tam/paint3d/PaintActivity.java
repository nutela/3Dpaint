package cz.vutbr.fit.tam.paint3d;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class PaintActivity extends Activity implements SensorEventListener {

    public static final String TAG = "3Dpaint|PaintActivity";
    private SensorManager mSensorManager;
    private TextView position;
    private TextView linear;
    private Button button;
    private Boolean isMeasuring = false;
    private List<Float> vertices = new ArrayList<Float>();
    private Float x = new Float(0);
    private Float y = new Float(0);
    private Float z = new Float(0);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        position = (TextView) findViewById(R.id.position);
        button = (Button) findViewById(R.id.button);
        linear = (TextView) findViewById(R.id.linear);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (isMeasuring) {
                    isMeasuring = false;
                    Intent intent = new Intent(PaintActivity.this, ViewActivity.class);
                    float[] result = new float[vertices.size()];
                    for (int i = 0; i < vertices.size(); i++) {
                        result[i] = vertices.get(i).floatValue();
                    }
                    button.setText("Start 3D paint!");
                    intent.putExtra("vertices", result);
                    startActivity(intent);
                } else {
                    isMeasuring = true;
                    vertices = new ArrayList<Float>();
                    x = new Float(0);
                    y = new Float(0);
                    z = new Float(0);
                    button.setText("Stop 3D paint!");
                }
            }
        });
    }

    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                if (isMeasuring) {
                    x += Math.round(event.values[0] * 100) / 100;
                    vertices.add(x);
                    y += Math.round(event.values[1] * 100) / 100;
                    vertices.add(y);
                    z += Math.round(event.values[2] * 100) / 100;
                    vertices.add(z);
                }
                linear.setText("\nLinear:\nx: " + event.values[0] + "\ny: " + event.values[1] + "\nz: " + event.values[2] + "\n");
                position.setText("\nPosition:\nx: " + x + "\ny: " + y + "\nz: " + z + "\n");
                break;
            default:
                break;
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(this);
        super.onStop();
    }
}
