package models;

import desmoj.core.simulator.*;
import desmoj.core.statistic.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Classe que representa um consultório em um sistema de simulação de atendimento hospitalar,
 * gerenciando filas de pacientes, atendimento e recursos associados.
 *
 * <p> Fornece métodos para manipular filas de pacientes, alterar status de disponibilidade,
 * e atualizar estatísticas relacionadas ao consultório. </p>
 * 
 * @see desmoj.core.simulator
 * @author Higor Augusto
 * @version 1.1.0
 * @since 03/11/2024
 */
public class Office extends Entity {

    private final Hospital model = (Hospital) getModel();               // Referência ao modelo principal
    private final static String uuid = UUID.randomUUID().toString();    // Identificador único do consultório
    private final int iIndex;                                           // Índice do consultório

    private final int maxPatientQueueSize;                              // Capacidade máxima da fila de pacientes
    private Queue<Patient> queuePatients;                               // Fila de pacientes aguardando atendimento
    private List<Patient> listAttendedPatients;                         // Lista de pacientes que já foram atendidos
    private Patient ptrPatient;                                         // Paciente atualmente em atendimento

    private Count countOccupiedTime;                                    // Contador do tempo total ocupado
    private Count countPatientsAttended;                                // Contador de pacientes atendidos
    private boolean bIsAvailable;                                       // Status atual do consultório (disponível ou ocupado)

    // Constructor
    /**
     * Construtor da classe Office.
     * Inicializa um consultório com filas de espera, contadores e status.
     *
     * @param owner              Modelo ao qual o consultório pertence.
     * @param showInTrace        Indica se o consultório será registrado no rastreamento.
     * @param index              Índice do consultório.
     * @param maxQueueSize       Capacidade máxima da fila de pacientes.
     */
    public Office(Model owner, boolean showInTrace, int index, int maxQueueSize) {
        super(owner, uuid, showInTrace);

        // Inicializa identificadores e configurações do consultório
        this.iIndex = index + 1;

        // Inicializa a fila de pacientes
        this.maxPatientQueueSize = Math.max(0, maxQueueSize + 1);
        this.queuePatients = new Queue<>(owner, "| Fila de Espera", showInTrace, showInTrace);
        this.listAttendedPatients = new ArrayList<>();

        // Define o paciente atual como 'null'
        this.ptrPatient = null;

        // Inicializa os contadores
        this.countOccupiedTime = new Count(owner, "Consultório " + iIndex + " | Tempo Ocupado", showInTrace, showInTrace);
        this.countPatientsAttended = new Count(owner, "Consultório " + iIndex + " | Pacientes Atendidos", showInTrace, showInTrace);

        // Define o status inicial do consultório como "disponível"
        this.bIsAvailable = true;
    }

    // Getters
    /**
     * Retorna o tempo total que o consultório esteve ocupado.
     * 
     * @return Valor do contador de tempo ocupado.
     */
    public long getTotalOccupiedTime() {
        return this.countOccupiedTime.getValue();
    }

    /**
     * Retorna o número total de pacientes atendidos pelo consultório.
     * 
     * @return Valor do contador de pacientes atendidos.
     */
    public long getTotalClientServed() {
        return this.countPatientsAttended.getValue();
    }

    /**
     * Retorna o identificador único do consultório.
     * 
     * @return O UUID do consultório.
     */
    public String getId() {
        return uuid;
    }

    /**
     * Retorna o índice do consultório.
     * 
     * @return O índice do consultório.
     */
    public int getIndex() {
        return iIndex;
    }

    /**
     * Retorna a fila de pacientes aguardando atendimento.
     * 
     * @return Fila de pacientes.
     */
    public Queue<Patient> getQueueWaitingPatients() {
        return queuePatients;
    }

    /**
     * Retorna o paciente atualmente em atendimento.
     * 
     * @return Paciente em atendimento ou 'null' se não houver.
     */
    public Patient getPatient() {
        return ptrPatient;
    }

    /**
     * Retorna a lista de pacientes que já foram atendidos.
     * 
     * @return Lista de pacientes atendidos.
     */
    public List<Patient> getListAttendedPatients() {
        return listAttendedPatients;
    }

    /**
     * Retorna a capacidade máxima da fila de espera.
     * 
     * @return Capacidade máxima da fila.
     */
    public int getMaxPatientQueueSize() {
        return maxPatientQueueSize;
    }

    /**
     * Verifica se a fila de espera está vazia.
     * 
     * @return True, se a fila estiver vazia.
     */
    public boolean getIsQueueEmpty() {
        return this.queuePatients.length() <= 0;
    }

    /**
     * Verifica se a fila de espera está cheia.
     * 
     * @return True, se a fila estiver cheia.
     */
    public boolean getIsQueueFull() {
        return this.queuePatients.length() > this.maxPatientQueueSize;
    }

    /**
     * Verifica se há pacientes aguardando na fila.
     * 
     * @return True, se houver pacientes na fila.
     */
    public boolean getHasPatientWaiting() {
        return this.queuePatients.length() > 0;
    }

    /**
     * Verifica se o consultório está disponível.
     * 
     * @return True, se o consultório estiver disponível.
     */
    public boolean getIsAvailable() {
        return bIsAvailable;
    }

    // Setters
    /**
     * Define o paciente atualmente em atendimento.
     * 
     * @param patient Paciente a ser atendido.
     */
    public void setPatient(Patient patient) {
        this.ptrPatient = patient;
    }

    /**
     * Define o contador de tempo ocupado.
     * 
     * @param occupiedTime Novo contador de tempo ocupado.
     */
    public void setCountOccupiedTime(Count occupiedTime) {
        this.countOccupiedTime = occupiedTime;
    }

    /**
     * Define o contador de pacientes atendidos.
     * 
     * @param patientsAttended Novo contador de pacientes atendidos.
     */
    public void setCountPatientsAttended(Count patientsAttended) {
        this.countPatientsAttended = patientsAttended;
    }

    // Métodos internos
    /**
     * Adiciona um paciente à fila de espera.
     * 
     * @param patient Paciente a ser adicionado.
     * @throws IllegalStateException Se a fila estiver cheia.
     */
    public void addPatientOnQueue(Patient patient) {
        if (getIsQueueFull()) {
            throw new IllegalStateException("Fila está cheia.");
        }
        this.queuePatients.insert(patient);
    }

    /**
     * Remove o primeiro paciente da fila de espera.
     * 
     * @throws IllegalStateException Se a fila estiver vazia.
     */
    public void removePatientOnQueue() {
        if (getIsQueueEmpty()) {
            throw new IllegalStateException("Fila está vazia.");
        }
        this.queuePatients.remove(this.queuePatients.first());
    }

    /**
     * Alterna o status de disponibilidade do consultório.
     */
    public void toggleStatus() {
        this.bIsAvailable = !this.bIsAvailable;
    }

    /**
     * Atualiza o contador de pacientes atendidos, incrementando em 1.
     */
    public void updateCountPatientsAttended() {
        this.countPatientsAttended.update(1);
    }
}