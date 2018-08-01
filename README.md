# ed-clj

A Clojure library for Elite Dangerous Tools:

1. Factions to InfluxDB
2. DiscordBot

THIS IS VERY MUCH A WORK IN PROGRESS PROJECT!
I doubt it will be useful to anyone other than myself or for seeing my crap CLJ skillz.

Completed:

1. Database design for holding a single value of any system's state
2. Loading nightly dumps into datbase

Currently, the project just loads base data in, there are routines for listening to journal changes, but
nothing is update by them.

I haven't worked out how to detect when the data is complete yet to kill off the pulse actors for loading it.

The idea of using InfluxDB will probably go away, as since writing this, I discovered EDSM's data, so this project
was more for doing some more clojure, and also writing some queries against a local database for things
like working out what systems are within range of a given system, top influence etc for planning BG
expansion.

Also, I haven't yet decided on whether to keep historical data locally if not doing the influxDB.

Basically this is a project looking for some requirements to guide its development.

## DESIGN

Nightly data and updates will be applied to a local d/b to hold
the 'latest' value for each system and faction therein.

Alterations will be sent to influxDB to handle time.


## DEV WALKTHROUGH

In intellij, start a repl for the project.
Run the following to load all the 'state'

    (dev)  ;; puts you in dev namespace with application loaded
    (go)   ;; loads the state objects, e.g. config, *db*

You can now run d/b functions etc from ed.db namespace, e.g.

    (ed.db/get-faction {:id 18979})
    => {:id 18979, :name "The Order of Mobius", :updated_at 1503711664, :government_id 80, :allegiance_id 4, :state_id 80, :home_system_id 1590, :is_player_faction true}

    (ed.db/get-faction-by-name {:name "The Order of Mobius"})
    => {:id 18979, :name "The Order of Mobius", :updated_at 1503711664, :government_id 80, :allegiance_id 4, :state_id 80, :home_system_id 1590, :is_player_faction true}

    (ed.db/get-system-by-name {:name "Exioce"})
    => ({:y -100.25, :power_state_id 32, :government_id 80, :allegiance_id 4, :controlling_minor_faction_id 18979, :needs_permit false, :name "Exioce", :reserve_type_id 4, :is_populated true, :updated_at 1503711664, :z 16.78125, :power "Arissa Lavigny-Duval", :id 4723, :population 16494916, :x 78.5, :edsm_id 18297, :security_id 32, :primary_economy_id 9, :simbad_ref "", :state_id 80})

    (ed.db/get-system-faction {:system-id 4723})
    =>
    ({:system_id 4723, :minor_faction_id 18979, :updated_at 1503711664, :state_id 80, :influence 42.7}
     {:system_id 4723, :minor_faction_id 15535, :updated_at 1503711664, :state_id 80, :influence 5.6}
     {:system_id 4723, :minor_faction_id 37614, :updated_at 1503711664, :state_id 16, :influence 3.8}
     {:system_id 4723, :minor_faction_id 18978, :updated_at 1503711664, :state_id 80, :influence 10.9}
     {:system_id 4723, :minor_faction_id 18977, :updated_at 1503711664, :state_id 64, :influence 12.9}
     {:system_id 4723, :minor_faction_id 18976, :updated_at 1503711664, :state_id 64, :influence 15.5}
     {:system_id 4723, :minor_faction_id 18975, :updated_at 1503711664, :state_id 16, :influence 8.6})

You can setup a clean database with:

    (ed.db/drop-db-tables!)
    (ed.db/create-db-tables!)

Done some code changes and want to reload it?

    (reset)

See also (refresh) and (refresh-all) which require you to start
the application state again (e.g. with "(go)")

## Command Line Startup

To run the application from the command line:

    $ lein godev

Currently this will drop all tables and recreate them in a pgsql
database named 'ed-factions', with user 'ed'.
This is controlled with {:new-db true} config value.
See the config.edn files for d/b etc config.

    $ lein goprod

This will run ed.server with prod profile, which by default does
not reinitialise the database ({:new-db false} in config.edn).

## DONE

- database definitions
- dev environment
- Import all factions: populate 'factions' (key: faction_id)
- Import all systems_populated, populate 'system' (key: system_id, updated_at), 'system_faction' (key: system_id, faction_id)

## TODO

- Process FSDJump events and update 'system_faction'

How does time affect the data? It doesn't. Time is handled by influxDB.

## Notes on data

- The updates are done by StarSystem, not ID.
- The update's factions are done by Name, not ID.
- The global systems_populated is done by ID

