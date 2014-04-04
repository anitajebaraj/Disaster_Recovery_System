package com.vmware.vim25.mo.samples;




 import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

 



import java.util.Random;

import org.tempuri.Service;
import org.tempuri.ServiceSoap;

 
















import com.vmware.vim25.AlarmSetting;
import com.vmware.vim25.AlarmSpec;
import com.vmware.vim25.AlarmTriggeringAction;
import com.vmware.vim25.DuplicateName;
import com.vmware.vim25.FileFault;
import com.vmware.vim25.HostVMotionCompatibility;
import com.vmware.vim25.InsufficientResourcesFault;
import com.vmware.vim25.InvalidName;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.InvalidState;
 
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SendEmailAction;
import com.vmware.vim25.SnapshotFault;
import com.vmware.vim25.StateAlarmExpression;
import com.vmware.vim25.StateAlarmOperator;
import com.vmware.vim25.TaskInProgress;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.VirtualHardware;
import com.vmware.vim25.VirtualMachineCapability;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineConfigInfo;
 
import com.vmware.vim25.VirtualMachineMovePriority;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineSnapshotTree;
import com.vmware.vim25.VmConfigFault;
import com.vmware.vim25.mo.AlarmManager;
import com.vmware.vim25.mo.ComputeResource;
 
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
 
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.VirtualMachineSnapshot;
import com.vmware.vim25.mo.samples.VMManager.FailOverTask;




public class Vm_Manager_Anita {

 	

	//Static declarations

 	static String IP_VC = "130.65.133.50";        // IpAddress of the Vcenter

	 static String URL_STR = "https://" + IP_VC + "/sdk"; // "https://130.65.157.160/sdk";
 
	static String USERNAME_1 = "administrator";

	 static String PASSWORD_1 = "12!@qwQW";
 
	static Long PING_FREQUENCY =  10000L; // 10 sec

	
 
	//to get the virtual machine by Name

	 private static VirtualMachine getVirtualMachine(ServiceInstance si) throws InvalidProperty, RuntimeFault, RemoteException {
 
		Folder rootFolder = si.getRootFolder();

InventoryNavigator navigator = new InventoryNavigator(rootFolder);
 
		ManagedEntity mes = navigator.searchManagedEntity("VirtualMachine","Team17_VM_Anita_Lab1Clone");

 		return (VirtualMachine) mes;

}
	 private static HostSystem getVirtualHost(ServiceInstance si) throws InvalidProperty, RuntimeFault, RemoteException
	 {
		 Folder rootFolder = si.getRootFolder();

		 InventoryNavigator navigator = new InventoryNavigator(rootFolder);
		 
		 ManagedEntity mes = navigator.searchManagedEntity("HostSystem","130.65.133.52");

		 return (HostSystem) mes;
	 }

	
 
	private static ServiceInstance getServiceInstance(String strUrl,

 			String username, String password, boolean ignoreCert) {




 		ServiceInstance si = null;

		 try {

 			URL url = new URL(strUrl);

			 si = new ServiceInstance(url, username, password, ignoreCert);
 
		} catch (MalformedURLException e) {

			 System.err.println("...Incorrect URL");
 
			e.printStackTrace();

		 } catch (RemoteException e) {
 
			System.err.println("Exception occurred while connecting");

 			e.printStackTrace();

		 }

 		return si;

	 }

 	//Printing Configuration ---> STEP-1 of Project

	 private static void printVMConfig(VirtualMachine vm) 
 
