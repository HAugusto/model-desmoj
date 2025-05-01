package models;
import desmoj.core.simulator.*;
import desmoj.core.statistic.*;
import desmoj.core.dist.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import events.*;

/**
 * Classe que representa o modelo de um hospital no sistema de simulação de atendimento hospitalar.
 * Esta classe gerencia os consultórios, recepcionistas, filas de pacientes e o processo de atendimento.
 * Além disso, mantém as estatísticas sobre o número de pacientes atendidos e o tempo de espera.
 *
 * <p> Contém métodos para inicializar o hospital, gerenciar os eventos de chegada de pacientes, 
 * atribuição de recepcionistas e relatórios estatísticos sobre o atendimento. </p>
 *
 * @see desmoj.core.simulator
 * @author Higor Augusto
 * @version 1.0.0
 * @since 03/11/2024
 */
public class Hospital extends Model {
    // Características da simulação
    private final String uuid;                      // Identificador único do hospital
    private double dSimulationTime;                 // Tempo total da simulação do hospital

    // Definição das variáveis operacionais
    private final int iNumReceptionists;            // Número de recepcionistas no hospital
    private final int iNumOffices;                  // Número de consultórios no hospital
    private List<Office> listOffice;                // Lista de consultórios
    private List<Receptionist> listReceptionist;    // Lista de recepcionistas
    public Queue<Patient> queuePatient;            // Fila de pacientes à espera
    private final int iMaxQueueSize;                 // Tamanho máximo da fila de pacientes
    
    // Definição das variáveis estatísticas
    public Count countPatientsServed;               // Contador de pacientes atendidos
    public Count countTotalTimestamp;               // Contador do tempo total de simulação
    public Tally tallyAverageWaitingTime;           // Estatística de tempo médio de espera
    public Tally tallyAverageSystemTime;            // Estatística de tempo médio no sistema

    // Definição das distribuições de tempo
    private ContDistExponential distTimeArrival;    // Distribuição exponencial para o tempo de chegada dos pacientes
    private ContDistNormal distTimeService;         // Distribuição normal para o tempo de serviço (atendimento)
    private ContDistUniform distUrgency;            // Para simular a probabilidade de urgência

    // Definição da triagem
    private double dAverageTimeServiceAttended;     // Tempo médio de serviço no atendimento
    private double dAverageTimeServiceEnding;       // Tempo médio de serviço na finalização

    // Definição para relatórios
    private boolean bShowInReport;                  // Indica se deve mostrar relatórios
    private boolean bShowInTrace;                   // Indica se deve mostrar o rastreamento dos eventos

    /**
     * Construtor do Hospital.
     * Inicializa os parâmetros do hospital, incluindo o número de recepcionistas, consultórios, 
     * e configurações do modelo de simulação.
     *
     * @param owner             O modelo ao qual o hospital pertence.
     * @param name              O nome do hospital.
     * @param showInReport      Indica se o hospital deve gerar relatórios.
     * @param showInTrace       Indica se o hospital deve gerar rastreamento de eventos.
     * @param simulationTime    Tempo total de simulação.
     * @param numOffices        Número de consultórios no hospital.
     * @param iMaxQueueSize      Tamanho máximo da fila de pacientes.
     */
    public Hospital(Model owner, String name, boolean showInReport, boolean showInTrace, double simulationTime, int numOffices, int iMaxQueueSize, double avgTimeServiceAttended, double avgTimeServiceEnding){
        super(owner, name, showInReport, showInTrace);

        // Inicializa os identificadores do hospital
        this.uuid = UUID.randomUUID().toString();

        // Configura o tempo de simulação e as opções de relatório e rastreamento
        this.dSimulationTime = (simulationTime >= 0) ? simulationTime : 600;
        this.bShowInReport = showInReport;
        this.bShowInTrace = showInTrace;
        
        // Inicializa as configurações dos consultórios e recepcionistas
        this.listOffice = new ArrayList<>();
        this.listReceptionist = new ArrayList<>();
        this.iNumOffices = (numOffices >= 0) ? numOffices : 5;
        this.iNumReceptionists = 1;

        this.dAverageTimeServiceAttended = avgTimeServiceAttended;
        this.dAverageTimeServiceEnding = avgTimeServiceEnding;

        this.iMaxQueueSize = (iMaxQueueSize >= 0) ? iMaxQueueSize : 5;
    }
    
