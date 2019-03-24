package example.hp.com.passwordkeeper;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ListAcc {

    public String accname, website, emailusername, password, enckey;

    public ListAcc() {
        // Default constructor required for calls to DataSnapshot.getValue(ListItem.class)
    }

    public ListAcc(String accname, String website, String emailusername, String password, String enckey) {
        this.accname = accname;
        this.website = website;
        this.emailusername = emailusername;
        this.password = password;
        this.enckey = enckey;
    }

    public String getAccname() {
        return accname;
    }

    public String getWebsite() {
        return website;
    }

    public String getEmailusername() {
        return emailusername;
    }

    public String getPassword() {
        return password;
    }

    public String getEnckey() {
        return enckey;
    }

    public void setAccname(String accname) {
        this.accname = accname;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setEmailusername(String emailusername) {
        this.emailusername = emailusername;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnckey(String enckey) {
        this.enckey = enckey;
    }



    @Override
    public String toString() {
        return this.accname;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("accname", accname);
        result.put("website", website);
        result.put("emailusername", emailusername);
        result.put("password", password);
        result.put("enckey", enckey);
        return result;
    }

}

