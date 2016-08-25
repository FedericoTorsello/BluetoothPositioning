/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.unibo.torsello.bluetoothpositioning.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import it.unibo.torsello.bluetoothpositioning.R;

//import com.bumptech.glide.Glide;

public class DeviceDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "cheese_name";


//    private MyCustomObjectListener listener ;

//    public interface MyCustomObjectListener {
//        // These methods are the different events and
//        // need to pass relevant arguments related to the event triggered
//        void onObjectReady();
//    }
//
//    // Assign the listener implementing events interface that will receive the events
//    public void setCustomObjectListener(MyCustomObjectListener listener) {
//        this.listener = listener;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_detail);

        Intent intent = getIntent();
        final String cheeseName = intent.getStringExtra(EXTRA_NAME);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        assert collapsingToolbar != null;
        collapsingToolbar.setTitle(cheeseName);
//        collapsingToolbar.setTitle(String.valueOf(cheeseValue));

        loadBackdrop();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (listener!= null){
//                    listener.onObjectReady();
//                }else{
                Log.i("sds", "wooooow");
//                }
            }
        });
    }

    private void loadBackdrop() {
//        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
//        Glide.with(this).load(Cheeses.getRandomCheeseDrawable()).centerCrop().into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
