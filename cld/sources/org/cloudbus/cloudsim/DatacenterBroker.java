/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.CloudletList;
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G5Xeon3075;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G3PentiumD930;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerIbmX3250XeonX3470;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerIbmX3550XeonX5675;

/**
 * DatacentreBroker represents a broker acting on behalf of a user. It hides VM management, as vm
 * creation, sumbission of cloudlets to this VMs and destruction of VMs.
 * 
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class DatacenterBroker extends SimEntity {

	/** The vm list. */
	protected List<? extends Vm> vmList;

	/** The vms created list. */
	protected List<? extends Vm> vmsCreatedList;

	/** The cloudlet list. */
	protected List<? extends Cloudlet> cloudletList;

	/** The cloudlet submitted list. */
	protected List<? extends Cloudlet> cloudletSubmittedList;

	/** The cloudlet received list. */
	protected List<? extends Cloudlet> cloudletReceivedList;
	public static ArrayList<Integer> vmhost = new ArrayList<Integer>();
	public ArrayList<Cloudlet> templist1 = new ArrayList<Cloudlet>();
	public ArrayList<Float> cloudletuti = new ArrayList<Float>();
	public HashMap<Integer,Host> hostvm = new HashMap<Integer,Host>();
	public HashMap<Integer, Host> cloudhost = new HashMap<Integer, Host>();
	//public List<Cloudlet> cloudletec = getCloudletReceivedList();
	public DecimalFormat dft = new DecimalFormat("###.##");
	public static PowerModelSpecPowerIbmX3250XeonX3470 PMibm = new PowerModelSpecPowerIbmX3250XeonX3470();//4cores
	public static PowerModelSpecPowerIbmX3550XeonX5675 PMibm2 = new PowerModelSpecPowerIbmX3550XeonX5675();//6cores
	public static PowerModelSpecPowerHpProLiantMl110G5Xeon3075 PMhp = new PowerModelSpecPowerHpProLiantMl110G5Xeon3075();//2cores
	public static PowerModelSpecPowerHpProLiantMl110G3PentiumD930 PMhp2 = new PowerModelSpecPowerHpProLiantMl110G3PentiumD930();//2cores

	public static float minhp= (float) PMhp.getPower(0);
	public static float maxhp= (float) PMhp.getPower(1);
	public static float minhp2= (float) PMhp2.getPower(0);
	public static float maxhp2= (float) PMhp2.getPower(1);
	public static float min= (float) PMibm.getPower(0);
	public static float max= (float) PMibm.getPower(1);
	public static float min2= (float) PMibm2.getPower(0);
	public static float max2= (float) PMibm2.getPower(1);
	public static float ratiohp = (float) minhp/maxhp;
	public static float ratiohp2 = (float) minhp2/maxhp2;
	public static float ratioibm = (float) min/max;
	public static float ratioibm2 = (float) min2/max2;
	/** The cloudlets submitted. */
	protected int cloudletsSubmitted;

	/** The vms requested. */
	protected int vmsRequested;

	/** The vms acks. */
	protected int vmsAcks;

	/** The vms destroyed. */
	protected int vmsDestroyed;

	/** The datacenter ids list. */
	protected List<Integer> datacenterIdsList;

	/** The datacenter requested ids list. */
	protected List<Integer> datacenterRequestedIdsList;

	/** The vms to datacenters map. */
	protected Map<Integer, Integer> vmsToDatacentersMap;

	/** The datacenter characteristics list. */
	protected Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsList;

	/**
	 * Created a new DatacenterBroker object.
	 * 
	 * @param name name to be associated with this entity (as required by Sim_entity class from
	 *            simjava package)
	 * @throws Exception the exception
	 * @pre name != null
	 * @post $none
	 */
	public DatacenterBroker(String name) throws Exception {
		super(name);

		setVmList(new ArrayList<Vm>());
		setVmsCreatedList(new ArrayList<Vm>());
		setCloudletList(new ArrayList<Cloudlet>());
		setCloudletSubmittedList(new ArrayList<Cloudlet>());
		setCloudletReceivedList(new ArrayList<Cloudlet>());

		cloudletsSubmitted = 0;
		setVmsRequested(0);
		setVmsAcks(0);
		setVmsDestroyed(0);

		setDatacenterIdsList(new LinkedList<Integer>());
		setDatacenterRequestedIdsList(new ArrayList<Integer>());
		setVmsToDatacentersMap(new HashMap<Integer, Integer>());
		setDatacenterCharacteristicsList(new HashMap<Integer, DatacenterCharacteristics>());
	}

	/**
	 * This method is used to send to the broker the list with virtual machines that must be
	 * created.
	 * 
	 * @param list the list
	 * @pre list !=null
	 * @post $none
	 */
	public void submitVmList(List<? extends Vm> list) {
		getVmList().addAll(list);
	}

	/**
	 * This method is used to send to the broker the list of cloudlets.
	 * 
	 * @param list the list
	 * @pre list !=null
	 * @post $none
	 */
	public void submitCloudletList(List<? extends Cloudlet> list) {
		getCloudletList().addAll(list);
	}

	/**
	 * Specifies that a given cloudlet must run in a specific virtual machine.
	 * 
	 * @param cloudletId ID of the cloudlet being bount to a vm
	 * @param vmId the vm id
	 * @pre cloudletId > 0
	 * @pre id > 0
	 * @post $none
	 */
	public void bindCloudletToVm(int cloudletId, int vmId) {
		CloudletList.getById(getCloudletList(), cloudletId).setVmId(vmId);
	}

	/**
	 * Processes events available for this Broker.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	@Override
	public void processEvent(SimEvent ev) {
		switch (ev.getTag()) {
		// Resource characteristics request
			case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST:
				processResourceCharacteristicsRequest(ev);
				break;
			// Resource characteristics answer
			case CloudSimTags.RESOURCE_CHARACTERISTICS:
				processResourceCharacteristics(ev);
				break;
			// VM Creation answer
			case CloudSimTags.VM_CREATE_ACK:
				processVmCreate(ev);
				break;
			// A finished cloudlet returned
			case CloudSimTags.CLOUDLET_RETURN:
				processCloudletReturn(ev);
				break;
			// if the simulation finishes
			case CloudSimTags.END_OF_SIMULATION:
				shutdownEntity();
				break;
			// other unknown tags are processed by this method
			default:
				processOtherEvent(ev);
				break;
		}
	}

	/**
	 * Process the return of a request for the characteristics of a PowerDatacenter.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processResourceCharacteristics(SimEvent ev) {
		DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
		getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);

		if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()) {
			setDatacenterRequestedIdsList(new ArrayList<Integer>());
			createVmsInDatacenter(getDatacenterIdsList().get(0));
		}
	}

	/**
	 * Process a request for the characteristics of a PowerDatacenter.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processResourceCharacteristicsRequest(SimEvent ev) {
		setDatacenterIdsList(CloudSim.getCloudResourceList());
		setDatacenterCharacteristicsList(new HashMap<Integer, DatacenterCharacteristics>());

		Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloud Resource List received with "
				+ getDatacenterIdsList().size() + " resource(s)");

		for (Integer datacenterId : getDatacenterIdsList()) {
			sendNow(datacenterId, CloudSimTags.RESOURCE_CHARACTERISTICS, getId());
		}
	}

	/**
	 * Process the ack received due to a request for VM creation.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processVmCreate(SimEvent ev) {
		int[] data = (int[]) ev.getData();
		int datacenterId = data[0];
		int vmId = data[1];
		int result = data[2];

		if (result == CloudSimTags.TRUE) {
			getVmsToDatacentersMap().put(vmId, datacenterId);
			getVmsCreatedList().add(VmList.getById(getVmList(), vmId));
			Log.printLine(CloudSim.clock() + ": " + getName() + ": VM #" + vmId
					+ " has been created in Datacenter #" + datacenterId + ", Host #"
					+ VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
			Host hostid = VmList.getById(getVmsCreatedList(), vmId).getHost();
			hostvm.put(vmId, hostid);
			//vmhost.add(VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
		} else {
			Log.printLine(CloudSim.clock() + ": " + getName() + ": Creation of VM #" + vmId
					+ " failed in Datacenter #" + datacenterId);
		}

		incrementVmsAcks();

		// all the requested VMs have been created
		if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed()) {
			submitCloudlets();
		} else {
			// all the acks received, but some VMs were not created
			if (getVmsRequested() == getVmsAcks()) {
				// find id of the next datacenter that has not been tried
				for (int nextDatacenterId : getDatacenterIdsList()) {
					if (!getDatacenterRequestedIdsList().contains(nextDatacenterId)) {
						createVmsInDatacenter(nextDatacenterId);
						return;
					}
				}

				// all datacenters already queried
				if (getVmsCreatedList().size() > 0) { // if some vm were created
					submitCloudlets();
				} else { // no vms created. abort
					Log.printLine(CloudSim.clock() + ": " + getName()
							+ ": none of the required VMs could be created. Aborting");
					finishExecution();
				}
			}
		}
	}

	/**
	 * Process a cloudlet return event.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processCloudletReturn(SimEvent ev) {
		Cloudlet cloudlet = (Cloudlet) ev.getData();
		getCloudletReceivedList().add(cloudlet);
		Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet " + cloudlet.getCloudletId()
				+ " received");
		cloudletsSubmitted--;
		if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) { // all cloudlets executed
			Log.printLine(CloudSim.clock() + ": " + getName() + ": All Cloudlets executed. Finishing...");
			clearDatacenters();
			finishExecution();
		} else { // some cloudlets haven't finished yet
			if (getCloudletList().size() > 0 && cloudletsSubmitted == 0) {
				// all the cloudlets sent finished. It means that some bount
				// cloudlet is waiting its VM be created
				clearDatacenters();
				createVmsInDatacenter(0);
			}

		}
	}

	/**
	 * Overrides this method when making a new and different type of Broker. This method is called
	 * by {@link #body()} for incoming unknown tags.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processOtherEvent(SimEvent ev) {
		if (ev == null) {
			Log.printLine(getName() + ".processOtherEvent(): " + "Error - an event is null.");
			return;
		}

		Log.printLine(getName() + ".processOtherEvent(): "
				+ "Error - event unknown by this DatacenterBroker.");
	}

	/**
	 * Create the virtual machines in a datacenter.
	 * 
	 * @param datacenterId Id of the chosen PowerDatacenter
	 * @pre $none
	 * @post $none
	 */
	protected void createVmsInDatacenter(int datacenterId) {
		// send as much vms as possible for this datacenter before trying the next one
		int requestedVms = 0;
		String datacenterName = CloudSim.getEntityName(datacenterId);
		for (Vm vm : getVmList()) {
			if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
				Log.printLine(CloudSim.clock() + ": " + getName() + ": Trying to Create VM #" + vm.getId()
						+ " in " + datacenterName);
				sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
				requestedVms++;
			}
		}

		getDatacenterRequestedIdsList().add(datacenterId);

		setVmsRequested(requestedVms);
		setVmsAcks(0);
	}

	/**
	 * Submit cloudlets to the created VMs.
	 * 
	 * @pre $none
	 * @post $none
	 */
	protected void submitCloudlets() {
		int vmIndex = 0;
		List<Cloudlet> sortlist = new ArrayList<Cloudlet>();
		List<Cloudlet> sortlistt = new ArrayList<Cloudlet>();
		ArrayList<Cloudlet> templist = new ArrayList<Cloudlet>();
		ArrayList<Cloudlet> templistt = new ArrayList<Cloudlet>();
		//ArrayList<Cloudlet> templist1 = new ArrayList<Cloudlet>();
		
		for (Cloudlet cloudlet : getCloudletList()) {
			templist.add(cloudlet);
			templistt.add(cloudlet);
			templist1.add(cloudlet);
		}
		
		int templistsize= templist.size();
		for(int i=0;i<templistsize;i++){
			Cloudlet smallcloudlet = templist.get(0);
			for(Cloudlet checkcloudlet : templist){
					if(smallcloudlet.getCloudletLength()>checkcloudlet.getCloudletLength()){
						smallcloudlet=checkcloudlet;
					}
				}
			sortlist.add(smallcloudlet);
			templist.remove(smallcloudlet);
			}
			
		int templistsizee= templist1.size();
		for(int i=0;i<templistsizee;i++){
			Cloudlet smallcloudlet = templist1.get(0);
			for(Cloudlet checkcloudlet : templist1){
					if(smallcloudlet.getCloudletLength()<checkcloudlet.getCloudletLength()){
						smallcloudlet=checkcloudlet;
					}
				}
			sortlistt.add(smallcloudlet);
			templist1.remove(smallcloudlet);
			}
		Random rdbj= new Random();
		List<List<Cloudlet>> givenList = Arrays.asList(sortlist,sortlistt,templistt);
		int randomIndex = rdbj.nextInt(givenList.size());
		List<Cloudlet> randomElement = givenList.get(randomIndex);
		if(randomElement == sortlist){
			Log.printLine(" ");	
			Log.printLine("Cloudlets are selected in Increasing Length Order");	
			Log.printLine(" ");	
		}
		if(randomElement == sortlistt){
			Log.printLine(" ");	
			Log.printLine("Cloudlets are selected in Decreasing Lenght Order");	
			Log.printLine(" ");	
		}
		if(randomElement == templistt){
			Log.printLine(" ");	
			Log.printLine("Cloudlets are selected in Random Order");
			Log.printLine(" ");	
		}
		
		int count=1;
		for(Cloudlet printcloudlet: sortlistt){
			Log.printLine(count + ". Cloudlet ID: "+ printcloudlet.getCloudletId()+", Cloudlet Length:" +printcloudlet.getCloudletLength());
			count++;
		}
		//ArrayList<Float> cloudletuti = new ArrayList<Float>();
		float largest,temp,te;
	    if (sortlist != null && !sortlist.isEmpty()) {
			  Cloudlet item = sortlist.get(sortlist.size()-1);
			  //Log.printLine("Largest Cloudlet: "+ item.getCloudletLength());
			  largest= (float) item.getCloudletLength();
			  Log.printLine("Largest Cloudlet: "+ largest);
			
	    for (int i = 0; i < sortlist.size(); i++) {
	    	Cloudlet smallcloud = sortlist.get(i);
	    	//Cloudlet tp = templist1.get(i);
	    	temp= (float) smallcloud.getCloudletLength();
	    	int idd= (int) smallcloud.getCloudletId();
	    	te= temp/largest;
	    	//tp.setCloudletLength((long) te);
	    	cloudletuti.add(i, te);
	    	//Log.printLine(te);
	    	//for(Cloudlet checkcloud : templist){
	        //sortlist[i] = Math.round(sortlist[i] / largest);
	       // num.set(i,Integer.toString((Integer.parseInt(num.get(i))*2)));
	       // templist.set(i, (Cloudlet));
	    	//}
	    }
	    }
	    //cloudletuti.values();
	    for(Float printcloudlet: cloudletuti){
		//	Log.printLine("Cloudlet Utilization:" +dft.format(printcloudlet));
		//	count++;
		}
	    //Log.printLine((Arrays.toString(cloudletuti));
		
		for (Cloudlet cloudlet : randomElement) {
			Vm vm;
			// if user didn't bind this cloudlet and it has not been executed yet
			if (cloudlet.getVmId() == -1) {
				vm = getVmsCreatedList().get(vmIndex);
			} else { // submit to the specific vm
				vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
				if (vm == null) { // vm was not created
					Log.printLine(CloudSim.clock() + ": " + getName() + ": Postponing execution of cloudlet "
							+ cloudlet.getCloudletId() + ": bount VM not available");
					continue;
				}
			}

		//	Log.printLine(CloudSim.clock() + ": " + getName() + ": Sending cloudlet "
		//			+ cloudlet.getCloudletId() + " to VM #" + vm.getId());
			//if (vm.getHost().getId()==0){
			//cloudlet.setVmId(vm.getId());
			//sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
			//}
			//if(vm.getHost().getId()==1){
			cloudlet.setVmId(vm.getId());
			sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);//}			
			cloudletsSubmitted++;
			vmIndex = (vmIndex + 1) % getVmsCreatedList().size();				
			getCloudletSubmittedList().add(cloudlet);				
		}

		// remove submitted cloudlets from waiting list
		for (Cloudlet cloudlet : getCloudletSubmittedList()) {
			getCloudletList().remove(cloudlet);
		}
	}
	public void calculatePower(){
		List<Cloudlet> cloudletec = getCloudletReceivedList();
		Cloudlet cloudlet;
		
		//cloudlet.
		float[] ut = new float[cloudletuti.size()];
		float[] cpc = new float[cloudletec.size()];
		float[] cec = new float[cloudletec.size()];
		for (int i = 0; i < cloudletuti.size(); i++) {
		float tp = cloudletuti.get(i);
    	//long temp= (long) tp.getCloudletLength();
		cloudlet = cloudletec.get(i);
		float ectime= (float) cloudlet.getActualCPUTime();
			for(Host ht : hostvm.values())
			{
				if(ht.getpowerratio() == ratioibm)// || ht.getpowerratio() == ratiohp)
				{		
					//Log.printLine(min);		
					//Log.printLine(max);				
					float di = min/max;
					float uti = min + (1-di)*max*tp;
					ut[i]=uti/1000;
					//float kw= uti/1000;
					//Log.printLine(uti);
					float dii =uti*ectime;
					float fii =dii/3600;
					cec[i]= fii;
					float mii =(float) (fii*53.91);//cents per kWh
					cpc[i]= mii/100;// dolar per kWh
				}
			
				if(ht.getpowerratio() == ratiohp)
				{		
					//Log.printLine(min);		
					//Log.printLine(max);				
					float di = minhp/maxhp;
					float uti = minhp + (1-di)*maxhp*tp;
					ut[i]=uti/1000;
					//float kw= uti/1000;
					//Log.printLine(uti);
					float dii =uti*ectime;
					float fii =dii/3600;
					cec[i]= fii;
					float mii =(float) (fii*53.91);//cents per kWh
					cpc[i]= mii/100;// dolar per kWh
				}
				
				if(ht.getpowerratio() == ratiohp2)
				{		
					//Log.printLine(min);		
					//Log.printLine(max);				
					float di = minhp2/maxhp2;
					float uti = minhp2 + (1-di)*maxhp2*tp;
					ut[i]=uti/1000;
					//float kw= uti/1000;
					//Log.printLine(uti);
					float dii =uti*ectime;
					float fii =dii/3600;
					cec[i]= fii;
					float mii =(float) (fii*53.91);//cents per kWh
					cpc[i]= mii/100;// dolar per kWh
				}
				
				if(ht.getpowerratio() == ratioibm2)
				{		
					//Log.printLine(min);		
					//Log.printLine(max);				
					float di = min2/max2;
					float uti = min2 + (1-di)*max2*tp;
					ut[i]=uti/1000;
					//float kw= uti/1000;
					//Log.printLine(uti);
					float dii =uti*ectime;
					float fii =dii/3600;
					cec[i]= fii;
					float mii =(float) (fii*53.91);//cents per kWh
					cpc[i]= mii/100;// dolar per kWh
				}
			}
		}
		Log.printLine("Ratio IBM Host2 " + ratioibm2);
		Log.printLine("Ratio IBM Host  " +ratioibm);
		Log.printLine("Ratio HP Host2 "  + ratiohp2);
		Log.printLine("Ratio HP Host  " +ratiohp);
		
		for(int i = 0; i < ut.length; i++){
			//Log.printLine("Cloutlet Power Consumption: "+ dft.format(ut[i])+"  Cloudlet Energy Consumption:" +dft.format(cec[i])+"  Cloudlet Cost: $" +dft.format(cpc[i]));
		}
		
		//for(int i = 0; i < cec.length; i++){
		 //   cloudlet = cloudletec.get(i);
		//	Log.printLine("Cloudlet Energy Consumption:" +dft.format(cec[i]));
			//count++;
		//}
		float temp1=0,temp=0,avg1=0,avg=0;
		float total1=0,total=0;
		for(int i = 0; i < cpc.length; i++){
			//cloudlet = cloudletec.get(i);
			temp = cpc[i];
			temp1=cec[i];
			total= total+temp;
			total1=total1+temp1;
			//Log.printLine("Cloudlet Cost: $" +dft.format(cpc[i]));
			//count++;
		}
		Log.printLine("Total Cost: $" +dft.format(total));
		avg=total/cpc.length;
		avg1=total1/cpc.length;
		Log.printLine("Average Cost: $" +dft.format(avg));
		Log.printLine("Total Energy Consumption: " +dft.format(total1));
		Log.printLine("Average Energy Consumption: " +dft.format(avg1));
	}
	
	public void calculateclid(){
		List<Cloudlet> cloudletec = getCloudletReceivedList();
		//VmList.getById(getVmsCreatedList(), vmId).getHost().getId();
		//ArrayList<Integer> VMid0 = new ArrayList<Integer>();
		//ArrayList<Integer> VMid1 = new ArrayList<Integer>();
		//List<Vm> vmlist = getVmList();
		//int vmidmax= (int) vmlist.size();
		//VmList.getById(getVmsCreatedList(), 1).getId();
		//Log.print(vmidmax);
		Cloudlet cloudlet;
		/*for (int i = 0; i < cloudletec.size(); i++) {
		cloudlet = cloudletec.get(i);
		int vmid= (int) cloudlet.getVmId();
		if(vmid==0 || vmid==1 || vmid==2 || vmid==4){
			int m= cloudlet.getCloudletId();
			VMid0.add(m);		
		}
		else{
			int m= cloudlet.getCloudletId();
			VMid1.add(m);
		}
		
		}*/
		HashMap<Integer, Integer> cloudhostt = new HashMap<Integer, Integer>();
		
		//for(int i=0;i<.size();i++){
			for (int x = 0; x < cloudletec.size(); x++) {
				cloudlet = cloudletec.get(x);
				int vmid= (int) cloudlet.getVmId();
				for(Integer key : hostvm.keySet())
                {
                    if(key.intValue() == vmid)
                    {				
					 int m= cloudlet.getCloudletId();
					 cloudhost.put(m, hostvm.get(key));
					 cloudhostt.put(m, hostvm.get(key).getId());
				    }							
		        }
			}
		
			Set set = cloudhostt.entrySet();
		      Iterator iterator = set.iterator();
		      while(iterator.hasNext()) {
		         Map.Entry mentry = (Map.Entry)iterator.next();
		      //   System.out.print("Cloudlet ID: "+ mentry.getKey() + " Host ID: ");
		      //   System.out.println(mentry.getValue());
		     }
		
		
		
		
		
		
		
		
		//Log.printLine("VMID:" +VMid0);
		//Log.printLine("VMID:" +VMid1);
		/*HashMap<Integer, Integer> cloudlethost = new HashMap<Integer, Integer>();
		for(int i = 0; i < vmhost.size(); i++){
			int x = vmhost.get(i);
			//Log.printLine("VMID:" +x);
			for(int c = 0; c < VMid0.size(); c++){			
			if(x==0){
				cloudlethost.put(VMid0.get(c), 0);				
			}}
			for(int c = 0; c < VMid1.size(); c++){	
			if(x==1){
				cloudlethost.put(VMid1.get(c), 1);				
			}}
			//count++;
		}
		//Log.printLine("VMID:" +cloudlethost.values());
		Set set = cloudlethost.entrySet();
	      Iterator iterator = set.iterator();
	      while(iterator.hasNext()) {
	         Map.Entry mentry = (Map.Entry)iterator.next();
	         System.out.print("Cloudlet ID: "+ mentry.getKey() + " Host ID: ");
	         System.out.println(mentry.getValue());
	     }*/
	}
	/**
	 * Destroy the virtual machines running in datacenters.
	 * 
	 * @pre $none
	 * @post $none
	 */
	protected void clearDatacenters() {
		for (Vm vm : getVmsCreatedList()) {
			Log.printLine(CloudSim.clock() + ": " + getName() + ": Destroying VM #" + vm.getId());
			sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.VM_DESTROY, vm);
		}

		getVmsCreatedList().clear();
	}

	/**
	 * Send an internal event communicating the end of the simulation.
	 * 
	 * @pre $none
	 * @post $none
	 */
	protected void finishExecution() {
		sendNow(getId(), CloudSimTags.END_OF_SIMULATION);
	}

	/*
	 * (non-Javadoc)
	 * @see cloudsim.core.SimEntity#shutdownEntity()
	 */
	@Override
	public void shutdownEntity() {
		Log.printLine(getName() + " is shutting down...");
	}

	/*
	 * (non-Javadoc)
	 * @see cloudsim.core.SimEntity#startEntity()
	 */
	@Override
	public void startEntity() {
		Log.printLine(getName() + " is starting...");
		schedule(getId(), 0, CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);
	}

	/**
	 * Gets the vm list.
	 * 
	 * @param <T> the generic type
	 * @return the vm list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Vm> List<T> getVmList() {
		return (List<T>) vmList;
	}

	/**
	 * Sets the vm list.
	 * 
	 * @param <T> the generic type
	 * @param vmList the new vm list
	 */
	protected <T extends Vm> void setVmList(List<T> vmList) {
		this.vmList = vmList;
	}

	/**
	 * Gets the cloudlet list.
	 * 
	 * @param <T> the generic type
	 * @return the cloudlet list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cloudlet> List<T> getCloudletList() {
		return (List<T>) cloudletList;
	}

	/**
	 * Sets the cloudlet list.
	 * 
	 * @param <T> the generic type
	 * @param cloudletList the new cloudlet list
	 */
	protected <T extends Cloudlet> void setCloudletList(List<T> cloudletList) {
		this.cloudletList = cloudletList;
	}

	/**
	 * Gets the cloudlet submitted list.
	 * 
	 * @param <T> the generic type
	 * @return the cloudlet submitted list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cloudlet> List<T> getCloudletSubmittedList() {
		return (List<T>) cloudletSubmittedList;
	}

	/**
	 * Sets the cloudlet submitted list.
	 * 
	 * @param <T> the generic type
	 * @param cloudletSubmittedList the new cloudlet submitted list
	 */
	protected <T extends Cloudlet> void setCloudletSubmittedList(List<T> cloudletSubmittedList) {
		this.cloudletSubmittedList = cloudletSubmittedList;
	}

	/**
	 * Gets the cloudlet received list.
	 * 
	 * @param <T> the generic type
	 * @return the cloudlet received list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cloudlet> List<T> getCloudletReceivedList() {
		return (List<T>) cloudletReceivedList;
	}

	/**
	 * Sets the cloudlet received list.
	 * 
	 * @param <T> the generic type
	 * @param cloudletReceivedList the new cloudlet received list
	 */
	protected <T extends Cloudlet> void setCloudletReceivedList(List<T> cloudletReceivedList) {
		this.cloudletReceivedList = cloudletReceivedList;
	}

	/**
	 * Gets the vm list.
	 * 
	 * @param <T> the generic type
	 * @return the vm list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Vm> List<T> getVmsCreatedList() {
		return (List<T>) vmsCreatedList;
	}

	/**
	 * Sets the vm list.
	 * 
	 * @param <T> the generic type
	 * @param vmsCreatedList the vms created list
	 */
	protected <T extends Vm> void setVmsCreatedList(List<T> vmsCreatedList) {
		this.vmsCreatedList = vmsCreatedList;
	}

	/**
	 * Gets the vms requested.
	 * 
	 * @return the vms requested
	 */
	protected int getVmsRequested() {
		return vmsRequested;
	}

	/**
	 * Sets the vms requested.
	 * 
	 * @param vmsRequested the new vms requested
	 */
	protected void setVmsRequested(int vmsRequested) {
		this.vmsRequested = vmsRequested;
	}

	/**
	 * Gets the vms acks.
	 * 
	 * @return the vms acks
	 */
	protected int getVmsAcks() {
		return vmsAcks;
	}

	/**
	 * Sets the vms acks.
	 * 
	 * @param vmsAcks the new vms acks
	 */
	protected void setVmsAcks(int vmsAcks) {
		this.vmsAcks = vmsAcks;
	}

	/**
	 * Increment vms acks.
	 */
	protected void incrementVmsAcks() {
		vmsAcks++;
	}

	/**
	 * Gets the vms destroyed.
	 * 
	 * @return the vms destroyed
	 */
	protected int getVmsDestroyed() {
		return vmsDestroyed;
	}

	/**
	 * Sets the vms destroyed.
	 * 
	 * @param vmsDestroyed the new vms destroyed
	 */
	protected void setVmsDestroyed(int vmsDestroyed) {
		this.vmsDestroyed = vmsDestroyed;
	}

	/**
	 * Gets the datacenter ids list.
	 * 
	 * @return the datacenter ids list
	 */
	protected List<Integer> getDatacenterIdsList() {
		return datacenterIdsList;
	}

	/**
	 * Sets the datacenter ids list.
	 * 
	 * @param datacenterIdsList the new datacenter ids list
	 */
	protected void setDatacenterIdsList(List<Integer> datacenterIdsList) {
		this.datacenterIdsList = datacenterIdsList;
	}

	/**
	 * Gets the vms to datacenters map.
	 * 
	 * @return the vms to datacenters map
	 */
	protected Map<Integer, Integer> getVmsToDatacentersMap() {
		return vmsToDatacentersMap;
	}

	/**
	 * Sets the vms to datacenters map.
	 * 
	 * @param vmsToDatacentersMap the vms to datacenters map
	 */
	protected void setVmsToDatacentersMap(Map<Integer, Integer> vmsToDatacentersMap) {
		this.vmsToDatacentersMap = vmsToDatacentersMap;
	}

	/**
	 * Gets the datacenter characteristics list.
	 * 
	 * @return the datacenter characteristics list
	 */
	protected Map<Integer, DatacenterCharacteristics> getDatacenterCharacteristicsList() {
		return datacenterCharacteristicsList;
	}

	/**
	 * Sets the datacenter characteristics list.
	 * 
	 * @param datacenterCharacteristicsList the datacenter characteristics list
	 */
	protected void setDatacenterCharacteristicsList(
			Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsList) {
		this.datacenterCharacteristicsList = datacenterCharacteristicsList;
	}

	/**
	 * Gets the datacenter requested ids list.
	 * 
	 * @return the datacenter requested ids list
	 */
	protected List<Integer> getDatacenterRequestedIdsList() {
		return datacenterRequestedIdsList;
	}

	/**
	 * Sets the datacenter requested ids list.
	 * 
	 * @param datacenterRequestedIdsList the new datacenter requested ids list
	 */
	protected void setDatacenterRequestedIdsList(List<Integer> datacenterRequestedIdsList) {
		this.datacenterRequestedIdsList = datacenterRequestedIdsList;
	}

}
