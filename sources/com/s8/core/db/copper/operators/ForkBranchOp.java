package com.s8.core.db.copper.operators;

import java.io.IOException;
import java.nio.file.Path;

import com.s8.api.flow.S8AsyncFlow;
import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.objects.RepoS8Object;
import com.s8.api.flow.repository.requests.ForkBranchS8Request;
import com.s8.api.flow.repository.requests.ForkBranchS8Request.Status;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.arch.titanium.db.MgResourceStatus;
import com.s8.core.arch.titanium.db.requests.AccessMgRequest;
import com.s8.core.bohr.atom.S8ShellStructureException;
import com.s8.core.bohr.neodymium.branch.NdBranch;
import com.s8.core.bohr.neodymium.branch.NdBranchMetadata;
import com.s8.core.bohr.neodymium.repository.NdRepository;
import com.s8.core.db.copper.CuRepoDB;

/**
 * 
 */
public class ForkBranchOp extends CuDbOperation {



	public final ForkBranchS8Request request;


	public ForkBranchOp(CuRepoDB db, long t, S8User initiator, SiliconChainCallback callback,  ForkBranchS8Request request) {
		super(db, t, initiator, callback);
		this.request = request;
	}



	public void process() {

		db.processRequest(new AccessMgRequest<NdRepository>(t, request.repositoryAddress, false) {

			@Override
			public MthProfile profile() {
				return MthProfile.IO_SSD;
			}

			@Override
			public String describe() {
				return "access branch";
			}

			@Override
			public boolean onResourceAccessed(Path resourceFolderPath, MgResourceStatus status, NdRepository repository) {
				try {
					if(status.isAvailable()) {
						if(repository.metadata.branches.containsKey(request.originBranchId)) {

							/* retrieve origin branch */
							NdBranch originBranch = db.ioModule.getBranch(resourceFolderPath, repository, request.originBranchId);


							/* standard cases */
							RepoS8Object[] objects = null;
							if(request.originBranchVersion >= 0L) {
								objects = originBranch.cloneVersion(request.originBranchVersion).exposure;
							}
							/* special cases */
							else if(request.originBranchVersion == S8AsyncFlow.HEAD_VERSION){
								objects = originBranch.cloneHead().exposure;
							}


							if(objects == null) {
								throw new IOException("Failed to retrieve objects from origin branch");
							}
							NdBranch targetBranch = db.ioModule.createBranch(request.targetBranchId);
							targetBranch.nIO_hasUnsavedChanges = true;

							/* <commit> */
							long targetVersion = targetBranch.commit(objects, 
									t, 
									initiator.getUsername(), 
									"Initial commit from FORK of "+originBranch.id);


							/* define a new branch */
							NdBranchMetadata targetBranchMetadata = new NdBranchMetadata();
							targetBranchMetadata.name = request.targetBranchId;
							targetBranchMetadata.owner = initiator.getUsername();
							targetBranchMetadata.info = "FORK from "+request.originBranchId+"["+request.originBranchVersion+"]";
							targetBranchMetadata.headVersion = 0L;
							targetBranchMetadata.forkedBranchId = request.originBranchId;
							targetBranchMetadata.forkedBranchVersion = request.originBranchVersion;

							/* add branch metadata to repo meta*/
							repository.metadata.branches.put(request.targetBranchId, targetBranchMetadata);
							repository.metadata.nIO_hasUnsavedChanges = true;

							repository.branches.put(request.targetBranchId, targetBranch);


							request.onResponded(Status.SUCCESSFULLY_FORKED, targetVersion);
							return true; /* modification occured */

						}
						else {
							request.onResponded(Status.NO_SUCH_ORIGIN_BRANCH, 0x00);
							return false; /* resource has not changed */
						}
					}
					else {
						request.onResponded(Status.REPOSITORY_DOES_NOT_EXIST, 0x00);
						return false; /* resource has not changed */
					}
				}
				catch(IOException | S8ShellStructureException exception) {
					request.onFailed(exception);
					return false; /* resource has not changed */
				}
			}
		});
	}


}
