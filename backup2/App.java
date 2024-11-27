import java.util.concurrent.TimeUnit;
import desmoj.core.simulator.*;
import desmoj.core.exception.*;
import models.Hospital;

/**
 * Classe que representa a simulaçao de um modelo hospitalar, realizando a execuçao
 * do experimento de atendimento hospitalar e controle de tempo de simulaçao.
 *
 * <p> Este programa cria e gerencia a simulaçao de chegada de pacientes no hospital,
 * controle de tempo de atendimento e gera o relatorio apos a execuçao. </p>
 * 
 * @see desmoj.core.simulator.Experiment
 * @author Higor Augusto
 * @version 1.0.2
 * @since 20/11/2024
 */
public class App {
    // Variáveis globais
    private static final int SIMULATION_TIME = 500;         // Tempo de simulaçao definido em minutos
    private static final int NUM_DOCTORS = 5;               // Define o numero de medicos
    private static final int NUM_OFFICES = 5;               // Define o numero de consultorios
    private static final int NUM_DOCTOR_PER_OFFICE = 5;     // Define o numero de medicos por consultorio
    private static final int NUM_MAX_QUEUE_SIZE = 5;     // Define o numero de medicos por consultorio

    /**
     * Metodo principal para inicializar a simulaçao do modelo hospitalar.
     * Este metodo cria a instância do hospital, conecta ao experimento, e executa a simulaçao.
     *
     * @param args Argumentos da linha de comando (nao utilizados neste caso).
     * @throws Exception Em caso de erro durante a execuçao da simulaçao.
     */
    public static void main(String[] args) {
        // Verificaçao de parâmetros de entrada
        if (args.length != 0) System.out.println("Parâmetros de linha de comando nao sao necessários.");
        
        // Criaçao da instância do hospital com parâmetros especificos
        Hospital hospital = new Hospital(null, "Modelo Hospitalar", true, true, SIMULATION_TIME, NUM_OFFICES, NUM_MAX_QUEUE_SIZE);

        // Criaçao do experimento para a simulaçao das filas
        Experiment experiment = new Experiment("Filas");

        // Conectar o hospital ao experimento para vincular o modelo à simulaçao
        hospital.connectToExperiment(experiment);

        // Configuraçao do experimento
        ExperimentConfig(experiment, hospital);

        try {
            // Iniciar a execuçao do experimento
            System.out.println("Iniciando a simulaçao...");
            experiment.start();

            // Gerar o relatorio da simulaçao apos a execuçao
            System.out.println("Simulaçao em andamento...");
            experiment.report();

            // Finalizar o experimento apos o termino da execuçao
            System.out.println("Relatorio gerado com sucesso.");
            experiment.finish();
        } catch (DESMOJException e) {
            System.err.println("Erro ao executar a simulaçao: " + e.getMessage());
        }
    }

    /**
     * Configura as opções e parâmetros do experimento de simulaçao.
     * 
     * @param experiment O experimento a ser configurado.
     * @param hospital O hospital que está sendo simulado.
     */
    private static void ExperimentConfig(Experiment experiment, Hospital hospital) {
        experiment.setShowProgressBar(true);
        experiment.stop(new TimeInstant(SIMULATION_TIME, TimeUnit.MINUTES));
        experiment.tracePeriod(new TimeInstant(0.0, TimeUnit.MINUTES), new TimeInstant(hospital.getSimulationTime(), TimeUnit.MINUTES));
    }
}
