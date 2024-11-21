package entities;
import desmoj.core.simulator.*;
import desmoj.core.statistic.*;
import models.Hospital;

import java.util.ArrayList;
import java.util.List;
// import java.util.ArrayList;
// import java.util.LinkedList;
// import java.util.List;
import java.util.UUID;

public class Office extends Entity{
    private final Hospital model = (Hospital) getModel();
    private final static String uuid = UUID.randomUUID().toString();        // Define o identificador unico do consultorio
    private final int iIndex;                                               // indice do consultorio

    // private final List<Doctor> assignedDoctors;                             // Lista de medicos atribuidos ao consultorio
    // private Queue<Doctor> availableDoctors;                                 // Fila de medicos disponiveis
    // private Queue<Doctor> busyDoctors;                                      // Fila de medicos ocupados
    // private final int minDoctorsRequired;                                   // Numero minimo de medicos necessários para funcionamento
    // private final int maxDoctorsAllowed;                                    // Numero máximo de medicos permitidos no consultorio

    private final int maxPatientQueueSize;                                  // Capacidade máxima da fila de espera de pacientes
    private Queue<Patient> queuePatients;                                   // Fila de pacientes aguardando atendimento
    private Patient ptrCurrentPatient;                                      // Define o paciente que está sendo atendido
    private List<Patient> listAttendedPatients;                             // Lista de pacientes que já foram atendidos

    private Count countOccupiedTime;                                        // Contador do tempo total ocupado
    private Count countPatientsAttended;                                    // Contador de pacientes atendidos

    // private LinkedList<Record> serviceHistory;                              // Historico de atendimentos realizados
    private boolean bIsAvailable;                                           // Status atual do consultorio (disponivel ou ocupado)
        
    /**
     * Construtor da classe Office.
     * 
     * @param owner                 O modelo ao qual o consultorio pertence.
     * @param showInTrace           Indica se o consultorio será registrado no rastreamento.
     * @param index                 indice do consultorio.
     * @param maxPatientQueueSize   Indica o numero máximo de pacientes na fila de espera.
     * @param doctorList            Indica os medicos atribuidos ao consultorio.
     * @param minDoctors            Indica o umero minimo de medicos necessários.
     * @param maxDoctors            Indica o numero máximo de medicos permitidos no consultorio.
     */
    public Office(Model owner, boolean showInTrace, int index, int maxPatientQueueSize){
        super(owner, uuid, showInTrace);
        this.iIndex = index;

        // Define o numero máximo e minimo de medicos permitidos no consultorio
        // this.minDoctorsRequired = minDoctors;
        // this.maxDoctorsAllowed = maxDoctors;
        // if (doctorList == null || doctorList.isEmpty()) throw new IllegalArgumentException("A lista de medicos nao pode ser nula ou vazia.");
        // if (doctorList.size() > maxDoctors) throw new IllegalStateException("Numero máximo de medicos excedido para o consultorio.");

        // Inicializaçao das filas de medicos
        // this.assignedDoctors = new ArrayList<>(doctorList);
        // this.availableDoctors = new Queue<>(owner, officeId + "_AvailableDoctors", showInTrace, showInTrace);
        // this.busyDoctors = new Queue<>(owner, officeId + "_BusyDoctors", showInTrace, showInTrace);
        // doctorList.forEach(availableDoctors::insert);

        this.ptrCurrentPatient = null;

        // Inicializaçao das filas de pacientes
        this.maxPatientQueueSize = (maxPatientQueueSize > 0) ? maxPatientQueueSize : 5;   // Define a capacidade máxima da fila de forma arbitrária
        this.queuePatients = new Queue<>(owner, uuid + "_WaitingPatients", showInTrace, showInTrace);
        this.listAttendedPatients = new ArrayList<>();

        // Inicializaçao dos contadores
        this.countOccupiedTime = new Count(owner, uuid + "Tempo Ocupado: " + countOccupiedTime, showInTrace, showInTrace);
        this.countPatientsAttended = new Count(owner, uuid + "Pacientes Atendidos: " + countPatientsAttended, showInTrace, showInTrace);

        // Define o status do consultorio como "livre"
        this.bIsAvailable = true;
    }

