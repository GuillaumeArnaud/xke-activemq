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
2. changer les urls pour les _senders_ et les _receivers_ afin de tenir compte du slave
3. lancer les deux serveurs `./activemq.sh start 1 2`
4. connecter vous sur les interfaces d'admin des deux brokers ( [http://localhost:8161/admin](http://localhost:8161/admin)  et [http://localhost:8162/admin](http://localhost:8162/admin) ). Que constate-t-on ?
5. lancer le main de l'exercice 3 (`main.groovy`)
6. arrêter le master et attendre la fin du tir. On ne doit pas perdre de message
7. refaire le test pour tester le failback.

## liens et explications

Ici on n'utilise le mode [master/slave](http://activemq.apache.org/masterslave.html) en mode [Shared File System](http://activemq.apache.org/shared-file-system-master-slave.html). Ce mode est très simple à mettre en place, il suffit que les brokers partagent le même datastore. Le premier broker qui démarre saisit un lock et l'autre broker se met alors en slave (d'où ça console d'admin down). Lorsque le master tombe le slave saisit le lock et devient master. 

Dans la vraie vie le filesystem sera peut-être sur un SAN ou autre, les performances peuvent donc très vite se dégrader. A la place on peut utiliser une base de donnée JDBC. La prochaine version 5.9 d'ActiveMQ proposera une version avec [Zookeeper](http://activemq.apache.org/replicated-leveldb-store.html) en association avec une base LevelDB.

Exercice 4

* 2 brokers en network duplex / 2 senders / 2 receivers
* start / stop d'un des brokers
* stop d'un des receivers
* aucune perte de messages

=> network de broker / failover / balancing des clients

Exercice 5

* 2 brokers _sender_ / 2 brokers _receiver_ / 2 senders / 2 receivers
* start / stop des brokers à tour de rôle
* aucune perte de messages

=> mode unidirectionnel / load balancing à l'émission et réception

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
