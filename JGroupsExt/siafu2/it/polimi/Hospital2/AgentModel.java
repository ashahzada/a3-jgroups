/*
 * Copyright NEC Europe Ltd. 2006-2007
 * 
 * This file is part of the context simulator called Siafu.
 * 
 * Siafu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * Siafu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package it.polimi.Hospital2;

import static it.polimi.Hospital2.Constants.Fields.ACTIVITY;
import static it.polimi.Hospital2.Constants.Fields.TYPE;
import static it.polimi.Hospital2.Constants.Fields.NUMBER;
import static it.polimi.Hospital2.Constants.Fields.TIME;
import static it.polimi.Hospital2.Constants.POPULATION;
import static it.polimi.Hospital2.Constants.Fields.NODE;

import it.polimi.Hospital2.Constants.Activity;
import it.polimi.A3Behavior2.BlueFollower;
import it.polimi.A3Behavior2.BlueSupervisor;
import it.polimi.A3Behavior2.GreenFollower;
import it.polimi.A3Behavior2.GreenSupervisor;
import it.polimi.A3Behavior2.MixedNode;
import it.polimi.A3Behavior2.RedFollower;
import it.polimi.A3Behavior2.RedSupervisor;
import it.polimi.A3Behavior2.ScreenFollower;
import it.polimi.A3Behavior2.ScreenSupervisor;
import it.polimi.A3Behavior2.SubBlueFollower;
import it.polimi.A3Behavior2.SubBlueSupervisor;
import it.polimi.A3Behavior2.SubGreenFollower;
import it.polimi.A3Behavior2.SubGreenSupervisor;
import it.polimi.A3Behavior2.SubRedFollower;
import it.polimi.A3Behavior2.SubRedSupervisor;
import it.polimi.A3Behavior2.SubYellowFollower;
import it.polimi.A3Behavior2.SubYellowSupervisor;
import it.polimi.A3Behavior2.YellowFollower;
import it.polimi.A3Behavior2.YellowSupervisor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import A3JGroups.A3JGroup;


import de.nec.nle.siafu.behaviormodels.BaseAgentModel;
import de.nec.nle.siafu.exceptions.InfoUndefinedException;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.Position;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.EasyTime;
import de.nec.nle.siafu.types.IntegerNumber;
import de.nec.nle.siafu.types.Text;

/**
 * This class extends the {@link BaseAgentModel} and implements the behaviour
 * of an agent in the office simulation.
 * 
 * @see it.polimi.hospital
 * @author miquel
 * 
 */
public class AgentModel extends BaseAgentModel {

	private WorldModel worldM;

	/**
	 * Instantiates this agent model.
	 * 
	 * @param world the simulation's world
	 */
	public AgentModel(final World world) {
		super(world);	
		worldM = (WorldModel)world.getWorldModel();
	}

