package example.hp.com.passwordkeeper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.FileNotFoundException;

public class ChangeActivity extends AppCompatActivity {

    FirebaseUser user;
    private ImageView accimageupdate;
    private EditText displayname;
    private Button btnupdatechange;
    public String Displayname;
    public Uri photoUrl, targetUri;
    public Bitmap bitmap;
    private TextInputLayout ChangeInputLayoutDisplayname;
    UserProfileChangeRequest profileUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);

        user = FirebaseAuth.getInstance().getCurrentUser();

        accimageupdate = (ImageView) findViewById(R.id.acc_imageupdate);

        ChangeInputLayoutDisplayname = (TextInputLayout) findViewById(R.id.chg_layout_displayname);

        displayname = (EditText) findViewById(R.id.chg_displayname);

        btnupdatechange = (Button) findViewById(R.id.btn_updatechange);

        if (user != null) {
            photoUrl = user.getPhotoUrl();
            Displayname = user.getDisplayName();
        }

        accimageupdate.setImageURI(photoUrl);
        displayname.setText(Displayname);

        accimageupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        btnupdatechange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitchange();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            targetUri = data.getData();
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                accimageupdate.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void submitchange(){

        Displayname = displayname.getText().toString().trim();

        if(!checkDisplayname(Displayname)) {
            return;
        }

        profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(targetUri)
                .setDisplayName(Displayname).build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User profile updated.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Displayname", Displayname);
        startActivity(intent);
    }

    private boolean checkDisplayname(String Displayname) {

        if (Displayname.isEmpty() || !isDisplaynameValid(Displayname)) {

            ChangeInputLayoutDisplayname.setErrorEnabled(true);
            ChangeInputLayoutDisplayname.setError(getString(R.string.err_msg_accname));
            displayname.setError(getString(R.string.err_msg_required));
            requestFocus(displayname);
            return false;
        }
        ChangeInputLayoutDisplayname.setErrorEnabled(false);
        return true;
    }

    private static boolean isDisplaynameValid(String Displayname) {
        return !TextUtils.isEmpty(Displayname);
    }

   private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