    @Override
    public String description(){
        return ("Modelo de eventos discretos de hospital");
    }

    @Override
    public void init(){
        // Inicializa as variáveis de estatísticas
        this.countPatientsServed = new Count(this, "Pacientes Atendidos: ", bShowInReport, bShowInTrace);
        this.countTotalTimestamp = new Count(this, "Tempo Total: ", bShowInReport, bShowInTrace);
        this.tallyAverageWaitingTime = new Tally(this, "Tempo Médio (Fila): ", bShowInReport, bShowInTrace);
        this.tallyAverageSystemTime = new Tally(this, "Tempo Médio (Sistema): ", bShowInReport, bShowInTrace);
    
        // Inicializa a fila de pacientes
        queuePatient = new Queue<Patient>(this, "Clientes na Fila: ", bShowInReport, bShowInTrace);

        // Cria os recepcionistas
        for(int i = 0; i < iNumReceptionists; i++) {
            try{
                Receptionist receptionist = new Receptionist(this, "Recepcionista", true, true, true, 5);
                listReceptionist.add(receptionist);
            } catch(Exception exception){
                exception.printStackTrace();
            }
        }

        // Cria os consultórios
        for(int i = 0; i < iNumOffices; i++) {
            try{
                Office office = new Office(this, true, i, iMaxQueueSize);
                listOffice.add(office);

                // Inicializa contadores específicos para os consultórios
                office.setCountPatientsAttended(new Count(this, "Consultório " + (office.getIndex() + 1) + " - Pacientes Atendidos: ", bShowInTrace, bShowInReport));
                office.setCountOccupiedTime(new Count(this, "Consultório " + (office.getIndex() + 1) + " - Tempo Ocupado: ", bShowInTrace, bShowInReport));
            } catch(Exception exception){
                exception.printStackTrace();
            }
        }

        // Inicializa as distribuições de tempo de chegada e tempo de serviço
        this.distTimeArrival = new ContDistExponential(this, "Tempo de Chegada", 15, bShowInTrace, bShowInReport);
        this.distTimeService = new ContDistNormal(this, "Tempo de Serviço", 20, 5, bShowInTrace, bShowInReport);
        this.distUrgency = new ContDistUniform(this, "Grau de Urgência", 0, 1, bShowInTrace, bShowInReport);
        this.distTimeArrival.setNonNegative(true);
        this.distTimeService.setNonNegative(true);
    }

    @Override   
    public void doInitialSchedules(){
        // Programa o evento de chegada de um paciente
        PatientArrivalEvent event = new PatientArrivalEvent(this, "Evento gerador de cliente", bShowInTrace);
        event.schedule(new TimeSpan(0.0));
    }

    // Getters
    /**
     * Retorna o identificador único do hospital.
     * 
     * @return O UUID do hospital.
     */
    public String getId() {
        return uuid;
    }

    /**
     * Retorna o número de consultórios no hospital.
     * 
     * @return O número de consultórios.
     */
    public int getNumOffices() {
        return iNumOffices;
    }

    /**
     * Retorna a lista de consultórios do hospital.
     * 
     * @return Lista de consultórios.
     */
    public List<Office> getListOffice() {
        return listOffice;
    }

    /**
     * Retorna o tempo de chegada de pacientes gerado pela distribuição.
     * 
     * @return Tempo de chegada de um paciente.
     */
    public double getTimeArrival(){
        // System.out.println(distTimeArrival.sample());
        return distTimeArrival.sample();
    }

    /**
     * Retorna o tempo de serviço gerado pela distribuição.
     * 
     * @return Tempo de serviço (atendimento).
     */
    public double getTimeService(){
        return distTimeService.sample();
    }

