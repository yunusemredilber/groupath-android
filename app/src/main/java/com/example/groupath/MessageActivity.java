package com.example.groupath;

import android.os.Bundle;

import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.View;

public class MessageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Set UserAgent
        TurbolinksSession.getDefault(this).getWebView().getSettings().setUserAgentString("AndroidApp");


        // Find the custom TurbolinksView object in your layout
        turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);

        // For this demo app, we force debug logging on. You will only want to do
        // this for debug builds of your app (it is off by default)

        // For this example we set a default location, unless one is passed in through an intent
        location = getIntent().getStringExtra(INTENT_URL) != null ? getIntent().getStringExtra(INTENT_URL) : BASE_URL;

        // Execute the visit

        View progressView = (View) findViewById(R.id.frameLayout);
        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .progressView(progressView, R.id.indeterminateBar, 200)
                .visit(location);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusInput();
            }
        });
    }

    public void focusInput(){
        String script = "document.activeElement.blur();" +
                        "document.getElementById('comment_text').focus();";
        findViewById(R.id.turbolinks_view).requestFocus();
        TurbolinksSession.getDefault(this).getWebView()
                .evaluateJavascript(script,null);
    }

}
