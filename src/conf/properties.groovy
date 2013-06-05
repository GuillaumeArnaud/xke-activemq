exercice1 {

    queuename = "myqueue"

    messages {
        size {
            mean = 100
            total = 4000000
        }
        count = size.total / size.mean
    }

    sender {
        count = 1
        url = "tcp://localhost:2001"
        delay = 0
    }

    receiver {
        count = 1
        url = "tcp://localhost:2001"
        delay = 0
    }

    brokers = [1]
}

exercice2 {

    queuename = "myqueue"

    messages {
        size {
            mean = 200
            total = 5000000
        }
        count = size.total / size.mean
    }

    sender {
        count = 1
        url = "tcp://localhost:2001?jms.useAsyncSend=true"
        delay = 0
    }

    receiver {
        count = 10
        url = "tcp://localhost:2001?jms.prefetchPolicy.all=1"
        delay = 2
    }

    brokers = [1]
}

exercice3 {

    queuename = "myqueue"

    messages {
        size {
            mean = 200
            total = 10000000
        }
        count = size.total / size.mean
    }

    sender {
        count = 1
        url = "failover:(tcp://localhost:2001,tcp://localhost:2002)"
        delay = 0
    }

    receiver {
        count = 1
        url = "failover:(tcp://localhost:2001,tcp://localhost:2002)"
        delay = 1
    }

    brokers = [1]
}

exercice4 {

    queuename = "myqueue"

    messages {
        size {
            mean = 200
            total = 5000000
        }
        count = size.total / size.mean
    }

    senders {
        sender1 {
            url = "failover:(tcp://localhost:2001)"
            delay = 0
        }
        sender2 {
            url = "failover:(tcp://localhost:2002)"
            delay = 0
        }
    }

    poisonpill = "sender1"

    receivers {
        receiver1 {
            url = "failover:(tcp://localhost:2001)"
            delay = 0
        }
        receiver2 {
            url = "failover:(tcp://localhost:2002)"
            delay = 0
            //maxMessages = 1000
        }
    }

    brokers = [1, 2]
}

exercice5 {

    queuename = "myqueue"

    messages {
        size {
            mean = 200
            total = 5000000
        }
        count = size.total / size.mean
    }

    senders {
        sender1 {
            url = "failover:(tcp://localhost:2001)"
            delay = 0
        }
        sender2 {
            url = "failover:(tcp://localhost:2002)"
            delay = 0
        }
    }

    poisonpill = "sender1"

    receivers {
        receiver1 {
            url = "failover:(tcp://localhost:2001)"
            delay = 0
        }
        receiver2 {
            url = "failover:(tcp://localhost:2002)"
            delay = 0
        }
    }

    brokers = [1, 2]
}


exercice6 {

    queuename = "myqueue"

    messages {
        size {
            mean = 200
            total = 5000000
        }
        count = size.total / size.mean
    }

    senders {
        sender1 {
            url = "failover:(tcp://localhost:2001)"
            delay = 0
        }
        sender2 {
            url = "failover:(tcp://localhost:2002)"
            delay = 0
        }
    }

    poisonpill = "sender1"

    receivers {
        receiver1 {
            url = "failover:(tcp://localhost:2001)"
            delay = 0
        }
        receiver2 {
            url = "failover:(tcp://localhost:2002)"
            delay = 0
        }
    }

    brokers = [1, 2]
}