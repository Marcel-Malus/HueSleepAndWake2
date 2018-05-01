package com.philips.lighting.hue.demo.huequickstartapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionType;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeResponseCallback;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.BridgeState;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;
import com.philips.lighting.hue.sdk.wrapper.domain.ReturnCode;
import com.philips.lighting.hue.sdk.wrapper.domain.clip.ClipResponse;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightState;

import java.util.List;
import java.util.Random;

/**
 * Since 23/01/2018.
 */
public class MyAppActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "MyAppActivity";
    private static final int MAX_HUE = 65535;

    private Button randomizeLightsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_app);

        randomizeLightsButton = findViewById(R.id.randomize_lights_button);
        randomizeLightsButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == randomizeLightsButton)
            randomizeLights();
    }


    /**
     * Randomize the color of all lights in the bridge
     * The SDK contains an internal processing queue that automatically throttles
     * the rate of requests sent to the bridge, therefore it is safe to
     * perform all light operations at once, even if there are dozens of lights.
     */
    private void randomizeLights() {
        BridgeState bridgeState = BridgeHolder.get().getBridgeState();
        List<LightPoint> lights = bridgeState.getLights();

        Random rand = new Random();

        for (final LightPoint light : lights) {
            final LightState lightState = new LightState();

            lightState.setHue(rand.nextInt(MAX_HUE));

            light.updateState(lightState, BridgeConnectionType.LOCAL, new BridgeResponseCallback() {
                @Override
                public void handleCallback(Bridge bridge, ReturnCode returnCode,
                                           List<ClipResponse> list, List<HueError> errorList) {
                    if (returnCode == ReturnCode.SUCCESS) {
                        Log.i(TAG, "Changed hue of light " + light
                                .getIdentifier() + " to " + lightState.getHue());
                    } else {
                        Log.e(TAG, "Error changing hue of light " + light.getIdentifier());
                        for (HueError error : errorList) {
                            Log.e(TAG, error.toString());
                        }
                    }
                }
            });
        }
    }
}
