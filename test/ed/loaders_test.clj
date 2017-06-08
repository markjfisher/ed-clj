(ns ed.loaders-test
  (:require [ed.errors :as e]
            [ed.loaders :as f]
            [clojure.test :refer :all]))

(deftest faction-validations-test
  (testing "loading bad faction data"
    (is (= (e/fail "Failed to parse faction data {}:\nValue does not match schema: {:updated-at missing-required-key, :home-system-id missing-required-key, :government-id missing-required-key, :allegiance missing-required-key, :name missing-required-key, :id missing-required-key, :is-player-faction missing-required-key, :allegiance-id missing-required-key, :government missing-required-key}")
           (f/validate-faction "{}"))))

  (testing "loading good faction data returns list of maps for faction and faction-state data"
    (is (= [{:updated-at 1495741557, :home-system-id 560, :government-id 96, :allegiance "Federation", :name "Social Ahauduwonai Future", :id 4011, :is-player-faction false, :allegiance-id 3, :government "Democracy"}
            {:state "Civil War", :state-id 64, :id 4011, :updated-at 1495741557}]
           (f/validate-faction "{\"id\":4011,\"name\":\"Social Ahauduwonai Future\",\"updated_at\":1495741557,\"government_id\":96,\"government\":\"Democracy\",\"allegiance_id\":3,\"allegiance\":\"Federation\",\"state_id\":64,\"state\":\"Civil War\",\"home_system_id\":560,\"is_player_faction\":false}")))))
