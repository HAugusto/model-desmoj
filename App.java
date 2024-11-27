import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.TimeInstant;

public class App {
    public static void main(String[] args) throws Exception {
        Market modelMarket;
        Experiment experiment;

        modelMarket = new Market(null, "Modelo de Supermercado", true, true, 3, 6, 2);
        experiment = new Experiment("Experimento de Supermercado - Cenário 3-6-2");

        modelMarket.connectToExperiment(experiment);
        experiment.setShowProgressBar(true);
        experiment.stop(new TimeInstant(modelMarket.getTimeSimulation()));
        experiment.tracePeriod(new TimeInstant(0.0), new TimeInstant(modelMarket.getTimeSimulation()));
        experiment.start();
        experiment.report();
        experiment.finish();
    }
}
