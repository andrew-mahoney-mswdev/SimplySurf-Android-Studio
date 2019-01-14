package com.example.mahoneandr.simplysurf;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText editURL;
    Spinner menu;
    WebView webView;

    static String prefKey = "SimplySurf";
    WebViewClient webViewClient;
    ArrayAdapter<CharSequence> menuAdapter;

    String homePage = "http://www.google.com/";
    String url; //The current URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editURL = (EditText)findViewById(R.id.editURL);
        menu = (Spinner)findViewById(R.id.menu);
        webView = (WebView)findViewById(R.id.webView);

        editURL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) editURL.requestFocus();
                return false;
            }
        });

        editURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditUrlClick();
            }
        });

        editURL.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent e) {
                loadCurrentPage();
                return true;
            }
        });

        menuAdapter = ArrayAdapter.createFromResource(this, R.array.menuItems, android.R.layout.simple_spinner_item);
        menuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menu.setAdapter(menuAdapter);
        menu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                menu.setSelection(0);
                switch(position) {
                    case 1: //Surf button
                        editURL.setVisibility(View.GONE);
                        menu.setVisibility(View.GONE);
                        break;
                    case 2: //Back button
                        webView.goBack();
                        break;
                    case 3: //Forward button
                        webView.goForward();
                        break;
                    case 4: //Home button
                        url = homePage;
                        webView.loadUrl(url);
                        break;
                    case 5: //Reload button
                        webView.loadUrl(url);
                        break;
                    case 6: //Close button
                        url = homePage; //App will reload homepage on legit exit.
                        onExit();
                        System.exit(1);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, String newURL) {
                updateEditUrl(newURL); //Updates text in editURL.
                return false;
            }
            public void onPageFinished(WebView v, String newURL) { //Necessesary to update editURL when back button pressed.
                updateEditUrl(newURL);
            }
        };
        webView.setWebViewClient(webViewClient);

        SharedPreferences prefs = getSharedPreferences(prefKey, MODE_PRIVATE);
        url = prefs.getString("url", homePage);
        editURL.setText(url);
        loadCurrentPage(); //Loads the web page
    }

    @Override
    public void onRestoreInstanceState(Bundle instanceState) {
        super.onRestoreInstanceState(instanceState);
        webView.restoreState(instanceState);
    }

    @Override protected void onStart() {
        super.onStart();
        editURL.setVisibility(View.VISIBLE);
        menu.setVisibility(View.VISIBLE);
    }

    @Override protected void onResume() {
        super.onResume();
    }
    @Override protected void onPause() {
        super.onPause();
    }
    @Override protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle instanceState) {
        super.onSaveInstanceState(instanceState);
        webView.saveState(instanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onExit();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            url = homePage; //Should load the homepage after legitimate exit.
            super.onBackPressed();
        }
    }

    public void onEditUrlClick() { //When the user clicks in the editURL box.
         editURL.selectAll();
    }

    public void loadCurrentPage() { //When the user presses enter on the editURL box.
        url = editURL.getText().toString();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
            editURL.setText((CharSequence)url);
        }
        webView.loadUrl(url);
        editURL.setEnabled(false);
        editURL.setEnabled(true);
        webView.requestFocus();
    }

    public void updateEditUrl(String newURL) { //When the user clicks a link on the webView.
        url = newURL;
        editURL.setText(url);
    }

    public void onExit() {
        SharedPreferences.Editor editor = getSharedPreferences(prefKey, MODE_PRIVATE).edit();
        editor.putString("url", url);
        editor.apply();
    }
}
