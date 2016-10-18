(ns reward-system.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [reward-system.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[reward_system started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[reward_system has shut down successfully]=-"))
   :middleware wrap-dev})
