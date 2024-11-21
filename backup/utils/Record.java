package utils;
import desmoj.core.simulator.*;
import entities.Doctor;
import entities.Patient;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Representa o registro de um atendimento medico em um sistema de simulaçao hospitalar.
 * Contem informações sobre o paciente, medico, tempos de espera e duraçao do atendimento,
 * alem de observações adicionais.
 *  
 * @see desmoj.core.simulator
 * @author Higor Augusto
 * @version 1.1.0
 * @since 15/11/2024
 */
public class Record {
    private final String uuid;
    private final Patient patient;
    private final Doctor doctor;
    private final TimeInstant arrivalTimestamp;
    private final TimeInstant startTimestamp;
    private final TimeInstant endTimestamp;
    private final TimeSpan waitingTime;
    private final TimeSpan durationTime;
    private final Status status;
    private final String notes;

    /**
     * Construtor para criar um registro de atendimento.
     * Valida as informações obrigatorias do paciente e do medico antes de inicializar o registro.
     * 
     * @param patient O paciente associado ao atendimento.
     * @param doctor O medico responsável pelo atendimento.
     * @param notes Observações adicionais sobre o atendimento (pode ser nulo).
     * @throws IllegalArgumentException Se qualquer campo obrigatorio for inválido.
     */
    public Record(Patient patient, Doctor doctor, Status status, String notes){
        validatePatient(patient);
        validateDoctor(doctor);

        this.uuid = UUID.randomUUID().toString();
        this.patient = patient;
        this.doctor = doctor;
        this.arrivalTimestamp = patient.getArrivalTimestamp();
        this.startTimestamp = patient.getStartTimestamp();
        this.endTimestamp = patient.getEndTimestamp();
        this.waitingTime = calculateWaitingTime();
        this.durationTime = calculateDurationTime();
        this.status = (status == null) ? status : new Status("WAITING");
        this.notes = (notes == null || notes.isEmpty()) ? "" : notes;
    }

    // Getters
    /**
     * Retorna o identificador unico do registro de atendimento.
     * Este ID e gerado automaticamente e e unico para cada atendimento.
     * 
     * @return O UUID do registro de atendimento.
     */
    public String getId(){
        return uuid;
    }

    /**
     * Retorna o paciente associado ao registro de atendimento.
     * 
     * @return O paciente que está sendo atendido.
     */
    public Patient getPatient(){
        return patient;
    }

    /**
     * Retorna o medico responsável pelo atendimento.
     * 
     * @return O medico que está atendendo o paciente.
     */
    public Doctor getDoctor(){
        return doctor;
    }

    /**
     * Retorna o timestamp de chegada do paciente.
     * O timestamp de chegada e um ponto no tempo que marca quando o paciente entrou no sistema de atendimento.
     * 
     * @return O timestamp de chegada do paciente.
     */
    public TimeInstant getArrivalTimestamp(){
        return arrivalTimestamp;
    }

    /**
     * Retorna o timestamp de inicio de atendimento do paciente.
     * O timestamp de inicio representa o momento em que o medico começou o atendimento ao paciente.
     * 
     * @return O timestamp de inicio de atendimento.
     */
    public TimeInstant getStartTimestamp(){
        return startTimestamp;
    }

    /**
     * Retorna o timestamp de fim de atendimento do paciente.
     * O timestamp de fim representa o momento em que o atendimento ao paciente foi concluido.
     * 
     * @return O timestamp de fim de atendimento.
     */
    public TimeInstant getEndTimestamp(){
        return endTimestamp;
    }

    /**
     * Retorna o tempo de espera do paciente entre sua chegada e o inicio do atendimento.
     * Esse valor e calculado em minutos e indica quanto tempo o paciente esperou antes de ser atendido.
     * 
     * @return O tempo de espera do paciente como um objeto TimeSpan.
     */
    public TimeSpan getWaitingTime(){
        return waitingTime;
    }

    /**
     * Retorna o tempo total que o paciente passou no sistema, desde a chegada ate o fim do atendimento.
     * Esse valor e calculado em minutos e representa o tempo total do paciente dentro do ambiente hospitalar.
     * 
     * @return O tempo total de atendimento como um objeto TimeSpan.
     */
    public TimeSpan getDurationTime(){
        return durationTime;
    }

    /**
     * Retorna o status associado ao atendimento.
     * Esse valor pode ser definido pelo usuário ou pelo sistema, durante o decorrer do atendimento.
     * 
     * @return O status final do atendimento.
     */
    public String getStatus(){
        return status.getStatus();
    }

