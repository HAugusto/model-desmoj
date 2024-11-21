package utils;
import java.util.HashMap;
import java.util.Map;
/**
 * Representa os status dos pacientes em um sistema de simulaçao hospitalar.
 * Contem informações sobre os status e descrições.
 * 
 * @author Higor Augusto
 * @version 1.0.2
 * @since 16/11/2024
 */
public class Status {
    // Inicializa algumas variáveis de configuraçao
    private String initialStatus;
    private String status;
    
    // Mapa para armazenar os status com seus respectivos nomes e descrições
    private Map<String, StatusInfo> statusMap = new HashMap<>();
    
    private static class StatusInfo{
        // Atributo que armazena a descriçao do status
        private String description;

        // Construtor da classe StatusInfo
        /**
         * Este construtor e responsável por inicializar a descriçao do status.
         * 
         * @param description A descriçao do status, como "Aguardando", "Em andamento", etc.
         */
        public StatusInfo(String description){
            this.description = description;
        }
        
        // Metodos internos
        /**
         * Este metodo retorna a descriçao associada a um status.
         * 
         * @return A descriçao do status, como "Aguardando", "Em andamento", etc.
         */
        public String getDescription(){
            return description;
        }
    }

    // Constructor
    /**
     * Construtor da classe Status.
     * Inicializa os status iniciais.
     */
    public Status(String initialStatus){
        statusMap.put("WAITING", new StatusInfo("Aguardando"));
        statusMap.put("IN_PROGRESS", new StatusInfo("Em progresso"));
        statusMap.put("COMPLETED", new StatusInfo("Completo"));
        statusMap.put("CANCELED", new StatusInfo("Cancelado"));

        this.initialStatus = (initialStatus == null || initialStatus.isEmpty() || !statusMap.containsKey(initialStatus)) ? "WAITING" : initialStatus;
        this.status = this.initialStatus;
    }

    // Getters
    /**
     * Retorna o status atual armazenado.
     * 
     * @return O nome do status atual (por exemplo, "WAITING", "IN_PROGRESS", etc.).
     */
    public String getStatus(){
        if(status == null) throw new IllegalStateException("O status nao foi inicializado corretamente.");
        return status;
    }

    /**
     * Retorna a descriçao do status atual.
     * 
     * @return A descriçao associada ao status atual, como "Aguardando", "Em progresso", etc.
     */
    public String getDescription(){
        return statusMap.get(status).getDescription();
    }

    /**
     * Retorna todos os status cadastrados no sistema.
     * 
     * @return Um mapa contendo todos os status e descrições cadastradas.
     */
    public Map<String, String> getAllStatuses(){
        Map<String, String> status = new HashMap<>();
        for(Map.Entry<String, StatusInfo> entry: statusMap.entrySet()) status.put(entry.getKey(), entry.getValue().getDescription());
        return status;
    }

    // Metodos internos
    /**
     * Adiciona um novo status ao sistema com o nome e a descriçao fornecidas pelo usuário.
     * 
     * @param name Nome do novo status (por exemplo, "ON_HOLD").
     * @param description Descriçao do novo status (por exemplo, "AGUARDANDO").
     */
    public void addStatus(String name, String description){
        if(name == null || statusMap.containsKey(name)) throw new IllegalArgumentException("Esse status já existe.");
        statusMap.put(name, new StatusInfo(description));
    }

    /**
     * Altera o status atual para um novo status do sistema.
     * 
     * @param status O novo status a ser atribuido pelo usuário.
     * @throws IllegalArgumentException Se a transiçao do status nao for permitida.
     */
    public void changeStatus(String status){
        if(status == null) throw new IllegalArgumentException("O status nao pode ser nulo.");
        if(!statusMap.containsKey(status)) throw new IllegalArgumentException("Status inválido.");
        if(this.status.equals(status)) throw new IllegalArgumentException("O status nao pode mudar para ele mesmo.");
        this.status = status;
    }

    /**
     * Reseta o status de volta ao status inicial configurado.
     */
    public void resetStatus() {
        this.status = this.initialStatus;
    }

    /**
     * Verifica se o status atual e "WAITING".
     * 
     * @return true se o status atual for "WAITING", caso contrário, false.
     */
    public boolean isWaiting(){
        return this.status.equals("WAITING");
    }

    /**
     * Verifica se o status atual e "IN_PROGRESS".
     * 
     * @return true se o status atual for "IN_PROGRESS", caso contrário, false.
     */
    public boolean isInProgress(){
        return this.status.equals("IN_PROGRESS");
    }

    /**
     * Verifica se o status atual e "COMPLETED".
     * 
     * @return true se o status atual for "COMPLETED", caso contrário, false.
     */
    public boolean isCompleted(){
        return this.status.equals("COMPLETED");
    }

    /**
     * Verifica se o status atual e "CANCELED".
     * 
     * @return true se o status atual for "CANCELED", caso contrário, false.
     */
    public boolean isCanceled(){
        return this.status.equals("CANCELED");
    }

    /**
     * Verifica se o status atual e variável inserido pelo usuário.
     * 
     * @return true se o status atual for o definido pelo usuário, caso contrário, false.
     */
    public boolean isThatStatus(String status){
        if(status == null || !statusMap.containsKey(status)) throw new IllegalArgumentException("O status e inválido ou nao foi reconhecido");
        return this.status.equals(status);
    }

    /** 
     * Retorna uma string do status atual.
     *
     * @return Uma string representando o status e sua descriçao.
     */
    @Override
    public String toString() {
        return String.format("ServiceStatus{status=%s, description=%s}", status, getDescription());
    }
}