	{

		 if (vm != null) 
 
		{

			 VirtualMachineConfigInfo vmConfigInfo = vm.getConfig();
 
			VirtualMachineCapability vmCapability = vm.getCapability();

 			// Print Configuration of the Virtual machine

			 System.out.println("*------------------------------------------------*");
 
			System.out.println("| Virtual Machine Name        : " 

 					+ vm.getName() + "|");

			 System.out.println("| Virtual Machine CPUs        : "
 
					+ vmConfigInfo.getHardware().getNumCPU() + "|");

 			System.out.println("| Virtual Machine Version     : " 

					 + vmConfigInfo.getVersion() + "|");
 
			System.out.println("| Virtual Machine Memory      : "

 					+ vmConfigInfo.getHardware().getMemoryMB());

			 System.out.println("| Guest Machine Name          : "
 
					+ vmConfigInfo.getGuestFullName() + "|");

			 System.out.println("| Multiple snapshot supported : "
 
					+ vmCapability.isMultipleSnapshotsSupported() + "|");

 			System.out.println("*------------------------------------------------*");

			 try 
 
			{

				 vm.getResourcePool();
 
			} 

			 catch (InvalidProperty e) 
 
			{

				 e.printStackTrace();
 
			}

			 catch (RuntimeFault e) 
 
			{

				 e.printStackTrace();
 
			}

			 catch (RemoteException e) 
 
			{

				 e.printStackTrace();
 
			}

		 }

 	}

	 public static String checkVMStatus(VirtualMachine vm) throws IOException
	 {
		 String vmStatus="";
		 String ip=vm.getGuest().getIpAddress();
		 System.out.println("ip=="+ip);
		 if(ip==null)
		 {
			 System.out.println("ip is NULL");
			 vmStatus="noIp";
			 return vmStatus;
		 }
		 else
		 {
		 Runtime r = Runtime.getRuntime();
			Process pingProcess = r.exec("ping " + ip);
			String pingResult = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(
					pingProcess.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) 
			{
				System.out.println(inputLine);
				pingResult += inputLine;
			}
// Ping fails
			if (pingResult.contains("Request timed out")) 
			{
				System.out.println("Host Not Found");
				vmStatus = "off";
			} 
			
// Ping Success 			
			else 
			{
				vmStatus = "on";
				System.out.println("Host is live");
			}
		 }
		 return vmStatus;
		
	 }
	 //check Host status before migration