- When we get an update, we should check its values for changes

- split the faction into 'faction', 'faction_state' both with updated_at
- an update should alter both the faction_state and system_faction tables, not faction

- need to check if the update is telling us a faction has changed allegiance.

## faction

    {
      "id": 1,
      "name": "39 b Draconis One",
      "updated_at": 1496091138,
      "government_id": 80,
      "government": "Cooperative",
      "allegiance_id": 4,
      "allegiance": "Independent",
      "state_id": 16,
      "state": "Boom",
      "home_system_id": 185,
      "is_player_faction": false
    }

## systems_populated

    {
      "id": 1,
      "edsm_id": 12695,
      "name": "1 G. Caeli",
      "x": 80.90625,
      "y": -83.53125,
      "z": -30.8125,
      "population": 6544826,
      "is_populated": true,
      "government_id": 144,
      "government": "Patronage",
      "allegiance_id": 2,
      "allegiance": "Empire",
      "state_id": 16,
      "state": "Boom",
      "security_id": 32,
      "security": "Medium",
      "primary_economy_id": 4,
      "primary_economy": "Industrial",
      "power": "Arissa Lavigny-Duval",
      "power_state": "Exploited",
      "power_state_id": 32,
      "needs_permit": false,
      "updated_at": 1496529498,
      "simbad_ref": "",
      "controlling_minor_faction_id": 31816,
      "controlling_minor_faction": "1 G. Caeli Empire League",
      "reserve_type_id": 3,
      "reserve_type": "Common",
      "minor_faction_presences": [
        {
          "minor_faction_id": 31816,
          "state_id": 16,
          "influence": 53.1,
          "state": "Boom"
        },
        {
          "minor_faction_id": 54517,
          "state_id": 64,
          "influence": 8.3,
          "state": "Civil War"
        },
        {
          "minor_faction_id": 54518,
          "state_id": 72,
          "influence": 2.2,
          "state": "Outbreak"
        },
        {
          "minor_faction_id": 54519,
          "state_id": 64,
          "influence": 4.2,
          "state": "Civil War"
        },
        {
          "minor_faction_id": 74917,
          "state_id": 16,
          "influence": 18.5,
          "state": "Boom"
        },
        {
          "minor_faction_id": 40897,
          "state_id": 80,
          "influence": 6.5,
          "state": "None"
        },
        {
          "minor_faction_id": 4017,
          "state_id": 80,
          "influence": 7.2,
          "state": "None"
        }
      ]
    }

## Update message

    {
      "header": {
        "softwareVersion": "2.3.3.0",
        "gatewayTimestamp": "2017-05-31T14:31:50.626309Z",
        "softwareName": "E:D Market Connector [Windows]",
        "uploaderID": "SillYcoNe"
      },
      "$schemaRef": "http:\/\/schemas.elite-markets.net\/eddn\/journal\/1",
      "message": {
        "StarSystem": "Hoff",
        "FactionState": "Boom",
        "SystemFaction": "Independents of Hoff",
        "timestamp": "2017-05-31T14:26:44Z",
        "SystemSecurity": "$SYSTEM_SECURITY_medium;",
        "SystemAllegiance": "Federation",
        "SystemEconomy": "$economy_Agri;",
        "StarPos": [
          -9.938,
          -69.219,
          -72.938
        ],
        "Factions": [
          {
            "Allegiance": "Federation",
            "FactionState": "Boom",
            "Influence": 0.426573,
            "Name": "Independents of Hoff",
            "Government": "Democracy"
          },
          {
            "Allegiance": "Federation",
            "FactionState": "Boom",
            "Influence": 0.250749,
            "Name": "Hoff Exchange",
            "Government": "Corporate"
          },
          {
            "Allegiance": "Federation",
            "FactionState": "Boom",
            "Influence": 0.064935,
            "Name": "Jaralland Group",
            "Government": "Corporate"
          },
          {
            "Allegiance": "Independent",
            "FactionState": "CivilWar",
            "Influence": 0.033966,
            "Name": "Official Hoff Order",
            "Government": "Dictatorship"
          },
          {
            "Allegiance": "Independent",
            "FactionState": "CivilWar",
            "Influence": 0.041958,
            "Name": "Hoff Vision Exchange",
            "Government": "Corporate"
          },
          {
            "Allegiance": "Federation",
            "FactionState": "Boom",
            "Influence": 0.120879,
            "Name": "Labour of Seleru Bao",
            "Government": "Democracy"
          },
          {
            "Allegiance": "Independent",
            "FactionState": "None",
            "Influence": 0.060939,
            "Name": "Clan of Hoff",
            "Government": "Anarchy"
          }
        ],
        "event": "FSDJump",
        "SystemGovernment": "$government_Democracy;"
      }
    }

