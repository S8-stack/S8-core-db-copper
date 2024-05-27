package com.s8.core.db.copper.operators;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.ForkRepositoryS8Request;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.db.copper.CuRepoDB;

/**
 * 
 * @author pierreconvert
 *
 */
public class ForkRepositoryOp extends CuDbOperation {



	
	public final ForkRepositoryS8Request request;



	/**
	 * 
	 * @param db
	 * @param t
	 * @param initiator
	 * @param callback
	 * @param request
	 */
	public ForkRepositoryOp(CuRepoDB db, long t, S8User initiator, SiliconChainCallback callback,
			ForkRepositoryS8Request request) {
		super(db, t, initiator, callback);
		this.request = request;
	}



	public void process() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	/**
	public void process() {
		db.processRequest(new AccessMgRequest<NdRepository>(t, request.originRepoAddress, false) {
			public @Override MthProfile profile() { return MthProfile.FX0; }
			public @Override String describe() { return "Retrieving origin repository"; }

			@Override
			public boolean onResourceAccessed(Path path, MgResourceStatus status, NdRepository originRepository) {
				

				NdRepositoryMetadata tgRepoMetadata = originRepository.metadata.shallowClone();
				tgRepoMetadata.name = request.targetRepositoryName;
				tgRepoMetadata.info = request.targetRepositoryInfo;
				tgRepoMetadata.branches = new HashMap<>();
				
				NdRepository targetRepository = new NdRepository(tgRepoMetadata);



				NdBranch originBranch = originRepository.branches.get(request.originBranchId);
				if(originBranch != null) {

					// define a new (main) branch 
					NdBranchMetadata targetBranchMetadata = new NdBranchMetadata();
					targetBranchMetadata.name = request.originBranchId;
					targetBranchMetadata.info = "FORK from "+request.originBranchId+"["+request.originBranchVersion+"]";
					targetBranchMetadata.headVersion = 0L;
					targetBranchMetadata.forkedBranchId = request.originBranchId;
					targetBranchMetadata.forkedBranchVersion = request.originBranchVersion;
					
					tgRepoMetadata.branches.put(request.originBranchId, targetBranchMetadata);


					MgBranchHandler targetBranchHandler = new MgBranchHandler(
							handler.ng, 
							store, 
							repository, 
							targetBranchMetadata, 
							false); // just created 
					
					targetRepository.branchHandlers.put(request.originBranchId, targetBranchHandler);

					originBranchHandler.forkRepo(timeStamp, initiator, callback, targetBranchHandler, request);
				
				
				return false;
			}
		});	
	}
	
	
	public ForkRepositoryOutput forkRespository(NdRepository originRepository, 
			long t, S8User initiator, ForkRepositoryS8Request request) {
		
		

		}
		else {
			
		}
		
	}
	
	public static class ForkRepositoryOutput {
		public final NdRepository repository;
		public final long version;
		public ForkRepositoryOutput(NdRepository repository, long version) {
			super();
			this.repository = repository;
			this.version = version;
		}
	}
	

	@Override
	public ConsumeResourceMgAsyncTask<RepoStore> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<RepoStore>(storeHandler) {


			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CREATE-REPO for "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(RepoStore store) throws JSON_CompilingException, IOException {

				MgRepositoryHandler originRepoHandler = store.getRepositoryHandler(request.originRepoAddress);
				if(originRepoHandler != null) {
					
					MgRepositoryHandler targetRepoHandler = store.createRepositoryHandler(request.targetRepositoryAddress);
					if(targetRepoHandler != null) {
						originRepoHandler.forkRepo(timeStamp, initiator, callback, targetRepoHandler, request);
					}	
					else {
						request.onResponded(Status.TARGET_REPO_ADDRESS_CONFLICT, 0x0L);
						callback.call();
					}
				}
				else {
					request.onResponded(Status.ORIGIN_REPOSITORY_DOES_NOT_EXIST, 0x0L);
					callback.call();
				}
				return false;
			}

			@Override
			public void catchException(Exception exception) {
				request.onFailed(exception);
				callback.call();
			}
		};
	}
	*/

}
