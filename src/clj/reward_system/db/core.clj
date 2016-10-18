(ns reward-system.db.core
    (:require [monger.core :as mg]
              [monger.collection :as mc]
              [monger.operators :refer :all]
              [mount.core :refer [defstate]]
              [reward-system.config :refer [env]]))

(defstate db*
  :start (-> env :database-url mg/connect-via-uri)
  :stop (-> db* :conn mg/disconnect))

(defstate db
  :start (:db db*))

(defn create-user [user]
  (mc/insert db "users" user))

(defn update-user [id update-map]
  (mc/update db "users" {:_id id} update-map))

(defn get-users []
  (mc/find-maps db "users"))

(defn get-user [id]
  (mc/find-one-as-map db "users" {:_id id}))
