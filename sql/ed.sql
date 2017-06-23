---------------------------------------------------------------------
-- sql/ed.sql
-- the Elite Dangerous Tools database

-- :name create-faction-table! :!
-- :doc create the faction table
CREATE TABLE faction (
  id                INT          NOT NULL,
  name              VARCHAR(200) NOT NULL,
  updated_at        INT          NOT NULL,
  government_id     INT,
  allegiance_id     INT,
  state_id          INT,
  home_system_id    INT,
  is_player_faction BOOLEAN      NOT NULL
);

-- :name create-faction-pk! :!
-- :doc create the faction primary key
CREATE UNIQUE INDEX faction_pk
  ON faction (id);

-- :name create-faction-name-index! :!
-- :doc create an index on faction name for searching
CREATE INDEX faction_name_idx
  ON faction (name);

-- :name create-government-table! :!
-- :doc create the government table
CREATE TABLE government (
  id   INT         NOT NULL,
  name VARCHAR(30) NOT NULL
);

-- :name create-government-pk! :!
-- :doc create the government primary key
CREATE UNIQUE INDEX government_pk
  ON government (id);

-- :name create-allegiance-table! :!
-- :doc create the government table
CREATE TABLE allegiance (
  id   INT         NOT NULL,
  name VARCHAR(30) NOT NULL
);

-- :name create-allegiance-pk! :!
-- :doc create the allegiance primary key
CREATE UNIQUE INDEX allegiance_pk
  ON allegiance (id);

-- :name create-state-table! :!
-- :doc create the state table
CREATE TABLE state (
  id   INT         NOT NULL,
  name VARCHAR(30) NOT NULL
);

-- :name create-state-pk! :!
-- :doc create the state primary key
CREATE UNIQUE INDEX state_pk
  ON state (id);

-- :name create-reserve-type-table! :!
-- :doc create the reserve_type table
CREATE TABLE reserve_type (
  id   INT         NOT NULL,
  name VARCHAR(30) NOT NULL
);

-- :name create-reserve-type-pk! :!
-- :doc create the reserve_type primary key
CREATE UNIQUE INDEX reserve_type_pk
  ON reserve_type (id);

-- :name create-primary-economy-table! :!
-- :doc create the primary_economy table
CREATE TABLE primary_economy (
  id   INT         NOT NULL,
  name VARCHAR(30) NOT NULL
);

-- :name create-primary-economy-pk! :!
-- :doc create the primary_economy primary key
CREATE UNIQUE INDEX primary_economy_pk
  ON primary_economy (id);

-- :name create-power-state-table! :!
-- :doc create the power_state table
CREATE TABLE power_state (
  id   INT         NOT NULL,
  name VARCHAR(30) NOT NULL
);

-- :name create-power-state-pk! :!
-- :doc create the power_state primary key
CREATE UNIQUE INDEX power_state_pk
  ON power_state (id);

-- :name create-secuirty-table! :!
-- :doc create the security table
CREATE TABLE security (
  id   INT         NOT NULL,
  name VARCHAR(30) NOT NULL
);

-- :name create-security-pk! :!
-- :doc create the security primary key
CREATE UNIQUE INDEX security_pk
  ON security (id);

-- :name create-system-table! :!
-- :doc create the system table
CREATE TABLE system (
  id                           INT          NOT NULL,
  updated_at                   INT          NOT NULL,
  edsm_id                      INT,
  name                         VARCHAR(100) NOT NULL,
  x                            REAL         NOT NULL,
  y                            REAL         NOT NULL,
  z                            REAL         NOT NULL,
  population                   BIGINT,
  is_populated                 BOOLEAN,
  government_id                INT,
  allegiance_id                INT,
  state_id                     INT,
  security_id                  INT,
  primary_economy_id           INT,
  power                        VARCHAR(50),
  power_state_id               INT,
  needs_permit                 BOOLEAN,
  simbad_ref                   VARCHAR(100),
  controlling_minor_faction_id INT,
  reserve_type_id              INT
);

-- :name create-system-pk! :!
-- :doc create the system primary key
CREATE UNIQUE INDEX system_pk
  ON system (id);

-- :name create-system-name-index! :!
-- :doc create the system name index for searching
CREATE INDEX system_name_idx
  ON system (name);

-- :name create-system-faction-table! :!
-- :doc create the system-faction table
CREATE TABLE system_faction (
  system_id        INT  NOT NULL,
  minor_faction_id INT  NOT NULL,
  updated_at       INT  NOT NULL,
  state_id         INT  NOT NULL,
  influence        REAL NOT NULL
);

-- :name create-system-faction-pk! :!
-- :doc create the system_faction primary key
CREATE UNIQUE INDEX system_faction_pk
  ON system_faction (system_id, minor_faction_id);

---------------------------------------------------------------------

-- :name drop-faction-table! :!
-- :doc drop the faction table
DROP TABLE faction;

-- :name drop-government-table! :!
-- :doc drop the government table
DROP TABLE government;

-- :name drop-allegiance-table! :!
-- :doc drop the allegiance table
DROP TABLE allegiance;

-- :name drop-state-table! :!
-- :doc drop the state table
DROP TABLE state;

-- :name drop-reserve-type-table! :!
-- :doc drop the reserve_type table
DROP TABLE reserve_type;

-- :name drop-primary-economy-table! :!
-- :doc drop the primary_economy table
DROP TABLE primary_economy;

