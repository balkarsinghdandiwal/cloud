/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */


package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.math3.analysis.function.Power;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
//import java.util.concurrent.ThreadLocalRandom;
import org.cloudbus.cloudsim.UtilizationModelStochastic;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.distributions.ExponentialDistr;
//import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.distributions.WeibullDistr;
import org.cloudbus.cloudsim.power.*;

/**
 * An example showing how to create
 * scalable simulations.
 */
public class project {

	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;
	
	/** The vmlist. */
	private static List<Vm> vmlist;
	public static List<Host> hostListBackup = new ArrayList<Host>();
	private static List<Vm> vmListForHostProvisioning;

	

	private static List<Vm> createVM(int userId, int vms) {

		//Creates a container to store VMs. This list is passed to the broker later
		LinkedList<Vm> list = new LinkedList<Vm>();

		//VM Parameters
		long size = 10000; //image size (MB)
		int ram = 512; //vm memory (MB)
		int mips = 1000;
		long bw = 1000;
		int pesNumber = 1; //number of cpus
		String vmm = "Xen"; //VMM name

		//create VMs
		Vm[] vm = new Vm[vms];

		for(int i=0;i<vms;i++){
			vm[i] = new Vm(i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
			//for creating a VM with a space shared scheduling policy for cloudlets:
			//vm[i] = Vm(i, userId, mips, pesNumber, ram, bw, size, priority, vmm, new CloudletSchedulerSpaceShared());

			list.add(vm[i]);
		}
		
		return list;
	}


	private static List<Cloudlet> createCloudlet(int userId, int cloudlets){
		// Creates a container to store Cloudlets
		LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

		//cloudlet parameters
		long length = 70000;
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;
		UtilizationModel utilizationModel = new UtilizationModelFull();

		Cloudlet[] cloudlet = new Cloudlet[cloudlets];

		for(int i=0;i<cloudlets;i++){
			Random rdbj= new Random();
			cloudlet[i] = new Cloudlet(i, (length + rdbj.nextInt(30000)), pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			// setting the owner of these Cloudlets
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);
		}

		return list;
	}


	////////////////////////// STATIC METHODS ///////////////////////

	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {
		Log.printLine("Starting project...");

		try {
			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities.
			int num_user = 1;   // number of grid users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
			@SuppressWarnings("unused")
			Datacenter datacenter0 = createDatacenter("Datacenter");
			
			//@SuppressWarnings("unused")
			//Datacenter datacenter1 = createDatacenter("Datacenter_1");

			//Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			//Fourth step: Create VMs and Cloudlets and send them to broker
			vmlist = createVM(brokerId,100); //creating 20 vms
			cloudletList = createCloudlet(brokerId,1000); // creating 40 cloudlets

			broker.submitVmList(vmlist);
			broker.submitCloudletList(cloudletList);
			//broker.calculatePower();

			// Fifth step: Starts the simulation
			CloudSim.startSimulation();

			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			broker.calculateclid();
			
			
			
			CloudSim.stopSimulation();
			broker.calculatePower();
			printCloudletList(newList);

			Log.printLine("Project finished!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	private static Datacenter createDatacenter(String name){
		DatacenterBroker broker = createBroker();
		float ibm = broker.ratioibm;//4 cores
		float ibm2 = broker.ratioibm2;//6 cores
		float hp = broker.ratiohp;//2 cores
		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store one or more
		//    Machines
		ArrayList<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
		//    create a list to store these PEs before creating
		//    a Machine.
	//	List<Pe>  = new ArrayList<Pe>();

		int mips = 1000;
		List<Pe> peList1 = new ArrayList<Pe>();
		// 3. Create PEs and add these into the list.
		//for a quad-core machine, a list of 4 PEs is required:
		peList1.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
		peList1.add(new Pe(1, new PeProvisionerSimple(mips)));
		peList1.add(new Pe(2, new PeProvisionerSimple(mips)));
		peList1.add(new Pe(3, new PeProvisionerSimple(mips)));

		//Another list, for a dual-core machine
		List<Pe> peList2 = new ArrayList<Pe>();

		peList2.add(new Pe(0, new PeProvisionerSimple(mips)));
		peList2.add(new Pe(1, new PeProvisionerSimple(mips)));


		List<Pe> peList3 = new ArrayList<Pe>();

		peList3.add(new Pe(0, new PeProvisionerSimple(mips)));
		peList3.add(new Pe(1, new PeProvisionerSimple(mips)));
		peList3.add(new Pe(2, new PeProvisionerSimple(mips)));
		peList3.add(new Pe(3, new PeProvisionerSimple(mips)));
		peList3.add(new Pe(4, new PeProvisionerSimple(mips)));
		peList3.add(new Pe(5, new PeProvisionerSimple(mips)));
		
		//4. Create Hosts with its id and list of PEs and add them to the list of machines
		int hostId;
		float powerratio = 0;
		int ram = 2048; //host memory (MB)
		long storage = 1000000; //host storage
		int bw = 10000;
		/*if (hp > ibm){
		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList1,
    				new VmSchedulerTimeShared(peList1)//quad core ibm
    			)
    		); // This is our first machine

		hostId++;
		//hostList.get(1).getPeList().size()
		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList2,
    				new VmSchedulerTimeShared(peList2)//dual core hp
    			)
    		); // Second machine
		}
		if (ibm > hp){
			hostList.add(
	    			new Host(
	    				hostId,
	    				new RamProvisionerSimple(ram),
	    				new BwProvisionerSimple(bw),
	    				storage,
	    				peList2,
	    				new VmSchedulerTimeShared(peList2)//dual core hp
	    			)
	    		); // This is our first machine

			hostId++;

			hostList.add(
	    			new Host(
	    				hostId,
	    				new RamProvisionerSimple(ram),
	    				new BwProvisionerSimple(bw),
	    				storage,
	    				peList1,
	    				new VmSchedulerTimeShared(peList1)//quad core ibm
	    			)
	    		); // Second machine
			}*/
		//int a= broker.getCloudletReceivedList().size();
		Host[] host = new Host[30];
		
		Random rdbj= new Random();
		List<List<Pe>> givenList = Arrays.asList(peList1, peList2);
		
		
		/*int which = (int)(Math.random() * 2); 
		;
		switch (which) {
	    case 0:  
	    		;
	             break;
	    case 1:  peList2;
	             break;	    
	}*/
		for(int i=0;i<30;i++){
			int randomIndex = rdbj.nextInt(givenList.size());
			List<? extends Pe> randomElement = givenList.get(randomIndex);
			if(randomElement.size() == 4){
				powerratio=ibm;	
			}
			if(randomElement.size() == 2){
				powerratio=hp;	
			}
			if(randomElement.size() == 6){
				powerratio=ibm2;	
			}
			host[i]= new Host(
	    				hostId=i,	    				
	    				powerratio, 
	    				new RamProvisionerSimple(ram),
	    				new BwProvisionerSimple(bw),
	    				storage,
	    				randomElement,
	    				new VmSchedulerTimeShared(randomElement)//random pelist
	    				);
			hostList.add(host[i]);
		}
		
		List<Host> sortlist = new ArrayList<Host>();
		List<Host> sortlistt = new ArrayList<Host>();
		ArrayList<Host> templist = new ArrayList<Host>();
		ArrayList<Host> templistt = new ArrayList<Host>();
		ArrayList<Host> templisttt = new ArrayList<Host>();
		//ArrayList<Cloudlet> templist1 = new ArrayList<Cloudlet>();
		
		for (Host hostt : hostList) {
			templist.add(hostt);
			templistt.add(hostt);
			templisttt.add(hostt);
		}
		
		int templistsize= templist.size();
		for(int i=0;i<templistsize;i++){
			Host smallhost = templist.get(0);
			for(Host checkhost : templist){
				if(smallhost.getpowerratio()>checkhost.getpowerratio()){
				smallhost=checkhost;
				}
			}
			sortlist.add(smallhost);
			templist.remove(smallhost);
		}
		
		int templistsizee= templistt.size();
		for(int i=0;i<templistsizee;i++){
			Host smallhost = templistt.get(0);
			for(Host checkhost : templistt){
				if(smallhost.getpowerratio()<checkhost.getpowerratio()){
				smallhost=checkhost;
				}
			}
			sortlistt.add(smallhost);
			templistt.remove(smallhost);
		}
		
		
		List<List<Host>> givenListt = Arrays.asList(sortlist,sortlistt,templisttt);
		int randomIndex = rdbj.nextInt(givenListt.size());
		List<Host> randomElement = givenListt.get(randomIndex);
		if(randomElement == sortlist){
			Log.printLine(" ");	
			Log.printLine("Hosts are selected in Increasing Power Consumption Order");
			Log.printLine(" ");	
		}
		if(randomElement == sortlistt){
			Log.printLine(" ");	
			Log.printLine("Hosts are selected in Decreasing Power Consumption Order");	
			Log.printLine(" ");	
		}
		if(randomElement == templisttt){
			Log.printLine(" ");	
			Log.printLine("Hosts are selected Randomly");	
			Log.printLine(" ");	
		}
		int count=1;
		for(Host printcloudlet: randomElement){
		//	Log.printLine(count + ". Host: "+printcloudlet.getId() +" power ratio: "+printcloudlet.getpowerratio() +" Ram: "+printcloudlet.getRam() +" Bandwidth: "+printcloudlet.getBw() +" Storage: "+printcloudlet.getStorage() +" No. of Processing Element: "+printcloudlet.getPeList().size());
			count++;
		}
		
		
	
		
		
		
		
		/*HashMap<Float,Host> hostratiosorted = new HashMap<Float,Host>();
		HashMap<Float,Host> hostratio = new HashMap<Float,Host>();
		for(int i=0;i<hostList.size();i++){
			Host x = hostList.get(i);
			if(x.getPeList().size() == 4){
				hostratio.put(ibm, x);	
			}
			if(x.getPeList().size() == 2){
				hostratio.put(hp, x);	
			}
		}
		
		TreeMap<Float, Host>SortHostratio = new TreeMap<>(hostratio);
		SortHostratio.forEach((key, value) -> {
			hostratiosorted.put(key, value);
		});
		
		Collection<Host> set = hostratiosorted.values();
	      Iterator iterator = set.iterator();
		for(int i=0;i<hostratiosorted.size();i++){
			//Host x = hostratiosorted.values().iterator(),
			int m= set.
			Log.printLine(m);
		}*?
		
		
		
		
		
		
		
		
		
		
		
		for(Host printcloudlet: hostList){
		//	Log.printLine("Host: "+printcloudlet.getId() +" Ram: "+printcloudlet.getRam() +" Bandwidth: "+printcloudlet.getBw() +" Storage: "+printcloudlet.getStorage() +" No. of Processing Element: "+printcloudlet.getPeList().size());
		}       
		//To create a host with a space-shared allocation policy for PEs to VMs:
		/* hostList.add(
    			new Host(
    				hostId,
    				new CpuProvisionerSimple(peList1),
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				new VmSchedulerSpaceShared(peList1)
    			)
    		);*/

		//To create a host with a oportunistic space-shared allocation policy for PEs to VMs:
		/*hostList.add(
    			new Host(
    				hostId,
    				new CpuProvisionerSimple(peList1),
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				new VmSchedulerOportunisticSpaceShared(peList1)
    			)
    		);*/


		// 5. Create a DatacenterCharacteristics object that stores the
		//    properties of a data center: architecture, OS, list of
		//    Machines, allocation policy: time- or space-shared, time zone
		//    and its price (G$/Pe time unit).
		String arch = "x86";      // system architecture
		String os = "Linux";          // operating system
		String vmm = "Xen";
		double time_zone = 10.0;         // time zone this resource located
		double cost = 3.0;              // the cost of using processing in this resource
		double costPerMem = 0.05;		// the cost of using memory in this resource
		double costPerStorage = 0.1;	// the cost of using storage in this resource
		double costPerBw = 0.1;			// the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);


		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(sortlist), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		hostListBackup = hostList;	
		for(Host printcloudlet: hostListBackup){
		//	Log.printLine("Host: "+printcloudlet.getId() +" power ratio: "+printcloudlet.getpowerratio() +" Ram: "+printcloudlet.getRam() +" Bandwidth: "+printcloudlet.getBw() +" Storage: "+printcloudlet.getStorage() +" No. of Processing Element: "+printcloudlet.getPeList().size());
		}
		return datacenter;
	}

	//We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
	//to the specific rules of the simulated scenario
	private static DatacenterBroker createBroker(){

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx New Edit xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	
	
	
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	
	/**
	 * Prints the Cloudlet objects
	 * @param list  list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet; 
		float avgmake,avgmakespan=0;
		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
			"Datacenter ID" + "  " + "VM ID" + indent +"  "+ "Time" + indent +indent + "Start Time" + indent + "Finish Time"+ indent + "Makespan");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			float starttime= (float) cloudlet.getExecStartTime();
			float finishtime = (float) cloudlet.getFinishTime();
			float makespan = finishtime - starttime;
			avgmakespan= avgmakespan+makespan;
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				Log.print("SUCCESS");

				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() + indent +
						indent  + "" + dft.format(cloudlet.getActualCPUTime()) +
						indent + indent + dft.format(cloudlet.getExecStartTime())+ indent + indent + indent + dft.format(cloudlet.getFinishTime())+ indent + indent + indent + makespan);
			}
		}
		avgmake=avgmakespan/size;
		Log.printLine("Average Makespan:"+avgmake);

	}
}