    /**
     * Retorna o tempo total da simulação.
     * 
     * @return Tempo de simulação.
     */
    public double getSimulationTime(){
        return dSimulationTime;
    }

    /**
     * Retorna o número de pacientes atendidos no hospital.
     * 
     * @return Número de pacientes atendidos.
     */
    public double getCountPatientsServed(){
        return countPatientsServed.getValue();
    }

    /**
     * Retorna o número de pacientes na fila de espera.
     * 
     * @return Número de pacientes na fila.
     */
    public double getClientsQueue(){
        return queuePatient.length();
    }

    /**
     * Retorna a média do tamanho da fila de pacientes.
     * 
     * @return Média do tamanho da fila de pacientes.
     */
    public double getAverageClientsQueue(){
        return queuePatient.averageLength();
    }

    /**
     * Retorna a lista de recepcionistas no hospital.
     * 
     * @return Lista de recepcionistas.
     */
    public List<Receptionist> getListReceptionist(){
        return listReceptionist;
    }

    /**
     * Retorna o primeiro recepcionista disponível.
     * 
     * @return O recepcionista disponível, ou null se nenhum estiver disponível.
     */
    public Receptionist getAvailableReceptionist(){
        for(Receptionist receptionist: listReceptionist){
            if(receptionist.getIsAvailable()) return receptionist;
        }
        return null;
    }

    /**
     * Retorna o recepcionista que está atendendo um paciente específico.
     * 
     * @param patient O paciente para o qual o recepcionista está atendendo.
     * @return O recepcionista que está atendendo o paciente, ou null se nenhum for encontrado.
     */
    public Receptionist getReceptionistByPatient(Patient patient){
        for (Receptionist receptionist : listReceptionist) {
            if (receptionist.getPatient() == patient) return receptionist;
        }
        return null;
    }

    /**
     * Retorna a fila de pacientes do hospital.
     * 
     * @return A fila de pacientes.
     */
    public Queue<Patient> getQueuePatient(){
        return queuePatient;
    }

    /**
     * Retorna a fila de pacientes do hospital.
     * 
     * @return A fila de pacientes.
     */
    public int getiMaxQueueSize() {
        return iMaxQueueSize;
    }

    public void insertQueue(Patient patient){
        if(patient == null) throw new IllegalArgumentException("Os parâmetros não podem ser nulos");
        queuePatient.insert(patient);
        sendTraceNote("Paciente <" + patient.getId() + "> foi inserido na fila do hospital");
        System.out.println("Fila Única | Recepcionista: " + queuePatient.size());
    }

    public void startReception(){
        Receptionist receptionist = getAvailableReceptionist();
        if(receptionist == null) {
            sendTraceNote("Não há recepcionistas disponíveis no momento...");
            return;
        } else if(!queuePatient.isEmpty()){
            Patient patient = queuePatient.first();
            queuePatient.remove(patient);

            double urgencyValue = distUrgency.sample(); // Gera um valor entre 0 e 1
            if(urgencyValue <= 0.3) patient.setIsUrgent(); // 30% de probabilidade para urgência

            receptionist.setPatient(patient);
            receptionist.toggleStatus();

            ReceptionStartEvent event = new ReceptionStartEvent(getModel(), "Início de triagem", bShowInTrace, receptionist);
            event.schedule(new TimeSpan(dAverageTimeServiceAttended, TimeUnit.MINUTES));

            sendTraceNote("Paciente <" + patient.getId() + "> sendo atendido na recepção.");
        } else {
            sendTraceNote("A fila está vazia.");
            System.out.println("A fila está vazia.");
        }
    }

