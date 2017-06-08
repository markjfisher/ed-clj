(defproject ed-clj "0.1.0-SNAPSHOT"
  :description "Elite Dangerous Tools in Clojure"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]

                 ;; utils
                 [org.clojure/tools.nrepl "0.2.12"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [org.clojure/algo.monads "0.1.6"]
                 [commons-io "2.5"]
                 [clj-time "0.13.0"]

                 ;; 0MQ for getting EDDB messages
                 [org.zeromq/jeromq "0.4.0"]
                 [org.zeromq/cljzmq "0.1.4" :exclusions [org.zeromq/jzmq]]

                 ;; json reading
                 [cheshire "5.7.1"]

                 ;; DB management
                 [com.layerware/hugsql "0.4.7"]
                 [org.postgresql/postgresql "9.4.1212"]
                 [hikari-cp "1.7.5"]
                 [conman "0.6.6"]
                 [mount "0.1.11"]
                 [prismatic/schema "1.1.6"]

                 ;; logging
                 [com.fzakaria/slf4j-timbre "0.3.5"]
                 [com.taoensso/timbre "4.10.0"]]

  :profiles {:dev     {:resource-paths ["resources/dev" "resources/test"]
                       :source-paths   ["dev" "dev/clj"]}
             :prod    {:resource-paths ["resources/prod"]}
             :uberjar {:aot  :all
                       :main ed.server}}

  :resource-paths ["sql" "resources"]

  :aliases {"godev" ["with-profile" "dev," "run" "-m" "ed.server"]})
