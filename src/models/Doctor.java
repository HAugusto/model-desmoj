package models;
import desmoj.core.simulator.*;
import desmoj.core.statistic.*;
import java.util.UUID;

/**
 * Representa um medico em um sistema de simulaçao de atendimento hospitalar.
 * Armazena dados como total de pacientes atendidos, paciente atual e disponibilidade.
 * Cada medico pode atender um paciente por vez e mantem um registro do numero total de atendimentos realizados.
 * 
 * @see desmoj.core.simulator
 * @author Higor Augusto
 * @version 1.2.0
 * @since 03/11/2024
 */
public class Doctor extends Entity {
    private final String doctorId;                // Identificador unico do medico
    private final Count totalPatientsServed;      // Contador de pacientes atendidos
    private Patient currentPatientBeingTreated;   // Paciente atualmente sendo atendido
    private boolean isAvailableForNewPatients;    // Indica se o medico está disponivel para novos atendimentos

    // Constructor
    /**
     * Construtor da classe Doctor.
     * Inicializa o medico como disponivel e configura o contador de pacientes atendidos.
     * 
     * @param modelOwner O modelo ao qual o medico pertence.
     * @param doctorName O nome do medico.
     * @param showInTrace Determina se o medico deve aparecer no rastreamento da simulaçao.
     */
    public Doctor(Model modelOwner, String doctorName, boolean showInTrace) {
        super(modelOwner, doctorName, showInTrace);
        this.doctorId = UUID.randomUUID().toString();  // Geraçao de um ID unico para o medico
        this.totalPatientsServed = new Count(modelOwner, doctorName + "_TotalPatientsServed", showInTrace, showInTrace);
        this.currentPatientBeingTreated = null;  // Inicializa o medico sem paciente sendo atendido
        this.isAvailableForNewPatients = true;   // Inicializa o medico como disponivel
    }

    // Getters
    /**
     * Retorna o identificador unico do medico.
     * 
     * @return O UUID do medico.
     */
    public String getDoctorId() {
        return doctorId;
    }

    /**
     * Retorna o estado de disponibilidade do medico para novos atendimentos.
     * 
     * @return true se o medico estiver disponivel para atender; false caso contrário.
     */
    public boolean isAvailableForNewPatients() {
        return isAvailableForNewPatients;
    }

    /**
     * Retorna o paciente atualmente sendo atendido pelo medico.
     * 
     * @return O paciente atual, ou null se o medico estiver disponivel.
     */
    public Patient getCurrentPatientBeingTreated() {
        return currentPatientBeingTreated;
    }

    /**
     * Retorna o numero total de pacientes atendidos pelo medico.
     * 
     * @return O numero total de atendimentos realizados.
     */
    public long getTotalPatientsServed() {
        return totalPatientsServed.getValue();
    }

    // Metodos internos
    /**
     * Atribui um paciente ao medico para inicio de atendimento.
     * Define o estado de ocupaçao do medico como indisponivel.
     * 
     * @param patient O paciente a ser atendido.
     * @throws IllegalStateException Se o medico já estiver ocupado com outro paciente.
     * @throws IllegalArgumentException Se o paciente for nulo.
     */
    // public void startService(Patient patient) {
    //     if (!isAvailableForNewPatients) {
    //         throw new IllegalStateException("O medico já está ocupado com outro paciente.");
    //     }
    //     if (patient == null) {
    //         throw new IllegalArgumentException("O paciente nao pode ser nulo.");
    //     }

    //     patient.setStartTimestamp();  // Marca o inicio do atendimento
    //     this.currentPatientBeingTreated = patient;  // Atribui o paciente ao medico
    //     this.isAvailableForNewPatients = false;     // Marca o medico como ocupado
    // }

    /**
     * Finaliza o atendimento do paciente atual.
     * Atualiza o estado do medico para disponivel e incrementa o contador de atendimentos realizados.
     * 
     * @param notes Observações adicionais sobre o atendimento.
     * @throws IllegalStateException Se o medico nao estiver atendendo nenhum paciente.
     */
    // public void endService(String notes) {
    //     if (isAvailableForNewPatients || currentPatientBeingTreated == null) {
    //         throw new IllegalStateException("Nao há atendimento em andamento para finalizar.");
    //     }

    //     currentPatientBeingTreated.setEndTimestamp();  // Marca o fim do atendimento
    //     // Aqui, podemos registrar informações adicionais sobre o atendimento, como historico
    //     this.currentPatientBeingTreated = null;  // Limpa o paciente atual
    //     this.isAvailableForNewPatients = true;    // Marca o medico como disponivel

    //     totalPatientsServed.update();  // Incrementa o contador de pacientes atendidos
    // }

    // Representaçao textual do estado do medico
    /**
     * Retorna uma representaçao textual do estado atual do medico.
     * Inclui informações como ID, nome, disponibilidade e o numero total de atendimentos realizados.
     * 
     * @return Uma string representando o estado atual do medico.
     */
    @Override
    public String toString() {
        return new StringBuilder()
                .append("Doctor{")
                .append("doctorId='").append(doctorId).append('\'')
                .append(", name='").append(getName()).append('\'')
                .append(", isAvailableForNewPatients=").append(isAvailableForNewPatients)
                .append(", totalPatientsServed=").append(totalPatientsServed.getValue())
                .append('}')
                .toString();
    }
}
