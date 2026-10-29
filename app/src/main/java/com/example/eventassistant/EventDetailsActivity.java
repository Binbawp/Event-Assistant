package com.example.eventassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Shows one event with all its details and its sessions (feature c).
 * From here the admin can edit/delete the event, search its sessions,
 * add a session (+), edit a session (tap) or delete a session (long press).
 */
public class EventDetailsActivity extends BaseActivity implements SessionAdapter.OnSessionActionListener {

    public static final String EXTRA_EVENT_ID = "event_id";

    private DatabaseHelper dbHelper;
    private SessionAdapter adapter;
    private long eventId;
    private Event event;
    private String currentQuery = "";

    private TextView tvName, tvSessionsTitle, tvNoSessions;
    private TextView tvDate, tvLocation, tvOrganiser, tvDescription, tvEmail, tvPhone;
    private View rowLocation, rowOrganiser, rowDescription, rowEmail, rowPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        setupToolbar(R.string.event_details_title);

        dbHelper = new DatabaseHelper(this);
        eventId = getIntent().getLongExtra(EXTRA_EVENT_ID, -1);

        bindViews();

        RecyclerView rvSessions = findViewById(R.id.rvSessions);
        rvSessions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SessionAdapter(this);
        rvSessions.setAdapter(adapter);

        // Search this event's sessions by name or description while typing
        SearchView searchView = findViewById(R.id.searchSessions);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText;
                loadSessions();
                return true;
            }
        });

        FloatingActionButton fabAdd = findViewById(R.id.fabAddSession);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditSessionActivity.class);
            intent.putExtra(AddEditSessionActivity.EXTRA_EVENT_ID, eventId);
            startActivity(intent);
        });
    }

    /** Reload on return so edits made in other screens appear straight away. */
    @Override
    protected void onResume() {
        super.onResume();
        if (loadEvent()) {
            loadSessions();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    private void bindViews() {
        tvName = findViewById(R.id.tvEventName);
        tvDate = findViewById(R.id.tvDate);
        tvLocation = findViewById(R.id.tvLocation);
        tvOrganiser = findViewById(R.id.tvOrganiser);
        tvDescription = findViewById(R.id.tvDescription);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        rowLocation = findViewById(R.id.rowLocation);
        rowOrganiser = findViewById(R.id.rowOrganiser);
        rowDescription = findViewById(R.id.rowDescription);
        rowEmail = findViewById(R.id.rowEmail);
        rowPhone = findViewById(R.id.rowPhone);
        tvSessionsTitle = findViewById(R.id.tvSessionsTitle);
        tvNoSessions = findViewById(R.id.tvNoSessions);
    }

    /** Loads the event and fills the details card. Returns false (and closes) if it was deleted. */
    private boolean loadEvent() {
        event = dbHelper.getEvent(eventId);
        if (event == null) {
            finish();
            return false;
        }

        tvName.setText(event.getName());
        tvDate.setText(event.getDate());
        bindRow(rowLocation, tvLocation, event.getLocation());
        bindRow(rowOrganiser, tvOrganiser, event.getOrganiser());
        bindRow(rowDescription, tvDescription, event.getDescription());
        bindRow(rowEmail, tvEmail, event.getContactEmail());
        bindRow(rowPhone, tvPhone, event.getContactPhone());

        // Total number of sessions for this event (not affected by the search box)
        tvSessionsTitle.setText(getString(R.string.sessions_title, event.getSessionCount()));
        return true;
    }

    /** Shows a label/value row only when the (optional) value has been entered. */
    private void bindRow(View row, TextView valueView, String value) {
        if (value == null || value.isEmpty()) {
            row.setVisibility(View.GONE);
        } else {
            row.setVisibility(View.VISIBLE);
            valueView.setText(value);
        }
    }

    private void loadSessions() {
        List<Session> sessions = dbHelper.getSessions(eventId, currentQuery);
        adapter.setSessions(sessions);

        if (sessions.isEmpty()) {
            tvNoSessions.setText(currentQuery.trim().isEmpty()
                    ? R.string.no_sessions : R.string.no_sessions_match);
            tvNoSessions.setVisibility(View.VISIBLE);
        } else {
            tvNoSessions.setVisibility(View.GONE);
        }
    }

    // ---------------------------------------------------------------------
    // Menu: edit / delete the event
    // ---------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_event) {
            Intent intent = new Intent(this, AddEditEventActivity.class);
            intent.putExtra(AddEditEventActivity.EXTRA_EVENT_ID, eventId);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_delete_event) {
            confirmDeleteEvent();
            return true;
        }
        return super.onOptionsItemSelected(item);   // handles the back arrow
    }

    private void confirmDeleteEvent() {
        if (event == null) {
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, event.getName(), event.getSessionCount()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    dbHelper.deleteEvent(eventId);       // its sessions are deleted by the cascade
                    finish();
                })
                .show();
    }

    // ---------------------------------------------------------------------
    // SessionAdapter.OnSessionActionListener
    // ---------------------------------------------------------------------

    @Override
    public void onSessionClick(Session session) {
        Intent intent = new Intent(this, AddEditSessionActivity.class);
        intent.putExtra(AddEditSessionActivity.EXTRA_EVENT_ID, eventId);
        intent.putExtra(AddEditSessionActivity.EXTRA_SESSION_ID, session.getId());
        startActivity(intent);
    }

    @Override
    public void onSessionLongClick(Session session) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_session_title)
                .setMessage(getString(R.string.delete_session_message, session.getName()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    dbHelper.deleteSession(session);
                    if (loadEvent()) {                    // refresh the session count too
                        loadSessions();
                    }
                })
                .show();
    }
}
