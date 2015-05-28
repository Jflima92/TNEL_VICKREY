package Logic;

public class Request extends CloneableObject {



	public AuctionAgent ba;

	
	public Request(AuctionAgent ba)
	{
		this.ba = ba;


	}
	


	public Request clone() {
		return (Request) super.clone();
	}
}
