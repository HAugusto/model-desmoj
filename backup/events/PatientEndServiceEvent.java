package events;

import java.util.List;

import desmoj.core.simulator.*;
import models.Hospital;
import models.Office;
import models.Patient;

public class PatientEndServiceEvent extends EventOf2Entities<Patient, Office> {
    private final Hospital model;

    /**
     * Construtor do evento de fim de atendimento do recepcionista.
     * 
     * @param owner         O modelo ao qual o evento pertence.
     * @param name          Nome do evento.
     * @param showInTrace   Indica se o evento será registrado no rastreamento.
     */
    public PatientEndServiceEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
        this.model = (Hospital) owner; // Casting do modelo
    }

    @Override
    public void eventRoutine(Patient patient, Office office) {
        // Verifica se o consultorio está disponivel
        if (office == null) {
            throw new IllegalArgumentException("O consultorio nao pode ser nulo.");
        }

        // for(Office offices: list) System.out.println("Consultorio Status " + offices.getIndex() + ": " + offices.getIsAvailable());
        if(!office.getIsQueueEmpty()){
            List<Office> list = model.getListOffice();
            // Libera o consultorio ao final do atendimento
            sendTraceNote("Consultorio " + office.getId() + " liberado apos atendimento do paciente " + patient.getId());
            
            // Verifica a fila de espera de pacientes para o consultorio
            // Remove o paciente da fila e começa o atendimento
            patient.setServiceEndTime(model.presentTime()); // Atribui o tempo de chegada ao paciente
            System.out.println(patient.getServiceEndTime());

            office.toggleStatus();  // Consultorio agora está disponivel
            office.removePatientOnQueue();
            office.updateCountPatientsAttended();

            // Pega o proximo paciente na fila do consultorio
            Patient nextPatient = office.getQueueWaitingPatients().first();
            
            if(!office.getIsQueueEmpty()){
            // Agendamento do evento de inicio de atendimento para o proximo paciente
                PatientStartServiceEvent startServiceEvent = new PatientStartServiceEvent(getModel(), "Inicio de Atendimento - Consultorio", true);
                startServiceEvent.schedule(nextPatient, office, new TimeSpan(0));  // Agendamento imediato (0 delay)
                sendTraceNote("Paciente " + nextPatient.getId() + " agora será atendido no consultorio " + office.getId());
            }
        } else {
            sendTraceNote("Consultorio " + office.getId() + " nao tem pacientes aguardando.");
        }
    }
}