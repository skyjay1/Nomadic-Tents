package nomadictents.proxies;

import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerEventHandlers() {
		super.registerEventHandlers();
		MinecraftForge.EVENT_BUS.register(nomadictents.event.ClientTentEventHandler.class);
	}
}
