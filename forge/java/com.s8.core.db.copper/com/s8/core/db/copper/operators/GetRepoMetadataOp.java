package com.s8.core.db.copper.operators;

import java.nio.file.Path;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.GetRepositoryMetadataS8Request;
import com.s8.api.flow.repository.requests.GetRepositoryMetadataS8Request.Status;
import com.s8.core.arch.silicon.SiliconChainCallback;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.arch.titanium.db.requests.AccessTiRequest;
import com.s8.core.bohr.neodymium.repository.NdRepository;
import com.s8.core.db.copper.CuRepoDB;

/**
 * 
 * @author pierreconvert
 *
 */
public class GetRepoMetadataOp extends CuDbOperation {
	
	
	public final GetRepositoryMetadataS8Request request;
	

	/**
	 * 
	 * @param storeHandler
	 * @param onSucceed
	 * @param onFailed
	 */
	public GetRepoMetadataOp(CuRepoDB db, long timestamp, S8User initiator, SiliconChainCallback callback, 
			GetRepositoryMetadataS8Request request) {
		super(db, timestamp, initiator, callback);
		this.request = request;
	}
	
	
	
	public void process() {
		db.processRequest(new AccessTiRequest<NdRepository>(t, request.address, false) {

			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "METADATA of "+request.address+ " repository";
			}


			@Override
			public boolean onProcessed(Path path, ResponseStatus status, NdRepository repository) {
				if(status == ResponseStatus.SUCCESSFULLY_ACCESSED) {
	 				request.onSucceed(Status.OK, repository.metadata);
				}
				else {
					request.onSucceed(Status.UNKNOWN_REPOSITORY, null);
				}
				
				callback.call();
				return false;
			}
		});
	}
	

}
