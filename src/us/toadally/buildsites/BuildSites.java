package us.toadally.buildsites;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class BuildSites extends JavaPlugin {
	
	public List<BuildSite> sites = new ArrayList<BuildSite>();
	public List<BuildSite> finishedSites = new ArrayList<BuildSite>();
	
	
	public void onEnable(){
		File file = new File(getDataFolder(), "config.yml");
		if(!file.exists()){
		    try{
		        saveDefaultConfig();
		    }catch(Exception e){
		        e.printStackTrace();
		    }
		}
		
	
		if(getConfig().getConfigurationSection("Sites") != null){
		for(String s : getConfig().getConfigurationSection("Sites").getKeys(false)){
			
			String name = getConfig().getString("Sites."+s+".name");
			boolean finished = getConfig().getBoolean("Sites."+s+".finished");
			String owner = getConfig().getString("Sites."+s+".owner");
			Location loc = getLocationString(getConfig().getString("Sites."+s+".location"));
			List<String> members = getConfig().getStringList("Sites."+s+".members");
			
			
			if(members == null){
				members = new ArrayList<String>();
			}
			
			BuildSite b = new BuildSite(name, loc, members, owner, finished, this);
			
			
			
		}
		}
		
		
		
		
	}
	
	public void onDisable(){
		saveConfig();
		
	}
	
	public BuildSite getSite(String name){
		
		
		for(BuildSite b : sites){
			
			if(eic(b.getName(), name)){
				return b;
			}
			
		}
		return null;
	}
	
	public BuildSite getOwnedSite(Player p){
		for(BuildSite b : sites){
			if(b.getOwner() != null){
		
				if(eic(b.getOwner().getUniqueId().toString(), p.getUniqueId().toString())){
					return b;
				}
			}
			
		}
		return null;
	}
	/**
     * Converts a location to a simple string representation
     * If location is null, returns empty string
     * @param l
     * @return
     */
    static public String getStringLocation(final Location l) {
    if (l == null) {
        return "";
    }
    return l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
    }
	static public Location getLocationString(final String s) {
	    if (s == null || s.trim() == "") {
	        return null;
	    }
	    final String[] parts = s.split(":");
	    if (parts.length == 4) {
	        final World w = Bukkit.getServer().getWorld(parts[0]);
	        final int x = Integer.parseInt(parts[1]);
	        final int y = Integer.parseInt(parts[2]);
	        final int z = Integer.parseInt(parts[3]);
	        return new Location(w, x, y, z);
	    }
	    return null;
	    }
	
	
	public boolean onCommand(CommandSender s, Command c, String l, String[] args){
		
		if(!(s instanceof Player)){
			return false;
		}
		Player p = (Player) s;
		
		if(eic(l,"site")){
			
			//BEGIN Commands
			
			if(args.length > 0){
				
				//CREATE/REMOVE BUILD SITE
				if(eic(args[0], "create") || eic(args[0], "remove")){
					
					String cmd = args[0].toLowerCase();
				
					
					
					if(p.hasPermission("sites.admin")){
						
						if(args.length > 1){
							
							String complete = args[1].toLowerCase();
							
							if(eic(cmd,"create")){
								//CREATE SITE
								createSite(p, complete);
							} else if(eic(cmd,"remove")){
								//REMOVE SITE
								removeSite(p, complete);
								
							}
							return true;		
							
						} else {
							needToSpecifyMsg(p, "site name");
							return true;
						}
						
						
					} else {
						notFoundMsg(p);
					}
					
				}
				//END CREATE/REMOVE BUILD SITE
				
				//CLAIMING, JOINING, CHECKING, AND TPING
				if(eic(args[0], "claim") || eic(args[0], "join") || eic(args[0], "check") || eic(args[0], "tp") || eic(args[0], "leave")){
					
					String cmd = args[0].toLowerCase();
					
					if(p.hasPermission("sites.use")){
						
						if(args.length > 1){
							
							String complete = args[1].toLowerCase();
							
							if(eic(cmd,"claim")){
								//CLAIM SITE
								claimSite(p, complete);
							} else if (eic(cmd,"join")){
								//JOIN SITE
								joinSite(p, complete);
							} else if (eic(cmd,"leave")){
								//LEAVE SITE
								leaveSite(p, complete);
							} else if (eic(cmd,"check")){
								checkSite(p, complete);
							} else if (eic(cmd,"tp")){
								tpToSite(p, complete);
							}
							return true;		
							
						} else {
							needToSpecifyMsg(p, "site name");
							return true;
						}
						
						
					}
					
				}
				//END CLAIMING, JOINING, CHECKING, AND TPING
				
				//SITE LIST, FINISH, UNFINISH, UNCLAIM
				
				if(eic(args[0], "list") || eic(args[0], "finish") || eic(args[0], "unfinish") || eic(args[0], "unclaim")){
					
					String cmd = args[0].toLowerCase();
					
					if(p.hasPermission("sites.use")){
						
						if(eic(cmd,"list")){
							//CLAIM SITE
							listSites(p);
						} else if(eic(cmd,"finish")) {
							//UNCLAIM SITE
							finishSite(p);
						} else if (eic(cmd,"unfinish")){
							//JOIN SITE
							unfinishSite(p);
						} else if(eic(cmd,"unclaim")) {
							//UNCLAIM SITE
							unclaimSite(p);
						}
						return true;
					}
					
				}
			}
			
			
			
			
			
			
			
			p.sendMessage(ChatColor.AQUA+"BuildSites Command Reference");
			p.sendMessage("§e/site ->");
			p.sendMessage("§fclaim §7[id] §8- §eClaims a build site");
			p.sendMessage("§funclaim §8- §eUnclaims currently claimed build site");
			p.sendMessage("§fcheck §7[id] §8- §eChecks status of a build site");
			p.sendMessage("§fjoin §7[id] §8- §eJoins a build site");
			p.sendMessage("§fleave §7[id] §8- §eLeaves a build site");
			p.sendMessage("§ffinish §7 §8- §eFinishes claimed build site");
			p.sendMessage("§funfinish §7 §8- §eUnfinishes claimed build site");
			return true;
			
		}
		return false;
		
		
		
	}
	private void listSites(Player p) {
		p.sendMessage(ChatColor.AQUA+"All Sites:");
		for(BuildSite b : sites){
			
			if(b.getOwner() == null){
				p.sendMessage("§7§o"+b.getName());
				continue;
			} else {
				
				String memberCt = "§7(§f"+b.getMembers().size()+"§7)";
				
				if(b.isFinished()){
					
					p.sendMessage("§a"+b.getName() + " §7" +b.getOwner().getName() + " " + memberCt );
					
				} else {
					p.sendMessage("§c"+b.getName() + " §7" +b.getOwner().getName() + " " + memberCt );
				}
				
				
			}
			
		}
		
	}

	private void finishSite(Player p) {
BuildSite b = getOwnedSite(p);
		
		if(b == null){
			p.sendMessage(ChatColor.RED + "You have not claimed a site.");
			return;
		}
		
		if(b.isFinished() == true){
			p.sendMessage(ChatColor.RED + "Your site is already finished.");
			return;
		}
		
		b.finish();
		
		Bukkit.broadcastMessage(ChatColor.YELLOW+b.getName() + ChatColor.GREEN + " has been finished!");
		
	}

	private void unfinishSite(Player p) {
		BuildSite b = getOwnedSite(p);
		
		if(b == null){
			p.sendMessage(ChatColor.RED + "You have not claimed a site.");
			return;
		}
		
		if(b.isFinished() == false){
			p.sendMessage(ChatColor.RED + "Your site is not finished yet.");
			return;
		}
		Bukkit.broadcastMessage(ChatColor.YELLOW+b.getName() + ChatColor.RED + " has been unfinished.");
		b.unfinish();
		
	}

	private void tpToSite(Player p, String complete){
		
		BuildSite b = getSite(complete);
		
		if(b == null){
			p.sendMessage(ChatColor.RED + "Site "+complete+" not found.");
			return;
		}
		
		p.teleport(b.loc);
		p.sendMessage(ChatColor.LIGHT_PURPLE + "Teleported to "+b.getName());
	}
	private void checkSite(Player p, String complete){
		BuildSite b = getSite(complete);
		if(b == null){
			p.sendMessage(ChatColor.RED + "Site "+complete+" not found.");
			return;
		}
		
		if(b.getOwner() == null){
			p.sendMessage(ChatColor.GREEN + "Site "+b.getName()+" is unclaimed!");
			return;
		} else {
			
			
			p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+b.getName());
			p.sendMessage(ChatColor.WHITE + "Claimed by "+ChatColor.YELLOW+b.getOwner().getName());
			String members = buildMemberString(b.getMembers());
			if(members.length() > 1){
				p.sendMessage(ChatColor.WHITE + "Members: "+ChatColor.YELLOW+members);
			} else {
				p.sendMessage(ChatColor.WHITE + "Members: "+ChatColor.YELLOW+"None");
			}
			p.sendMessage(ChatColor.WHITE + "Finished: "+ChatColor.YELLOW+b.isFinished());
			
		}
	}
	
	private void leaveSite(Player p, String complete){
		BuildSite b = getSite(complete);
		if(b == null){
			p.sendMessage(ChatColor.RED + "Site "+complete+" not found.");
			return;
		}
		
		if(!(b.getMembers().contains(p))){
			p.sendMessage(ChatColor.RED + "You are not apart of "+complete+".");
			return;
		}
		
		b.removeMember(p);
		p.sendMessage(ChatColor.GREEN + "Left site "+b.getName()+"!");
		Bukkit.broadcastMessage(ChatColor.RED + p.getName() + " has left "+ChatColor.YELLOW+b.getName());
	}
	
	
	private void joinSite(Player p, String complete) {
		BuildSite b = getSite(complete);
		if(b == null){
			p.sendMessage(ChatColor.RED + "Site "+complete+" not found.");
			return;
		}
		
		if(b.getOwner() == null){
			p.sendMessage(ChatColor.RED + "Site "+complete+" is not claimed.");
			return;
		}
		
		if(b.getOwner().getUniqueId().equals(p.getUniqueId())){
			p.sendMessage(ChatColor.RED + "You cannot join your own claimed site.");
			return;
		}
		
		if(b.getMembers().contains(p)){
			p.sendMessage(ChatColor.RED + "You are already apart of "+complete+".");
			return;
		}
		
		b.addMember(p);
		p.sendMessage(ChatColor.GREEN + "Joined site "+b.getName()+"!");
		Bukkit.broadcastMessage(ChatColor.GREEN + p.getName() + " has joined "+ChatColor.YELLOW+b.getName());
		
	}

	private void unclaimSite(Player p) {
		BuildSite b = getOwnedSite(p);
		if(b == null){
			p.sendMessage(ChatColor.RED + "You have not claimed a site.");
			return;
		}
		
		
		b.unclaim();
		p.sendMessage(ChatColor.GREEN + "Unclaimed site "+b.getName()+"!");
		Bukkit.broadcastMessage(ChatColor.RED + p.getName() + " has unclaimed "+ChatColor.YELLOW+b.getName());
	}

	private void claimSite(Player p, String complete) {
		
		
		BuildSite b = getOwnedSite(p);
		if(b != null){
			p.sendMessage(ChatColor.RED + "You already claimed site "+b.getName()+".");
			return;
		}
		
		BuildSite target = getSite(complete);
		
		if(target == null){
			p.sendMessage(ChatColor.RED + "Site "+complete+" not found.");
			return;
		}
		
		if(target.getOwner() != null){
			p.sendMessage(ChatColor.RED + "That site is already owned by "+target.getOwner().getName()+". Use /site join "+target.getName()+ " to join it.");
			return;
		}
		
		
		target.claim(p);
		p.sendMessage(ChatColor.GREEN + "Claimed site "+target.getName()+"!");
		Bukkit.broadcastMessage(ChatColor.GREEN + p.getName() + " has claimed "+ChatColor.YELLOW+target.getName());
		
	}

	private void removeSite(Player p, String complete) {
		
		BuildSite b = getSite(complete);
		
		if(b != null){
			b.delete();
			removedMsg(p, complete);
		} else {
			p.sendMessage(ChatColor.RED + "Site "+complete+" not found.");
		}
		
	}

	private void createSite(Player p, String complete) {
		
		BuildSite b = getSite(complete);
		
		if(b != null){
			p.sendMessage(ChatColor.RED + "Site "+complete+" already exists.");
			return;
		}
		
		BuildSite s = new BuildSite(complete, p.getLocation(), this);
		createdMsg(p, s.getName());
		
		
	}
	
	

	public String buildArgs(String[] args){
		String complete = "";
		for(int x = 1; x < args.length; x ++){
			
			complete = complete + args[x] + " ";
			
		}
		return complete.trim();
		
	}
	public String buildMemberString(List<OfflinePlayer> args){
		String complete = "";
		for(int x = 1; x < args.size(); x ++){
			
			complete = complete + args.get(x).getName() + " ";
			
		}
		return complete.trim().replaceAll(" ", ", ");
		
	}
	
	public boolean eic(String s, String q){
		
		return s.equalsIgnoreCase(q);
		
	}
	
	public void notFoundMsg(Player p){
		p.sendMessage(ChatColor.RED + "Command not found.");
	}
	public void needToSpecifyMsg(Player p, String spec){
		p.sendMessage(ChatColor.RED + "You need to specify a "+spec+".");
	}
	public void createdMsg(Player p, String site){
		p.sendMessage(ChatColor.GREEN + "Build site §b"+site+ " §acreated!");
	}
	public void removedMsg(Player p, String site){
		p.sendMessage(ChatColor.GREEN + "Build site §b"+site+ " §aremoved!");
	}
	

}
