package example.hp.com.passwordkeeper;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.Key;

public class Testing extends Activity{
    public static String seedValue = "I AM UNBREAKABLE";
    public static String MESSAGE = "No one can read this message without decrypting me.";
    public  String encryptedData, decryptedData, message,result;
    public byte[] test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        TextView txe = (TextView) findViewById(R.id.txe);
        TextView txe1 = (TextView) findViewById(R.id.txe1);

        try {

            encryptedData = AESHelper.encrypt(seedValue, MESSAGE);
            txe.setText(encryptedData);
            //Log.v("EncryptDecrypt", "Encoded String " + encryptedData);

            result = AESHelper.decrypt(seedValue, encryptedData);
            //decryptedData = AESHelper.decrypt(seedValue, AESHelper.encrypt(seedValue, MESSAGE));
            //Log.v("EncryptDecrypt", "Decoded String " + decryptedData);
            txe1.setText(result);

        } catch (Exception e) {
            message = "Message Cannot Be Decrypted!";
            txe1.setText(message);
            e.printStackTrace();
        }
    }

    public static byte[] hex2byte(byte[] b) {

        if ((b.length % 2) != 0)

            throw new IllegalArgumentException("hello");

        byte[] b2 = new byte[b.length / 2];

        for (int n = 0; n < b.length; n += 2) {

            String item = new String(b, n, 2);

            b2[n / 2] = (byte) Integer.parseInt(item, 16);

        }

        return b2;

    }

    // decryption function

    public static byte[] decryptSMS(String secretKeyString, byte[] encryptedMsg)

            throws Exception {

        // generate AES secret key from the user input secret key

        Key key = generateKey(secretKeyString);

        // get the cipher algorithm for AES

        Cipher c = Cipher.getInstance("AES");

        // specify the decryption mode

        c.init(Cipher.DECRYPT_MODE, key);

        // decrypt the message

        byte[] decValue = c.doFinal(encryptedMsg);

        return decValue;

    }

    private static Key generateKey(String secretKeyString) throws Exception {

        // generate AES secret key from a String

        Key key = new SecretKeySpec(secretKeyString.getBytes(), "AES");

        return key;

    }


}
