---------------------------------------------------------------------
-- sql/ed.sql
-- the Elite Dangerous Tools database

-- :name create-faction-table! :!
-- :doc create the faction table
CREATE TABLE faction (
  id            INT          NOT NULL,
  updated_at    INT          NOT NULL,
  name          VARCHAR(200) NOT NULL,
  government_id INT          NOT NULL,
  allegiance_id INT          NOT NULL
);

-- :name create-faction-pk! :!
-- :doc create the faction primary key
CREATE UNIQUE INDEX faction_pk ON faction (id, updated_at);

-- :name create-faction-name-index! :!
-- :doc create an index on faction name for searching
CREATE INDEX faction_name_idx ON faction (name);

-- :name create-government-table! :!
-- :doc create the government table
CREATE TABLE government (
  id   INT         NOT NULL,
  name VARCHAR(30) NOT NULL
);

-- :name create-government-pk! :!
-- :doc create the government primary key
CREATE UNIQUE INDEX government_pk ON government (id);

-- :name create-allegiance-table! :!
-- :doc create the government table
CREATE TABLE allegiance (
  id   INT         NOT NULL,
  name VARCHAR(30) NOT NULL
);

-- :name create-allegiance-pk! :!
-- :doc create the allegiance primary key
CREATE UNIQUE INDEX allegiance_pk ON allegiance (id);

-- :name create-state-table! :!
-- :doc create the state table
CREATE TABLE state (
  id   INT         NOT NULL,
  name VARCHAR(30) NOT NULL
);

-- :name create-state-pk! :!
-- :doc create the state primary key
CREATE UNIQUE INDEX state_pk ON state (id);

-- :name create-reserve-type-table! :!
-- :doc create the reserve_type table
CREATE TABLE reserve_type (
  id   INT         NOT NULL,
  name VARCHAR(30) NOT NULL
);

-- :name create-reserve-type-pk! :!
-- :doc create the reserve_type primary key
CREATE UNIQUE INDEX reserve_type_pk ON reserve_type (id);

-- :name create-primary-economy-table! :!
-- :doc create the primary_economy table
CREATE TABLE primary_economy (
  id   INT         NOT NULL,
  name VARCHAR(30) NOT NULL
);

-- :name create-primary-economy-pk! :!
-- :doc create the primary_economy primary key
CREATE UNIQUE INDEX primary_economy_pk ON primary_economy (id);

-- :name create-power-state-table! :!
-- :doc create the power_state table
CREATE TABLE power_state (
  id   INT         NOT NULL,
  name VARCHAR(30) NOT NULL
);

-- :name create-power-state-pk! :!
-- :doc create the power_state primary key
CREATE UNIQUE INDEX power_state_pk ON power_state (id);

-- :name create-secuirty-table! :!
-- :doc create the security table
CREATE TABLE security (
  id   INT         NOT NULL,
  name VARCHAR(30) NOT NULL
);

-- :name create-security-pk! :!
-- :doc create the security primary key
CREATE UNIQUE INDEX security_pk ON security (id);

-- :name create-faction-state-table! :!
-- :doc create the faction_state table
CREATE TABLE faction_state (
  faction_id INT NOT NULL,
  updated_at INT NOT NULL,
  state_id   INT NOT NULL
);

-- :name create-faction-state-pk! :!
-- :doc create the faction_state primary key
CREATE UNIQUE INDEX faction_state_pk ON faction_state (faction_id, updated_at);

-- :name create-faction-state-faction-id-index! :!
-- :doc create an index on faction_id for searching
CREATE INDEX faction_state_faction_id_idx ON faction_state (faction_id);

-- :name create-system-table! :!
-- :doc create the system table
CREATE TABLE system (
  id                           INT          NOT NULL,
  updated_at                   INT          NOT NULL,
  edsm_id                      INT          NOT NULL,
  name                         VARCHAR(100) NOT NULL,
  x                            REAL         NOT NULL,
  y                            REAL         NOT NULL,
  z                            REAL         NOT NULL,
  population                   BIGINT       NOT NULL,
  is_populated                 BOOLEAN      NOT NULL,
  government_id                INT          NOT NULL,
  allegiance_id                INT          NOT NULL,
  state_id                     INT          NOT NULL,
  security_id                  INT          NOT NULL,
  primary_economy_id           INT          NOT NULL,
  power                        VARCHAR(50)  NOT NULL,
  power_state_id               INT          NOT NULL,
  needs_permit                 BOOLEAN      NOT NULL,
  simbad_ref                   VARCHAR(100),
  controlling_minor_faction_id INT          NOT NULL,
  reserve_type_id              INT          NOT NULL
);

-- :name create-system-pk! :!
-- :doc create the system primary key
CREATE UNIQUE INDEX system_pk ON system (id, updated_at);

-- :name create-system-name-index! :!
-- :doc create the system name index for searching
CREATE INDEX system_name_idx ON system (name);

-- :name create-system-faction-table! :!
-- :doc create the system-faction table
CREATE TABLE system_faction (
  system_id        INT  NOT NULL,
  minor_faction_id INT  NOT NULL,
  updated_at       INT  NOT NULL,
  idx              INT  NOT NULL,
  state_id         INT  NOT NULL,
  influence        REAL NOT NULL
);

-- :name create-system-faction-pk! :!
-- :doc create the system_faction primary key
CREATE UNIQUE INDEX system_faction_pk ON system_faction(system_id, minor_faction_id, updated_at, idx);

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

-- :name drop-faction-state-table! :!
-- :doc drop the faction_state table
DROP TABLE faction_state;

-- :name drop-system-table! :!
-- :doc drop the system table
DROP TABLE system;

-- :name drop-system-faction-table! :!
-- :doc drop the system_faction table
DROP TABLE system_faction;

---------------------------------------------------------------------

-- :name get-faction :? :1
-- :doc get faction by id
select * from faction
 where id = :id;
