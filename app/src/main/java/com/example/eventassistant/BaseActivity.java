package com.example.eventassistant;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/** Shared toolbar + back-arrow behaviour for every screen except the home screen. */
public abstract class BaseActivity extends AppCompatActivity {

    /** Call after setContentView(). The layout must include layout/toolbar.xml. */
    protected void setupToolbar(@StringRes int titleRes) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleRes);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // back arrow
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
