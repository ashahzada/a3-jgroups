package it.polimi.A3Behavior2;

import de.nec.nle.siafu.exceptions.NothingNearException;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.World;
import A3JGroups.A3JGMessage;
import A3JGroups.A3JGFollowerRole;

public class YellowFollower extends A3JGFollowerRole{
	
	private Agent agent;
	private World world;
	private Place screen;
	
	public YellowFollower(int resourceCost) {
		super(resourceCost);
		this.setElectionTime(500);
		this.setMaxAttempt(1);
	}
	
	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	public void setScreen(Place screen) {
		this.screen = screen;
	}

	@Override
	public void run() {
		A3JGMessage mex = new A3JGMessage("info");
		mex.setContent(this.getChan().getAddress());
		sendMessageToSupervisor(mex);
		while (this.active) {
			try {
				if(!world.findAllAgentsNear(screen.getPos(), 70, true).contains(agent)){
					agent.setImage("HumanYellow");
					this.getNode().terminate("sub" + this.getChan().getClusterName());
					this.getNode().terminate(this.getChan().getClusterName());
				}
			} catch (NothingNearException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		agent.setDestination((Place) msg.getContent());
	}

}
