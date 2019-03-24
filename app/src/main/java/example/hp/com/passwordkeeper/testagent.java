package example.hp.com.passwordkeeper;

/**
 * Created by HP on 3/11/2016.
 */
import jade.core.AID;
import jade.core.Agent;

public class testagent extends Agent {
    String nickname = "Pip";
    AID id = new AID(nickname, AID.ISLOCALNAME);

    protected void setup() {
        // Printout a welcome message
        System.out.println("Hello! Buyer-agent " +getAID().getName()+" is ready.");
    }

}
