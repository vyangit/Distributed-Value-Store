package roles;

import requests.AcceptRequest;
import requests.PrepareRequest;

public interface AcceptorsInterface {
	public void promiseTransaction(PrepareRequest prepareReq);
	public void acceptProposal(AcceptRequest acceptedProposal);
}
