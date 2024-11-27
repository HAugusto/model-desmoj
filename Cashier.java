import desmoj.core.simulator.*;
import desmoj.core.statistic.*;

public class Cashier extends Entity {
    private int index;
    private boolean status;
    private Client client;
    protected Count totalOccupiedTime;
    protected Count totalClientServed;
    
    public Cashier(Model owner, String name, boolean showInTrace, int index){
        super(owner, name, showInTrace);
        this.index = index;
        this.status = false;
        this.totalOccupiedTime = new Count(owner, "Tempo Ocupado: ", showInTrace, showInTrace);
        this.totalClientServed = new Count(owner, "Clientes Atendidos: ", showInTrace, showInTrace);
    }

    // Getters
    public int getIndex(){
        return this.index;
    }

    public boolean getStatus(){
        return this.status;
    }

    public Client getClient(){
        return this.client;
    }

    public Count getTotalOccupiedTime(){
        return this.totalOccupiedTime;
    }

    public Count getTotalClientServed(){
        return this.totalClientServed;
    }

    // Setters
    public void toggleStatus() {
        this.status = !this.status;
    }

    public void attend(Client client){
        Market modelMarket = (Market) getModel();
        double timeService;
        CashierEvent event;

        this.client = client;
        timeService = modelMarket.getTimeService();
        modelMarket.sendTraceNote(this + " atendeu " + client + " por " + timeService + " minutos.");
        
        this.totalOccupiedTime.update((long) timeService);
        this.totalClientServed.update(1);
        
        event = new CashierEvent(modelMarket, "Evento de fim de atendimento", true);
        event.schedule(this, client, new TimeSpan(timeService));
        modelMarket.countTotalTimestamp.update((long) timeService);
        modelMarket.throughputMarket.update((long) timeService);
        event.sendTraceNote("----------------------------------------------------------------------------");
    }
}
