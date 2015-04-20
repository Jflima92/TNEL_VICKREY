import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.*;

import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.*;

/**
 * Created by jorgelima on 20-04-2015.
 */

/*@Arguments({
        @Argument(name="product", clazz=String.class, defaultvalue="N/A"),
        @Argument(name="initPrice", clazz=Double.class, defaultvalue="-1"),
        @Argument(name="initStock", clazz=Double.class, defaultvalue="-1"),
        @Argument(name="strategy", clazz=Double.class, defaultvalue="1"),
        @Argument(name="variation", clazz=Double.class, defaultvalue="5"),
        @Argument(name="negotiaton", clazz=Double.class, defaultvalue="5")
})*/
@Service
@Description("This agent sells products.")
//@ProvidedServices(@ProvidedService(type=ISellService.class))
@Agent
public class DummyAgent {

    @Agent
    protected static BDIAgent agent;

    @Belief
    protected long negotiationTime = 10000;

    @Belief(updaterate=15000)
    protected long receiveStockTime = System.currentTimeMillis();

    @Belief(updaterate=500)
    protected long time = System.currentTimeMillis();

    @Plan(trigger = @Trigger(factchangeds = "receiveStockTime"))
    public void stockArrival()
    {

    }

    @Plan(trigger=@Trigger(goals=achieveGoal.class))
    protected void basicPlan() {
        System.out.println("Executing basic plan.");
    }


    @Goal(recur=true)
    public class achieveGoal {
        public achieveGoal(int totalToFullfill) {
            totalMissingMoney = totalToFullfill;
            totalUnits = 0;
        }

        public int totalMissingMoney;
        public int totalUnits;

        @GoalRecurCondition(beliefs="time")
        public boolean checkRecur() {
            // The buyer's job is done when all required units have been purchased
            return totalMissingMoney > 0;
        }
    }

    @AgentCreated
    public void init() {
        System.out.println("My goal is to receive 3 of something");
        /*achieveGoal ach = new achieveGoal(100);
        agent.dispatchTopLevelGoal(ach);*/


    }

    @AgentBody
    public void body() {



    }



}
