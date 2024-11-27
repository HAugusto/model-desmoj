import desmoj.core.simulator.*;

public class Client extends Entity{
    private TimeInstant startTimestamp;

    // Constructor
    public Client(Model owner, String name, boolean showInTrace) {
        super (owner, name, showInTrace);
        startTimestamp = owner.presentTime();
    }

    // Getters
    public TimeInstant getTimeInstant() {
        return startTimestamp;
    }

}