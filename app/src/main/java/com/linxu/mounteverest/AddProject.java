package com.linxu.mounteverest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by lin xu on 15.12.2016.
 */
public class AddProject extends AppCompatActivity {
    private CustomSliderView customSliderView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_project);
        customSliderView = (CustomSliderView)findViewById(R.id.slider);
        customSliderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddProject.this, "box clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
