-- Optional MySQL schema reference. The default application profile uses H2 for easy local startup.
CREATE DATABASE IF NOT EXISTS network_simulator;
USE network_simulator;
-- Spring JPA creates the five entity tables automatically when configured for MySQL.
-- Tables: topology, router, link, simulation, simulation_result.
