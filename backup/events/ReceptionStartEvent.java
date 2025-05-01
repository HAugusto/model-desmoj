package events;

import java.util.concurrent.TimeUnit;

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
        // System.out.println(receptionist.getHasQueue());
        // if(!receptionist.getHasQueue()){
        //     if(!model.getQueuePatient().isEmpty()){
        //         Patient patient = model.getQueuePatient().first();
        //         model.getQueuePatient().remove(patient);
        //         receptionist.setPatient(patient);

        //         // Simula o tempo de atendimento para a triagem
        //         model.sendTraceNote("Triagem iniciada para o paciente ID: " + patient.getId());

        //         // Agendamos o fim do atendimento após o tempo de serviço do recepcionista
        //         model.releaseReceptionist(receptionist);

        //         if(!model.getQueuePatient().isEmpty()) scheduleNextReceptionEvent();
        //     } else {
        //         // Caso não haja pacientes, o recepcionista fica ocioso até que haja algum para atender
        //         model.sendTraceNote("Nenhum paciente na fila para triagem.");
        //     }
        // } else {
            // Verifica se há pacientes na fila da recepção
            // System.out.println(receptionist.getIsAvailable());
                // Recebe o primeiro, ou o próximo paciente da fila, atribuindo-o a recepcionista
                Patient patient = receptionist.getWaitingQueue().first();
                receptionist.getWaitingQueue().remove(patient); // Remove o paciente da fila
                receptionist.setPatient(patient);

                // System.out.println("Início de atendimento: " + receptionist.getIsAvailable());

                // Simula o tempo de atendimento para a triagem
                model.sendTraceNote("Triagem iniciada para o paciente ID: " + patient.getId());

                // Agendamos o fim do atendimento após o tempo de serviço do recepcionista
                
                // if(!receptionist.getWaitingQueue().isEmpty()) scheduleNextReceptionEvent();
        // }
    }

    /**
     * Método para agendar o próximo evento início de triagem.
     */
    private void scheduleNextReceptionEvent() {
        if (receptionist.getWaitingQueue().size() > 0) {
            // Agendar o próximo evento de triagem
            ReceptionStartEvent nextEvent = new ReceptionStartEvent(model, "Início da Triagem", true, receptionist);
            nextEvent.schedule(new TimeSpan(15, TimeUnit.MINUTES));  // Agendamento após 15 minutos (tempo de atendimento)
            
            model.sendTraceNote("Próximo evento de triagem agendado.");
        }
    }
}
