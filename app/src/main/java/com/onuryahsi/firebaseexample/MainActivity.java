package com.onuryahsi.firebaseexample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.onuryahsi.firebaseexample.auth.AuthActivity;
import com.onuryahsi.firebaseexample.model.MyNotification;
import com.onuryahsi.firebaseexample.model.User;
import com.onuryahsi.firebaseexample.notification.NotificationDetailActivity;
import com.onuryahsi.firebaseexample.notification.NotificationsAdapter;
import com.onuryahsi.firebaseexample.util.ClientBroadcastReceiver;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 05.04.2020
 * this app includes firebase push notification
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    ClientBroadcastReceiver broadcastReceiver;

    private RecyclerView recyclerViewNotification;
    private NotificationsAdapter notificationsAdapter;
    private List<MyNotification> allNotifications;

    private Integer ACTION_RESULT_CODE = 0; // if selected Logout or Delete

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getAuthenticatedUser();

        broadcastReceiver = new ClientBroadcastReceiver();

        getFirebaseInstance();

        updateUser(getApplicationContext());
        // readUserChangesFromRemoteDatabase();
        // readUserUpdatesFromRemoteDatabase();

        recyclerViewNotification = findViewById(R.id.recycler_view_notifications);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(this));

        notificationsAdapter = new NotificationsAdapter();

        recyclerViewNotification.setAdapter(notificationsAdapter);
        recyclerViewNotification.setHasFixedSize(true);

        readNotificationsFromRemoteDatabase();

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateUser(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // SearchBar // https://www.youtube.com/watch?v=3aQgSsLkgJo
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.search_in_notifications);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        final List<MyNotification> filteredNotifications = new ArrayList<>();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: search for : " + newText);

                filteredNotifications.clear();

                for (MyNotification notification : allNotifications) {
                    if (notification.content.toLowerCase().contains(newText.toLowerCase())) {
                        filteredNotifications.add(notification);
                        Log.d(TAG, "onQueryTextChange: found : " + newText + " amount: " + filteredNotifications.size());
                    }
                }

                notificationsAdapter.setNotificationList(filteredNotifications);

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void readUserChangesFromRemoteDatabase() {

        final DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference("users");

        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(getApplicationContext(), "Data changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
            }
        });

        // mPostReference.addValueEventListener(notificationListener);
    }

    @SuppressLint("HardwareIds")
    public void updateUser(Context context) {
        final DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference();

        User u = new User();
        u.userId = "" + Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        mUserReference.child("users").child(u.userId).child("lastVisited").setValue(getDate());
    }

    private void readUserUpdatesFromRemoteDatabase() {

        final DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference("users");

        mUserReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @SuppressLint("HardwareIds")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                User u = snapshot.getValue(User.class);

                assert u != null;
                Toast.makeText(MainActivity.this,
                        "Changed: " + u.lastVisited,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // mPostReference.addValueEventListener(notificationListener);
    }

    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss a";

    private String getDate() {
        return new SimpleDateFormat(DATE_FORMAT, Locale.UK).format(System.currentTimeMillis());
    }

    private void readNotificationsFromRemoteDatabase() {

        final DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference("push-messages");

        allNotifications = new ArrayList<>();

        mPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                allNotifications.clear();

                Log.i(TAG, "onDataChanged");

                for (DataSnapshot child : snapshot.getChildren()) {
                    allNotifications.add(child.getValue(MyNotification.class));
                    Log.i(TAG, "Notification count: " + allNotifications.size());
                }
                notificationsAdapter.setNotificationList(allNotifications);
                notificationsAdapter.setOnItemClickListener(new NotificationsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(@NotNull MyNotification myNotification) {
                        // Toast.makeText(getApplicationContext(),"Clicked " + myNotification.message_id, Toast.LENGTH_LONG).show();

                        Intent i = new Intent(MainActivity.this, NotificationDetailActivity.class);
                        i.putExtra("title", myNotification.title);
                        i.putExtra("content", myNotification.content);
                        i.putExtra("message_id", myNotification.message_id);
                        i.putExtra("channel_id", myNotification.channel_id);
                        i.putExtra("is_read", myNotification.isRead);
                        startActivityForResult(i, ACTION_RESULT_CODE);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String mData = data.getStringExtra("ACTION_TYPE");
                if (mData.equals("LOGOUT")) {
                    logoutUser();

                } else if (mData.equals("DELETE")) {
                    deleteUser();

                }
            }
        }
    }

    private void getAuthenticatedUser() {
        FirebaseUser authUser = FirebaseAuth.getInstance()
                .getCurrentUser();
        if (authUser != null) {
            authUser.getEmail();
            authUser.getUid();

            Toast.makeText(this, "User Uid: " + authUser.getUid(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getFirebaseInstance() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "Firebase getInstanceId failed", task.getException());
                            return;
                        } else {// Get new Instance ID token
                            String token = task.getResult().getToken();
                            Log.i(TAG, "Firebase Token : " + token);
                        }
                    }
                });
    }

    private void logoutUser() {
        FirebaseAuth user = FirebaseAuth.getInstance();
        if (user.getCurrentUser() != null) {
            if (!user.getCurrentUser().getUid().equals("")) {
                AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(MainActivity.this, AuthActivity.class));
                        finish();
                    }
                });
            }
        }
    }

    private void deleteUser() {
        FirebaseAuth user = FirebaseAuth.getInstance();
        if (user.getCurrentUser() != null) {
            if (!user.getCurrentUser().getUid().equals("")) {
                AuthUI.getInstance().delete(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(MainActivity.this, AuthActivity.class));
                        finish();
                    }
                });
            }
        }
    }

// https://firebase.google.com/docs/cloud-messaging/android/client
}
