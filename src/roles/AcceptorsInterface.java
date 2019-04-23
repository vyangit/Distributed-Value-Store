package roles;

import responses.AcceptedResponse;
import responses.PromiseResponse;

public interface AcceptorsInterface {
	public void promiseTransaction(PromiseResponse promise);
	public void acceptProposal(AcceptedResponse acceptedRes);
}
