exercice1 {

    queuename = "myqueue"

    messages {
        size {
            mean = 100   // taille moyenne des messages envoyés en octet
            total = 4000000 // taille totale des messages envoyés en octet
        }
        count = size.total / size.mean // le nombre de message à envoyer
    }

    sender {
        count = 1 // nombre de sender envoyant des messages
        url = "TODO"  // url de connection
        delay = 0 // délai en ms entre deux envois
    }

    receiver {
        count = 1 // nombre de receiver receptionnant des messages
        url = "TODO"  // url de connection
        delay = 0 // délai en ms entre deux réceptions
    }

    brokers = [1] // liste des brokers utilisés (utile pour purger les queues)
}

exercice2 {

    queuename = "myqueue"

    messages {
        size {
            mean = 200   // taille moyenne des messages envoyés en octet
            total = 5000000 // taille totale des messages envoyés en octet
        }
        count = size.total / size.mean // le nombre de message à envoyer
    }

    sender {
        count = 1 // nombre de sender envoyant des messages
        url = "TODO"  // url de connection
        delay = 0 // délai en ms entre deux envois
    }

    receiver {
        count = 1 // nombre de receiver receptionnant des messages
        url = "TODO"  // url de connection
        delay = 0 // délai en ms entre deux réceptions
    }

    brokers = [1] // liste des brokers utilisés (utile pour purger les queues)
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
        count = 1 // nombre de sender envoyant des messages
        url = "TODO"  // url de connection
        delay = 0 // délai en ms entre deux envois
    }

    receiver {
        count = 1 // nombre de receiver receptionnant des messages
        url = "TODO"  // url de connection
        delay = 0 // délai en ms entre deux réceptions
    }

    brokers = [1,2] // liste des brokers utilisés (utile pour purger les queues)
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
            url = "TODO"
            delay = 0
        }
        sender2 {
            url = "TODO"
            delay = 0
        }
    }

    poisonpill = "sender1" // le sender utilisé pour envoyer la poison pill

    receivers {
        receiver1 {
            url = "TODO"
            delay = 0
        }
        receiver2 {
            url = "TODO"
            delay = 0
            //maxMessages = 1000 // utile pour arrêter un receiver avant la fin du test
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
            url = "TODO"
            delay = 0
        }
        sender2 {
            url = "TODO"
            delay = 0
        }
    }

    poisonpill = "sender1" // le sender utilisé pour envoyer la poison pill

    receivers {
        receiver1 {
            url = "TODO"
            delay = 0
        }
        receiver2 {
            url = "TODO"
            delay = 0
            //maxMessages = 1000 // utile pour arrêter un receiver avant la fin du test
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
            url = "TODO"
            delay = 0
        }
        sender2 {
            url = "TODO"
            delay = 0
        }
    }

    poisonpill = "sender1" // le sender utilisé pour envoyer la poison pill

    receivers {
        receiver1 {
            url = "TODO"
            delay = 0
        }
        receiver2 {
            url = "TODO"
            delay = 0
            //maxMessages = 1000 // utile pour arrêter un receiver avant la fin du test
        }
    }

    brokers = [1, 2]
}