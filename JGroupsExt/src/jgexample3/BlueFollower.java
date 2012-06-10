package jgexample3;

import A3JGroups.A3JGMessage;
import A3JGroups.JGFollowerRole;


public class BlueFollower extends JGFollowerRole {

	public BlueFollower(int resourceCost, String groupName) {
		super(resourceCost, groupName);
	}

	private int people;
	
	@Override
	public void run() {
		
		while (this.active) {
			
			people = (int) (Math.random()*35);
			System.out.println("["+this.getNode().getID()+"] number of people: "+people);
			
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void messageFromSupervisor(A3JGMessage msg) {
		if(msg.getContent().equals("people")){
			A3JGMessage mex = new A3JGMessage();
			mex.setContent(people);
			sendMessageToSupervisor(mex);
		}
	}
}