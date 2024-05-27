package com.s8.core.db.copper.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.s8.core.arch.magnesium.db.MgIOException;
import com.s8.core.arch.magnesium.db.MgIOModule;
import com.s8.core.arch.magnesium.db.MgResourceStatus;
import com.s8.core.bohr.neodymium.branch.NdBranch;
import com.s8.core.bohr.neodymium.codebase.NdCodebase;
import com.s8.core.bohr.neodymium.io.NdIOModule;
import com.s8.core.bohr.neodymium.repository.NdRepository;
import com.s8.core.bohr.neodymium.repository.NdRepositoryMetadata;
import com.s8.core.io.json.parsing.JSON_ParsingException;
import com.s8.core.io.json.types.JSON_CompilingException;

public class IOModule implements MgIOModule<NdRepository> {
	

	public final static String METADATA_FILENAME = "repo-meta.js";
	

	/**
	 * 
	 */
	public final static String BRANCH_DATA_PATHNAME = "branch-data.nd";


	
	public final NdIOModule nd;


	/**
	 * 
	 * @param handler
	 * @throws JSON_CompilingException
	 */
	public IOModule(NdCodebase codebase) throws JSON_CompilingException {
		super();
		this.nd = new NdIOModule(codebase);
	}


	@Override
	public boolean hasResource(Path path) {
		Path dataPath = path.resolve(METADATA_FILENAME);
		return dataPath.toFile().exists();
	}

	

	@Override
	public NdRepository readResource(Path path) throws MgIOException {
		try {

			Path dataPath = path.resolve(METADATA_FILENAME);
			NdRepositoryMetadata metadata = nd.readMetadata(dataPath);
			NdRepository repository = new NdRepository(metadata);
			return repository;

		}
		catch (JSON_ParsingException e) {
			throw new MgIOException(new MgResourceStatus(0x0802, e.getMessage()));
		}
		catch (IOException e) {
			throw new MgIOException(new MgResourceStatus(0x0802, e.getMessage()));
		}	
	}
	
	
	public void writeMetadata(Path resourceFolderPath, NdRepository repository) throws IOException {
		nd.writeMetadata(repository.metadata, resourceFolderPath.resolve(METADATA_FILENAME));
	}



	@Override
	public void writeResource(Path resourceFolderPath, NdRepository repository) throws IOException {
		writeMetadata(resourceFolderPath, repository);
		for (NdBranch branch : repository.branches.values()) { 

			/* calculate path */
			Path path = resourceFolderPath.resolve(branch.id).resolve(BRANCH_DATA_PATHNAME);
			
			/* write resource */
			nd.writeBranch(branch, path);
		}
	}
	

	/**
	 * 
	 * @param id
	 * @return
	 */
	public NdBranch createBranch(String id) {
		return nd.createBranch(id);
	}

	
	/**
	 * 
	 * @param branchId
	 * @return
	 * @throws IOException
	 */
	public NdBranch getBranch(Path resourceFolderPath, NdRepository repository, String branchId) throws IOException {
		NdBranch branch = repository.branches.get(branchId);
		if(branch == null) {

			/* branch */
			branch = readBranch(resourceFolderPath, branchId, false);
			
			/* branches */
			repository.branches.put(branchId, branch);
		}
		return branch;

	}
	



	/**
	 * 
	 * @param codebase
	 * @param repoPath
	 * @param id
	 * @param isVerbose
	 * @return
	 * @throws IOException
	 */
	public NdBranch readBranch(Path repoPath, String id, boolean isVerbose) throws IOException {

		/* read from disk */
		Path path = repoPath.resolve(id).resolve(BRANCH_DATA_PATHNAME);
		
		return nd.readBranch(path, id, isVerbose);
	}

	
	


	@Override
	public boolean deleteResource(Path path) {
		try {
			clearDirectory(path);
			return true;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	public void clearDirectory(Path directory) throws IOException{
	    if (Files.exists(directory)) {
	            Files.list(directory)
	                 .forEach(
	                         path -> {
	                             try {
	                                 if (Files.isDirectory(path)) {
	                                     clearDirectory(path);
	                                 }
	                                 Files.delete(path);
	                             } catch (IOException e) {
	                                 // todo handle exception
	                             }
	                         }
	                 );
	        }
	}


	
}
