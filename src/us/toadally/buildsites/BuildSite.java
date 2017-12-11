package us.toadally.buildsites;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BuildSite {
	
	
	String owner = null;
	List<String> members = new ArrayList<String>();
	String name;
	Location loc;
	BuildSites pl;
	boolean finished;
	
	
	public BuildSite(String name, Location loc, BuildSites pl){
		
		this.name = name;
		this.loc = loc;
		this.pl = pl;
		
		pl.getConfig().set("Sites."+name+".name", name);
		pl.getConfig().set("Sites."+name+".location", BuildSites.getStringLocation(loc));
		pl.getConfig().set("Sites."+name+".finished", false);
		pl.saveConfig();
		pl.sites.add(this);
	}
	
	public BuildSite(String name, Location loc, List<String> members, String owner, boolean finished, BuildSites pl){
		
		this.name = name;
		this.pl = pl;
		this.loc = loc;
		this.members = members;
		this.owner = owner;
		this.finished = finished;
		this.pl = pl;
		pl.sites.add(this);
	}
	
	public OfflinePlayer getOwner(){
		if(owner == null){
			return null;
		}
		return Bukkit.getOfflinePlayer(UUID.fromString(owner));
	}
	
	public List<OfflinePlayer> getMembers(){
		List<OfflinePlayer> result = new ArrayList<OfflinePlayer>();
		for(String s : members){
			result.add(Bukkit.getOfflinePlayer(UUID.fromString(s)));
		}
		return result;
		
		
	}
	public String getName(){
		return name;
	}
	
	public void addMember(Player p){
		members.add(p.getUniqueId().toString());
		
		pl.getConfig().set("Sites."+name+".members", members);
		pl.saveConfig();
	}
	public boolean removeMember(Player p){
		
		boolean s = members.remove(p.getUniqueId().toString());
		pl.getConfig().set("Sites."+name+".members", members);
		pl.saveConfig();
		return s;
	}
	
	public void unclaim(){
		members.clear();
		owner = null;
	}
	
	public void claim(Player p){
		
		this.owner = p.getUniqueId().toString();
		pl.getConfig().set("Sites."+name+".owner", p.getUniqueId());
		pl.saveConfig();
		
	}
	
	public void delete(){
		pl.getConfig().set("Sites."+name, null);
		pl.saveConfig();
		pl.sites.remove(this);
	}
	
	public boolean isFinished(){
		return finished;
	}
	
	public void finish(){
		this.finished = true;
		pl.getConfig().set("Sites."+name+".finished", true);
		pl.saveConfig();
		pl.finishedSites.add(this);
	}
	
	public void unfinish(){
		this.finished = false;
		pl.getConfig().set("Sites."+name+".finished", false);
		pl.saveConfig();
		pl.finishedSites.remove(this);
		
	}

}
