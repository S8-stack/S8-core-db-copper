package com.s8.core.db.copper.store;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.s8.api.flow.repository.objects.RepoS8Object;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.bohr.neodymium.codebase.NdCodebase;
import com.s8.core.db.copper.entry.MgRepositoryHandler;
import com.s8.core.io.json.types.JSON_CompilingException;


/**
 * 
 * @author pierreconvert
 *
 */
public class RepoMgStore {
	

	public final static String METADATA_FILENAME = "store-meta.js";
	
	
	public final RepoMgDatabase handler;
	
	public final NdCodebase codebase;
	
	public final RepoMgStoreMetadata metadata;
	
	
	
	private Path rootPath;
	
	public final MgPathComposer repoPathComposer;
	
	private final Map<String, MgRepositoryHandler> repositoryHandlers = new HashMap<>();
	
	
	public RepoMgStore(RepoMgDatabase handler, NdCodebase codebase, RepoMgStoreMetadata metadata) {
		super();
		this.handler = handler;
		this.codebase = codebase;
		
		this.metadata = metadata;
		String rootPathname = metadata.rootPathname;
		this.rootPath = Path.of(rootPathname);
		this.repoPathComposer = new MgPathComposer(rootPath);
	}
	
	
	
	
	/**
	 * 
	 * @param repositoryAddress
	 * @return
	 * @throws JSON_CompilingException 
	 * @throws IOException 
	 */
	
	MgRepositoryHandler getRepositoryHandler(String repositoryAddress) 
			throws JSON_CompilingException, IOException {
		MgRepositoryHandler repoHandler = repositoryHandlers.get(repositoryAddress);
		if(repoHandler != null) {
			return repoHandler;
		}
		else {
			Path dataPath = repoPathComposer.composePath(repositoryAddress);
			boolean isExisting = dataPath.toFile().exists();
			if(isExisting) {
				repoHandler = new MgRepositoryHandler(handler.ng, this, repositoryAddress, true);
				repositoryHandlers.put(repositoryAddress, repoHandler);
				return repoHandler;
			}
			else {
				return null;
			}
		}
	}
	
	
	/**
	 * 
	 * @param repositoryAddress
	 * @return
	 * @throws JSON_CompilingException
	 * @throws IOException
	 */
	MgRepositoryHandler createRepositoryHandler(String repositoryAddress) 
			throws JSON_CompilingException, IOException {
		MgRepositoryHandler repoHandler = repositoryHandlers.get(repositoryAddress);
		if(repoHandler != null) {
			return null;
		}
		else {
			Path dataPath = repoPathComposer.composePath(repositoryAddress);
			boolean hasAlreadyBeenCreated = dataPath.toFile().exists();
			if(!hasAlreadyBeenCreated) {
				repoHandler = new MgRepositoryHandler(handler.ng, this, repositoryAddress, false);
				repositoryHandlers.put(repositoryAddress, repoHandler);
				return repoHandler;
			}
			else {
				return null;
			}
		}
	}
	
	
	
	
	/*
	private void JOOS_init() {
		try {
			mapLexicon = JOOS_Lexicon.from(MgRepositoryHandler.class);
		} 
		catch (JOOS_CompilingException e) {
			e.printStackTrace();
		}
	}
	*/
	
	
	public Path getRootPath() {
		return rootPath;
	}
	
	public Path composeRepositoryPath(String address) {
		return repoPathComposer.composePath(address);
	}
	

	public NdCodebase getCodebase() {
		return codebase;
	}
	
	
	/**
	 * 
	 * @param id
	 * @param name
	 */
	public void createRepository(String id, String name) {
		
	}

	public void commit(String repositoryId, String branchId, RepoS8Object[] objects) {
		
	}
	
	
	

	
	



	public List<H3MgHandler<?>> crawl() {
		List<H3MgHandler<?>> subHandlers = new ArrayList<>();
		repositoryHandlers.forEach((k, repo) -> subHandlers.add(repo));
		return subHandlers;
	}
	
}
