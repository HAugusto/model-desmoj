package models;
import desmoj.core.simulator.*;
import java.util.UUID;

/**
 * Classe que representa um paciente em um sistema de simulaçao de atendimento hospitalar,
 * armazenando dados como horário de chegada, inicio e fim de atendimento.
 *
 * <p> Fornece metodos para calcular o tempo de espera e o tempo total de permanencia do paciente no sistema. </p>
 * 
 * @see desmoj.core.simulator
 * @author Higor Augusto
 * @version 1.1.0
 * @since 03/11/2024
 */
public class Patient extends Entity {
    private final static String uuid = UUID.randomUUID().toString();    // Identificador unico do paciente
    private TimeInstant instantArrivalTime;                             // Indica o tempo de chegada do paciente ao hospital
    private TimeInstant instantStartTime;                               // Indica o tempo de inicio de atendimento
    private TimeInstant instantEndTime;                                 // Indica o tempo de fim de atendimento
    private boolean bIsUrgent;                                          // Indica se o caso e urgente

    // Constructor
    /**
     * Construtor do Paciente.
     * Inicializa uma instância de paciente.
     *
     * @param owner         Indica o modelo ao qual o paciente pertence.
     * @param name          Indica o nome do paciente.
     * @param showInTrace   Indica se o paciente deve aparecer no rastreamento da simulaçao.
     * @param isUrgent      Indica se o caso do paciente e urgente.
     */
    public Patient(Model owner, String name, boolean showInTrace, boolean isUrgent) {
        super(owner, name, showInTrace);

        // Incializa as variáveis de tempo como 'null'
        this.instantArrivalTime = null;
        this.instantStartTime = null;
        this.instantEndTime = null;

        // Determina a gravidade do paciente
        this.bIsUrgent = isUrgent;
    }

    // Getters
    /**
     * Retorna o identificador unico do paciente.
     * Este ID e gerado automaticamente pelo sistema e e unico para cada paciente.
     * 
     * @return O UUID gerado para o paciente.
     */
    public String getId() {
        return uuid;
    }
    
    /**
     * Retorna o instante de tempo de chegada do paciente ao sistema.
     * 
     * @return O timestamp de chegada do paciente ao hospital.
     */
    public TimeInstant getArrivalTime() {
        return this.instantArrivalTime;
    }

    /**
     * Retorna o instante de tempo de inicio de atendimento do paciente no sistema.
     * 
     * @return O timestamp de inicio de atendimento do paciente.
     */
    public TimeInstant getServiceStartTime() {
        return this.instantStartTime;
    }

    /**
     * Retorna o instante de tempo de fim de atendimento do paciente no sistema.
     * 
     * @return O timestamp de fim de atendimento do paciente.
     */
    public TimeInstant getServiceEndTime() {
        return this.instantEndTime;
    }

    /**
     * Retorna se o paciente e um caso urgente.
     * 
     * @return True, para caso urgente.
     */
    public boolean getIsUrgent(){
        return bIsUrgent;
    }

    // Setters
    /**
     * Define o tempo de chegada do paciente ao hospital.
     * 
     * @param instantArrivalTime O timestamp de chegada do paciente ao hospital.
     * @throws IllegalStateException Se o tempo de chegada nao for válido.
     *                                Caso o paciente já tenha sido registrado anteriormente, esse metodo nao pode ser chamado.
     */
    public void setArrivalTime(TimeInstant instantArrivalTime) {
        // if(instantArrivalTime == null) throw new IllegalArgumentException("O tempo nao pode ser nulo.");
        this.instantArrivalTime = instantArrivalTime;
    }
    
    /**
     * Define o tempo de inicio do atendimento do paciente. Este metodo deve ser chamado quando o paciente
     * for chamado para atendimento e a consulta começar.
     *
     * @param serviceStartTime O timestamp de inicio de atendimento do paciente.
     * @throws IllegalStateException Se o paciente nao chegou antes de ser atendido. 
     *                               Este metodo so pode ser chamado apos o tempo de chegada estar registrado.
     */
    public void setServiceStartTime(TimeInstant serviceStartTime) {
        // if(instantArrivalTime == null) throw new IllegalStateException("O paciente ainda nao chegou ao hospital.");
        this.instantStartTime = serviceStartTime;
    }

    /**
     * Define o tempo de fim do atendimento do paciente. Este metodo deve ser chamado quando o atendimento do
     * paciente for concluido e o medico terminar o serviço.
     *
     * @param serviceEndTime O timestamp de fim de atendimento do paciente.
     * @throws IllegalStateException Se o atendimento nao foi iniciado antes de ser finalizado.
     *                                Este metodo so pode ser chamado apos o atendimento começar.
     */
    public void setServiceEndTime(TimeInstant serviceEndTime) {
        // if (this.instantStartTime == null) throw new IllegalStateException("O atendimento ainda nao foi iniciado.");
        this.instantEndTime = serviceEndTime;
    }

    // Metodos internos
    // @Override
    // public String toString() {
    //     return "Patient{" +
    //             "id=" + getPatientId() +
    //             ", name=" + getName() +
    //             ", instantArrivalTime=" + instantArrivalTime +
    //             ", serviceStartTime=" + (instantStartTime != null ? instantStartTime : "N/A") +
    //             ", serviceEndTime=" + (instantEndTime != null ? instantEndTime : "N/A") +
    //             ", serviceDuration=" + (dTimeService >= 0 ? dServiceDuration : "N/A") +
    //             '}';
    // }
}
