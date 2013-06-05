#!/usr/bin/env groovy

package exercice2

@Grapes([
@Grab(group = 'com.netflix.rxjava', module = 'rxjava-groovy', version = '0.8.4'),
@Grab(group = "org.apache.activemq", module = "activemq-all", version = "5.8.0")
])
import javax.jms.Message
import javax.management.ObjectName
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicLong

import static java.lang.System.nanoTime
import static java.util.concurrent.TimeUnit.MILLISECONDS
import static java.util.concurrent.TimeUnit.NANOSECONDS


def config = new ConfigSlurper().parse(new File('conf/properties.groovy').toURL()).exercice2

println config

int msqCnt = config.messages.count
int nbSndr = config.sender.count
int nbRcvr = config.receiver.count
String queueName = config.queuename

println "$msqCnt messages of size ${config.messages.size.mean} with $nbSndr senders and $nbRcvr receivers for queue $queueName"

AtomicLong currSndMsg = new AtomicLong(0), currRcvMsg = new AtomicLong(0)
def rcvStopped = new LinkedBlockingQueue<Long>()
def sndStopped = new LinkedBlockingQueue<Long>()

// Purge the queue via JMX for each brokers
config.brokers.each { brokerId ->
    def server = JMXConnectorFactory.connect(new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:${3000 + brokerId}/jmxrmi")).MBeanServerConnection
    def queueObjName = "org.apache.activemq:type=Broker,brokerName=broker${brokerId},destinationType=Queue,destinationName=$queueName"
    def objectName = ObjectName.getInstance(queueObjName)
    if (server.isRegistered(objectName)) {
        def mbean = new GroovyMBean(server, queueObjName)
        println "purge queue $queueName on broker ${brokerId}:"
        println "current queue size = ${mbean.QueueSize}"
        mbean.purge()
        println "current queue size (after purge) = ${mbean.QueueSize}"
    } else {
        println "mbean not registered, no need to purge."
    }

}

// Create and start the threads for sending messages
nbSndr.times {
    new Thread(new Runnable() {
        @Override
        void run() {
            def sender
            try {
                sender = new Sender(config)
                def sndCounter = 0
                println "start the sender $it"
                long startTime = nanoTime()
                while (currSndMsg.get() < msqCnt) {
                    long m = currSndMsg.getAndIncrement()
                    sender.send(sender.create(m))
                    sndCounter++
                    // simulate application work
                    sleep(config.sender.delay)
                }

                sndStopped << (nanoTime() - startTime - sndCounter * MILLISECONDS.toNanos(config.sender.delay))
            } finally {
                if (sender) sender.close()
            }
        }
    }).start()
}

// Create and start the threads for receiving messages
nbRcvr.times {
    new Thread(new Runnable() {
        @Override
        void run() {
            Receiver receiver
            try {
                def stop = false
                receiver = new Receiver(config)
                println "start the receiver $it"
                Message msg
                int rcvCounter = 0
                def startTime = nanoTime()
                while (!stop) {
                    msg = receiver.receive()
                    rcvCounter++
                    stop = msg.getBooleanProperty("poison.pill")
                    if (!stop) currRcvMsg.incrementAndGet()
                    else println "poison pill received for receiver $it"

                    // simulate application work
                    sleep(config.receiver.delay)
                }
                if(msg) msg.acknowledge()
                rcvStopped << (nanoTime() - startTime - rcvCounter * MILLISECONDS.toNanos(config.receiver.delay))
            } finally {
                if (receiver) receiver.close()
            }
        }
    }).start()
}

// Create and start the thread which collects statistics about send and receive threads.
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
    avg req/s : ${currSndMsg.get().intValue() / Math.max(NANOSECONDS.toSeconds((long) overallElapsedTime), 1)}req/s
    throughput : ${config.messages.size.total / Math.max(NANOSECONDS.toSeconds((long) overallElapsedTime), 1)}o/s
 start poison pills
"""
        // send poison pills to receivers
        def sender
        try {
            sender = new Sender(config)

            def poisonPill = sender.create(0)
            poisonPill.setBooleanProperty("poison.pill", true)
            def rcvElapsed = []
            nbRcvr.times {
                sender.send(poisonPill)
                rcvElapsed << rcvStopped.take()
            }

            cumulativeElapsedTime = rcvElapsed.sum() as long
            overallElapsedTime = rcvElapsed.max() as long

            println """
 end of receptions:
    total elapsed time : ${NANOSECONDS.toMillis(overallElapsedTime)}ms
    cumulative total elapsed time : ${NANOSECONDS.toMillis(cumulativeElapsedTime)}ms
    total received requests : ${currRcvMsg.intValue()}
    avg reception elapsed time : ${NANOSECONDS.toMillis((long) cumulativeElapsedTime / currRcvMsg.intValue())}ms/req
    avg req/s : ${currRcvMsg.get().intValue() / NANOSECONDS.toSeconds((long) overallElapsedTime)} req/s
    throughput : ${config.messages.size.total / NANOSECONDS.toSeconds((long) overallElapsedTime)} o/s
"""
        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            if (sender) sender.close()
            System.exit(0)
        }
    }
}).start()
