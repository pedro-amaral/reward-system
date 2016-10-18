(ns reward-system.test.db.core
  (:require [clojure.test :refer :all]
            [reward-system.db.core :as db]
            [reward-system.config :refer [env]]            
            [mount.core :as mount]
            [monger.collection :as mc]
            [monger.operators :refer [$inc $push]]))

(use-fixtures  
  :once  
  (fn [f]    
    (mount/start
      #'reward-system.config/env
      #'reward-system.db.core/db*
      #'reward-system.db.core/db)
    (f)))

(use-fixtures 
  :each 
  (fn [f]
    (f)
    (mc/remove db/db "users")))

(deftest test-create-user
  (testing "create user - success"
    (let [result (db/create-user {:_id 1 :points 0.0 :invitees [] :inviter nil})
          user (mc/find-one-as-map db/db "users" {:_id 1})]
      (is (= 1 (:_id user)))
      (is (= 0.0 (:points user)))
      (is (= [] (:invitees user)))
      (is (nil? (:inviter user)))))
  
  (testing "create user with duplicate id - failed"
    (let [result (db/create-user {:_id 5 :points 0.0})]
      (is (thrown-with-msg? Exception #"duplicate key error"
            (db/create-user {:_id 5 :points 1.0}))))))

(deftest test-update-user
  (testing "update user points with $inc operator - success"
    (let [result-insert (mc/insert db/db "users" {:_id 1 :points 0.5})
          result-update (db/update-user 1 {$inc {:points (Math/pow 0.5 0)}})
          user (mc/find-one-as-map db/db "users" {:_id 1})]
      (is (= 1.5 (:points user)))))
  
  (testing "update user invitees array with $push operator - success"
    (let [result-insert (mc/insert db/db "users" {:_id 2 :invitees []})
          result-update (db/update-user 2 {$push {:invitees 1}})
          user (mc/find-one-as-map db/db "users" {:_id 2})]
      (is (= [1] (:invitees user))))))
             
      
    
  

