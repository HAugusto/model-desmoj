package events;

import desmoj.core.simulator.*;
import models.Hospital;
import models.Office;
import models.Patient;
import models.Receptionist;

/**
 * Evento que representa a chegada de um paciente ao hospital.
 */
public class PatientArrivalEvent extends ExternalEvent {
    private final Hospital model;

    /**
     * Construtor para o evento de chegada de um paciente.
     * 
     * @param owner       O modelo ao qual o evento pertence.
     * @param name        O nome do evento.
     * @param showInTrace Indica se o evento será registrado no rastreamento.
     */
    public PatientArrivalEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
        this.model = (Hospital) owner;
    }

    @Override
    public void eventRoutine() {
        handlePatientArrival();
    }

    /**
     * Método auxiliar para processar a chegada do paciente e seu encaminhamento.
     */
    private void handlePatientArrival() {
        // Criação do paciente e atribuição de tempo de chegada
        Patient patient = new Patient(model, "Paciente", true, false);
        patient.setArrivalTime(model.presentTime()); // Atribui o tempo de chegada ao paciente
        
        // Registra a chegada do paciente no hospital
        model.sendTraceNote("ID: " + patient.getId() + " chegou ao hospital.");
        
        // Verifica se há recepcionista disponível para atendimento
        Receptionist availableReceptionist = model.getAvailableReceptionist();

        // Se houver recepcionista disponível, tenta encaminhar o paciente para a fila da recepção
        if (availableReceptionist != null) {
            model.attendPacientReceptionist(patient, availableReceptionist);
        } else {
            // Se não houver recepcionista disponível, o paciente entra na fila global
            model.getListReceptionist().getFirst().getWaitingQueue().insert(patient);
            model.sendTraceNote("Fila | Recepcionista " +  model.getListReceptionist().getFirst().getId() + ": " +  model.getListReceptionist().getFirst().getWaitingQueue().size() + " foi adicionado à fila global.");
        }

        for(Receptionist recp: model.getListReceptionist()) System.out.println("Fila | Recepcionista " + recp.getId() + ": " + recp.getWaitingQueue().size() + " pacientes.");

        // Agendamento do próximo evento de chegada de paciente
        PatientArrivalEvent nextEvent = new PatientArrivalEvent(model, "Chegada de Paciente", true);
        nextEvent.schedule(new TimeSpan(model.getTimeArrival())); // Agendamento do próximo evento de chegada
    }
}
