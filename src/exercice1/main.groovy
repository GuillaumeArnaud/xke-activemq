@Grab(group = 'com.netflix.rxjava', module = 'rxjava-groovy', version = '0.8.4') @Grab(group = "org.apache.activemq", module = "activemq-all", version = "5.8.0")
import javax.jms.Connection
import javax.jms.Session
import javax.management.ObjectName
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicLong

import static java.lang.System.nanoTime
import static java.util.concurrent.TimeUnit.NANOSECONDS
import static java.util.concurrent.TimeUnit.SECONDS

def config = new ConfigSlurper().parse(new File('../conf/properties.groovy').toURL()).exercice1

println config

int msqCnt = config.messages.count
int nbSndr = config.sender.count
int nbRcvr = config.receiver.count
String queueName = config.queuename

println "$msqCnt messages of size ${config.messages.size.mean} with $nbSndr senders and $nbRcvr receivers for queue $queueName"

AtomicLong currSndMsg = new AtomicLong(0), currRcvMsg = new AtomicLong(0)
def rcvStopped = new LinkedBlockingQueue<Long>()
def sndStopped = new LinkedBlockingQueue<Long>()

purgeQueue(queueName)

// add closeAll mixin
Session.mixin CloseableSession

nbSndr.times {
    new Thread(new Runnable() {
        @Override
        void run() {
            def sender
            try {
                sender = new Sender(config, queueName)

                long startTime = nanoTime()
                while (currSndMsg.get() < msqCnt) {
                    long m = currSndMsg.getAndIncrement()
                    sender.send(sender.create(m))
                }
                sndStopped << nanoTime() - startTime
            } finally {
                if (sender) sender.close()
            }
        }
    }).start()
}

nbRcvr.times {
    new Thread(new Runnable() {
        @Override
        void run() {
            Receiver receiver
            try {
                def stop = false

                receiver = new Receiver(config,queueName)

                def startTime = nanoTime()
                while (!stop) {
                    def msg = receiver.receive()

                    stop = msg.getBooleanProperty("poison.pill")
                    if (!stop) currRcvMsg.incrementAndGet()
                }
                rcvStopped << nanoTime() - startTime
            } finally {
                if (receiver) receiver.close()
            }
        }
    }).start()
}

new Thread(new Runnable() {

    @Override
    void run() {
        // waiting the end of all senders
        def sndElapsed = []
        nbSndr.times { sndElapsed << sndStopped.take() }
        long cumulativeElapsedTime = sndElapsed.sum() as long
        long overallElapsedTime = sndElapsed.max() as long

        println """
 end of sends:
    total elapsed time : ${NANOSECONDS.toMillis(overallElapsedTime)}ms
    cumulative total elapsed time : ${NANOSECONDS.toMillis(cumulativeElapsedTime)}ms
    total sent requests : ${currSndMsg.intValue()}
    avg send elapsed time : ${NANOSECONDS.toMillis((long) cumulativeElapsedTime / currSndMsg.get().intValue())}ms/req
 start poison pills
"""
        // send poison pills to receivers
        def sender
        try {
            sender = new Sender(config,queueName)

            def poisonPill = sender.create(0)
            poisonPill.setBooleanProperty("poison.pill" +
                    "", true)

            def rcvElapsed = []
            nbRcvr.times {
                sender.send(poisonPill)
                rcvElapsed << rcvStopped.poll(1, SECONDS)
            }

            cumulativeElapsedTime = rcvElapsed.sum() as long
            overallElapsedTime = rcvElapsed.max() as long

            println """
 end of receptions:
    total elapsed time : ${NANOSECONDS.toMillis(overallElapsedTime)}ms
    cumulative total elapsed time : ${NANOSECONDS.toMillis(cumulativeElapsedTime)}ms
    total received requests : ${currRcvMsg.intValue()}
    avg reception elapsed time : ${NANOSECONDS.toMillis((long) cumulativeElapsedTime / currRcvMsg.intValue())}ms/req
"""
        } finally {
            if (sender) sender.close()
            System.exit(0)
        }
    }
}).start()

// Utils

def purgeQueue(String host = "localhost", int port = 1099, String brokerName = "localhost", String queueName) {
    def server = JMXConnectorFactory.connect(new JMXServiceURL("service:jmx:rmi:///jndi/rmi://$host:$port/jmxrmi")).MBeanServerConnection
    def queueObjName = "org.apache.activemq:type=Broker,brokerName=$brokerName,destinationType=Queue,destinationName=$queueName"
    if (server.isRegistered(ObjectName.getInstance(queueObjName))) {
        def mbean = new GroovyMBean(server, queueObjName)
        println "purge queue $queueName:"
        println "current topic size = ${mbean.QueueSize}"
        mbean.purge()
        println "current topic size (after purge) = ${mbean.QueueSize}"
    }
}

class CloseableSession {
    Session session
    Connection connection

    public void closeAll() {
        try {
            if (session) {
                session.close()
            }
            if (connection) {
                connection.close()
            }
        } catch (Throwable t) {
            println "can't close connection"
            t.printStackTrace()
        }
    }
}




