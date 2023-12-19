package com.s8.core.db.copper.entry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.db.copper.branch.MgBranchHandler;




public class MgRepository {
	
	
	
	/*
	public static MgRepository create(SiliconEngine ng, MgRepoStore store, String address) {
		Path path = store.composeRepositoryPath(address);
		MgRepository repository = new MgRepository(address, path);
		
		MgBranchHandler branchHandler = MgBranchHandler.create(ng, store, repository, "Default (prime) branch");
		repository.branchHandlers.put(branchHandler.getIdentifier(), branchHandler);
		
		return repository;
	}
	*/
	
	
	
	public final MgRepositoryMetadata metadata;
	
	public final Path path;

	public final Map<String, MgBranchHandler> branchHandlers = new HashMap<>();
	
	
	
	/**
	 * 
	 */
	public MgRepository(MgRepositoryMetadata metadata, Path path) {
		super();
		this.metadata = metadata;
		this.path = path;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getAddress() {
		return metadata.address;
	}

	
	public List<H3MgHandler<?>> crawl() {
		List<H3MgHandler<?>> subHandlers = new ArrayList<>();
		branchHandlers.forEach((k, branch) -> subHandlers.add(branch));
		return subHandlers;
	}


	public Path getFolderPath() {
		return path;
	}
	
	
	
	


	
}