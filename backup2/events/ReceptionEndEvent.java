package events;

import java.util.concurrent.TimeUnit;

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
        System.out.println("Estou aqui");
        // System.out.println(receptionist.getPatient().getId());
        // System.out.println(receptionist.getIsAvailable());
        
        // Finaliza o atendimento do paciente e registra a ação
        Patient patient = receptionist.getPatient();  // Obtemos o paciente que está sendo atendido
        model.sendTraceNote("Fim do atendimento do paciente ID: " + patient.getId());
        System.out.println("Fim do atendimento: <" + patient.getId() + "> | Tempo: " + model.presentTime().getTimeAsDouble());
        
        receptionist.setPatient(null);
        receptionist.updateCountPatientsAttended();  // Incrementa o contador de pacientes atendidos
        receptionist.toggleStatus();
        
        // Libera o recepcionista e altera seu status para disponível
        // scheduleNextEndReceptionEvent();
    }

    /**
     * Método para agendar o próximo evento término de triagem.
     */
    private void scheduleNextEndReceptionEvent() {
        // System.out.println("Aqui: " + receptionist.getPatient());

        ReceptionEndEvent nextEvent = new ReceptionEndEvent(model, "Fim da Triagem", true, receptionist);
        nextEvent.schedule(new TimeSpan(5, TimeUnit.MINUTES));  // Agendamento após 15 minutos (tempo de atendimento)
        
        model.sendTraceNote("Próximo evento de fim de triagem agendado.");
    }
}
