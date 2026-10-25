# Network Routing & Failure Simulation Engine

## Project Overview

The Network Routing & Failure Simulation Engine is a Java-based project designed to simulate network routing between interconnected routers. It uses Dijkstra's shortest-path algorithm to calculate a minimum-cost route and demonstrates how routing changes when network links or routers become unavailable.

## Problem Statement

In a computer network, a link failure, router failure, or change in link weight can affect the route between two routers. A static shortest-path calculation may no longer provide the best available route after a network change.

This project models a weighted network and recalculates routes when topology changes occur.

## Objectives

* Represent a network using a weighted graph.
* Calculate minimum-cost routes using Dijkstra's algorithm.
* Simulate link failure and restoration.
* Simulate router failure and restoration.
* Change link weights dynamically.
* Display route results and simulation history.

## Main Features

* View the sample network topology.
* Calculate the shortest route between selected routers.
* Simulate link failures and restoration.
* Simulate router failures and restoration.
* Change link weights.
* View simulation history.
* Reset the sample network.

## Technologies Used

* Java
* Spring Boot
* Maven
* HTML and JavaScript for the web interface
* Spring Data JPA
* H2 database for the default configuration
* MySQL configuration for database integration
* Dijkstra's shortest-path algorithm

## Database Design

The planned database entities are:

| Entity           | Purpose                                 |
| ---------------- | --------------------------------------- |
| Topology         | Stores network topology information     |
| Router           | Stores routers belonging to a topology  |
| Link             | Stores connections, weights, and status |
| Simulation       | Stores simulation inputs and details    |
| SimulationResult | Stores calculated routes and costs      |

## Project Structure

```text
Network-Routing-Failure-Simulation/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── edu/project/network/
        │       ├── model/
        │       ├── repo/
        │       ├── service/
        │       ├── web/
        │       └── NetworkRoutingSimulatorApplication.java
        └── resources/
```

## Requirements

* Java Development Kit (JDK) 17 or compatible.
* Maven, or an IDE that supports Maven projects.
* A web browser.

## Running the Project

1. Clone or download this repository.
2. Open the project in an IDE such as IntelliJ IDEA or Eclipse.
3. Confirm that JDK 17 is configured.
4. Allow Maven to resolve the dependencies in `pom.xml`.
5. Run `NetworkRoutingSimulatorApplication.java`.
6. Open the application URL shown in the console.

**Note:** The application must be built and tested in your environment before its running instructions and features can be considered verified.

## Team Members

* Barani Sankar
* Sanjai
* Sanjay
* Sujith
* Srijan S

## Academic Project

This project demonstrates graph modelling, shortest-path algorithms, dynamic network changes, Java application development, and database integration.