    public void assignOffice(Patient patient){
        if(patient == null) throw new IllegalArgumentException("Os parâmetros não podem ser nulos");
        // Optional<Office> availableOffice = listOffice.stream().filter(Office::getIsAvailable).findFirst();
        Office office = getOfficeWithShortestQueue();

        if(office == null) {
            sendTraceNote("Não há consultórios livres.");
            return;
        } else {
            if(patient.getIsUrgent()){
                office.startConsultation(patient, office);
            } else {
                office.getQueueWaitingPatients().insert(patient);
                sendTraceNote("Paciente <" + patient.getId() + "> adicionado a fila do Consultório " + office.getIndex());
                // Se a recepcionista estiver disponível, agenda o atendimento do paciente
                if(!office.getQueueWaitingPatients().isEmpty()){
                    office.startConsultation(office.getQueueWaitingPatients().first(), office);

                    // Emite nota de rastreamento sobre o atendimento imediato
                    sendTraceNote("Paciente sendo atendido imediatamente | Fila do Consultório: " + office.getQueueWaitingPatients().size());
                    System.out.println("Paciente sendo atendido imediatamente | Fila do Consultório: " + office.getQueueWaitingPatients().size());
                } else if(office.getQueueWaitingPatients().size() > 5) {
                    // Se a fila estiver cheia, o paciente é rejeitado
                    sendTraceNote("Fila do Consultório cheia! Paciente rejeitado.");
                    System.out.println("Fila do Consultório cheia! Paciente rejeitado.");

                    // Liberar o paciente
                }
            }
        }
    }

    // Função para atender paciente através de um recepcionista
    /**
     * Atende um paciente e o coloca na fila para ser atendido por um recepcionista.
     * 
     * @param patient O paciente a ser atendido.
     * @param receptionist O recepcionista que atenderá o paciente.
     */
    // public void attendPatient(Patient patient) {
    //     // 1. Verifica se há espaço na fila (5 pacientes)
    //     // 2. Se há espaço na fila, o paciente entra na fila
    //     // 3. Se a fila contém somente um paciente, ele vai para atendimento direto
    //     // 4. Se a fila for maior que 0, atende o primeiro paciente
    //     // 5. Se a fila estiver vazia, a recepcionista aguarda a chegada de um paciente
    //     if(patient == null) throw new IllegalArgumentException("Os parâmetros não podem ser nulos");
        
    //     Receptionist receptionist = listReceptionist.getFirst();

    //     if(receptionist.getWaitingQueue().size() < 5) receptionist.getWaitingQueue().insert(patient);
    //     else ;

    //     // Se a recepcionista estiver disponível, agenda o atendimento do paciente
    //     if(receptionist.getWaitingQueue().size() > 0){
    //         System.out.println(receptionist.getIsAvailable());

    //         ReceptionStartEvent event = new ReceptionStartEvent(getModel(), "Recepcionista | " + receptionist.getId(), bShowInTrace, receptionist);
    //         event.schedule(new TimeSpan(15, TimeUnit.MINUTES));

    //         sendTraceNote("Paciente sendo atendido imediatamente | Fila da Recepção: " + receptionist.getWaitingQueue().size());
    //         // Emite nota de rastreamento sobre o atendimento imediato
    //     } else if(receptionist.getWaitingQueue().size() > 5) {
    //         // Se a fila estiver cheia, o paciente é rejeitado
    //         sendTraceNote("Fila da Recepcionista cheia! Paciente rejeitadoeje.");
    //         System.out.println("Fila da Recepcionista cheia! Paciente ritado.");

    //         // Destruir o paciente
    //     }
    // }

    // public void releaseReceptionist(Receptionist receptionist){
    //     // Verifica se uma recepcionista foi selecionada
    //     if(receptionist == null) throw new IllegalStateException("A recepcionista nao pode ser nula.");

    //     receptionist.releasePatient();
    //     // ReceptionEndEvent event = new ReceptionEndEvent(getModel(), "Fim da Triagem", true, receptionist);
    //     // event.schedule(new TimeSpan(5, TimeUnit.MINUTES)); // Tempo de atendimento (tempo de serviço do recepcionista)
    // }

    // public void attendPatientOffice(Patient patient, Office office) {
    //     // Verifica se o consultório foi encontrado
    //     if (office == null) {
    //         throw new IllegalArgumentException("Não há consultórios disponíveis para atendimento.");
    //     }
    
