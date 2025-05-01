package events;

import java.util.concurrent.TimeUnit;

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
        
        // Agendamento do próximo evento de chegada de paciente
        scheduleNextPatientArrivalEvent();
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
        System.out.println("Paciente <" + patient.getId() + "> chegou ao hospital.");
        
        // Se houver recepcionista disponível, tenta encaminhar o paciente para a fila da recepção
        model.startScreening(patient);
    }

    /**
     * Agendamento do próximo evento de chegada de paciente.
     * 
     * O próximo evento será agendado de acordo com o tempo configurado no modelo.
     */
    private void scheduleNextPatientArrivalEvent(){
        // Cria o próximo evento de chegada de paciente
        PatientArrivalEvent nextEvent = new PatientArrivalEvent(model, "Chegada de Paciente", true);
        
        // Agendamento do próximo evento com base no tempo de chegada configurado no hospital
        nextEvent.schedule(new TimeSpan(model.getTimeArrival(), TimeUnit.MINUTES));

        // Próximo evento de chegada de paciente escalonado
        model.sendTraceNote("Próximo evento de triagem agendado.");
    }
}