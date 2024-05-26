package com.s8.core.db.copper;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.CloneBranchS8Request;
import com.s8.api.flow.repository.requests.CommitBranchS8Request;
import com.s8.api.flow.repository.requests.CreateRepositoryS8Request;
import com.s8.api.flow.repository.requests.ForkBranchS8Request;
import com.s8.api.flow.repository.requests.ForkRepositoryS8Request;
import com.s8.api.flow.repository.requests.GetBranchMetadataS8Request;
import com.s8.api.flow.repository.requests.GetRepositoryMetadataS8Request;
import com.s8.core.arch.magnesium.db.MgDbSwitcher;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.SiliconEngine;
import com.s8.core.bohr.neodymium.codebase.NdCodebase;
import com.s8.core.bohr.neodymium.repository.NdRepository;
import com.s8.core.db.copper.io.IOModule;
import com.s8.core.db.copper.io.PathComposer;
import com.s8.core.db.copper.io.RepoStore;
import com.s8.core.db.copper.operators.CloneBranchOp;
import com.s8.core.db.copper.operators.CommitBranchOp;
import com.s8.core.db.copper.operators.CreateRepositoryOp;
import com.s8.core.db.copper.operators.ForkBranchOp;
import com.s8.core.db.copper.operators.ForkRepositoryOp;
import com.s8.core.db.copper.operators.GetBranchMetadataOp;
import com.s8.core.db.copper.operators.GetRepoMetadataOp;
import com.s8.core.io.json.JSON_Lexicon;
import com.s8.core.io.json.types.JSON_CompilingException;
import com.s8.core.io.json.utilities.JOOS_BufferedFileWriter;


/**
 * 
 * @author pc
 *
 */
public class CuRepoDB extends MgDbSwitcher<NdRepository> {


	public final NdCodebase codebase;
	
	/**
	 * 
	 * @param ng
	 * @param codebase
	 * @param rootFolderPath
	 * @param isSaved
	 * @throws JSON_CompilingException
	 */
	public CuRepoDB(SiliconEngine ng, NdCodebase codebase, Path rootFolderPath) throws JSON_CompilingException {
		super(ng, new PathComposer(rootFolderPath), new IOModule());
		this.codebase = codebase;
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
		new CreateRepositoryOp(this, t, initiator, callback, request).process();
	}



	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkRepository(long t, S8User initiator, SiliconChainCallback callback, ForkRepositoryS8Request request) {
		new ForkRepositoryOp(this, t, initiator, callback, request).process();
	}



	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void forkBranch(long t, S8User initiator, SiliconChainCallback callback, ForkBranchS8Request request) {
		new ForkBranchOp(this, t, initiator, callback, request).process();
	}


	/**
	 * 
	 * @param onSucceed
	 * @param onFailed
	 */
	public void commitBranch(long t, S8User initiator, SiliconChainCallback callback, CommitBranchS8Request request) {
		new CommitBranchOp(this, t, initiator, callback, request).process();
	}




	/**
	 * 
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public void cloneBranch(long t, S8User initiator, SiliconChainCallback callback, CloneBranchS8Request request) {
		new CloneBranchOp(this, t, initiator, callback, request).process();
	}


	/**
	 * 
	 * @param pre
	 * @param post
	 * @return 
	 */
	public void getRepositoryMetadata(long t,  S8User initiator, SiliconChainCallback callback, 
			GetRepositoryMetadataS8Request request) {
		new GetRepoMetadataOp(this, t, initiator, callback, request).process();
	}


	/**
	 * 
	 * @param pre
	 * @param post
	 * @return 
	 */
	public void getBranchMetadata(long t,  S8User initiator, SiliconChainCallback callback, 
			GetBranchMetadataS8Request request) {
		new GetBranchMetadataOp(this, t, initiator, callback, request).process();
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
