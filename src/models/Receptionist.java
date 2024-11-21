package models;

import desmoj.core.simulator.*;
import desmoj.core.statistic.*;
import java.util.UUID;
import desmoj.core.simulator.Queue;

/**
 * Classe que representa a recepcionista de um sistema de simulação de atendimento hospitalar,
 * responsável por realizar a triagem inicial e gerenciar o fluxo de pacientes.
 *
 * <p> Fornece métodos para atribuir pacientes, liberar pacientes, alternar status de disponibilidade 
 * e atualizar o número de pacientes atendidos. </p>
 * 
 * @see desmoj.core.simulator
 * @author Higor Augusto
 * @version 1.2.0
 * @since 03/11/2024
 */
public class Receptionist extends Entity {

    private final String uuid = UUID.randomUUID().toString();  // Identificador único da recepcionista
    private Patient ptrPatient;                                  // Referência ao paciente atribuído
    private Count countPatientsAttended;                         // Contador de pacientes atendidos
    private boolean bIsAvailable;                                // Indica se a recepcionista está disponível
    private Queue<Patient> waitingQueue;                         // Fila interna de pacientes aguardando atendimento

    // Constructor
    /**
     * Construtor da Recepcionista.
     * Inicializa uma instância de recepcionista e configura os atributos iniciais.
     *
     * @param owner         Indica o modelo ao qual a recepcionista pertence.
     * @param name          Nome da recepcionista.
     * @param showInTrace   Indica se a recepcionista deve aparecer no rastreamento da simulação.
     */
    public Receptionist(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        // Define o paciente atual como 'nulo'.
        this.ptrPatient = null;

        // Inicializa o contador de pacientes atendidos.
        this.countPatientsAttended = new Count(owner, "Recepcionista | Pacientes Atendidos: ", showInTrace, showInTrace);

        // Define o status inicial da recepcionista como "disponível".
        this.bIsAvailable = true;

        // Inicializa a fila interna de pacientes.
        this.waitingQueue = new Queue<>(owner, "Fila de Pacientes", showInTrace, showInTrace);
    }

    // Getters
    /**
     * Retorna o identificador único da recepcionista.
     * Este ID é gerado automaticamente pelo sistema e é único para cada recepcionista.
     * 
     * @return O UUID gerado para a recepcionista.
     */
    public String getId() {
        return uuid;
    }

    /**
     * Retorna o paciente atualmente atribuído à recepcionista.
     * 
     * @return O paciente atribuído ou 'null' se nenhum paciente estiver sendo atendido.
     */
    public Patient getPatient() {
        return ptrPatient;
    }

    /**
     * Verifica se a recepcionista está disponível.
     * 
     * @return True, se a recepcionista estiver disponível.
     */
    public boolean getIsAvailable() {
        return bIsAvailable;
    }

    /**
     * Retorna o número total de pacientes atendidos pela recepcionista.
     * 
     * @return O valor do contador de pacientes atendidos.
     */
    public long getCountPatientsAttended() {
        return countPatientsAttended.getValue();
    }

    /**
     * Retorna a fila interna de pacientes aguardando atendimento.
     * 
     * @return A fila de pacientes.
     */
    public Queue<Patient> getWaitingQueue() {
        return waitingQueue;
    }

    // Setters
    /**
     * Atribui um paciente à recepcionista para atendimento.
     * Se a recepcionista não estiver disponível, o paciente é adicionado à fila.
     * 
     * @param patient O paciente a ser atribuído.
     * @throws IllegalStateException Se a recepcionista estiver ocupada e a fila estiver cheia.
     */
    public void assignPatient(Patient patient) {
        if (!bIsAvailable) {
            waitingQueue.insert(patient);  // Adiciona o paciente na fila se a recepcionista estiver ocupada
        } else {
            this.ptrPatient = patient;    // Se a recepcionista estiver disponível, ela recebe o paciente
            toggleStatus();
        }
    }

    /**
     * Libera o paciente atualmente sendo atendido pela recepcionista.
     * Este método também altera o status da recepcionista para "disponível".
     * Quando a recepcionista fica disponível, ela verifica se há pacientes na fila.
     */
    public void releasePatient() {
        if (bIsAvailable) {
            throw new IllegalStateException("A recepcionista já está disponível.");
        }
        this.ptrPatient = null;
        updateCountPatientsAttended();  // Incrementa o contador de pacientes atendidos
        toggleStatus();
        
        // Verifica se há pacientes na fila e atribui o próximo paciente
        if (!waitingQueue.isEmpty()) {
            Patient nextPatient = waitingQueue.first();
            waitingQueue.remove(nextPatient);
            assignPatient(nextPatient);
        }
    }

    // Métodos internos
    /**
     * Alterna o status de disponibilidade da recepcionista.
     * Este método é chamado internamente para alternar entre "ocupada" e "disponível".
     */
    public void toggleStatus() {
        this.bIsAvailable = !this.bIsAvailable;
    }

    /**
     * Atualiza o contador de pacientes atendidos, incrementando seu valor em 1.
     * Deve ser chamado sempre que a recepcionista concluir o atendimento de um paciente.
     */
    public void updateCountPatientsAttended() {
        this.countPatientsAttended.update(1); // Incrementa o contador em 1
    }
}
