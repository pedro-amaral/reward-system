(ns user
  (:require [mount.core :as mount]
            reward-system.core))

(defn start []
  (mount/start-without #'reward-system.core/repl-server))

(defn stop []
  (mount/stop-except #'reward-system.core/repl-server))

(defn restart []
  (stop)
  (start))


