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
        // Atribui o tempo de término de serviço ao paciente
        patient.setServiceStartTime(model.presentTime());
		office.toggleStatus();
        office.setPatient(patient);

        
        // Atualiza o tempo de atendimento do paciente
        model.tallyAverageWaitingTime.update(model.presentTime().getTimeAsDouble() - patient.getArrivalTime().getTimeAsDouble());
        
        // Atualiza a contagem de pacientes atendidos
        model.countPatientsServed.update();
        
        // Atualiza contadores de ocupaçao
        // office.updateCountOccupiedTime();
        office.updateCountPatientsAttended();
        
		// Atualiza metricas globais do modelo
		model.countTotalTimestamp.update((long) model.getTimeService());
        
        // Cria o evento de fim de atendimento (agendar o proximo evento)
        PatientStartServiceEvent endServiceEvent = new PatientStartServiceEvent(model, "Fim do Atendimento", true);
        endServiceEvent.schedule(patient, office, new TimeSpan(model.getTimeService())); // Defina o tempo de atendimento aqui, se necessário
        // Chama o metodo do consultorio para iniciar o atendimento ao paciente
        // model.freePatientOffice(patient, office);
    }
}