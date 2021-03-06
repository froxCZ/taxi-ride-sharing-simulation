# Ridesharing simulation #

This is an app for simulating taxis with and without ride sharing. More details are in the [paper.pdf](https://bitbucket.org/froxCZ/taxi-ride-sharing-simulation/raw/46446360d0f50531374d78b78814cb2fe25f3d7f/paper.pdf)

### Requirements ###

* Java 8
* Maven
* OSRM routing service https://github.com/Project-OSRM/osrm-backend
* mysql (not necessary)

### Setup & run ###

* Create orders in `orders` table (create script below) or specify them in `OrdersProvider` class
* Set initial taxis' postions in `InitialData` class
* Run OSRM routing service at `127.0.0.1:5000`
* Run `mvn compile` followed by `mvn exec:java -Dexec.mainClass=com.company.Main` (if there is no cache file created yet, the first run can take longer time)
* Simulation properties can be changed in `Simulator` class


#### orders table create script ####
```
CREATE TABLE `orders` (
	`orderId` BIGINT(20) NOT NULL,
	`rideId` BIGINT(20) NULL DEFAULT NULL,
	`orderedAt` DATETIME NULL DEFAULT NULL,
	`avgDistanceTariffOffered` DOUBLE NULL DEFAULT NULL,
	`requestedPickupLat` DOUBLE NULL DEFAULT NULL,
	`requestedPickupLon` DOUBLE NULL DEFAULT NULL,
	`requestedDestinationLat` DOUBLE NULL DEFAULT NULL,
	`requestedDestinationLon` DOUBLE NULL DEFAULT NULL,
	`completionState` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8_general_ci',
	`passengersCount` INT(11) NULL DEFAULT NULL,
	PRIMARY KEY (`orderId`)
)
COLLATE='utf16_czech_ci'
ENGINE=InnoDB
;
```