-- :name drop-power-state-table! :!
-- :doc drop the power_state table
DROP TABLE power_state;

-- :name drop-security-table! :!
-- :doc drop the security table
DROP TABLE security;

-- :name drop-system-table! :!
-- :doc drop the system table
DROP TABLE system;

-- :name drop-system-faction-table! :!
-- :doc drop the system_faction table
DROP TABLE system_faction;

---------------------------------------------------------------------

-- :name get-faction :? :1
-- :doc get faction record by id
SELECT *
FROM faction
WHERE id = :id;

-- :name get-faction-by-name :? :1
-- :doc get faction record by name
SELECT *
FROM faction
WHERE name = :name;

-- :name get-system :? :1
-- :doc get system record by id
SELECT *
FROM system
WHERE id = :id;

-- :name get-system-by-name :? :1
-- :doc get system record by name
SELECT *
FROM system
WHERE name = :name;

---------------------------------------------------------------------

-- :name get-allegiance :? :1
-- :doc get allegiance by id
SELECT *
FROM allegiance
WHERE id = :id;

-- :name get-government :? :1
-- :doc get government by id
SELECT *
FROM government
WHERE id = :id;

-- :name get-power-state :? :1
-- :doc get power-state by id
SELECT *
FROM power_state
WHERE id = :id;

-- :name get-primary-economy :? :1
-- :doc get primary-economy by id
SELECT *
FROM primary_economy
WHERE id = :id;

-- :name get-security :? :1
-- :doc get security by id
SELECT *
FROM security
WHERE id = :id;

-- :name get-state :? :1
-- :doc get state by id
SELECT *
FROM state
WHERE id = :id;

---------------------------------------------------------------------

-- :name get-allegiance-by-name :? :1
-- :doc get allegiance by name
SELECT *
FROM allegiance
WHERE name = :name;

-- :name get-government-by-name :? :1
-- :doc get government by name
SELECT *
FROM government
WHERE name = :name;

-- :name get-power-state-by-name :? :1
-- :doc get power-state by name
SELECT *
FROM power_state
WHERE name = :name;

-- :name get-primary-economy-by-name :? :1
-- :doc get primary-economy by name
SELECT *
FROM primary_economy
WHERE name = :name;

-- :name get-security-by-name :? :1
-- :doc get security by name
SELECT *
FROM security
WHERE name = :name;

-- :name get-state-by-name :? :1
-- :doc get state by name
SELECT *
FROM state
WHERE name = :name;

---------------------------------------------------------------------

-- :name insert-allegiance :! :n
-- :doc insert a new allegiance record
INSERT INTO allegiance (id, name)
VALUES (:id, :name);

-- :name insert-government :! :n
-- :doc insert a new government record
INSERT INTO government (id, name)
VALUES (:id, :name);

-- :name insert-power-state :! :n
-- :doc insert a new power-state record
INSERT INTO power_state (id, name)
VALUES (:id, :name);

-- :name insert-primary-economy :! :n
-- :doc insert a new primary-economy record
INSERT INTO primary_economy (id, name)
VALUES (:id, :name);

-- :name insert-security :! :n
-- :doc insert a new security record
INSERT INTO security (id, name)
VALUES (:id, :name);

-- :name insert-state :! :n
-- :doc insert a new state record
INSERT INTO state (id, name)
VALUES (:id, :name);

---------------------------------------------------------------------

-- :name insert-faction! :! :n
-- :doc insert a new faction record
INSERT INTO faction (id, name, updated_at, government_id, allegiance_id, state_id, home_system_id, is_player_faction)
VALUES (:id, :name, :updated-at, :government-id, :allegiance-id, :state-id, :home-system-id, :is-player-faction);

-- :name update-faction! :! :n
-- :doc update a faction record based on its id
UPDATE faction
SET
  name = :name,
  updated_at = :updated-at,
  government_id = :government-id,
  allegiance_id = :allegiance-id,
  state_id = :state-id,
  home_system_id = :home-system-id,
  is_player_faction = :is-player-faction
WHERE id = :id;

---------------------------------------------------------------------

-- :name insert-system! :! :n
-- :doc insert a new system record
INSERT INTO system (id, updated_at, edsm_id, name, x, y, z, population, is_populated, government_id, allegiance_id, state_id, security_id, primary_economy_id, power, power_state_id, needs_permit, simbad_ref, controlling_minor_faction_id, reserve_type_id)
VALUES (:id, :updated-at, :edsm-id, :name, :x, :y, :z, :population, :is-populated, :government-id, :allegiance-id, :state-id, :security-id, :primary-economy-id, :power, :power-state-id, :needs-permit, :simbad-ref, :controlling-minor-faction-id, :reserve-type-id);

-- :name update-system! :! :n
-- :doc update a system record based on its id
UPDATE system
SET
  updated_at = :updated-at,
  edsm_id = :edsm-id,
  name = :name,
  x = :x,
  y = :y,
  z = :z,
  population = :population,
  is_populated = :is-populated,
  government_id = :government-id,
  allegiance_id = :allegiance-id,
  state_id = :state-id,
  security_id = :security-id,
  primary_economy_id = :primary-economy-id,
  power = :power,
  power_state_id = :power-state-id,
  needs_permit = :needs-permit,
  simbad_ref = :simbad-ref,
  controlling_minor_faction_id = :controlling-minor-faction-id,
  reserve_type_id = :reserve-type-id
WHERE id = :id;