    // Getters
    public String getId(){
        return uuid;
    }

    public int getIndex(){
        return iIndex;
    }

    // public List<Doctor> getAssignedDoctors(){
    //     return assignedDoctors;
    // }

    // public Queue<Doctor> getAvailableDoctors(){
    //     return availableDoctors;
    // }

    // public Queue<Doctor> getBusyDoctors(){
    //     return busyDoctors;
    // }

    public Queue<Patient> getQueueWaitingPatients() {
        return queuePatients;
    }

    public Patient getCurrentPatient() {
        return ptrCurrentPatient;
    }

    public List<Patient> getListAttendedPatients() {
        return listAttendedPatients;
    }

    public int getMaxPatientQueueSize(){
        return maxPatientQueueSize;
    }

    public boolean getIsAvailable(){
        return bIsAvailable;
    }

    // Metodos internos
    // public void addDoctor(Doctor doctor){
    //     if(canAddMoreDoctors()) {
    //         assignedDoctors.add(doctor);
    //         availableDoctors.insert(doctor);
    //     } else throw new IllegalStateException("Numero máximo de medicos atingido no consultorio.");
    // }

    // public boolean isStaffed(){
    //     return this.assignedDoctors.size() >= minDoctorsRequired;
    // }

    // public boolean canAddMoreDoctors() {
    //     return assignedDoctors.size() < maxDoctorsAllowed;
    // }

    // public Doctor assignDoctorToPatient(){
    //     if(!availableDoctors.isEmpty()){
    //         Doctor doctor = availableDoctors.first();
    //         availableDoctors.remove(doctor);
    //         busyDoctors.insert(doctor);
    //         return doctor;
    //     } throw new IllegalStateException("Nenhum medico disponivel para atendimento.");
    // }

    // public void releaseDoctor(Doctor doctor){
    //     if(doctor == null) throw new IllegalArgumentException("O medico nao pode ser nulo.");
    //     if(!busyDoctors.contains(doctor)) throw new IllegalStateException("O medico nao está ocupado.");
    //     busyDoctors.remove(doctor);
    //     availableDoctors.insert(doctor);
    // }

    // public void addPatientToWaitingQueue(Patient patient){
    //     if(patient == null) throw new IllegalArgumentException("O paciente nao pode ser nulo.");
    //     if(queuePatients.contains(patient)) throw new IllegalStateException("O paciente já está alocado nesta fila.");
    //     queuePatients.insert(patient);
    // }

    public void startService(Patient patient){
        double timeService;
        OfficeEvent event;

        this.ptrCurrentPatient = patient;
        timeService = model.getTimeService();
    }

    // public void finishPatientService(Patient patient){
    //     if(patient == null) throw new IllegalArgumentException("O paciente nao pode ser nulo.");
    //     if(patient != ptrCurrentPatient) throw new IllegalStateException("O paciente nao está em atendimento.");

    //     // if(!listAttendedPatients.contains(patient)) throw new IllegalStateException("O paciente já teve o atendimento finalizado.");

    //     listAttendedPatients.add(patient);
    // }
}

public void attend(Client client){
    Market modelMarket = (Market) getModel();
    double timeService;
    CashierEvent event;

    this.client = client;
    timeService = modelMarket.getTimeService();
    modelMarket.sendTraceNote(this + " atendeu " + client + " por " + timeService + " minutos.");
    
    this.totalOccupiedTime.update((long) timeService);
    this.totalClientServed.update(1);
    
    event = new CashierEvent(modelMarket, "Evento de fim de atendimento", true);
    event.schedule(this, client, new TimeSpan(timeService));
    modelMarket.countTotalTimestamp.update((long) timeService);
    event.sendTraceNote("----------------------------------------------------------------------------");
}