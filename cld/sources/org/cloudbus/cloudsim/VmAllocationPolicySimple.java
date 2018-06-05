/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.core.CloudSim;

/**
 * VmAllocationPolicySimple is an VmAllocationPolicy that chooses, as the host for a VM, the host
 * with less PEs in use.
 * 
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class VmAllocationPolicySimple extends VmAllocationPolicy {

	/** The vm table. */
	private Map<String, Host> vmTable;

	/** The used pes. */
	private Map<String, Integer> usedPes;

	/** The free pes. */
	private List<Integer> freePes;
	public List<Integer> hostcores;

	/**
	 * Creates the new VmAllocationPolicySimple object.
	 * 
	 * @param list the list
	 * @pre $none
	 * @post $none
	 */
	public VmAllocationPolicySimple(List<? extends Host> list) {
		super(list);

		setFreePes(new ArrayList<Integer>());
		for (Host host : getHostList()) {
			getFreePes().add(host.getNumberOfPes());
			//gethostcore().add(host.getNumberOfPes());

		}
		//Log.print(freePes.get(0));
		//Log.print(freePes.get(1));
		setVmTable(new HashMap<String, Host>());
		setUsedPes(new HashMap<String, Integer>());
		//int x = hostcores.size();
		//Log.printLine("No of free cores " +x);
		
	}
	
	/**
	 * Allocates a host for a given VM.
	 * 
	 * @param vm VM specification
	 * @return $true if the host could be allocated; $false otherwise
	 * @pre $none
	 * @post $none
	 */
	
	float ibm = DatacenterBroker.ratioibm;
	float hp = DatacenterBroker.ratiohp;
	//Host hostt = new Host();
	//int y = DatacenterBroker.vmhost.;
	//int host1=freePes.get(0);
	//int host2=freePes.get(1);
	
	@Override
	public boolean allocateHostForVm(Vm vm) {
		Host host1 = null;		
		int peCount;
		int vmCount;
		boolean result = false;		
		if (!getVmTable().containsKey(vm.getUid())) {
		for(Host host: getHostList()){
			if(host.isFailed()==true){
				System.out.println("Host is in failed state");
				continue;
			}
			host1 = host;		
			peCount = host1.getNumberOfPes();
			vmCount = host1.getVmList().size();
			//Log.printLine(vmCount);
			if(peCount > vmCount){
				if(host1.vmCreate(vm)){
				vmTable.put(vm.getUid(), host1);
                return true;}
				//HostVMMapping.sethostVmMapTable(host1, vm);						
				//Log.formatLine("%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host1.getId(),
						//CloudSim.clock());
			}
		}
		
		
		//Log.printLine("Ratio IBM Host " +ibm);
		//Log.printLine("Ratio HP Host " +hp);
		//int requiredPes = vm.getNumberOfPes();
		//boolean result = false;
		//int tries = 0;
		
		//List<Integer> freePesTmp = new ArrayList<Integer>();
		//for (Integer freePes : getFreePes()) {
		//	freePesTmp.add(freePes);
		//}

		/*if (!getVmTable().containsKey(vm.getUid())) { // if this vm was not created
			do {// we still trying until we find a host or until we try all of them
				int moreFree = Integer.MIN_VALUE;
				int idx = -1;

				// we want the host with less pes in use
				/*for (int i = 0; i < freePesTmp.size(); i++) {
					if (freePesTmp.get(i) > moreFree) {
						moreFree = freePesTmp.get(i);
						idx = i;
					}
				}*/
				//for(int i = 0; i < getHostList().size(); i++){
					//int x = (int) getHostList().get(i);
					
					//Log.printLine("No of free cores " +x);
				  //  if (hp > ibm){
				  /*  	for (int m = 0; m < 2; m++) {
							if (freePesTmp.get(m) > moreFree) {
								moreFree = freePesTmp.get(m);
								//Log.print(freePesTmp.get(0));
								
								idx = m;
								Log.printLine("1");
								//Log.print("idxvalue:"+idx+" ");
							}
				    	}*/
				//    }
				    //else{
				    	/*for (int m = 0; m < 2; m++) {
							if (freePesTmp.get(1) > moreFree) {
								moreFree = freePesTmp.get(1);
								idx = 1;
							}
				    	}*/
				  //  }
				//}
				//Host host = getHostList().get(idx);
				
			//	result = host.vmCreate(vm);

			/*	if (result) { // if vm were succesfully created in the host
					getVmTable().put(vm.getUid(), host);
					getUsedPes().put(vm.getUid(), requiredPes);
					getFreePes().set(idx, getFreePes().get(idx) - requiredPes);
					result = true;
					break;
				} else {
			//		freePesTmp.set(idx, Integer.MIN_VALUE);
			//	}
			//	tries++;
		//	} while (!result && tries < getFreePes().size());
			
		}
		Collections.sort(getHostList(), new Comparator<Host>() {
            @Override
            public int compare(Host h1, Host h2) {
        		return (int)(h2.getAvailableMips() - h1.getAvailableMips());
            }
        });*/
    	
    	
       /* for (Host h : getHostList()) {
            if (h.vmCreate(vm)) {
                //track the host
                vmTable.put(vm.getUid(), h);
                return true;
            }
        }*/
		//return false;
		//if(result){
		//	getVmTable().put(vm.getUid(), host1);			
		//}
	//	else{
			//if(RunTimeConstants.test == true){
			//	System.out.println("An another node need to be provisioned");
			//}
			//host1 = provisionNode();
			//result = host1.vmCreate(vm);	
			//HostVMMapping.sethostVmMapTable(host1, vm);						
		//	Log.formatLine("%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host1.getId(),CloudSim.clock());
			//ReliabilityCalculator(host1, vm);
			//getVmTable().put(vm.getUid(), host1);			
	//	}
	}
		return false;
	}

	/**
	 * Releases the host used by a VM.
	 * 
	 * @param vm the vm
	 * @pre $none
	 * @post none
	 */
	@Override
	public void deallocateHostForVm(Vm vm) {
		/*Host host = getVmTable().remove(vm.getUid());
		int idx = getHostList().indexOf(host);
		int pes = getUsedPes().remove(vm.getUid());
		if (host != null) {
			host.vmDestroy(vm);
			getFreePes().set(idx, getFreePes().get(idx) + pes);
		}*/
		vmTable.get(vm.getUid()).vmDestroy(vm);
	}

	/**
	 * Gets the host that is executing the given VM belonging to the given user.
	 * 
	 * @param vm the vm
	 * @return the Host with the given vmID and userID; $null if not found
	 * @pre $none
	 * @post $none
	 */
	@Override
	public Host getHost(Vm vm) {
		return getVmTable().get(vm.getUid());
	}

	/**
	 * Gets the host that is executing the given VM belonging to the given user.
	 * 
	 * @param vmId the vm id
	 * @param userId the user id
	 * @return the Host with the given vmID and userID; $null if not found
	 * @pre $none
	 * @post $none
	 */
	@Override
	public Host getHost(int vmId, int userId) {
		return getVmTable().get(Vm.getUid(userId, vmId));
	}

	/**
	 * Gets the vm table.
	 * 
	 * @return the vm table
	 */
	public Map<String, Host> getVmTable() {
		return vmTable;
	}

	/**
	 * Sets the vm table.
	 * 
	 * @param vmTable the vm table
	 */
	protected void setVmTable(Map<String, Host> vmTable) {
		this.vmTable = vmTable;
	}

	/**
	 * Gets the used pes.
	 * 
	 * @return the used pes
	 */
	protected Map<String, Integer> getUsedPes() {
		return usedPes;
	}

	/**
	 * Sets the used pes.
	 * 
	 * @param usedPes the used pes
	 */
	protected void setUsedPes(Map<String, Integer> usedPes) {
		this.usedPes = usedPes;
	}

	/**
	 * Gets the free pes.
	 * 
	 * @return the free pes
	 */
	protected List<Integer> getFreePes() {
		return freePes;
	}

	protected List<Integer> gethostcore() {
		return hostcores;
	}

	/**
	 * Sets the free pes.
	 * 
	 * @param freePes the new free pes
	 */
	protected void setFreePes(List<Integer> freePes) {
		this.freePes = freePes;
	}

	/*
	 * (non-Javadoc)
	 * @see cloudsim.VmAllocationPolicy#optimizeAllocation(double, cloudsim.VmList, double)
	 */
	public static Object optimizeAllocation() {
        return null;
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vms) {
    	
    	List<Map<String, Object>> map = new ArrayList<Map<String, Object>>();
    	
    	Set<Host> morePowerfulHosts = new HashSet<Host>();
    	List<Vm> unAllocatedVMs = new ArrayList<Vm>();
    	unAllocatedVMs.addAll(vms);
    	
    	for(Vm v : vms)
    		morePowerfulHosts.add(v.getHost());
    	
    	Set<Host> lessPowerfulHosts = new HashSet<Host>();
    	lessPowerfulHosts.addAll(getHostList());
    	lessPowerfulHosts.removeAll(morePowerfulHosts);
    	
    	Map<Host, Double> hostAvailableMips = new HashMap<Host, Double>();
    	for(Host h : lessPowerfulHosts)
    		hostAvailableMips.put(h, h.getAvailableMips());
    	
    	for(Host h : lessPowerfulHosts)
    	{
    		List<Vm> removeVMs = new ArrayList<Vm>();
    		for(Vm v : unAllocatedVMs)
    		{
    			if(hostAvailableMips.get(h) > v.getMips())
    			{
    				Map<String, Object> m1 = new HashMap<String, Object>();
    				m1.put("vm", v);
    				m1.put("host", h);
    				map.add(m1);
    				removeVMs.add(v);
    				hostAvailableMips.put(h, hostAvailableMips.get(h) - v.getMips());
    			}
    			
    		}
    		unAllocatedVMs.removeAll(removeVMs);
    	}
    	return map;
    }

	/*
	 * (non-Javadoc)
	 * @see org.cloudbus.cloudsim.VmAllocationPolicy#allocateHostForVm(org.cloudbus.cloudsim.Vm,
	 * org.cloudbus.cloudsim.Host)
	 */
	@Override
	public boolean allocateHostForVm(Vm vm, Host host) {
		if (host.vmCreate(vm)) { // if vm has been succesfully created in the host
			getVmTable().put(vm.getUid(), host);

			//int requiredPes = vm.getNumberOfPes();
			//int idx = getHostList().indexOf(host);
			//getUsedPes().put(vm.getUid(), requiredPes);
			//getFreePes().set(idx, getFreePes().get(idx) - requiredPes);

			Log.formatLine(
					"%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host.getId(),
					CloudSim.clock());
			return true;
		}

		return false;
	}
}
