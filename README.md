# ed-clj

A Clojure library for Elite Dangerous Tools

## DEV WALKTHROUGH

In intellij, start a repl for the project.
Run the following to load all the 'state'

    (dev)  ;; puts you in dev namespace with application loaded
    (go)   ;; loads the state objects, e.g. config, *db*

You can now run d/b functions etc from ed.db namespace, e.g.

    (ed.db/get-faction {:id 0})

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
See the config.edn files for d/b etc config.


## DONE

- database definitions
- dev environment

## TODO

1. Import all factions: populate 'factions' (key: faction_id, updated_at)
2. Import all systems_populated, populate 'system' (key: system_id, updated_at), 'system_faction' (key: system_id, faction_id, updated_at)
3. Process FSDJump events and update 'system_faction'

How does time affect the data? Add 'updated_at' field to PK

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
    "Civil Unrest":48
    "Civil War":64
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

## License

Copyright © 2017 Mark Fisher

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
