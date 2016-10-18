(ns reward-system.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [reward-system.layout :refer [error-page]]
            [reward-system.routes.home :refer [home-routes]]
            [reward-system.routes.services :refer [service-routes]]
            [compojure.route :as route]
            [reward-system.env :refer [defaults]]
            [mount.core :as mount]
            [reward-system.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    #'service-routes
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
