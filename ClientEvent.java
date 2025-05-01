import desmoj.core.simulator.*;

public class ClientEvent extends ExternalEvent{ 
	public ClientEvent(Model owner, String name, boolean showInTrace) {
		super (owner, name, showInTrace);
	}

	@Override
	public void eventRoutine() {
		Market modelMarket = (Market) getModel();
		Client client = new Client (modelMarket, "Cliente", true);
		ClientEvent event;
		double timestampClientArrival;

		modelMarket.sendTraceNote ("Chegada: " + modelMarket.presentTime());
		modelMarket.serveClient(client);
		
		timestampClientArrival = modelMarket.getTimestampClientArrival();
		
		event = new ClientEvent (modelMarket, "Evento gerador de cliente", true);
		event.schedule (new TimeSpan(timestampClientArrival));
	}
}