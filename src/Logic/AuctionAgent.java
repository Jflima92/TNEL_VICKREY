package Logic;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@Agent
@Service
@Description("This agent participates in auctions")
@ProvidedServices(@ProvidedService(type= IAuctionService.class))
public class AuctionAgent implements IAuctionService  {

    @Agent
    protected BDIAgent agent;
    protected ArrayList<Proposal> allProposals;
    protected Double balance;
    protected Request request;
    protected Boolean isProcessing;
    protected Integer stock;

    @Belief(updaterate=2000)
    protected long time = System.currentTimeMillis();

    @AgentCreated
    public void init() {
        this.balance = (Double)agent.getArgument("balance");
        isProcessing = false;
        allProposals = new ArrayList<Proposal>();
        stock = (Integer)agent.getArgument("stock");


    }
    @Goal(recur=true)
    public class AuctionGoal {
        public AuctionGoal() {

        }
        @GoalRecurCondition(beliefs="time")
        public boolean checkRecur() {
            if(allProposals.size() == 0)
                request = null;
            // The buyer's job is done when all required units have been purchased
            return stock != 0;
        }
    }

    @Plan(trigger=@Trigger(goals=AuctionGoal.class))
    protected void launchRequestPlan() throws InterruptedException {

        if (request != null) {
            if (!isProcessing) {
                processProposals();
            }} else {
            request = new Request( this);
            SServiceProvider.getServices(agent.getServiceProvider(), IAuctionService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new IntermediateDefaultResultListener<IAuctionService>() {
                public void intermediateResultAvailable(IAuctionService is) {

                    allProposals.clear();
                    is.requireProposal(request.clone());
                }
            });
        }


        throw new PlanFailureException();
    }


    public Proposal chooseProposal()
    {
        proposalComparator comp = new proposalComparator();
        Collections.sort(allProposals, comp);
        return allProposals.get(0);
    }

    public Proposal chooseProposal2()
    {
        proposalComparator comp = new proposalComparator();
        Collections.sort(allProposals, comp);
        return allProposals.get(1);
    }

    public class proposalComparator implements Comparator<Proposal> {

        @Override
        public int compare(Proposal o1, Proposal o2) {
            // TODO Auto-generated method stub
            if(o1.getPrice() > o2.getPrice())
            {
                return 1;
            }
            else if(o1.getPrice() == o2.getPrice())
                return 0;
            else
                return -1;

        }
    }

    @Override
    public IFuture<Boolean> requireProposal(Request r) {
        return null;
    }

    @Override
    public IFuture<Boolean> acceptedProposal(Proposal p) {
        return null;
    }

    @Override
    public IFuture<Double> negotiation(Proposal p, int count) {
        return null;
    }

    @Override
    public IFuture<Boolean> retrieveSeller() {
        return null;
    }

    @Override
    public IFuture<Boolean> sendProposal(Proposal p) {

        System.out.println("Buyer received a valid proposal, analysing...");


        allProposals.add(p);

        return new Future<Boolean>(true);

    }

    @Override
    public IFuture<Boolean> retrieveBuyer() {
        return null;
    }


    public void processProposals()
    {

        System.out.println("Processing Proposal");
        isProcessing = true;
        int count = 1;

        final Proposal chosen = chooseProposal();
        final Proposal chosen2 = chooseProposal2();


        Proposal chosenClone = chosen.clone();
        if(chosen.getSa().acceptedProposal(chosenClone).get()) {

            allProposals.remove(chosen);
            request = null;

            SServiceProvider.getServices(agent.getServiceProvider(), IManagerService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new IntermediateDefaultResultListener<IManagerService>()
            {
                @Override
                public void intermediateResultAvailable(IManagerService is) {
                    is.submitEvaluation(chosen.getSa().agent.getAgentName(), true);
                }
            });

        }
        else {
            SServiceProvider.getServices(agent.getServiceProvider(), IManagerService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new IntermediateDefaultResultListener<IManagerService>() {
                @Override
                public void intermediateResultAvailable(IManagerService is) {
                    is.submitEvaluation(chosen.getSa().agent.getAgentName(), false);
                }
            });
        }


        request = null;
        isProcessing = false;
    }



}