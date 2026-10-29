package com.example.eventassistant;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Home screen: lists every event, lets the admin search them, delete them (long press)
 * add events (+), open an event's details (tap), run advanced search and (later) upload to the cloud.
 */
public class MainActivity extends AppCompatActivity implements EventAdapter.OnEventActionListener {

    private DatabaseHelper dbHelper;
    private EventAdapter adapter;
    private TextView tvEmpty;
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));

        dbHelper = new DatabaseHelper(this);
        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rvEvents = findViewById(R.id.rvEvents);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(this);
        rvEvents.setAdapter(adapter);

        // Filter the list live as the user types
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;   // list is already filtered while typing
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText;
                loadEvents();
                return true;
            }
        });

        findViewById(R.id.btnAdvancedSearch).setOnClickListener(
                v -> startActivity(new Intent(this, AdvancedSearchActivity.class)));
        FloatingActionButton fabAdd = findViewById(R.id.fabAddEvent);
        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, AddEditEventActivity.class)));
    }

    /** Reload when returning from other screens so changes appear straight away. */
    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    /** Reads events from SQLite (filtered by the search text) and refreshes the list. */
    private void loadEvents() {
        List<Event> events = dbHelper.getEvents(currentQuery);
        adapter.setEvents(events);

        if (events.isEmpty()) {
            tvEmpty.setText(currentQuery.trim().isEmpty() ? R.string.empty_events : R.string.empty_search);
            tvEmpty.setVisibility(TextView.VISIBLE);
        } else {
            tvEmpty.setVisibility(TextView.GONE);
        }
    }

    // ---------------------------------------------------------------------
    // Menu
    // ---------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_upload) {
            showMessage(R.string.coming_soon);          // feature e
            return true;
        } else if (id == R.id.action_sample_data) {
            dbHelper.insertSampleData();                 // temporary - remove before submitting
            loadEvents();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ---------------------------------------------------------------------
    // EventAdapter.OnEventActionListener
    // ---------------------------------------------------------------------

    @Override
    public void onEventClick(Event event) {
        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, event.getId());
        startActivity(intent);
    }

    @Override
    public void onEventLongClick(Event event) {
        // Ask before deleting, and warn that the event's sessions go with it
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, event.getName(), event.getSessionCount()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    dbHelper.deleteEvent(event.getId());
                    loadEvents();
                })
                .show();
    }

    private void showMessage(@StringRes int messageId) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }
}