package com.example.groupath;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class BaseActivity extends AppCompatActivity implements TurbolinksAdapter, ActionBottomDialogFragment.ItemClickListener {

    // Change the BASE_URL to an address that your VM or device can hit.
    protected static final String BASE_URL = "https://groupathx.herokuapp.com";
    protected static final String INTENT_URL = "intentUrl";

    protected String location;
    protected TurbolinksView turbolinksView;

    protected BottomAppBar bottomAppBar;
    protected FloatingActionButton fab;

    // -----------------------------------------------------------------------
    // Activity overrides
    // -----------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Set UserAgent
        TurbolinksSession.getDefault(this).getWebView().getSettings().setUserAgentString("AndroidApp");


        // Find the custom TurbolinksView object in your layout
        turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);

        // For this demo app, we force debug logging on. You will only want to do
        // this for debug builds of your app (it is off by default)

        // For this example we set a default location, unless one is passed in through an intent
        location = getIntent().getStringExtra(INTENT_URL) != null ? getIntent().getStringExtra(INTENT_URL) : BASE_URL;

        bottomAppBar = (BottomAppBar) findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);
        bottomAppBar.setVisibility(View.INVISIBLE);

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSnackbar(view, "Test text");
            }
        });
        fab.hide();

        // Execute the visit

        View progressView = (View) findViewById(R.id.frameLayout);
        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .progressView(progressView, R.id.indeterminateBar, 200)
                .visit(location);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // Since the webView is shared between activities, we need to tell Turbolinks
        // to load the location from the previous activity upon restarting
        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .restoreWithCachedSnapshot(true)
                .view(turbolinksView)
                .visit(location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottom_navigation_menu, menu);
        return true;
    }

    @Override
    public void onItemClick(String item) {
        Toast.makeText(BaseActivity.this, item, Toast.LENGTH_SHORT)
                .show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getClass().getSimpleName().equals("ActionMenuItem")) {
            // Open Drawer
            Toast.makeText(BaseActivity.this, "Open Drawer", Toast.LENGTH_SHORT)
                    .show();
            showBottomSheet(findViewById(R.id.frameLayout));
        } else {
            // Regular MenuItem (MenuItemImpl Object)
            Toast.makeText(BaseActivity.this, item.getTitle().toString(), Toast.LENGTH_SHORT)
                    .show();
        }
        return true;
    }

    public void showBottomSheet(View view) {
        ActionBottomDialogFragment addPhotoBottomDialogFragment =
                ActionBottomDialogFragment.newInstance();
        addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                ActionBottomDialogFragment.TAG);
    }

    // -----------------------------------------------------------------------
    // TurbolinksAdapter interface
    // -----------------------------------------------------------------------

    @Override
    public void onPageFinished() {

    }

    @Override
    public void onReceivedError(int errorCode) {
        handleError(errorCode);
    }

    @Override
    public void pageInvalidated() {

    }

    @Override
    public void requestFailedWithStatusCode(int statusCode) {
        handleError(statusCode);
    }

    @Override
    public void visitCompleted() {
        bottomAppBar.setVisibility(View.VISIBLE);
        fab.show();
    }

    // The starting point for any href clicked inside a Turbolinks enabled site. In a simple case
    // you can just open another activity, or in more complex cases, this would be a good spot for
    // routing logic to take you to the right place within your app.
    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        Intent intent;
        String[] pathArr = getSplitPath(location);
        // Group Operations
        if(pathArr[1].equals("g")){
            if(pathArr.length == 5 && pathArr[3].equals("m")){
                // Message Screen
                intent = new Intent(this, MessageActivity.class);
            }
            else {
                // Group Dashboard Screen
                intent = new Intent(this, GroupActivity.class);
            }
        }
        else {
            // Regular Screen
            intent = new Intent(this, MainActivity.class);
        }
        intent.putExtra(INTENT_URL, location);

        // In here we can control our routing
        // Location is our full url of clicked turbolinks-enabled link.

        //Toast.makeText(this, getSplitPath(location)[1] + " ~> " + action, Toast.LENGTH_SHORT)
        //        .show();

        this.startActivity(intent);

    }

    // -----------------------------------------------------------------------
    // Protected
    // -----------------------------------------------------------------------

    protected void showSnackbar(View view,String text){
        fab.hide();
        bottomAppBar.setVisibility(View.INVISIBLE);
        final Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        snackbar.addCallback(
            new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    bottomAppBar.setVisibility(View.VISIBLE);
                    fab.show();
                }

                @Override
                public void onShown(Snackbar snackbar) {

                }
            }
        );
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    // Simply forwards to an error page, but you could alternatively show your own native screen
    // or do whatever other kind of error handling you want.
    private void handleError(int code) {
        if (code == 0){
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT)
                    .show();
        }
        else if (code == 404) {
            TurbolinksSession.getDefault(this)
                    .activity(this)
                    .adapter(this)
                    .restoreWithCachedSnapshot(false)
                    .view(turbolinksView)
                    .visit(BASE_URL + "/error");
        }
        else {
            Toast.makeText(this, "handleError" + String.valueOf(code), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private String getPath(String location){
        return location.substring(BASE_URL.length());
    }

    private String[] getSplitPath(String location){
        String path = getPath(location);
        return path.split("/");
    }
}
