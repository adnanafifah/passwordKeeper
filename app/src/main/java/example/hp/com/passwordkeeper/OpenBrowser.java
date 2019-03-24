package example.hp.com.passwordkeeper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OpenBrowser extends AppCompatActivity {

    FirebaseUser user;
    Firebase myFirebaseRef;
    String key, uri;
    TextView  webAcc, webPass;
    WebView browser;
    ClipboardManager clipboard;
    ClipData clip;
    public static String seedValue;
    public String decWebsite, decEmailUsername, decPassword;
    String js;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openbrowser);
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://passwordkeeper-84e19.firebaseio.com/");

        Bundle bundle = getIntent().getExtras();
        key = bundle.getString("key");

        webAcc = (TextView) findViewById(R.id.webAcc);
        webPass = (TextView) findViewById(R.id.webPass);
        browser = (WebView) findViewById(R.id.webView);

        ValueEventListener postListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(user != null ) {
                    ListAcc person = snapshot.child(user.getUid()).child(key).getValue(ListAcc.class);
                    seedValue = person.getEnckey();
                    try{
                        decWebsite = AESHelper.decrypt(seedValue, person.getWebsite());
                        decEmailUsername = AESHelper.decrypt(seedValue, person.getEmailusername());
                        decPassword = AESHelper.decrypt(seedValue, person.getPassword());
                        webAcc.setText(decEmailUsername);
                        webPass.setText(decPassword);
                        checkURI(decWebsite);

                        startBrowser();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        };
        myFirebaseRef.addListenerForSingleValueEvent(postListener);

        webAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clip = ClipData.newPlainText("label", webAcc.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
            }
        });
        webPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clip = ClipData.newPlainText("label",webPass.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkURI(String web){
        if(!web.startsWith("www.") && !web.startsWith("http://")){
            web = "www."+web;
        }
        if(!web.startsWith("http://")){
            web = "http://"+web;
        }
        uri = web;
    }

    public void startBrowser(){

        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setDomStorageEnabled(true);
        browser.loadUrl(uri);

        browser.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if(uri.equals("http://www.yahoo.com")){
                js = "javascript:{" +
                        "document.getElementById('login-username').value = '" + decEmailUsername + "';"  +
                        "document.getElementById('login-passwd').value = '" + decPassword + "';" +
                        "var frms = document.getElementsByName('mbrMbLogin');" +
                        "frms[0].submit(); };";
                }else if(uri.equals("http://www.facebook.com")){
                    js = "javascript:{" +
                            "document.getElementByName('email').value = '" + decEmailUsername + "';"  +
                            "document.getElementByName('pass').value = '" + decPassword + "';" +
                            "document.getElementsById('u_0_1');}";
                }else if(uri.equals("http://www.twitter.com")){
                    js = "javascript:{" +
                            "document.getElementById('session[username_or_email]').value = '" + decEmailUsername + "';"  +
                            "document.getElementById('session[password]').value = '" + decPassword + "'};";
                }else if(uri.equals("http://www.google.com")){
                    js = "javascript:{" +
                            "document.getElementById('Email').value = '" + decEmailUsername + "';"  +
                            "document.getElementById('Passwd-hidden').value = '" + decPassword + "'};";
                }else if(uri.equals("http://www.instagram.com")){
                    js = "javascript:{" +
                            "document.getElementByName('username').value = '" + decEmailUsername + "';"  +
                            "document.getElementByName('password').value = '" + decPassword + "'};";
                }

                if (Build.VERSION.SDK_INT >= 19) {
                    view.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {

                        }
                    });
                } else {
                    view.loadUrl(js);
                }
            }
        });


    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    }
}


