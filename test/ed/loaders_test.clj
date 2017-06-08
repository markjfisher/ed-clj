(ns ed.loaders-test
  (:require [ed.errors :as e]
            [ed.loaders :as f]
            [clojure.test :refer :all]))

(deftest faction-validations-test
  (testing "loading bad faction data"
    (is (= (e/fail "Failed to parse faction data {}:\nValue does not match schema: {:updated-at missing-required-key, :home-system-id missing-required-key, :government-id missing-required-key, :allegiance missing-required-key, :name missing-required-key, :state missing-required-key, :id missing-required-key, :state-id missing-required-key, :is-player-faction missing-required-key, :allegiance-id missing-required-key, :government missing-required-key}")
           (f/validate-faction "{}"))))

  (testing "loading good faction data returns map of faction and faction-state data"
    (is (= {:faction {:updated-at        1495741557
                      :home-system-id    560
                      :government-id     96
                      :allegiance        "Federation"
                      :name              "Social Ahauduwonai Future"
                      :id                4011
                      :is-player-faction false
                      :allegiance-id     3
                      :government        "Democracy"
                      :state             "Civil War"
                      :state-id          64}}
           (f/validate-faction "{\"id\":4011,\"name\":\"Social Ahauduwonai Future\",\"updated_at\":1495741557,\"government_id\":96,\"government\":\"Democracy\",\"allegiance_id\":3,\"allegiance\":\"Federation\",\"state_id\":64,\"state\":\"Civil War\",\"home_system_id\":560,\"is_player_faction\":false}")))))

(deftest system-validations-test
  (testing "loading bad system data"
    (is (= (e/fail "Failed to parse system data {}:\nValue does not match schema: {:y missing-required-key, :updated-at missing-required-key, :simbad-ref missing-required-key, :controlling-minor-faction missing-required-key, :reserve-type missing-required-key, :government-id missing-required-key, :security-id missing-required-key, :power-state missing-required-key, :allegiance missing-required-key, :security missing-required-key, :name missing-required-key, :primary-economy-id missing-required-key, :state missing-required-key, :z missing-required-key, :power missing-required-key, :primary-economy missing-required-key, :id missing-required-key, :minor-faction-presences missing-required-key, :population missing-required-key, :reserve-type-id missing-required-key, :controlling-minor-faction-id missing-required-key, :state-id missing-required-key, :x missing-required-key, :power-state-id missing-required-key, :allegiance-id missing-required-key, :needs-permit missing-required-key, :government missing-required-key, :is-populated missing-required-key, :edsm-id missing-required-key}")
           (f/validate-system "{}"))))

  (testing "loading good system data returns list of maps for faction and faction-state data"
    (is (= {:system {:allegiance                   "Independent"
                     :allegiance-id                4
                     :controlling-minor-faction    "The Order of Mobius"
                     :controlling-minor-faction-id 18979
                     :edsm-id                      1061
                     :government                   "Cooperative"
                     :government-id                80
                     :id                           1120
                     :is-populated                 true
                     :minor-faction-presences      [{:influence        12.6
                                                     :minor-faction-id 3992
                                                     :state            "Civil War"
                                                     :state-id         64}
                                                    {:influence        6.4
                                                     :minor-faction-id 3993
                                                     :state            "None"
                                                     :state-id         80}
                                                    {:influence        19.6
                                                     :minor-faction-id 3995
                                                     :state            "None"
                                                     :state-id         80}
                                                    {:influence        12.4
                                                     :minor-faction-id 3996
                                                     :state            "Civil War"
                                                     :state-id         64}
                                                    {:influence        37.2
                                                     :minor-faction-id 18979
                                                     :state            "None"
                                                     :state-id         80}
                                                    {:influence        11.8
                                                     :minor-faction-id 61579
                                                     :state            "Boom"
                                                     :state-id         16}]
                     :name                         "Apathaam"
                     :needs-permit                 false
                     :population                   3506390
                     :power                        "Arissa Lavigny-Duval"
                     :power-state                  "Control"
                     :power-state-id               16
                     :primary-economy              "Industrial"
                     :primary-economy-id           4
                     :reserve-type                 "Low"
                     :reserve-type-id              4
                     :security                     "Medium"
                     :security-id                  32
                     :simbad-ref                   ""
                     :state                        "None"
                     :state-id                     80
                     :updated-at                   1496798790
                     :x                            89.03125
                     :y                            -102.625
                     :z                            20.875}}
           (f/validate-system "{\"id\":1120,\"edsm_id\":1061,\"name\":\"Apathaam\",\"x\":89.03125,\"y\":-102.625,\"z\":20.875,\"population\":3506390,\"is_populated\":true,\"government_id\":80,\"government\":\"Cooperative\",\"allegiance_id\":4,\"allegiance\":\"Independent\",\"state_id\":80,\"state\":\"None\",\"security_id\":32,\"security\":\"Medium\",\"primary_economy_id\":4,\"primary_economy\":\"Industrial\",\"power\":\"Arissa Lavigny-Duval\",\"power_state\":\"Control\",\"power_state_id\":16,\"needs_permit\":false,\"updated_at\":1496798790,\"simbad_ref\":\"\",\"controlling_minor_faction_id\":18979,\"controlling_minor_faction\":\"The Order of Mobius\",\"reserve_type_id\":4,\"reserve_type\":\"Low\",\"minor_faction_presences\":[{\"minor_faction_id\":3992,\"state_id\":64,\"influence\":12.6,\"state\":\"Civil War\"},{\"minor_faction_id\":3993,\"state_id\":80,\"influence\":6.4,\"state\":\"None\"},{\"minor_faction_id\":3995,\"state_id\":80,\"influence\":19.6,\"state\":\"None\"},{\"minor_faction_id\":3996,\"state_id\":64,\"influence\":12.4,\"state\":\"Civil War\"},{\"minor_faction_id\":18979,\"state_id\":80,\"influence\":37.2,\"state\":\"None\"},{\"minor_faction_id\":61579,\"state_id\":16,\"influence\":11.8,\"state\":\"Boom\"}]}\n")))))
