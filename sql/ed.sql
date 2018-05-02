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
  state_id         INT,
  influence        REAL
);

-- :name create-system-faction-pk! :!
-- :doc create the system_faction primary key
CREATE UNIQUE INDEX system_faction_pk
  ON system_faction (system_id, minor_faction_id);

-- :name create-system-faction-system-index! :!
-- :doc create index on system_faction for searching against system
CREATE INDEX system_faction_system_idx
  ON system_faction (system_id);

---------------------------------------------------------------------

-- :name drop-faction-table! :!
-- :doc drop the faction table
DROP TABLE faction;

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

-- :name get-system-by-name :? :*
-- :doc get system record by name. returns a list as names are not unique.
SELECT *
FROM system
WHERE name = :name;

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

-- :name update-system-from-fsdjump! :! :n
-- :doc updates a system record from subset of data in fsd-jump
UPDATE system
SET
  state_id = :state-id,
  allegiance_id = :allegiance-id,
  updated_at = :updated-at,
  system_faction = :system-faction-id
WHERE id = :id;

---------------------------------------------------------------------

-- :name get-system-faction :? :*
-- :doc selects the faction information in a given system. returns multiple rows.

SELECT *
FROM system_faction
WHERE system_id = :system-id;

-- :name delete-system-faction-by-system-id! :! :n
-- :doc delete all system-faction information for a particular system-id
-- All data is cleared from system_faction table by system_id so that any expansions/removals
-- of factions from a system do not get left around. We are only keeping the most current
-- values. If historical data is required, that is for consumers to arrange (e.g. InfluxDB)

DELETE FROM system_faction
WHERE system_id = :system-id;

-- :name insert-system-faction! :! :n
-- :doc inserts system-faction data

INSERT INTO system_faction (system_id, minor_faction_id, updated_at, state_id, influence)
VALUES (:system-id, :minor-faction-id, :updated-at, :state-id, :influence);

-- :name update-system-faction! :! :n
-- :doc updates the system-faction table for the given system-id

UPDATE system_faction
SET
  updated_at = :updated-at,
  state_id = :state-id,
  influence = :influence
WHERE system_id = :system-id
AND minor_faction_id = :minor-faction-id;

-- :name get-num-system-faction :? :1
-- :doc gets the count of the factions for a particular system-id

SELECT COUNT(*)
FROM system_faction
WHERE system_id = :system-id;

-- :name get-system-info :? :n
-- :doc gets summary information for a given system-id

SELECT s.id, s.name, f.count(*) as fcount, s.x, s.y, s.z
FROM system s, system_faction f
WHERE s.id = :system-id;