	/**
	 * Create the agents for the Office simulation. There's two types: staff
	 * and students. The main difference lies in the amount of meetings they
	 * have to attend.
	 * 
	 * @return the created agents.
	 */
	@Override
	public ArrayList<Agent> createAgents() {
		ArrayList<Agent> people = new ArrayList<Agent>(POPULATION + 25);
		
		//create block agent
		Agent ab = new Agent("block", worldM.getBlock().getPos(),"Block", world);
		ab.setSpeed(0);
		ab.set(TYPE, new Text("block"));
		ab.set(ACTIVITY, Activity.INACTIVE);
		ab.set(NUMBER, new IntegerNumber(-1));
		ab.set(TIME, null);
		ab.set(NODE, new MixedNode("Block"));
		ab.setDir(0);
		people.add(ab);
		worldM.setBlockAgent(ab);
		
		//Group information
		A3JGroup groupInfo = new A3JGroup(RedSupervisor.class.getCanonicalName(), RedFollower.class.getCanonicalName());
		A3JGroup groupInfo2 = new A3JGroup(BlueSupervisor.class.getCanonicalName(), BlueFollower.class.getCanonicalName());
		A3JGroup groupInfo3 = new A3JGroup(GreenSupervisor.class.getCanonicalName(), GreenFollower.class.getCanonicalName());
		A3JGroup groupInfo4 = new A3JGroup(YellowSupervisor.class.getCanonicalName(), YellowFollower.class.getCanonicalName());
		A3JGroup groupInfo5 = new A3JGroup(SubRedSupervisor.class.getCanonicalName(), SubRedFollower.class.getCanonicalName());
		A3JGroup groupInfo6 = new A3JGroup(SubBlueSupervisor.class.getCanonicalName(), SubBlueFollower.class.getCanonicalName());
		A3JGroup groupInfo7 = new A3JGroup(SubGreenSupervisor.class.getCanonicalName(), SubGreenFollower.class.getCanonicalName());
		A3JGroup groupInfo8 = new A3JGroup(SubYellowSupervisor.class.getCanonicalName(), SubYellowFollower.class.getCanonicalName());
		A3JGroup groupInfo9 = new A3JGroup(ScreenSupervisor.class.getCanonicalName(), ScreenFollower.class.getCanonicalName());
				
		//create screens Supervisor
		MixedNode m = new MixedNode("ScreenLeader");
		m.addGroupInfo("screen", groupInfo9);
		ScreenSupervisor  sc = new ScreenSupervisor(1, worldM);
		m.addSupervisorRole(sc);
		try {
			m.joinGroup("screen");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//create Red Indicator Agents
		for (int i=0; i<6; i++) {
			Agent a = new Agent("redIndicator" + i, worldM.getRedArrow(i).getPos(),"RedArrow", world);
			a.setSpeed(0);
			a.set(TYPE, new Text("redIndicator"));
			a.set(ACTIVITY, Activity.INACTIVE);
			a.set(NUMBER, new IntegerNumber(-1));
			a.set(TIME, null);
			a.setDir(1);
			people.add(a);
			worldM.addRedIndicator(a);
			System.out.println("Created red indicator " + i);
		
			Agent a1 = new Agent("greenIndicator" + i, worldM.getGreenArrow(i).getPos(),"GreenArrow", world);
			a1.setSpeed(0);
			a1.set(TYPE, new Text("greenIndicator"));
			a1.set(ACTIVITY, Activity.INACTIVE);
			a1.set(NUMBER, new IntegerNumber(-1));
			a1.set(TIME, null);
			a1.setDir(1);
			people.add(a1);
			worldM.addGreenIndicator(a1);
			System.out.println("Created green indicator " + i);
		
			Agent a2 = new Agent("blueIndicator" + i, worldM.getBlueArrow(i).getPos(),"BlueArrow", world);
			a2.setSpeed(0);
			a2.set(TYPE, new Text("blueIndicator"));
			a2.set(ACTIVITY, Activity.INACTIVE);
			a2.set(NUMBER, new IntegerNumber(-1));
			a2.set(TIME, null);
			a2.setDir(1);
			people.add(a2);
			worldM.addBlueIndicator(a2);
			System.out.println("Created blue indicator " + i);
		
			Agent a3 = new Agent("yellowIndicator" + i, worldM.getYellowArrow(i).getPos(),"YellowArrow", world);
			a3.setSpeed(0);
			a3.set(TYPE, new Text("yellowIndicator"));
			a3.set(ACTIVITY, Activity.INACTIVE);
			a3.set(NUMBER, new IntegerNumber(-1));
			a3.set(TIME, null);
			a3.setDir(1);
			people.add(a3);
			worldM.addYellowIndicator(a3);
			System.out.println("Created yellow indicator " + i);
			
			MixedNode node = new MixedNode("Screen"+i);
			node.addGroupInfo("red"+i, groupInfo);
			node.addGroupInfo("blue"+i, groupInfo2);
			node.addGroupInfo("green"+i, groupInfo3);
			node.addGroupInfo("yellow"+i, groupInfo4);
			node.addGroupInfo("screen", groupInfo9);
			RedSupervisor redIndicator = new RedSupervisor(0);
			GreenSupervisor greenIndicator = new GreenSupervisor(0);
			BlueSupervisor blueIndicator = new BlueSupervisor(0);
			YellowSupervisor yellowIndicator = new YellowSupervisor(0);
			ScreenFollower scf = new ScreenFollower(1, i);
			redIndicator.setAgent(a);
			greenIndicator.setAgent(a1);
			blueIndicator.setAgent(a2);
			yellowIndicator.setAgent(a3);
			node.addSupervisorRole(redIndicator);
			node.addSupervisorRole(greenIndicator);
			node.addSupervisorRole(blueIndicator);
			node.addSupervisorRole(yellowIndicator);
			node.addFollowerRole(scf);
			a.set(NODE, node);
			a1.set(NODE, node);
			a2.set(NODE, node);
			a3.set(NODE, node);
			
			try {
				node.joinGroup("red"+i);
				node.joinGroup("green"+i);
				node.joinGroup("blue"+i);
				node.joinGroup("yellow"+i);
				node.joinGroup("screen");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(i==0)
				worldM.setScreen(node);
		}
		
		//Create people
		
		for (int i=0; i<POPULATION; i++) {
			
			Position pos = null;
			String type = null;
			
			//generate position and type
			int posNum = (int) (Math.random()*2);
			
			if (posNum==0) {
				pos = worldM.getLeftEntrance().getPos();
			}
			else {
				pos = worldM.getRightEntrance().getPos();
			}
			
			int typeNum = (int) (Math.random()*3);
			
			if (typeNum==0) {
				type = "Magenta";
			}
			else if (typeNum==1) {
				type = "Blue";
			}
			else {
				type = "Yellow";
			}
			
			Agent a5 =new Agent(type + i, pos, "Human" + type, world);
			a5.setVisible(false);
			a5.set(TYPE, new Text(type));
			a5.set(ACTIVITY, Activity.OUT);
			a5.set(NUMBER, new IntegerNumber(i));
			a5.set(TIME, null);
			a5.setSpeed(0);
			
			//create personNode
			MixedNode mixed = new MixedNode("p_"+i);
			
			for(int ind=0; ind<6; ind++){
				mixed.addGroupInfo("red"+ind, groupInfo);
				mixed.addGroupInfo("blue"+ind, groupInfo2);
				mixed.addGroupInfo("green"+ind, groupInfo3);
				mixed.addGroupInfo("yellow"+ind, groupInfo4);
				mixed.addGroupInfo("subred"+ind, groupInfo5);
				mixed.addGroupInfo("subblue"+ind, groupInfo6);
				mixed.addGroupInfo("subgreen"+ind, groupInfo7);
				mixed.addGroupInfo("subyellow"+ind, groupInfo8);
			}
			
			RedFollower red = new RedFollower(0);
			GreenFollower green = new GreenFollower(0);
			BlueFollower blue = new BlueFollower(0);
			YellowFollower yellow = new YellowFollower(0);
			
			SubRedFollower sred = new SubRedFollower(0);
			SubGreenFollower sgreen = new SubGreenFollower(0);
			SubBlueFollower sblue = new SubBlueFollower(0);
			SubYellowFollower syellow = new SubYellowFollower(0);
			
			SubRedSupervisor sured = new SubRedSupervisor(0);
			SubGreenSupervisor sugreen = new SubGreenSupervisor(0);
			SubBlueSupervisor sublue = new SubBlueSupervisor(0);
			SubYellowSupervisor suyellow = new SubYellowSupervisor(0);
			
			red.setAgent(a5);
			red.setWorld(world);
			green.setAgent(a5);
			green.setWorld(world);
			blue.setAgent(a5);
			blue.setWorld(world);
			yellow.setAgent(a5);
			yellow.setWorld(world);
			
			sred.setAgent(a5);
			sred.setWorld(world);
			sgreen.setAgent(a5);
			sgreen.setWorld(world);
			sblue.setAgent(a5);
			sblue.setWorld(world);
			syellow.setAgent(a5);
			syellow.setWorld(world);
			
			sured.setAgent(a5);
			sugreen.setAgent(a5);
			sublue.setAgent(a5);
			suyellow.setAgent(a5);
			
			mixed.addFollowerRole(red);
			mixed.addFollowerRole(green);
			mixed.addFollowerRole(blue);
			mixed.addFollowerRole(yellow);
			
			mixed.addFollowerRole(sred);
			mixed.addFollowerRole(sgreen);
			mixed.addFollowerRole(sblue);
			mixed.addFollowerRole(syellow);
			
			mixed.addSupervisorRole(sured);
			mixed.addSupervisorRole(sugreen);
			mixed.addSupervisorRole(sublue);
			mixed.addSupervisorRole(suyellow);
			a5.set(NODE, mixed);
			
			people.add(a5);
		}
		
		return people;
	}

	


	
	/**
	 * Handle the agents by checking if they need to respond to an event
	 * 
	 * @param agents the people in the simulation
	 */
	@Override
	public void doIteration(final Collection<Agent> agents) {
		Iterator<Agent> peopleIt = agents.iterator();
		while (peopleIt.hasNext()) {
			handlePerson(peopleIt.next());
		}
	}

	/**
	 * Handle the people in the simulation.
	 * 
	 * @param a the agent to handle
	 * @param now the current time
	 */
	private void handlePerson(final Agent a) {
		
		Calendar time = world.getTime();
		EasyTime now =	new EasyTime(time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
		
		if (!a.isOnAuto()) {
			return; // This guy's being managed by the user interface
		}
		try {
			switch ((Activity) a.get(ACTIVITY)) {

			case WALKING:
					if (a.isAtDestination()) {
						if(a.getPos().equals(worldM.getLeftEntrance().getPos()) || a.getPos().equals(worldM.getRightEntrance().getPos())) {
							a.set(ACTIVITY, Activity.OUT);
							a.setVisible(false);
							a.setSpeed(0);
						}else if(!sensiblePos(a.getPos())){
							;
						}else{
							a.set(ACTIVITY, Activity.WAITING);
							a.set(TIME, now.shift(0, 20));
						}
					}
				break;
			
			case WAITING:
				if (now.isAfter((EasyTime) a.get(TIME))) {
					int colour = (int) (Math.random() * 4);
					if (colour == 0 && !((Text) a.get(TYPE)).getText().equalsIgnoreCase("Magenta")) {
						if (((Text) a.get(TYPE)).getText().equalsIgnoreCase("Blue"))
							a.setDestination(worldM.getScreen(2));
						else
							a.setDestination(worldM.getScreen(1));

						a.set(TYPE, new Text("Magenta"));
						a.setImage("HumanMagenta");

					} else if (colour == 1 && !((Text) a.get(TYPE)).getText().equalsIgnoreCase("Blue")) {
						if (((Text) a.get(TYPE)).getText().equalsIgnoreCase("Magenta"))
							a.setDestination(worldM.getScreen(0));
						else
							a.setDestination(worldM.getScreen(3));

						a.set(TYPE, new Text("Blue"));
						a.setImage("HumanBlue");

					} else if (colour == 2 && !((Text) a.get(TYPE)).getText().equalsIgnoreCase("Yellow")) {
						if (((Text) a.get(TYPE)).getText().equalsIgnoreCase("Magenta"))
							a.setDestination(worldM.getScreen(0));
						else
							a.setDestination(worldM.getScreen(5));

						a.set(TYPE, new Text("Yellow"));
						a.setImage("HumanYellow");

					} else {
						if (((Text) a.get(TYPE)).getText().equalsIgnoreCase("Magenta"))
							a.setDestination(worldM.getScreen(0));
						else if (((Text) a.get(TYPE)).getText().equalsIgnoreCase("Blue"))
							a.setDestination(worldM.getScreen(2));
						else
							a.setDestination(worldM.getScreen(3));

						a.set(TYPE, new Text("Green"));
						a.setImage("HumanGreen");
					}
					a.set(ACTIVITY, Activity.WALKING);
				}else{
					if(((Text) a.get(TYPE)).getText().equalsIgnoreCase("Magenta"))
						a.wanderAround(worldM.getLab((int)(Math.random()*5)), 100, 10);
					else if(((Text) a.get(TYPE)).getText().equalsIgnoreCase("Blue"))
						a.wanderAround(worldM.getRadiology((int)(Math.random()*5)), 100, 5);
					else 
						a.wanderAround(worldM.getPhysiotherapy((int)(Math.random()*5)), 100, 1);
				}
				break;
				
			case INACTIVE:
				break;
				
			case OUT:
				int num = ((IntegerNumber) a.get(NUMBER)).getNumber();
				int div = 2;
				int rand = 100;
				boolean start = false;
				if(num<=5 && (now.getHour()<2)||(now.getHour()>=11 && now.getHour()<13)){
					int visible = (int) (Math.random()*rand);
					if(visible==0 && now.getMinute()%div==0){
						a.setSpeed(3 + (int) (Math.random()*3));
						start=true;
					}
				}else if(num>5 && num<=10 && ((now.getHour()>=2 && now.getHour()<4)||(now.getHour()>=13 && now.getHour()<14))){
					int visible = (int) (Math.random()*rand);
					if(visible==0 && now.getMinute()%div==0){
						a.setSpeed(3 + (int) (Math.random()*3));
						start=true;
					}
				}else if(num>10 && num<=15 && ((now.getHour()>=4 && now.getHour()<6)||(now.getHour()>=14 && now.getHour()<16))){
					int visible = (int) (Math.random()*rand);
					if(visible==0 && now.getMinute()%div==0){
						a.setSpeed(3 + (int) (Math.random()*3));
						start=true;
					}
				}else if(num>15 && num<=25 && ((now.getHour()>=6 && now.getHour()<8)||(now.getHour()>=16 && now.getHour()<18))){
					int visible = (int) (Math.random()*rand);
					if(visible==0 && now.getMinute()%div==0){
						a.setSpeed(3 + (int) (Math.random()*3));
						start=true;
					}
				}else if(num>25 && num<=35 && ((now.getHour()>=8 && now.getHour()<9)||(now.getHour()>=18 && now.getHour()<20))){
					int visible = (int) (Math.random()*rand);
					if(visible==0 && now.getMinute()%div==0){
						a.setSpeed(3 + (int) (Math.random()*3));
						start=true;
					}
				}else if(num>35 && num<=42 && ((now.getHour()>=9 && now.getHour()<10)||(now.getHour()>=20 && now.getHour()<22))){
					int visible = (int) (Math.random()*rand);
					if(visible==0 && now.getMinute()%div==0){
						a.setSpeed(3 + (int) (Math.random()*3));
						start=true;
					}
				}else if(num>42 && num<=50 && ((now.getHour()>=10 && now.getHour()<11)||now.getHour()>=22)){
					int visible = (int) (Math.random()*rand);
					if(visible==0 && now.getMinute()%div==0){
						a.setSpeed(3 + (int) (Math.random()*3));
						start=true;
					}
				}
				
				if (start) {
					int typeNum = (int) (Math.random()*3);
					String type;
					if (typeNum==0) {
						type = "Magenta";
					}
					else if (typeNum==1) {
						type = "Blue";
					}
					else {
						type = "Yellow";
					}
					a.set(TYPE, new Text(type));
					a.setImage("Human"+type);
					
					if (a.getPos() == worldM.getLeftEntrance().getPos()) {
						goToScreen(a, worldM.getScreen(2));
					} else {
						goToScreen(a, worldM.getScreen(4));
					}
				}
				
				break;
				

			default:
				throw new RuntimeException("Unknown Activity");
			}

		} catch (InfoUndefinedException e) {
			throw new RuntimeException("Unknown info requested for " + a,
					e);
		}
	}

	/**
	 * Send the agent to the screen
	 * 
	 * @param a the agent that just has to go
	 */
	private void goToScreen(final Agent a, Place screen) {
		a.setDestination(screen);
		a.set(ACTIVITY, Activity.WALKING);
		a.setVisible(true);
	}
	
	private boolean sensiblePos(Position pos){
		for(Place p: worldM.getLab()){
			if(pos.equals(p.getPos()))
				return true;
		}
		for(Place p: worldM.getRadiology()){
			if(pos.equals(p.getPos()))
				return true;
		}
		for(Place p: worldM.getPhysiotherapy()){
			if(pos.equals(p.getPos()))
				return true;
		}
		return false;
	}

}