## States

    "Boom":16
    "Bust":32
    "Civil Unrest":48 // CivilUnrest in FSDJump
    "Civil War":64 // CivilWar in FSDJump
    "Election":65
    "Expansion":67
    "Famine":37
    "Investment":101
    "Lockdown":69
    "None":80
    "Outbreak":72
    "Retreat":96
    "War":73
    null

## allegiance

    "Alliance":1
    "Empire":2
    "Federation":3
    "Independent":4

## government

    "Anarchy":16
    "Communism":32
    "Confederacy":48
    "Cooperative":80
    "Corporate":64
    "Democracy":96
    "Dictatorship":112
    "Feudal":128
    "Patronage":144
    "Prison Colony":150
    "Theocracy":160

## Power_state

    "Contested":48
    "Control":16
    "Exploited":32
    null

## reserve_type

    "Common":3
    "Depleted":5
    "Low":4
    "Major":2
    "Pristine":1
    null

## primary_economy

    "Agriculture":1
    "Extraction":2
    "High Tech":3
    "Industrial":4
    "Military":5
    "Refinery":6
    "Service":7
    "Terraforming":8
    "Tourism":9
    "None":10
    "Colony":11
    null

## Power

    "Aisling Duval"
    "Archon Delaine"
    "Arissa Lavigny-Duval"
    "Denton Patreus"
    "Edmund Mahon"
    "Felicia Winters"
    "Li Yong-Rui"
    "Pranav Antal"
    "Yuri Grom"
    "Zachary Hudson"
    "Zemina Torval"
    null

## security

    "Anarchy":64
    "High":48
    "Lawless":80
    "Low":16
    "Medium":32
    null

# ACTOR MODEL NOTES

## EVERYTHING IS AN ACTOR

If we are modelling using everything is an actor, we could spawn named actors for each system/faction,
(but this would generate 75k faction actors, and 20k system actors).

Each would hold a small amount of state for itself, systems would hold the faction list and other data,
factions would hold their systems they are active in, and influence value therein.
They could hold their influence history.

Each actor would send a message to a persistance actor for its type, which would check if there's a
d/b entry needed to update based on last updated time, and only write if it's newer.

There would be actors for:
 - reading (nightly) system jsonl, sending appropriate message to named system actor,
 - similar for the nightly faction jsonl
 - update from edsm.net (FSDJump) send change to system and faction actors.

This would give us chance to write massively concurrent simulations of influence changes.
Factions could become 'aware' of other factions in their neighbourhoods.

How would new factions come about?

## PROCESSING ACTORS

Actor for processing the system/faction jsonl entries to update the d/b, we just push lots of messages
onto the actor to process per line read.
Update actor would send messages to same actors to update.

This is a much simpler and smaller model.

## Duplicate System names

    Arti                           |     2
    Almar                          |     2

So when finding the system by its name in a journal event, there could be multiple.
We can find the closest to the matching name:

    Hoff | -9.9375 | -69.2188 | -72.9375

      "StarPos":[
         -9.938,
         -69.219,
         -72.938
      ],

    (u/distance-between
      {:x -9.9375 :y -69.2188 :z -72.9375}
      {:x -9.938  :y -69.219  :z -72.938})
    => 7.348469228349329E-4
    == 0.0007348
    < 0.1

In the case of Arti/Almar they are sufficiently far apart from each other to make
this test.

Because of the small difference here, we are going to ignore Jump events trying to 'reposition' stars due to
lack of precision.
It is simply good enough to get a distance between the stored version and the incoming version and say it is
within a level as shown.

# Faction DB Query

running a query against local db

    $ psql ed-factions ed
    => with mcmafia (name) as (values ('Kulici')) select (jl_within(name, 25.1)).* from mcmafia;

      sys   |       name       |     pop     |   d   | fc | pc | inf
    --------+------------------+-------------+-------+----+----+------
     Kulici | Kulici           |    28244875 |  0.00 |  6 |  0 | 36.7
     Kulici | Wargis           |    31900297 |  5.37 |  7 |  1 | 62.9
     ...

See analysis.clj for other functions.

A useful one for getting all loading data counts is:

    select (select count(*) from system) as systems, (select count(*) from system_faction) as sys_fac, (select count(*) from faction) as factions;


## License

Copyright © 2017-2018 Mark Fisher

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