	 private static String checkHostStatus(String ip) throws IOException 
		{
		 System.out.println("in check host status");
			String isReachable = "off";
			if(ip=="")
			{
				isReachable="noIp";
			}
			else
			{
			Runtime r = Runtime.getRuntime();
				Process pingProcess = r.exec("ping " + ip);
				String pingResult = "";
				BufferedReader in = new BufferedReader(new InputStreamReader(
						pingProcess.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) 
				{
					System.out.println(inputLine);
					pingResult += inputLine;
				}
	// Ping fails
				if (pingResult.contains("Request timed out")) 
				{
					System.out.println("Host Not Found");
					isReachable = "off";
				} 
	// Ping Success 			
				else 
				{
					isReachable = "on";
					System.out.println("Host is live");
				}
			}
			return isReachable;
		}
	

 	//migrate VM to Another host

 		 public static void migrateToAnotherHost(String vmname1, String newHostName1) {
 	 
 			if (vmname1.equals(null) || newHostName1.equals(null)) {
 	 
 					System.out.println("Usage: java MigrateVM <url> "
 	 
 							+ "<username> <password> <vmname> <newhost>");
 	 
 					System.exit(0);

 				 }
 	 
 			ServiceInstance si = getServiceInstance(URL_STR,

 	 					USERNAME_1, PASSWORD_1, true);
 	 
 			String vmname= vmname1;

 			 String newHostName=newHostName1;
 	 
 			Folder rootFolder = si.getRootFolder();

 			     try {
 	 
 					VirtualMachine vm = (VirtualMachine) new InventoryNavigator(

 	 				    rootFolder).searchManagedEntity(

 					         "VirtualMachine", vmname);
 	 
 					HostSystem newHost = (HostSystem) new InventoryNavigator(

 	 				        rootFolder).searchManagedEntity(

 					             "HostSystem", newHostName);
 	 
 					    ComputeResource cr = (ComputeResource) newHost.getParent();

 	 				    String[] checks = new String[] {"cpu", "software"};
 	 
 					    HostVMotionCompatibility[] vmcs =

 					       si.queryVMotionCompatibility(vm, new HostSystem[] 
 	 
 					        {newHost},checks );

 					    
 	 
 					    String[] comps = vmcs[0].getCompatibility();

 					     if(checks.length != comps.length)
 	 
 					    {

 					       System.out.println("CPU/software NOT compatible. Exit.");
 	 
 					      si.getServerConnection().logout();

 					       return;
 	 
 					    }

 					     Task task;
 	 
 					    task = vm.migrateVM_Task(cr.getResourcePool(), newHost,

 	 				        VirtualMachineMovePriority.highPriority, 

 					         VirtualMachinePowerState.poweredOff);
 	 
 					 

 					     if(task.waitForTask()==Task.SUCCESS)
 	 
 					    {

 					       System.out.println("COLD MIGRATION!!!--VM Migrated Successfully!");
 	 
 					      vm.powerOnVM_Task(null);

 	 				    }

 					     else if(task.waitForTask()!=Task.SUCCESS)
 	 
 					    {
 					    	 

 					    	 task = vm.migrateVM_Task(cr.getResourcePool(), newHost,
 	 
 							        VirtualMachineMovePriority.highPriority, 

 	 						        VirtualMachinePowerState.poweredOn);
 					    	System.out.println("LIVE MIGRATION!!!--VM Migration Successful!!");

 					     }
 	 
 					    else

 					     {
 	 
 					   	 System.out.println("VM Migration failed!");
 	 
 					        TaskInfo info = task.getTaskInfo();

 					         System.out.println(info.getError().getFault());
 	 
 					         

 					     }
 	 
 					    si.getServerConnection().logout();

 				 } catch (Exception e) {				
 	 
 					e.printStackTrace();

 				 }	    
 	 


 		}


	 //power off virtual machine
 
	public void powerOffVirtualMachine(VirtualMachine vm)

	 {
 
		try {

			 Task task=vm.powerOffVM_Task();
 
			if(task.waitForTask() == Task.SUCCESS)

			 {
 
				System.out.println("Virtual machine is powered OFF");

 			}

		 }catch (Exception e) {

 			e.printStackTrace();

		 }

 	}

	 //power On virtual machine

 	public void powerOnVirtualMachine(VirtualMachine vm)

	 {

 		Task task;

		 try {

 			task = vm.powerOnVM_Task(null);			

				 if(task.waitForTask() ==Task.SUCCESS)
 
				{
					 System.out.println("Virtual Machine is powered ON");
				}			
		 } 
 
		catch (Exception e) {
			 e.printStackTrace(); 
		}	

	 }
 	
 	//do Clone
 	private static void doClone(VirtualMachine vm, String cloneName)
			throws Exception 
			{
		if (vm == null) 
		{
			throw new Exception("Alram!  Virtual Machine Not Found");
		}
		System.out.println("------------------------------------------");
		System.out.println("Virtual Machine name        : " + vm.getName());
		System.out.println("Virtual Machine Parent name : " + vm.getParent());
		// *
		// clone spec
		// *
		VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
		VirtualMachineRelocateSpec relocate = new VirtualMachineRelocateSpec();
		System.out.println("relocate       : " + relocate);
		
		relocate.diskMoveType = "createNewChildDiskBacking";
		cloneSpec.setLocation(relocate);
		cloneSpec.setPowerOn(false);
		cloneSpec.setTemplate(false);
		cloneSpec.snapshot = vm.getCurrentSnapShot().getMOR();
		System.out.println("Cloning from " + vm.getName() + " into " + cloneName);

		Task task = vm.cloneVM_Task((Folder) vm.getParent(), cloneName,
				cloneSpec);
		//String status = task.waitForTask();

		if (task.waitForTask()==Task.SUCCESS) 
		{
			System.out.println("Virtual Machine cloned successfully ");
		} 
		else 
		{
			TaskInfo info = task.getTaskInfo();
			System.out.println(info.getError().getFault());
			throw new RuntimeException("Error while cloning VM");
		}
	}
 	
 //alarm manager
 	private static AlarmManager createAlarmManager(VirtualMachine vm1,
			ServiceInstance serviceInst1) throws InvalidName, DuplicateName,
			RuntimeFault, RemoteException 
			{
		System.out.println("------------------------------------------");
		System.out.println("Creating Alarm: AlarmOnPowerOff");
		System.out.println("1");
		AlarmManager am = serviceInst1.getAlarmManager();
		System.out.println("2"+am);
		StateAlarmExpression sae = new StateAlarmExpression();
		sae.setType("VirtualMachine");
		sae.setStatePath("runtime.powerState");
		sae.setOperator(StateAlarmOperator.isEqual);
		sae.setRed("poweredOff");
        System.out.println("3");
		SendEmailAction action = new SendEmailAction();
		action.setToList("anita.tvl@gmail.com");
		action.setCcList("anita.tvl@gmail.com");
		action.setSubject("Alarm trigger");
		action.setBody("User powered off the VM.");

		AlarmTriggeringAction alarmAction = new AlarmTriggeringAction();
		alarmAction.setYellow2red(true);
		alarmAction.setAction(action);
		AlarmSetting as = new AlarmSetting();
		as.setReportingFrequency(0); // as often as possible
		as.setToleranceRange(0);

		AlarmSpec spec = new AlarmSpec();
		spec.setAction(alarmAction);
		spec.setExpression(sae);
		Random r=new Random();
		int alarmNum=r.nextInt();
		//System.out.println(alarmNum);
		spec.setName("VmPowerStateAlarm"+alarmNum);
		spec.setDescription("Monitor VM state and send email if VM power's off");
		spec.setEnabled(true);
		spec.setSetting(as);
		
		am.createAlarm(vm1, spec);

		System.out.println("Successfully created Alarm: AlarmOnPowerOff");

		return am;
	}

 	private static void takeSnapshot(VirtualMachine vm, String snapshotName,String operation) 
	{
		
		if (vm != null && !snapshotName.isEmpty()) 
		{
			String Vm_Name = vm.getName();
			System.out.println("------------------------------------------");
			System.out.println("Snapshot for " + Vm_Name);

			try 
			{
				if("create".equalsIgnoreCase(operation))
				{
				vm.removeAllSnapshots_Task();

				Task task = vm.createSnapshot_Task(snapshotName,
						"Snapshot for " + Vm_Name, false, false);

				//System.out.println("Current snapshot updated for " + Vm_Name);

				String status = null;

				status = task.waitForTask();

				System.out.println(task.getServerConnection());

				if (status.equalsIgnoreCase(Task.SUCCESS)) 
				{
					//System.out.println("VM cloned");
					System.out.println("Current snapshot updated for " + Vm_Name);
				}
				else 
				{
					//System.out.println("Error, VM not cloned!");
					TaskInfo info = task.getTaskInfo();
					System.out.println(info.getError().getFault());
					throw new RuntimeException("Error while cloning VM");
				}
				}
				else if("revert".equalsIgnoreCase(operation))
				{
					
					VirtualMachineSnapshot vmSnap=getSnapshotInTree(vm,snapshotName);
					if(vmSnap!=null)
					{
					Task revertTask=vm.revertToCurrentSnapshot_Task(null);
					String revertStatus=null;
					revertStatus=revertTask.waitForTask();
					if(revertStatus.equalsIgnoreCase(Task.SUCCESS))
					{
						System.out.println("VM is reverted to available snapshot");
					}
					System.out.println("power on VM");
					Task powerOnVM=vm.powerOnVM_Task(null);
					String powerStatus=null;
					powerStatus=powerOnVM.waitForTask();
					if(powerStatus.equalsIgnoreCase(Task.SUCCESS))
					{
						System.out.println("VM is switched on after reverting snapshot!!!");
					}
					}
				}
				else 
				{
					System.out.println("snapshot operation is not successful");
				}

			} 
			catch (SnapshotFault e) 
			{
				e.printStackTrace();
			} 
			catch (TaskInProgress e) 
			{
				e.printStackTrace();
			}
			catch (InvalidState e) 
			{
				e.printStackTrace();
			}
			catch (RuntimeFault e) 
			{
				e.printStackTrace();
			}
			catch (RemoteException e) 
			{
				e.printStackTrace();
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}

			//System.out.println("Snapshot taken successfully -> " + Vm_Name);
			//System.out.println("**********************************************");
		}
		
		
	}
 	private static void takeSnapshotOfHost(VirtualMachine vm, String snapshotName,String operation) 
	{
		
		if (vm != null && !snapshotName.isEmpty()) 
		{
			String Vm_Name = vm.getName();
			System.out.println("****************************************");
			System.out.println("Snapshot for Host " + Vm_Name);

			try 
			{
				if("create".equalsIgnoreCase(operation))
				{
				vm.removeAllSnapshots_Task();

				Task task = vm.createSnapshot_Task(snapshotName,
						"Snapshot for " + Vm_Name, false, false);

				//System.out.println("Current snapshot updated for " + Vm_Name);

				String status = null;

				status = task.waitForTask();

				System.out.println(task.getServerConnection());

				if (status.equalsIgnoreCase(Task.SUCCESS)) 
				{
					//System.out.println("VM cloned");
					System.out.println("Current snapshot updated for " + Vm_Name);
				}
				else 
				{
					//System.out.println("Error, VM not cloned!");
					TaskInfo info = task.getTaskInfo();
					System.out.println(info.getError().getFault());
					throw new RuntimeException("Error while cloning VM");
				}
				}
				else if("revert".equalsIgnoreCase(operation))
				{
					
					VirtualMachineSnapshot vmSnap=getSnapshotInTree(vm,snapshotName);
					if(vmSnap!=null)
					{
					Task revertTask=vm.revertToCurrentSnapshot_Task(null);
					String revertStatus=null;
					revertStatus=revertTask.waitForTask();
					if(revertStatus.equalsIgnoreCase(Task.SUCCESS))
					{
						System.out.println("VM is reverted to available snapshot");
					}
					System.out.println("power on VM");
					Task powerOnVM=vm.powerOnVM_Task(null);
					String powerStatus=null;
					powerStatus=powerOnVM.waitForTask();
					if(powerStatus.equalsIgnoreCase(Task.SUCCESS))
					{
						System.out.println("VM is switched on after reverting snapshot!!!");
					}
					}
				}
				else 
				{
					System.out.println("snapshot operation is not successful");
				}

			} 
			catch (SnapshotFault e) 
			{
				e.printStackTrace();
			} 
			catch (TaskInProgress e) 
			{
				e.printStackTrace();
			}
			catch (InvalidState e) 
			{
				e.printStackTrace();
			}
			catch (RuntimeFault e) 
			{
				e.printStackTrace();
			}
			catch (RemoteException e) 
			{
				e.printStackTrace();
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}

			//System.out.println("Snapshot taken successfully -> " + Vm_Name);
			//System.out.println("**********************************************");
		}
		
		
	}
 	static VirtualMachineSnapshot getSnapshotInTree(VirtualMachine vm, String snapName)	 
	  {
	    if (vm == null || snapName == null) 

	     {

	      return null;

	    }
	    VirtualMachineSnapshotTree[] snapTree = 

	        vm.getSnapshot().getRootSnapshotList();

	    if(snapTree!=null)

	    {

	      ManagedObjectReference mor = findSnapshotInTree(

	          snapTree, snapName);

	     

	      if(mor!=null)

	      {

	        return new VirtualMachineSnapshot(

	             vm.getServerConnection(), mor);

	      }

	    }

	    return null;

	   }

	static ManagedObjectReference findSnapshotInTree(
			 
		      VirtualMachineSnapshotTree[] snapTree, String snapName)
	{

		    for(int i=0; i <snapTree.length; i++) 

		     {

		      VirtualMachineSnapshotTree node = snapTree[i];

		      if(snapName.equals(node.getName()))

		      {

		        return node.getSnapshot();

		       

		      } 

		      else 

		      {

		        VirtualMachineSnapshotTree[] childTree = 

		            node.getChildSnapshotList();

		        if(childTree!=null)

		         {

		          ManagedObjectReference mor = findSnapshotInTree(

		              childTree, snapName);

		          if(mor!=null)

		          {

		            return mor;

		          }

		        }

		      }

		    }

		    return null;

	}

 	
	public static void main(String[] args) throws Exception {
 
		ServiceInstance si = getServiceInstance(URL_STR,

				 USERNAME_1, PASSWORD_1, true);
		VirtualMachine vm = getVirtualMachine(si);		
		printVMConfig(vm);
	   AlarmManager am = createAlarmManager(vm, si);
		Thread thread = new Thread(new ThreadTask(si, vm,am),
				"Disaster Recovery");
		thread.start();
		Thread.sleep(10000L);
	}
	static class ThreadTask implements Runnable
	{
		//final String snapshotSuffix = "_snapshot1";
		ServiceInstance serviceInst1;
		VirtualMachine vm1;
		AlarmManager am1;
		String vmName;
		Folder rootFolder;
		String cloneName;

		public ThreadTask(ServiceInstance serviceInst1, VirtualMachine vm1,AlarmManager am) {
			this.serviceInst1 = serviceInst1;
			this.vm1 = vm1;
			this.am1 = am;
			this.vmName = vm1.getName();
			this.rootFolder = serviceInst1.getRootFolder();
			this.cloneName = vm1.getName() + "_Clone1";
		}
		@Override
		public void run()
		{
			for(;;)
			{
				try{
					ServiceInstance si = getServiceInstance(URL_STR,
							USERNAME_1, PASSWORD_1, true);

					VirtualMachine vm = getVirtualMachine(si);
					String ip = vm.getGuest().ipAddress;
					String ipstatus=checkVMStatus(vm);
					String hostStatus=checkHostStatus("130.65.133.52");
					URL urlHost=new URL("https://130.65.132.14/sdk");
					ServiceInstance siHost=new ServiceInstance(urlHost,"administrator","12!@qwQW");
					
					Folder rootFolderOfHost = siHost.getRootFolder();

					InventoryNavigator navigator = new InventoryNavigator(rootFolderOfHost);
					 
							ManagedEntity mes = navigator.searchManagedEntity("VirtualMachine","t17-vhost01-cum3-lab2_.133.52");

							VirtualMachine vmHost = (VirtualMachine) mes; 
							
							String vmHostName= vmHost.getName();
							System.out.println("vm Host Name=="+vmHostName);
					if(hostStatus.equalsIgnoreCase("on"))
					{
						//take host snapshot when host is working
						System.out.println("TAKING SNAPSHOT OF HOST");
						
								takeSnapshotOfHost(vmHost,vmHostName+"_Snapshot","create");
						
					if (ipstatus.equalsIgnoreCase("on")) {

						System.out.println(Thread.currentThread().getName()
								+ " : " + "...taking snapshot");

						takeSnapshot(vm1,vmName+"_Snapshot","create");
					}
					else if(ipstatus.equalsIgnoreCase("off"))
					{
						//logic should be added
						System.out.println("VM is switched OFF");
						
					}
					else if(ipstatus.equalsIgnoreCase("noIp"))
					{
						System.out.println("NIC disabled in VM");
						takeSnapshot(vm1,vmName+"_Snapshot","revert");
					}
					Thread.sleep(5*60*1000);
					
					}
					else if(hostStatus.equalsIgnoreCase("off"))
					{
						takeSnapshotOfHost(vmHost,vmHostName+"_Snapshot","revert");
						System.out.println("!!!!!VHOST VM is REVERTING!!!!");	
					}
					else if(hostStatus.equalsIgnoreCase("noIp"))
					{
						takeSnapshotOfHost(vmHost,vmHostName+"_Snapshot","revert");
						System.out.println("in revert host snapshot");//revert host snapshot when host is not working
					}
					
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	

}



