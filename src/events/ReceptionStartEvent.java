package events;

import desmoj.core.simulator.*;
import models.Hospital;
import models.Patient;
import models.Receptionist;

/**
 * Evento que representa o início do atendimento de um paciente na recepção.
 */
public class ReceptionStartEvent extends ExternalEvent {

    private final Hospital model;
    private final Receptionist receptionist;

    /**
     * Construtor para o evento de início de triagem.
     * 
     * @param owner       O modelo ao qual o evento pertence.
     * @param name        O nome do evento.
     * @param showInTrace Indica se o evento será registrado no rastreamento.
     * @param receptionist O recepcionista que irá realizar a triagem.
     */
    public ReceptionStartEvent(Model owner, String name, boolean showInTrace, Receptionist receptionist) {
        super(owner, name, showInTrace);
        this.model = (Hospital) owner;
        this.receptionist = receptionist;
    }

    @Override
    public void eventRoutine() {
        // Verifica se há pacientes na fila da recepção
        if (receptionist.getWaitingQueue().size() > 0) {
            // Pega o próximo paciente da fila
            Patient patient = model.getQueuePatient().first();
            model.getQueuePatient().remove(patient); // Remove o paciente da fila

            // Atribui o paciente ao recepcionista
            receptionist.assignPatient(patient);
            model.sendTraceNote("Triagem iniciada para o paciente ID: " + patient.getId());

            // Alterna o status do recepcionista para ocupado
            receptionist.toggleStatus();

            // Simula o tempo de atendimento para a triagem
            // Agendamos o fim do atendimento após o tempo de serviço do recepcionista
            ReceptionEndEvent event = new ReceptionEndEvent(model, "Fim da Triagem", true, receptionist);
            event.schedule(new TimeSpan(0)); // Tempo de atendimento (tempo de serviço do recepcionista)
        } else {
            // Caso não haja pacientes, o recepcionista fica ocioso até que haja algum para atender
            model.sendTraceNote("Nenhum paciente na fila para triagem.");
        }

        // Após o atendimento do paciente, agendamos o próximo evento de triagem, caso haja pacientes na fila
        if (model.getQueuePatient().length() > 0) {
            ReceptionStartEvent nextEvent = new ReceptionStartEvent(model, "Início da Triagem", true, receptionist);
            nextEvent.schedule(new TimeSpan(0));  // Agendado imediatamente após o atendimento atual
        }
    }
}
