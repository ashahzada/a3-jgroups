package A3JGroups;

import java.util.HashMap;
import java.util.Map;

import org.jgroups.Address;
import org.jgroups.Event;
import org.jgroups.JChannel;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap;

public abstract class A3JGNode{
	
	protected A3JGMiddleware middleware;
	private int resourceThreshold;
	private String ID;
	private GenericRole generic = new GenericRole();
	
	
	private Map<String,JGSupervisorRole> supervisorRoles = new HashMap<String, JGSupervisorRole>(); 
	private Map<String,JGFollowerRole> followerRoles = new HashMap<String, JGFollowerRole>();
	private Map<String,JChannel> channels = new HashMap<String, JChannel>();
	private long timeout = 1000;
	
	public A3JGNode(String ID, A3JGMiddleware middleware) {
		super();
		this.ID = ID;
		this.middleware = middleware;
	}
	
	//////////////////
	public A3JGNode(String ID){
		super();
		this.ID = ID;
	}
	
	public void setResourceThreshold(int resourceThreshold) {
		this.resourceThreshold = resourceThreshold;
	}

	public int getResourceThreshold() {
		return resourceThreshold;
	}
	
	public JChannel getChannels(String groupName) {
		return channels.get(groupName);
	}

	public void addSupervisorRole(String groupName, JGSupervisorRole role, String nodeID) {
		this.supervisorRoles.put(groupName, role);
		role.setNodeID(nodeID);
		if(channels.get(groupName)==null)
			try {
				channels.put(groupName, new JChannel());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public JGSupervisorRole getSupervisorRole(String groupName) {
		return supervisorRoles.get(groupName);
	}
	
	public JGFollowerRole getFollowerRole(String groupName) {
		return followerRoles.get(groupName);
	}
	
	public void addFollowerRole(String groupName, JGFollowerRole role, String nodeID) {
		this.followerRoles.put(groupName, role);
		role.setNodeID(nodeID);
		if(channels.get(groupName)==null)
			try {
				channels.put(groupName, new JChannel());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public String getID() {
		return ID;
	}

	
	public boolean joinGroup(String groupName) throws Exception {
		JChannel chan = channels.get(groupName);
		ReplicatedHashMap<String, Address> map;
		chan.connect(groupName);
		chan.setReceiver(generic);
		map = new ReplicatedHashMap<String, Address>(chan);
		map.start(timeout);
		Event ev = new Event(resourceThreshold);
		if(map.get("supervisor")==null)
			if(this.getSupervisorRole(groupName)!=null)
				if(map.putIfAbsent("supervisor", chan.getAddress())==null){
					this.getSupervisorRole(groupName).setActive(true);
					chan.setReceiver(this.getSupervisorRole(groupName));
					this.getSupervisorRole(groupName).setChan(chan);
					this.getSupervisorRole(groupName).setMap(map);
					new Thread(this.getSupervisorRole(groupName)).start();
					return true;
				}
				else
					if(this.getFollowerRole(groupName)!=null){
						this.getFollowerRole(groupName).setActive(true);
						chan.setReceiver(this.getFollowerRole(groupName));
						this.getFollowerRole(groupName).setChan(chan);
						this.getFollowerRole(groupName).setMap(map);
						new Thread(this.getFollowerRole(groupName)).start();
						return true;
					}
		else
			if(this.getFollowerRole(groupName)!=null){
				this.getFollowerRole(groupName).setActive(true);
				chan.setReceiver(this.getFollowerRole(groupName));
				this.getSupervisorRole(groupName).setChan(chan);
				this.getFollowerRole(groupName).setMap(map);
				new Thread(this.getFollowerRole(groupName)).start();
				return true;
			}
		close(groupName);
		return false;
	}
	
	private void close(String groupName) {
		JChannel chan = channels.get(groupName);
		chan.disconnect();
		chan.close();
	}
	
	public void terminate(String groupName, boolean role){
		if(role){
			this.getSupervisorRole(groupName).setActive(false);
		}else
			this.getFollowerRole(groupName).setActive(false);
		close(groupName);
	}
	
}
