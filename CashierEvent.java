import desmoj.core.simulator.*;

public class CashierEvent extends EventOf2Entities<Cashier, Client> {
    public CashierEvent(Model owner, String name, boolean showInTrace){
        super(owner, name, showInTrace);
    }

	@Override
	public void eventRoutine(Cashier cashier, Client client) {
		Market modelMarket = (Market) getModel();
		modelMarket.sendTraceNote("Finalizou Atendimento: " + cashier);
		modelMarket.freeCashier(cashier);
		double totalClientTimeService = modelMarket.presentTime().getTimeAsDouble() - client.getTimeInstant().getTimeAsDouble();
		modelMarket.averageClientTime.update(totalClientTimeService);
        modelMarket.countClients.update();
	}
}
