// package models;

// import desmoj.core.simulator.*;
// import desmoj.core.statistic.*;
// import entities.Patient;
// import entities.Doctor;
// import entities.Office;
// import events.PatientArrivalEvent;
// import java.util.LinkedList;
// import java.util.Queue;

// /**
//  * Classe que representa o hospital em um sistema de simulaçao de atendimento.
//  * 
//  * Esta classe controla os eventos de chegada de pacientes, aloca medicos para atendimento,
//  * e mantem as filas de espera e os consultorios disponiveis.
//  * 
//  * @see desmoj.core.simulator.Model
//  * @author Higor Augusto
//  * @version 1.0
//  * @since 03/11/2024
//  */
// public class Hospital extends Model {
    
//     private Queue<Patient> patientQueue;        // Fila de pacientes aguardando atendimento
//     private Queue<Doctor> doctorQueue;          // Fila de medicos disponiveis
//     private Office office;                      // Consultorio para realizar o atendimento
    
//     // Estatisticas
//     public Count totalPatientsArrived;          // Contador de pacientes que chegaram
//     public Count totalPatientsServed;           // Contador de pacientes atendidos
    
//     /**
//      * Construtor da classe Hospital.
//      * Inicializa o hospital com as filas de pacientes e medicos e configura as estatisticas.
//      * 
//      * @param owner Modelo pai.
//      * @param name Nome do hospital.
//      * @param showInTrace Determina se o hospital será rastreado na simulaçao.
//      */
//     public Hospital(Model owner, String name, boolean showInReport, boolean showInTrace) {
//         super(owner, name, showInReport, showInTrace);
        
//         // Inicializaçao das filas e estatisticas
//         this.patientQueue = new LinkedList<>();
//         this.doctorQueue = new LinkedList<>();
//         this.office = new Office(owner, "Consultorio", showInTrace);
        
//         this.totalPatientsArrived = new Count(owner, "Total de Pacientes Chegados", showInReport, showInTrace);
//         this.totalPatientsServed = new Count(owner, "Total de Pacientes Atendidos", showInReport, showInTrace);
        
//         // Criando medicos ficticios para a simulaçao
//         createDoctors();
        
//         // Agendando o primeiro evento de chegada de paciente
//         PatientArrivalEvent firstArrival = new PatientArrivalEvent(this, "Chegada de Paciente", true);
//         firstArrival.schedule(new TimeSpan(0.0));  // Primeiro paciente chega imediatamente
//     }
    
//     // Metodos de controle de pacientes e medicos
    
//     /**
//      * Adiciona um paciente à fila de espera.
//      * 
//      * @param patient Paciente a ser adicionado.
//      */
//     public void addPatientToQueue(Patient patient) {
//         patientQueue.add(patient);
//         totalPatientsArrived.update();
//     }
    
//     /**
//      * Aloca um medico disponivel para atender um paciente.
//      * 
//      * @param doctor O medico a ser alocado.
//      * @param patient O paciente a ser atendido.
//      */
//     public void assignDoctorToPatient(Doctor doctor, Patient patient) {
//         // Atribui o paciente ao consultorio
//         office.assignDoctorToPatient(doctor, patient);
        
//         // Atualiza as estatisticas de pacientes atendidos
//         totalPatientsServed.update();
//     }
    
//     /**
//      * Aloca medicos ficticios para a simulaçao.
//      */
//     private void createDoctors() {
//         for (int i = 1; i <= 5; i++) {
//             Doctor doctor = new Doctor(this, "Doutor-" + u, true);
//             doctorQueue.add(doctor);  // Adiciona medicos à fila
//         }
//     }

//     // Metodos de consulta de filas e status
    
//     /**
//      * Retorna o proximo paciente da fila de espera.
//      * 
//      * @return O proximo paciente ou null se nao houver pacientes na fila.
//      */
//     public Patient getNextPatient() {
//         return patientQueue.poll();
//     }

//     /**
//      * Retorna o proximo medico disponivel da fila de medicos.
//      * 
//      * @return O proximo medico disponivel ou null se nao houver medicos disponiveis.
//      */
//     public Doctor getNextAvailableDoctor() {
//         return doctorQueue.poll();
//     }

//     /**
//      * Retorna o consultorio onde os atendimentos sao realizados.
//      * 
//      * @return O consultorio.
//      */
//     public Office getOffice() {
//         return office;
//     }

//     // Metodos de execuçao de eventos
    
//     /**
//      * Inicia o atendimento de um paciente, alocando um medico e registrando o evento.
//      */
//     public void startPatientService() {
//         Patient patient = getNextPatient(); // Retira o proximo paciente da fila
//         Doctor doctor = getNextAvailableDoctor(); // Retira o proximo medico disponivel da fila
        
//         if (patient != null && doctor != null) {
//             // Atribui o medico ao paciente
//             assignDoctorToPatient(doctor, patient);
//             // Inicia o atendimento no consultorio
//             office.startService(doctor, patient);
//         }
//     }

//     // Representaçao textual do Hospital
    
//     /**
//      * Retorna uma representaçao textual do estado atual do hospital.
//      * Inclui informações sobre as filas de pacientes, medicos e atendimentos.
//      * 
//      * @return Uma string representando o estado atual do hospital.
//      */
//     @Override
//     public String toString() {
//         return new StringBuilder()
//                 .append("Hospital{")
//                 .append("totalPatientsArrived=").append(totalPatientsArrived.getValue())
//                 .append(", totalPatientsServed=").append(totalPatientsServed.getValue())
//                 .append(", patientQueueSize=").append(patientQueue.size())
//                 .append(", doctorQueueSize=").append(doctorQueue.size())
//                 .append('}')
//                 .toString();
//     }
// }
