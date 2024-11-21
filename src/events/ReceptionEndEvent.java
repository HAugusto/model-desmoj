package events;

import desmoj.core.simulator.*;
import models.Hospital;
import models.Patient;
import models.Receptionist;

/**
 * Evento que representa o fim do atendimento de um paciente na recepção.
 */
public class ReceptionEndEvent extends ExternalEvent {

    private final Hospital model;
    private final Receptionist receptionist;

    /**
     * Construtor para o evento de fim de atendimento.
     * 
     * @param owner       O modelo ao qual o evento pertence.
     * @param name        O nome do evento.
     * @param showInTrace Indica se o evento será registrado no rastreamento.
     * @param receptionist O recepcionista que realizou o atendimento.
     */
    public ReceptionEndEvent(Model owner, String name, boolean showInTrace, Receptionist receptionist) {
        super(owner, name, showInTrace);
        this.model = (Hospital) owner;
        this.receptionist = receptionist;
    }

    @Override
    public void eventRoutine() {
        // Finaliza o atendimento do paciente e registra a ação
        Patient patient = receptionist.getPatient();  // Obtemos o paciente que está sendo atendido
        model.sendTraceNote("Fim do atendimento do paciente ID: " + patient.getId());
        
        // Libera o recepcionista e altera seu status para disponível
        receptionist.releasePatient();
        receptionist.toggleStatus();  // Recepcionista fica disponível novamente
        
        System.out.println("Estou aqui");
        // Verifica se há pacientes na fila de espera da recepção
        if (receptionist.getWaitingQueue().length() > 0) {
            // Se houver pacientes na fila, pega o próximo paciente
            Patient nextPatient = receptionist.getWaitingQueue().first();
            receptionist.getWaitingQueue().remove(nextPatient);  // Remove o paciente da fila
            receptionist.assignPatient(nextPatient);  // Atribui o paciente ao recepcionista
            model.sendTraceNote("Recepcionista agora atendendo o paciente ID: " + nextPatient.getId());
            
            // Agendar o início do atendimento para o próximo paciente
            ReceptionStartEvent nextStartEvent = new ReceptionStartEvent(model, "Início da Triagem", true, receptionist);
            nextStartEvent.schedule(new TimeSpan(0));  // Agendamento imediato do próximo evento
        }

        // Caso não haja pacientes na fila, o recepcionista ficará disponível
        else {
            model.sendTraceNote("Recepcionista está ocioso, aguardando novos pacientes.");
        }
    }
}
