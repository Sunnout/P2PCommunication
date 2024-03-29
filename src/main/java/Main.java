import java.net.InetAddress;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import babel.core.Babel;
import babel.core.GenericProtocol;
import network.data.Host;
import protocols.apps.BroadcastApp;
import protocols.broadcast.eagerpush.EagerPushBroadcast;
import protocols.broadcast.plumtree.PlumtreeBroadcast;
import protocols.membership.cyclon.Cyclon;
import protocols.membership.hyparview.HyParView;
import utils.InterfaceToIp;


public class Main {

    //Sets the log4j (logging library) configuration file
    static {
        System.setProperty("log4j.configurationFile", "log4j2.xml");
    }

    //Creates the logger object
    private static final Logger logger = LogManager.getLogger(Main.class);

    //Default babel configuration file (can be overridden by the "-config" launch argument)
    private static final String DEFAULT_CONF = "config.properties";

    public static void main(String[] args) throws Exception {

        //Get the (singleton) babel instance
        Babel babel = Babel.getInstance();

        //Loads properties from the configuration file, and merges them with properties passed in the launch arguments
        Properties props = Babel.loadConfig(args, DEFAULT_CONF);

        //If you pass an interface name in the properties (either file or arguments), this wil get the IP of that interface
        //and create a property "address=ip" to be used later by the channels.
        InterfaceToIp.addInterfaceIp(props);

        //The Host object is an address/port pair that represents a network host. It is used extensively in babel
        //It implements equals and hashCode, and also includes a serializer that makes it easy to use in network messages
        Host myself =  new Host(InetAddress.getByName(props.getProperty("address")),
                Integer.parseInt(props.getProperty("port")));

        logger.info("Hello, I am {}", myself);

        // Application
        BroadcastApp broadcastApp;
        GenericProtocol broadcast;
        GenericProtocol membership;
        
        int protocolCombination = Integer.parseInt(props.getProperty("dissemination_and_membership", "0"));
        switch(protocolCombination) {
        	case 0:
                broadcastApp = new BroadcastApp(myself, props, EagerPushBroadcast.PROTOCOL_ID);
                broadcast = new EagerPushBroadcast(props, myself);
                membership = new Cyclon(props, myself);
                doSomeStuff(babel, broadcastApp, broadcast, membership, props);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("Goodbye")));
                break;

        	case 1:
                broadcastApp = new BroadcastApp(myself, props, EagerPushBroadcast.PROTOCOL_ID);
                broadcast = new EagerPushBroadcast(props, myself);
                membership = new HyParView(props, myself);
                doSomeStuff(babel, broadcastApp, broadcast, membership, props);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("Goodbye")));
                break;

        	case 2:
                broadcastApp = new BroadcastApp(myself, props, PlumtreeBroadcast.PROTOCOL_ID);
                broadcast = new PlumtreeBroadcast(props, myself);
                membership = new Cyclon(props, myself);
                doSomeStuff(babel, broadcastApp, broadcast, membership, props);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> ((PlumtreeBroadcast)broadcast).printMetrics()));
                Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("Goodbye")));
                break;

        	case 3:
                broadcastApp = new BroadcastApp(myself, props, PlumtreeBroadcast.PROTOCOL_ID);
                broadcast = new PlumtreeBroadcast(props, myself);
                membership = new HyParView(props, myself);
                doSomeStuff(babel, broadcastApp, broadcast, membership, props);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> ((PlumtreeBroadcast)broadcast).printMetrics()));
                Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("Goodbye")));
                break;

        	default:
        		broadcastApp = new BroadcastApp(myself, props, EagerPushBroadcast.PROTOCOL_ID);
                broadcast = new EagerPushBroadcast(props, myself);
                membership = new Cyclon(props, myself);
                doSomeStuff(babel, broadcastApp, broadcast, membership, props);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("Goodbye")));
                break;
        }
    }
    
    private static final void doSomeStuff(Babel babel, BroadcastApp broadcastApp,
    		GenericProtocol broadcast, GenericProtocol membership, Properties props ) throws Exception {
        babel.registerProtocol(broadcastApp);
        babel.registerProtocol(broadcast);
        babel.registerProtocol(membership);

        //Init the protocols
        broadcastApp.init(props);
        broadcast.init(props);
        membership.init(props);

        //Start babel and protocol threads
        babel.start();
    }

}