    //     // Verifica se a fila do consultório está cheia
    //     if (office.getQueueWaitingPatients().size() >= office.getMaxPatientQueueSize()) {
    //         sendTraceNote("A fila do consultório " + office.getId() + " está cheia.");
    //         return; // Se a fila do consultório estiver cheia, o paciente não será atendido
    //     }
    
    //     // Adiciona o paciente na fila do consultório
    //     office.addPatientOnQueue(patient);
    //     sendTraceNote("Paciente " + patient.getId() + " foi adicionado à fila do Consultório " + office.getId() + ".");
    //     for(Office offices: listOffice) System.out.println("Fila | Consultório " + offices.getIndex() + ": " + offices.getQueueWaitingPatients().size() + " pacientes.");
    
    //     // Se o consultório está disponível, começa a atender o paciente imediatamente
    //     if (office.getIsAvailable()) {
    //         // Agenda o início do atendimento do paciente
    //         PatientStartServiceEvent event = new PatientStartServiceEvent(getModel(), "Início de Atendimento - Consultório", bShowInTrace);
    //         event.schedule(patient, office, new TimeSpan(getTimeService()));  // Tempo de serviço configurado
    //         sendTraceNote("Atendimento iniciado para o paciente " + patient.getId() + " no Consultório " + office.getId());
    //     } else {
    //         sendTraceNote("O Consultório " + office.getId() + " está ocupado. O paciente " + patient.getId() + " aguarda na fila.");
    //     }
    
    //     // Exibe a quantidade de pacientes na fila do consultório
    //     sendTraceNote("Fila do Consultório " + office.getId() + " tem " + office.getQueueWaitingPatients().size() + " pacientes.");
    // }
    
    public void freePatientOffice(Patient patient, Office office) {
        if (office == null) throw new IllegalArgumentException("O consultorio nao pode ser nulo.");
    
        // Verifica se o consultorio está ocupado

            // Se o consultorio nao está disponivel, libera o consultorio
            office.toggleStatus(); // Alterando o status do consultorio para "disponivel"
            sendTraceNote("Consultorio " + office.getId() + " liberado apos atendimento.");

            for(Office offices : listOffice)
            // Se houver pacientes na fila, agenda o atendimento do proximo
            if (!office.getQueueWaitingPatients().isEmpty()) {
                // Pega o proximo paciente da fila
                Patient nextPatient = office.getQueueWaitingPatients().first();
                
                // Remove o paciente da fila, pois será atendido
                // office.removePatientOnQueue();

                // Agendando o evento para iniciar o atendimento
                PatientEndServiceEvent Evento = new PatientEndServiceEvent(getModel(), "Inicio de Atendimento", bShowInTrace);
                Evento.schedule(nextPatient, office, new TimeSpan(getTimeService()));  // Agendamento imediato (0 delay)
    
                sendTraceNote("Paciente " + nextPatient.getId() + " agora está sendo atendido no consultorio " + office.getId());
            } else sendTraceNote("Consultorio " + office.getId() + " nao tem pacientes aguardando.");
        sendTraceNote("Consultorio " + office.getId() + " já está disponivel.");
    }

    public Office getOfficeWithShortestQueue() {
        Office selectedOffice = null;
        int shortestQueueSize = Integer.MAX_VALUE; // Inicializa com o maior valor possivel
        
        // Percorre todos os consultorios disponiveis
        for (Office office : this.listOffice) {
            Queue<Patient> queue = office.getQueueWaitingPatients(); // Obtem a fila do consultorio
            int currentQueueSize = queue.size(); // Obtem o tamanho da fila

            System.out.println("Fila | Consultorio " + office.getIndex() + ": " + office.getQueueWaitingPatients().size());

            // Verifica se este consultorio tem a menor fila ate o momento
            if (currentQueueSize < shortestQueueSize && currentQueueSize < iMaxQueueSize) {
                shortestQueueSize = currentQueueSize;
                selectedOffice = office;
            }
        }
    
        // Retorna o consultorio com a menor fila ou null se nao houver consultorios
        return selectedOffice;
    }
}