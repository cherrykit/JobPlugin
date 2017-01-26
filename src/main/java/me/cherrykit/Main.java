package me.cherrykit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		SQLData.getConnection();
	}
	
	@Override
	public void onDisable() {
		SQLData.closeConnection();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("balance") && sender instanceof Player) {
			Player p = (Player) sender;
			p.sendMessage(ChatColor.GREEN + "Your balance is " + SQLData.getMoney(p.getName()));
		}
		
		if (cmd.getName().equalsIgnoreCase("jobs") && sender instanceof Player) {
            Player p = (Player) sender;
            
            String type;
            try {
            	type = args[0];
            } catch (Exception e) {
            	type = null;
            }
            
            //Differs between breaker and builder
            String job;
        	try {
            	job = args[1];
            } catch (Exception e) {
            	job = null;
            }
            
            //Differs between join, leave and info
            switch (type) {
            case "join" :
            	if (SQLData.getJob(p.getName())[0] == "0") {
            		if (job.equalsIgnoreCase("builder")) {
            			SQLData.setJob(p.getName(),"builder",0);
                		p.sendMessage(ChatColor.GOLD + "You have joined the job builder.");
            		} 
            		else if (job.equalsIgnoreCase("breaker")) {
                		SQLData.setJob(p.getName(), "breaker", 0);
                		p.sendMessage(ChatColor.GOLD + "You have joined the job breaker.");
                	} 
            		else {
                		p.sendMessage(ChatColor.RED + "Invalid job name. Valid jobs: breaker, builder");
                	}
            	}
            	else {
        			p.sendMessage(ChatColor.RED + "You already have a job.");
        		}
            	
            	break;
            	
            case "leave" :
            	SQLData.removeJob(p.getName());
            	p.sendMessage(ChatColor.GOLD + "You have left your job.");
            	break;
            	
            case "info" :
            	if (job.equalsIgnoreCase("builder")) {
            		p.sendMessage(ChatColor.AQUA + "Builder gives you money for every block you place. You get 1% more "
            				+ "money every 100 blocks.");
            	}
            	else if (job.equalsIgnoreCase("breaker")) {
            		p.sendMessage(ChatColor.AQUA + "Builder gives you money for every block you break. You get 1% more "
            				+ "money every 100 blocks.");
            	} else {
            		p.sendMessage(ChatColor.RED + "Invalid job name. Valid jobs: breaker, builder");
            	}
            	break;
            	
            default:
            	p.sendMessage(ChatColor.RED + "Syntax: /jobs <join/leave/info> <builder/breaker>");
            }
            
            return true;
        }
		
		return false;
	}
	
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent e) {
		String pname = e.getPlayer().getName();
		String[] currentJob = SQLData.getJob(pname);
		
		if (currentJob[0].equals("builder")) {
			SQLData.setJob(pname, currentJob[0], Integer.parseInt(currentJob[1]) + 1);
			int percentage = (int) ((Integer.parseInt(currentJob[1]) + 1) * 0.01 + 100);
			double money = Double.parseDouble(SQLData.getMoney(pname)) + 1 * (percentage/100.0);
			SQLData.setMoney(pname, money);
		}
		
	}
	
	@EventHandler
	public void onBreakBlock(BlockBreakEvent e) {
		String pname = e.getPlayer().getName();
		String[] currentJob = SQLData.getJob(pname);
		
		if (currentJob[0].equals("breaker")) {
			SQLData.setJob(pname, currentJob[0], Integer.parseInt(currentJob[1]) + 1);
			int percentage = (int) ((Integer.parseInt(currentJob[1]) + 1) * 0.01 + 100);
			double money = Double.parseDouble(SQLData.getMoney(pname)) + 1 * (percentage/100.0);
			SQLData.setMoney(pname, money);
		}
		
		
	}
	
}
