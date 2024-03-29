package changeRoleExample;

import java.util.ArrayList;

import org.jgroups.View;

import A3JGroups.A3JGMessage;
import A3JGroups.A3JGSupervisorRole;

public class RedSupervisor extends A3JGSupervisorRole {

	
	public RedSupervisor(int resourceCost) {
		super(resourceCost);
	}

	private int fitness = 4;
	private ArrayList<Integer> temp = new ArrayList<Integer>();
	private View vista;
	
	

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	@Override
	public void run() {
		System.out.println("I'm red type");
		while (this.active) {
			vista = this.node.getChannels("mix").getView();
			try {
				Thread.sleep(2000);

			} catch (Exception e) {
				e.printStackTrace();
			}
			A3JGMessage msg = new A3JGMessage("temperature");
			sendMessageToFollower(msg, null);
			System.out.println("["+this.getNode().getID()+"] Sending message to followers...  " + (vista.getMembers().size()-1));
			
			
		}
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		if (!msg.getValueID().equals("people")) {
			temp.add((Integer) msg.getContent());
			if (temp.size() == (vista.getMembers().size() - 1)) {
				int avarage = 0;
				for (int i = 0; i < (vista.getMembers().size() - 1); i++) {
					avarage += temp.get(i);
				}
				avarage = (avarage / (vista.getMembers().size() - 1));
				temp = new ArrayList<Integer>();
				System.out.println(this.getNode().getID()+ " The average temperature is " + avarage);
			}
		}
	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		System.out.println(this.getNode().getID()+"  has recived update from someone");
	}

	@Override
	public int fitnessFunc() {
		return fitness;
	}
	
	public void writeOnMap(){
		map.put("test di prova", "vediamo se c'�");
	}

}
