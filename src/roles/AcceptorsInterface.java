package roles;

import requests.AcceptRequest;
import responses.PromiseResponse;

public interface AcceptorsInterface {
	public void promiseTransaction(PromiseResponse promise);
	public void acceptProposal(AcceptRequest acceptedProposal);
}
