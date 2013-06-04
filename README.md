xke-activemq
============

Hands on pour le XKE d'ActiveMQ

# Exercice 1

* 1 broker / 1 sender / 1 receiver
* start / stop du broker
* pas de perte de message

A faire:

1. créer les connexions pour le `Sender` et pour le `Receiver`
2. lancer le script `./activemq.sh start 1`
3. lancer le script `main.groovy`
4. vérifier que tous les messages sont bien reçus
5. relancer l'envoi des messages mais redémarrer le broker durant le test: `./activemq.sh restart 1`
6. corriger les erreurs afin de ne perdre aucun message

# Exercice 2

* 1 broker / 1 sender / 1 receiver
* taille des messages variables
* test de performances

=> grouper / acknowledge / fetch

# Exercice 3

## contexte
* 2 brokers actif/passif / 1 à n senders / 1 à n receiver
* start / stop du master

## à faire

1. éditer le fichier `brokers/broker2/broker.xml` afin qu'il pointe sur la même base que le _broker 1_
2. changer les uris pour les _senders_ et les _receivers_ afin de tenir compte du slave
3. lancer les deux serveurs `./activemq.sh start 1 2`
4. connecter vous sur les interfaces d'admin des deux brokers ( [http://localhost:8161/admin](http://localhost:8161/admin)  et [http://localhost:8162/admin](http://localhost:8162/admin) ). Que constate-t-on ?
5. lancer le main de l'exercice 3 (`main.groovy`)
6. arrêter le master et attendre la fin du tir. On ne doit pas perdre de message
7. refaire le test pour tester le failback.

## liens et explications

Ici on n'utilise le mode [master/slave](http://activemq.apache.org/masterslave.html) en mode [Shared File System](http://activemq.apache.org/shared-file-system-master-slave.html). Ce mode est très simple à mettre en place, il suffit que les brokers partagent le même datastore. Le premier broker qui démarre saisit un lock et l'autre broker se met alors en slave (d'où ça console d'admin down). Lorsque le master tombe le slave saisit le lock et devient master. 

Dans la vraie vie le filesystem sera peut-être sur un SAN ou autre, les performances peuvent donc très vite se dégrader. A la place on peut utiliser une base de donnée JDBC. La prochaine version 5.9 d'ActiveMQ proposera une version avec [Zookeeper](http://activemq.apache.org/replicated-leveldb-store.html) en association avec une base LevelDB.

# Exercice 4

## contexte

* 2 brokers en network / 2 senders / 2 receivers
* start / stop d'un des brokers
* balancing des clients
* aucune perte de messages

## à faire

1. mettre en place le _network of brokers_ pour les brokers 1 et 2 (rollbacker la configuration master/slave) afin de _load balancer_ les clients.
2. changer les uris des _senders_ et _receivers_ afin qu'il y ait un sender et un receiver sur chacun des brokers.
3. lancer les deux serveurs `./activemq.sh start 1 2`
4. lancer le _main_ de l'exercice 4 (`main.groovy`)
5. si tout fonctionne bien (réception de tous les messages), recommencer en stoppant un deux brokers (`./activemq.sh stop 1`) en cours de route. Que constate-t-on ?
6. reconfigurer les clients afin d'être résistant à la perte et au retour d'un broker. Vérifier également la répartition des messages à l'aide de la console d'admin ( [http://localhost:8161/admin](http://localhost:8161/admin)  et [http://localhost:8162/admin](http://localhost:8162/admin) )

##  liens et explications

Le [network of brokers](http://activemq.apache.org/networks-of-brokers.html) est un peu plus compliqué à mettre en place. Ici on s'en sert pour simuler un _load balancing_ entre deux brokers, ce qui n'est pas forcément intéressant car on pourrait sans doute obtenir la même chose avec les connexions failover.

La configuration côté cliente pourrait se faire de façon explicite:

    failover:(tcp:\\broker1,tcp:\\broker2)

Mais ça a l'inconvénient d'être plus difficile à scaler. On préfère donc que chaque client se connecte à un seul broker (ça peut être le même) et configurer les brokers pour qu'ils rééquilibrent les clients (cf. cet [article](http://bsnyderblog.blogspot.fr/2010/10/new-features-in-activemq-54-automatic.html)):

    client => failover:(tcp:\\broker1)
    broker => updateClusterClients="true" rebalanceClusterClients="true" 


# Exercice 5

## contexte

* 2 brokers _sender_ / 2 brokers _receiver_ / 2 senders / 2 receivers
* start / stop des brokers à tour de rôle
* aucune perte de messages

## à faire

1. mettre en place le _network of brokers_ pour les brokers 1,2 ,3 et 4 (rollbacker la configuration master/slave) afin de _load balancer_ les _senders_ d'un côté et les _receivers_ de l'autre.
2. changer les uris des _senders_ et _receivers_ afin qu'il y ait les senders pointent sur les brokers 1 et 2 et que les receivers pointent sur les brokers 3 et 4. 
3. lancer les quatre serveurs `./activemq.sh start 1 2 3 4`
4. lancer le _main_ de l'exercice 5 (`main.groovy`)
5. assurez-vous de bien recevoir tous les messages. Dans les consoles d'admin, observez le nombre de messages qui passent dans les topics _ActiveMQ.Advisory.*_.
6. recommencez les tests en stoppant certains serveurs et en vous assurant de ne pas perdre de messages.
7. recommencez les tests avec cette fois 10 receveurs sur le broker 3 et 1 seul sur le broker 4. Que constate-t-on ? Essayer de répartir les messages équitablement.

##  liens et explications

Ce mode permet de découpler le _load balancing_ entre les émetteurs et les receveurs. 
Les liens entre les brokers 1/2 et les brokers 3/4 peuvent être configurer en mode _duplex_, ce qui permet de passer certains firewall.
Exercice 6

* 2 brokers en network duplex / 2 brokers en passif / 1 à n senders / 1 à n receivers
* start / stop des masters à tour de rôles
* aucune perte de messages
* load balancing _equitable_ entre les brokers

=> actif/passif + network / performance par rapport à l'exercice 3

Exercice 7

* 1 à n brokers en multicast / 0 network / n senders / n receivers
* request / reply (queues temporaires)

=> scaling / performance
