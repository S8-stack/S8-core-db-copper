package com.s8.core.db.copper.operators;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.CreateRepositoryS8Request;
import com.s8.api.flow.repository.requests.CreateRepositoryS8Request.Status;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.titanium.db.requests.CreateTiRequest;
import com.s8.core.bohr.atom.S8ShellStructureException;
import com.s8.core.bohr.neodymium.branch.NdBranch;
import com.s8.core.bohr.neodymium.branch.NdBranchMetadata;
import com.s8.core.bohr.neodymium.repository.NdRepository;
import com.s8.core.bohr.neodymium.repository.NdRepositoryMetadata;
import com.s8.core.db.copper.CuRepoDB;

/**
 * 
 * @author pierreconvert
 *
 */
public class CreateRepositoryOp extends CuDbOperation {


	public final CreateRepositoryS8Request request;
	
	/**
	 * 
	 * @param handler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public CreateRepositoryOp(CuRepoDB db, long t, S8User initiator, SiliconChainCallback callback,
			CreateRepositoryS8Request request) {
		super(db, t, initiator, callback);
		this.request = request;
	}


	/**
	 * 
	 */
	public void process() {

		try {			

			/* <metadata> */
			NdRepositoryMetadata metadata = new NdRepositoryMetadata();
			metadata.name = request.repositoryName;
			metadata.address = request.repositoryAddress;
			metadata.info = request.repositoryInfo;
			metadata.owner = initiator.getUsername();
			metadata.creationDate = t;
			metadata.branches = new HashMap<>();

			/* define a new (main) branch */
			NdBranchMetadata mainBranchMetadata = new NdBranchMetadata();
			mainBranchMetadata.name = request.mainBranchName;
			mainBranchMetadata.info = "Created as MAIN branch";
			mainBranchMetadata.headVersion = 0L;
			mainBranchMetadata.forkedBranchId = null;
			mainBranchMetadata.forkedBranchVersion = 0L;
			mainBranchMetadata.owner = initiator.getUsername();
			metadata.branches.put(request.mainBranchName, mainBranchMetadata);

			metadata.nIO_hasUnsavedChanges = true;
			/* </metadata> */

			NdRepository repository = new NdRepository(metadata);

			/* <nd-branch> */
			NdBranch ndBranch = db.ioModule.createBranch(request.mainBranchName);
			long version = ndBranch.commit(request.objects, t, initiator.getUsername(), request.initialCommitComment);
			repository.branches.put(request.mainBranchName, ndBranch);
			ndBranch.nIO_hasUnsavedChanges = true;
			/* </nd-branch> */
			
			
			db.processRequest(new CreateTiRequest<NdRepository>(t, 
					request.repositoryAddress, repository, 
					request.isResourceSaved,
					request.isOverrideEnabled) {


				@Override
				public void onPathGenerated(Path path) {
					//resource.nIO_path = path;
				}


				@Override
				public void onEntryCreated(boolean isSucessful) {

					if(isSucessful) {
						request.onResponse(Status.OK, version);
					}
					else {
						request.onResponse(Status.IS_ADDRESS_CONFLICTING, version);
					}

					/* chain */
					callback.call();
				}
			});
		} 
		catch (IOException e) {
			e.printStackTrace();
			request.onFailed(e);
		} 
		catch (S8ShellStructureException e) {
			e.printStackTrace();
			request.onFailed(e);
		}
	}

}
