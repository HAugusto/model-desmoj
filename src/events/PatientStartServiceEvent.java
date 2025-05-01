package events;
import desmoj.core.simulator.*;
import models.Hospital;
import models.Office;
import models.Patient;

public class PatientStartServiceEvent extends EventOf2Entities<Patient, Office> {

    public PatientStartServiceEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
    }

    @Override
    public void eventRoutine(Patient patient, Office office) {
        Hospital model = (Hospital) getModel();

        // Envia um trace note sobre o inicio do atendimento
        model.sendTraceNote("Inicio de Atendimento para o paciente " + patient.getId() + " no consultorio " + office.getId());
        patient.setServiceStartTime(model.presentTime());

        System.out.println("Paciente <" + patient.getId() + "> sendo atendido | Início: " + patient.getServiceStartTime());
        // Atribui o tempo de término de serviço ao paciente
		office.toggleStatus();
        office.setPatient(patient);
        office.getQueueWaitingPatients().remove(office.getQueueWaitingPatients().first());
        
        // Atualiza o tempo de atendimento do paciente
        model.tallyAverageWaitingTime.update(patient.getServiceStartTime().getTimeAsDouble() - patient.getArrivalTime().getTimeAsDouble());
        
        // Atualiza a contagem de pacientes atendidos
        model.countPatientsServed.update();
        
        // Atualiza contadores de ocupaçao
        // office.updateCountOccupiedTime();
        office.updateCountPatientsAttended();
        office.endConsultation();

		// Atualiza metricas globais do modelo
		model.countTotalTimestamp.update((long) model.getTimeService());

        // Cria o evento de fim de atendimento (agendar o proximo evento)
        // PatientEndServiceEvent event = new PatientEndServiceEvent(model, "Fim do Atendimento", true);
        // event.schedule(patient, office, new TimeSpan(model.getTimeService())); // Defina o tempo de atendimento aqui, se necessário

        // Chama o metodo do consultorio para iniciar o atendimento ao paciente
        // model.freePatientOffice(patient, office);
    }
}