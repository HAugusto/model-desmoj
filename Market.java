import desmoj.core.dist.*;
import desmoj.core.simulator.*;
import desmoj.core.statistic.*;
import java.util.ArrayList;

public class Market extends Model{
    protected Count countClients;
    protected Count countTotalTimestamp;
    protected Tally averageClientTime;
    protected Tally throughputMarket;
    
    private static double timeSimulation = 720;
    private int countCashiers;
    private ArrayList<Cashier> cashiers;
    private Queue<Cashier> freeCashiers;
    private Queue<Client> queueClients;
    private ContDistUniform distributionClientArrival;
    private ContDistUniform distributionServiceTime;
    private double minClientArrival;
    private double maxClientArrival;
    private double minTimeService;
    private double maxTimeService;


    public Market(Model owner, String name, boolean showInReport, boolean showInTrace, double clientArrival, double timeService, int countCashiers){
        super(owner, name, showInReport, showInTrace);
        this.minClientArrival = clientArrival;
        this.maxClientArrival = clientArrival;
        this.minTimeService = timeService;
        this.maxTimeService = timeService;
        this.countCashiers = countCashiers;
    }
    
    @Override
    public String description(){
        return ("Modelo de eventos discretos de supermercado");
    }

    @Override
    public void init(){
        this.countClients = new Count(this, "Clientes Atendidos: ", true, false);
        this.countTotalTimestamp = new Count(this, "Tempo Total: ", true, false);
        this.averageClientTime = new Tally(this, "Tempo Medio por Cliente: ", true, false);
        this.throughputMarket = new Tally(this, "Throughput do Mercado: ", true, false);
    
        queueClients = new Queue<Client>(this, "Clientes na Fila: ", true, true);
        freeCashiers = new Queue<Cashier>(this, "Caixas na Fila: ", true, true);
        cashiers = new ArrayList<>();

        for(int i = 0; i < countCashiers; i++) {
            try{
                Cashier cashier = new Cashier(this, "Caixa", true, i);
                cashiers.add(cashier);
                freeCashiers.insert(cashier);
            } catch(Exception exception){
                exception.printStackTrace();
            }
        }

        for(int i = 0; i < cashiers.size(); i++){
            Cashier cashier = this.cashiers.get(i);
            cashier.totalClientServed = new Count(this, "Caixa " + (cashier.getIndex() + 1) + " - Clientes Atendidos: ", true, false);
            cashier.totalOccupiedTime = new Count(this, "Caixa " + (cashier.getIndex() + 1) + " - Tempo Ocupado: ", true, false);
        }
        
        distributionClientArrival = new ContDistUniform(this, "Distribuiçao do tempo entre chegadas sucessivas de clientes", minClientArrival - 1, maxClientArrival + 1, true, true);
        distributionClientArrival.setNonNegative(true);
        distributionServiceTime = new ContDistUniform(this, "Distribuiçao do tempo de atendimento dos caixas", minTimeService - 1, maxTimeService + 1, true, true);
        distributionServiceTime.setNonNegative(true);
    }

    @Override
    public void doInitialSchedules(){
        ClientEvent event = new ClientEvent(this, "Evento gerador de cliente", true);
        event.schedule(new TimeSpan(0.0));
    }

    // Getters
    public double getTimestampClientArrival(){
        return (distributionClientArrival.sample());
    }

    public double getTimeService(){
        return (distributionServiceTime.sample());
    }

    public double getTimeSimulation(){
        return timeSimulation;
    }

    public double getCountClients(){
        return countClients.getValue();
    }

    public double getClientsQueue(){
        return queueClients.length();
    }

    public double getAverageClientsQueue(){
        return queueClients.averageLength();
    }
    
    // Functions
    public void serveClient(Client client){
        if(freeCashiers.isEmpty()) queueClients.insert(client);
        else {
            Cashier cashier = freeCashiers.first();
            cashier.toggleStatus();
            cashier.attend(client);
            freeCashiers.remove(cashier);
        }
        sendTraceNote("Fila: " + queueClients.length());
    }

    public void freeCashier(Cashier cashier){
        Client client;
        sendTraceNote("Caixa livre");
        
        if(queueClients.isEmpty()){
            sendTraceNote("Aguardando cliente...");
            sendTraceNote("<h3>--------------------------------</h3>");
            cashier.toggleStatus();
        } else {
            sendTraceNote("Realocando caixa");
            client = queueClients.first();
            queueClients.remove(queueClients.first());
            cashier.attend(client);
        }

        if(cashier.getStatus() == false) freeCashiers.insert(cashier);
    }
}