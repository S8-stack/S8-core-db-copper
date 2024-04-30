package com.s8.core.db.copper.store;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.CloneBranchS8Request;
import com.s8.api.flow.repository.requests.CommitBranchS8Request;
import com.s8.api.flow.repository.requests.CreateRepositoryS8Request;
import com.s8.api.flow.repository.requests.ForkBranchS8Request;
import com.s8.api.flow.repository.requests.ForkRepositoryS8Request;
import com.s8.api.flow.repository.requests.GetBranchMetadataS8Request;
import com.s8.api.flow.repository.requests.GetRepositoryMetadataS8Request;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.magnesium.handlers.h3.H3MgIOModule;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.bohr.neodymium.codebase.NdCodebase;
import com.s8.core.io.json.JSON_Lexicon;
import com.s8.core.io.json.types.JSON_CompilingException;
import com.s8.core.io.json.utilities.JOOS_BufferedFileWriter;


/**
 * 
 * @author pc
 *
 */
public class CuRepoDB extends H3MgHandler<RepoStore> {

	
	public final NdCodebase codebase;
	
	public final IOModule ioModule;

	private Path rootFolderPath;
	
	
	public CuRepoDB(SiliconEngine ng, NdCodebase codebase, Path rootFolderPath, boolean isSaved) throws JSON_CompilingException {
		super(ng, isSaved);
		this.codebase = codebase;
		this.rootFolderPath = rootFolderPath;
		
		ioModule = new IOModule(this);
	}

	@Override
	public String getName() {
		return "store";
	}

	@Override
	public H3MgIOModule<RepoStore> getIOModule() {
		return ioModule;
	}
	

	@Override
	public List<H3MgHandler<?>> getSubHandlers() {
		RepoStore store = getResource();
		if(store != null) { 
			return store.crawl(); 
		}
		else {
			return new ArrayList<>();
		}
	}
	

	public Path getRootFolderPath() {
		return rootFolderPath;
	}
	
	
	public Path getMetadataPath() {
		return rootFolderPath.resolve(RepoStore.METADATA_FILENAME);
	}
	
	
	public static Path getMetadataPath(Path rootFolderPath) {
		return rootFolderPath.resolve(RepoStore.METADATA_FILENAME);
	}


	
	
	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void createRepository(long t, S8User initiator, SiliconChainCallback callback, CreateRepositoryS8Request request) {
		pushOpLast(new CreateRepoOp(t, initiator, callback, this, request));
	}
	
	
	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkRepository(long t, S8User initiator, SiliconChainCallback callback, ForkRepositoryS8Request request) {
		pushOpLast(new ForkRepoOp(t, initiator, callback, this, request));
	}
	
	
	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkBranch(long t, S8User initiator, SiliconChainCallback callback, ForkBranchS8Request request) {
		pushOpLast(new ForkBranchOp(t, initiator, callback, this, request));
	}
	
	
	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void commitBranch(long t, S8User initiator, SiliconChainCallback callback, CommitBranchS8Request request) {
		pushOpLast(new CommitBranchOp(t, initiator, callback, this, request));
	}




	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneBranch(long t, S8User initiator, SiliconChainCallback callback, CloneBranchS8Request request) {
		pushOpLast(new CloneBranchOp(t, initiator, callback, this, request));
	}

	
	/**
	 * 
	 * @param pre
	 * @param post
	 * @return 
	 */
	public void getRepositoryMetadata(long t,  S8User initiator, SiliconChainCallback callback, 
			GetRepositoryMetadataS8Request request) {
		pushOpLast(new GetRepositoryMetadataOp(t, initiator, callback, this, request));
	}
	

	/**
	 * 
	 * @param pre
	 * @param post
	 * @return 
	 */
	public void getBranchMetadata(long t,  S8User initiator, SiliconChainCallback callback, 
			GetBranchMetadataS8Request request) {
		pushOpLast(new GetBranchMetadataOp(t, initiator, callback, this, request));
	}
	
	
	
	
	/* <utilities> */
	
	public static void init(String rootFolderPathname) throws IOException, JSON_CompilingException {
		RepoStoreMetadata metadata = new RepoStoreMetadata();
		metadata.rootPathname = rootFolderPathname;
		
		Path rootFolderPath = Path.of(rootFolderPathname);
		Files.createDirectories(rootFolderPath);
		JSON_Lexicon lexicon = JSON_Lexicon.from(RepoStoreMetadata.class);
		FileChannel channel = FileChannel.open(rootFolderPath.resolve(RepoStore.METADATA_FILENAME), 
				new OpenOption[]{ StandardOpenOption.WRITE, StandardOpenOption.CREATE });
		JOOS_BufferedFileWriter writer = new JOOS_BufferedFileWriter(channel, StandardCharsets.UTF_8, 256);

		lexicon.compose(writer, metadata, "   ", false);
		writer.close();
	}
	
	
}
