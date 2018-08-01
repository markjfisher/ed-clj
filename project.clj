(defproject ed-clj "0.1.0-SNAPSHOT"
  :description "Elite Dangerous Tools in Clojure"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :java-agents [[co.paralleluniverse/quasar-core "0.7.9"]]
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.reader "1.2.2"]

                 ;; utils
                 [org.clojure/tools.nrepl "0.2.13"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [org.clojure/algo.monads "0.1.6"]
                 [commons-io "2.6"]
                 [clj-time "0.14.3"]

                 ;; 0MQ for getting EDDB messages
                 [org.zeromq/jeromq "0.4.3"]
                 [org.zeromq/cljzmq "0.1.4" :exclusions [org.zeromq/jzmq]]

                 ;; Pulsar for light threads
                 [co.paralleluniverse/pulsar "0.7.9"]

                 ;; json reading
                 [cheshire "5.8.0"]

                 ;; DB management
                 [com.layerware/hugsql "0.4.8"]
                 [org.postgresql/postgresql "42.2.2"]
                 [hikari-cp "2.4.0"]
                 [conman "0.7.8"]
                 [mount "0.1.12"]
                 [prismatic/schema "1.1.9"]

                 ;; logging
                 [com.fzakaria/slf4j-timbre "0.3.8"]
                 [com.taoensso/timbre "4.10.0"]]

  :profiles {:dev     {:resource-paths ["resources/dev" "resources/test"]
                       :source-paths   ["dev" "dev/clj"]
                       :jvm-opts       ["-Xmx2g" "-Xms1g" "-server" "-Dco.paralleluniverse.fibers.detectRunawayFibers=false"]}
             :prod    {:resource-paths ["resources/prod"]
                       :jvm-opts       ["-Xmx4g" "-Xms2g" "-server" "-Dco.paralleluniverse.fibers.detectRunawayFibers=false"]}
             :uberjar {:aot  :all
                       :main ed.server}}

  :resource-paths ["sql" "resources"]

  :aliases {"godev" ["with-profile" "dev," "run" "-m" "ed.server"]
            "goprod" ["with-profile" "prod," "run" "-m" "ed.server"]
            "gozmq" ["with-profile" "prod," "run" "-m" "ed.zmq"]})