    /**
     * Retorna as observações adicionais associadas ao atendimento.
     * As observações podem incluir qualquer informaçao extra relevante sobre o atendimento,
     * como condições especiais ou notas do medico.
     * 
     * @return As notas ou observações sobre o atendimento.
     */
    public String getNotes(){
        return notes;
    }

    // Metodos internos
    /**
     * Valida os campos obrigatorios do paciente.
     * Garante que o paciente tenha os timestamps corretos de chegada, inicio e fim de atendimento.
     *
     * @param patient O paciente a ser validado.
     * @throws IllegalArgumentException Se informações essenciais do paciente estiverem ausentes.
     */
    private void validatePatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("O paciente nao pode ser nulo.");
        }
        if (patient.getArrivalTimestamp() == null) {
            throw new IllegalArgumentException("Timestamp de chegada do paciente e obrigatorio.");
        }
        if (patient.getStartTimestamp() == null) {
            throw new IllegalArgumentException("Timestamp de inicio de atendimento do paciente e obrigatorio.");
        }
        if (patient.getEndTimestamp() == null) {
            throw new IllegalArgumentException("Timestamp de fim de atendimento do paciente e obrigatorio.");
        }
        if (TimeInstant.isBefore(patient.getStartTimestamp(), patient.getArrivalTimestamp())) {
            throw new IllegalArgumentException("Timestamp de inicio de atendimento nao pode ser anterior ao de chegada.");
        }
        if (TimeInstant.isBefore(patient.getEndTimestamp(), patient.getStartTimestamp())) {
            throw new IllegalArgumentException("Timestamp de fim de atendimento nao pode ser anterior ao de inicio de atendimento.");
        }
    }

    /**
     * Valida os campos obrigatorios do medico.
     * Garante que o medico associado ao atendimento nao seja nulo.
     *
     * @param doctor O medico a ser validado.
     * @throws IllegalArgumentException Se o medico for nulo.
     */
    private void validateDoctor(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("O medico nao pode ser nulo.");
        }
    }

    /**
     * Calcula o tempo de espera do paciente entre sua chegada e o inicio do atendimento.
     * O tempo de espera e calculado em minutos.
     *
     * @return TimeSpan representando o tempo de espera.
     */
    private TimeSpan calculateWaitingTime() {  
        if(arrivalTimestamp == null || startTimestamp == null) return null;
        double arrivalTimeInMinutes = patient.getArrivalTimestamp().getTimeAsDouble(TimeUnit.MINUTES);
        double startTimeInMinutes = patient.getStartTimestamp().getTimeAsDouble(TimeUnit.MINUTES);

        return new TimeSpan((startTimeInMinutes - arrivalTimeInMinutes), TimeUnit.MINUTES);
    }

    /**
     * Calcula o tempo total do paciente no sistema, desde sua chegada ate o fim do atendimento.
     * O tempo total e calculado em minutos e representa o tempo entre a chegada e o fim do atendimento.
     * 
     * @return TimeSpan representando o tempo total no sistema.
     */
    private TimeSpan calculateDurationTime() {       
        if(arrivalTimestamp == null || endTimestamp == null) return null;
        double arrivalTimeInMinutes = patient.getArrivalTimestamp().getTimeAsDouble(TimeUnit.MINUTES);
        double endTimeInMinutes = patient.getEndTimestamp().getTimeAsDouble(TimeUnit.MINUTES);

        return new TimeSpan((endTimeInMinutes - arrivalTimeInMinutes), TimeUnit.MINUTES);
    }

    /** 
     * Representaçao em string do registro de atendimento.
     * Inclui informações relevantes sobre o paciente, medico e tempos registrados.
     * 
     * @return Uma string representando o registro de atendimento.
     */
    @Override
    public String toString() {
        return new StringBuilder()
                .append("Record{")
                .append("uuid='").append(uuid).append('\'')
                .append(", patient=").append(patient.getName())
                .append(", doctor=").append(doctor.getName())
                .append(", arrival=").append(patient.getArrivalTimestamp())
                .append(", start=").append(patient.getStartTimestamp())
                .append(", end=").append(patient.getEndTimestamp())
                .append(", waitingTime=").append(waitingTime)
                .append(", durationTime=").append(durationTime)
                .append(", notes=").append(notes)
                .append('}')
                .toString();
    }
}
