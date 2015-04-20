import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jorgelima on 19-04-2015.
 */
public class init {

    public static void main(String[] args) throws IOException {

        String[] defargs = new String[]
                {
                        "-gui", "false",
                        "-welcome", "false",
                        "-cli", "false",
                        "-printpass", "false"
                };
        String[] newargs = new String[defargs.length+args.length];
        System.arraycopy(defargs, 0, newargs, 0, defargs.length);
        System.arraycopy(args, 0, newargs, defargs.length, args.length);

        IComponentManagementService cms;

        IFuture<IExternalAccess> platfut = Starter.createPlatform(defargs);
        final ThreadSuspendable sus = new ThreadSuspendable();
        final IExternalAccess platform = platfut.get(sus);
        System.out.println("Started platform: " +platform.getComponentIdentifier());


        cms = SServiceProvider.getService(platform.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(sus);

        String name = "Agent " + Integer.toString(1);
        Map<String, Object> agentArgs = new HashMap<String, Object>();




        CreationInfo SellerInfo = new CreationInfo(agentArgs);

        IComponentIdentifier cid = cms.createComponent(name, "DummyAgent.class", SellerInfo).getFirstResult(sus);
    }

}
