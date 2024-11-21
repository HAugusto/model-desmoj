import desmoj.core.simulator.*;
import models.Hospital;

public class App {
    public static void main(String[] args) throws Exception {
        Hospital hospital = new Hospital(null, "Modelo Hospitalar", false, false);
        Experiment experiment = new Experiment("Experimento de Supermercado");

        hospital.connectToExperiment(experiment);
        experiment.setShowProgressBar(true);
        experiment.stop(new TimeInstant(hospital.getSimulationTime()));
        experiment.tracePeriod(new TimeInstant(0.0), new TimeInstant(hospital.getSimulationTime()));
        experiment.start();
        experiment.report();
        experiment.finish();
    }
}
