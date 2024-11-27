package models;

import desmoj.core.simulator.*;
import desmoj.core.statistic.*;
import events.ReceptionEndEvent;
import events.ReceptionStartEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    private final String uuid = UUID.randomUUID().toString();   // Identificador único da recepcionista
    private Patient ptrPatient;                                 // Referência ao paciente atribuído

    private Count countPatientsAttended;                        // Contador de pacientes atendidos
    private boolean bIsAvailable;                               // Indica se a recepcionista está disponível
    
    private Queue<Patient> waitingQueue;                        // Fila interna de pacientes aguardando atendimento, geralmente (0)
    private boolean bHasInternQueue;
    private int iMaxQueueSize;

    // Definição para relatórios
    private boolean bShowInReport;                              // Indica se deve mostrar relatórios
    private boolean bShowInTrace;                               // Indica se deve mostrar o rastreamento dos eventos

    // Constructor
    /**
     * Construtor da Recepcionista.
     * Inicializa uma instância de recepcionista e configura os atributos iniciais.
     *
     * @param owner         Indica o modelo ao qual a recepcionista pertence.
     * @param name          Nome da recepcionista.
     * @param showInTrace   Indica se a recepcionista deve aparecer no rastreamento da simulação.
     */
    public Receptionist(Model owner, String name, boolean showInTrace, boolean showInReport, boolean hasInternQueue, int maxQueueSize ) {
        super(owner, name, showInTrace);

        this.bShowInReport = showInReport;
        this.bShowInTrace = showInTrace;
        
        this.iMaxQueueSize = (maxQueueSize > 0 ? maxQueueSize : 0);
        this.bHasInternQueue = hasInternQueue;

        // Define o paciente atual como 'nulo'.
        this.ptrPatient = null;

        // Inicializa o contador de pacientes atendidos.
        this.countPatientsAttended = new Count(owner, "Recepcionista | Pacientes Atendidos: ", bShowInTrace, bShowInReport);

        // Define o status inicial da recepcionista como "disponível".
        this.bIsAvailable = true;

        // Inicializa a fila interna de pacientes.
        if(hasInternQueue) this.waitingQueue = new Queue<>(owner, "Fila de Pacientes", bShowInTrace, bShowInReport);
    }
    
    // public void init(){

    // };

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

    public boolean getHasQueue(){
        return this.bHasInternQueue;
    }
    
    // Setters
    /**
     * Atribui um paciente à recepcionista para atendimento.
     * Se a recepcionista não estiver disponível, o paciente é adicionado à fila.
     * 
     * @param patient O paciente a ser atribuído.
     * @throws IllegalStateException Se a recepcionista estiver ocupada e a fila estiver cheia.
     */
    public void setPatient(Patient patient) {
        this.ptrPatient = patient;    // Se a recepcionista estiver disponível, ela recebe o paciente
        toggleStatus();
    }

    // Métodos internos
    // Função para atender paciente através de um recepcionista
    /**
     * Atende um paciente e o coloca na fila para ser atendido por um recepcionista.
     * 
     * @param patient O paciente a ser atendido.
     * @param receptionist O recepcionista que atenderá o paciente.
     */
    public void attendPatient(Patient patient) {
        // 1. Verifica se há espaço na fila (5 pacientes)
        // 2. Se há espaço na fila, o paciente entra na fila
        // 3. Se a fila contém somente um paciente, ele vai para atendimento direto
        // 4. Se a fila for maior que 0, atende o primeiro paciente
        // 5. Se a fila estiver vazia, a recepcionista aguarda a chegada de um paciente
        if(this == null || patient == null) throw new IllegalArgumentException("Os parâmetros não podem ser nulos");
        if(!bHasInternQueue){
            ReceptionStartEvent event = new ReceptionStartEvent(getModel(), "Recepcionista | " + this.getId(), bShowInTrace, this);
            event.schedule(new TimeSpan(15, TimeUnit.MINUTES));
        } else {
            System.out.println("Paciente <" + patient.getId() + "> inserido na fila.");
            if(this.getWaitingQueue().size() < 5) this.getWaitingQueue().insert(patient);
            else return;

            // Se a recepcionista estiver disponível, agenda o atendimento do paciente
            if(!this.getWaitingQueue().isEmpty()){
                System.out.println(this.getIsAvailable());

                ReceptionStartEvent event = new ReceptionStartEvent(getModel(), "Recepcionista | " + this.getId(), bShowInTrace, this);
                event.schedule(new TimeSpan(15, TimeUnit.MINUTES));

                // Emite nota de rastreamento sobre o atendimento imediato
                sendTraceNote("Paciente sendo atendido imediatamente | Fila da Recepção: " + this.getWaitingQueue().size());
            } else if(this.getWaitingQueue().size() > 5) {
                // Se a fila estiver cheia, o paciente é rejeitado
                sendTraceNote("Fila da Recepcionista cheia! Paciente rejeitado.");
                System.out.println("Fila da Recepcionista cheia! Paciente rejeitado.");

                // Liberar o paciente
            }
        }
    }

    /**
     * Libera o paciente atualmente sendo atendido pela recepcionista.
     * Este método também altera o status da recepcionista para "disponível".
     * Quando a recepcionista fica disponível, ela verifica se há pacientes na fila.
     */
    public void releasePatient() {
        if (bIsAvailable) throw new IllegalStateException("A recepcionista já está disponível.");
        
        ReceptionEndEvent event = new ReceptionEndEvent(getModel(), "Fim da Triagem", bShowInTrace, this);
        event.schedule(new TimeSpan(5, TimeUnit.MINUTES)); // Tempo de atendimento (tempo de serviço do recepcionista)
    }

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
