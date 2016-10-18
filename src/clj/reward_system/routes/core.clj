(ns reward-system.routes.core
  (:require [reward-system.db.core :as db]
            [monger.operators :refer [$inc $push]]
            [clojure.string :refer [split]]
            [clojure.tools.logging :as log]
            [clojure.java.io :refer [reader]]))

(defn update-inviters-points! [id level]
  (if-not (nil? id)
    (let [user (db/get-user id)]
      (if-not (empty? user) 
        (do 
          (db/update-user id {$inc {:points (Math/pow 0.5 level)}})
          (update-inviters-points! (get user :inviter) (+ level 1)))))))
  
(defn save-new-invite! [inviter invitee]
  (let [user-inviter (db/get-user inviter) user-invitee (db/get-user invitee)]
    (do 
      (if (empty? user-invitee)
        (db/create-user {:_id invitee :inviter inviter :points 0.0 :invitees []}))
      (if (empty? user-inviter) 
        (db/create-user {:_id inviter :inviter nil :points 0.0 :invitees [invitee]})
        (do
          (if (empty? (get user-inviter :invitees))
            (update-inviters-points! (get user-inviter :inviter) 0))
          (if (empty? (filter #(= % invitee) (get user-inviter :invitees))) 
            (db/update-user inviter {$push {:invitees invitee}})))))))

(defn save-invites-from-file! [file]
  (with-open [file-reader (reader file)]
    (do
      (doseq [line (line-seq file-reader)]        
        (try
          (if-not (empty? line)
           (do
             (def ids (map #(Integer/parseInt %) (split line #"\s")))
             (if (= 2 (count ids))
               (save-new-invite! (first ids) (last ids))
               (throw (Exception. "Invalid values count. 
                        It is required exactly two.")))))
          (catch Exception e
           (log/error (str "Error on line content '" line 
                           "': " (.getMessage e))))))
      (db/get-users))))

