package models;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.dist.ContDistNormal;
import desmoj.core.simulator.*;
import entities.Office;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import events.PatientArrivalEvent;


public class Hospital extends Model {
    private final String id;
    private final double simulationTime;
    private final int numTotalOffices;
    private List<Office> offices;

    private ContDistExponential distTimeArrival;
    private ContDistNormal distTimeService;

    public Hospital(Model owner, String name, boolean showInReport, boolean showInTrace){
        super(owner, name, showInReport, showInTrace);
        this.id = UUID.randomUUID().toString();
        this.simulationTime = 600;
        this.numTotalOffices = 4;
        this.offices = new ArrayList<>();
    }
    
    @Override
    public String description(){
        return "Modelo de Simulaçao Hospitalar com multiplos consultorios e filas independentes.";
    }

    @Override
    public void init(){
        for(int i = 0; i < numTotalOffices; i++){
            Office office = new Office(getModel(), isConnected(), (i+1));
            offices.add(office);
        }

        this.distTimeArrival = new ContDistExponential(this, "Tempo de Chegada", 15, true, true);
        this.distTimeService = new ContDistNormal(this, "Tempo de Serviço", 20, 5, true, true);
        this.distTimeArrival.setNonNegative(true);
        this.distTimeService.setNonNegative(true);
    }

    @Override
    public void doInitialSchedules() {
        PatientArrivalEvent patientGenerator = new PatientArrivalEvent(getModel(), isConnected());
        patientGenerator.schedule(new TimeSpan(0.0)); // Gera o primeiro paciente no tempo 0
    }

    // Getters
    public String getId() {
        return id;
    }

    public double getSimulationTime() {
        return simulationTime;
    }

    public int getNumTotalOffices() {
        return numTotalOffices;
    }

    public List<Office> getOffices() {
        return offices;
    }

    public ContDistExponential getContDistExponential(){
        return distTimeArrival;
    }
    
    public ContDistNormal getContDistNormal(){
        return distTimeService;
    }
}
