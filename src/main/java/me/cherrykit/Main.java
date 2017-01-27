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
		
		//Registers /balance command and sends message to player
		if (cmd.getName().equalsIgnoreCase("balance") && sender instanceof Player) {
			Player p = (Player) sender;
			p.sendMessage(ChatColor.GREEN + "Your balance is " + SQLData.getMoney(p.getName()));
		}
		
		//Registers /jobs commands
		if (cmd.getName().equalsIgnoreCase("jobs") && sender instanceof Player) {
			Player p = (Player) sender;
			
			//Registers type of command
			String type;
			try {
				type = args[0];
			} catch (Exception e) {
				type = null;
			}
			
			//Registers name of job
			String job;
			try {
				job = args[1];
			} catch (Exception e) {
				job = null;
			}
			
			//Differs between join, leave and info
			switch (type) {
			
				//If command was /jobs join
				case "join" :
					//If player doesn't have job yet, sets job
					if (SQLData.getJob(p.getName())[0] == "0") {
						//Joins builder
						if (job.equalsIgnoreCase("builder")) {
							SQLData.setJob(p.getName(),"builder",0);
							p.sendMessage(ChatColor.GOLD + "You have joined the job builder.");
						} 
						//Joins breaker
						else if (job.equalsIgnoreCase("breaker")) {
							SQLData.setJob(p.getName(), "breaker", 0);
							p.sendMessage(ChatColor.GOLD + "You have joined the job breaker.");
						} 
						
						else {
							p.sendMessage(ChatColor.RED + "Invalid job name. Valid jobs: breaker, builder");
						}
					} else {
        			p.sendMessage(ChatColor.RED + "You already have a job.");
					}
					
					break;
				
				//If command was /jobs leave
				case "leave" :
					//Leaves job
					SQLData.removeJob(p.getName());
					p.sendMessage(ChatColor.GOLD + "You have left your job.");
					
					break;
				
				//If command was /jobs info
				case "info" :
					//Gives info to builder
					if (job.equalsIgnoreCase("builder")) {
						p.sendMessage(ChatColor.AQUA + "Builder gives you money for every block you place. "
							     + "You get 1% more money every 100 blocks.");
					}
					//Gives info to breaker
					else if (job.equalsIgnoreCase("breaker")) {
						p.sendMessage(ChatColor.AQUA + "Builder gives you money for every block you break. "
							      + "You get 1% more money every 100 blocks.");
					
					} else {
						p.sendMessage(ChatColor.RED + "Invalid job name. Valid jobs: breaker, builder");
					}
					
					break;
			
				//If invalid command:
				default:
					p.sendMessage(ChatColor.RED + "Syntax: /jobs <join/leave/info> <builder/breaker>");
			}
			return true;
		}
		return false;
	}
	
	//Registers blocks being placed
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent e) {
		//Get players job
		String pname = e.getPlayer().getName();
		String[] currentJob = SQLData.getJob(pname);
		
		//If player is builder
		if (currentJob[0].equals("builder")) {
			//Updates blockamount
			SQLData.setJob(pname, currentJob[0], Integer.parseInt(currentJob[1]) + 1);
			//Determines and sets amount of money earned
			int percentage = (int) ((Integer.parseInt(currentJob[1]) + 1) * 0.01 + 100);
			double money = Double.parseDouble(SQLData.getMoney(pname)) + 1 * (percentage/100.0);
			SQLData.setMoney(pname, money);
		}
		
	}
	
	//Registers blocks being broken
	@EventHandler
	public void onBreakBlock(BlockBreakEvent e) {
		//Gets players job
		String pname = e.getPlayer().getName();
		String[] currentJob = SQLData.getJob(pname);
		
		//If player is breaker
		if (currentJob[0].equals("breaker")) {
			//Updates blockamount
			SQLData.setJob(pname, currentJob[0], Integer.parseInt(currentJob[1]) + 1);
			//Determines and sets amount of money earned
			int percentage = (int) ((Integer.parseInt(currentJob[1]) + 1) * 0.01 + 100);
			double money = Double.parseDouble(SQLData.getMoney(pname)) + 1 * (percentage/100.0);
			SQLData.setMoney(pname, money);
		}
		
		
	}
	
}
