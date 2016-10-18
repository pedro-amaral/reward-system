(ns reward-system.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[reward_system started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[reward_system has shut down successfully]=-"))
   :middleware identity})
