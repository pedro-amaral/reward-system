(ns reward-system.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [reward-system.handler :refer :all]
            [reward-system.db.core :as db]
            [reward-system.config :refer [env]]            
            [mount.core :as mount]
            [monger.collection :as mc]))

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

(deftest test-general-users-route
  (testing "users route"
    (let [response ((app) (request :get "/api/users"))]
      (is (= 200 (:status response))))))

(deftest test-general-user-detail-route
  (testing "user detail route success"
    (let [user (db/create-user {:_id 5 :points 0 :invitees [] :inviter nil})
          response ((app) (request :get "/api/users/5"))]
      (is (= 200 (:status response)))))
  
  (testing "user detail route failed - user not found"
    (let [response ((app) (request :get "/api/users/1000"))]
      (is (= 404 (:status response))))))

(deftest test-general-new-invite-route 
  (testing "new invite route success"
    (let [response ((app) (-> (request :post "/api/users/new-invite") 
                              (body "{\"inviter\": 1, \"invitee\": 2}")
                              (content-type "application/json")))]
      (is (= 200 (:status response)))))
  
  (testing "new invite route failed - null inviter"
    (let [response ((app) (-> (request :post "/api/users/new-invite") 
                              (body "{\"inviter\": null, \"invitee\": 2}")
                              (content-type "application/json")))]
      (is (= 400 (:status response)))))
  
  (testing "new invite route failed - nil invitee"
    (let [response ((app) (-> (request :post "/api/users/new-invite") 
                              (body "{\"inviter\": 1, \"invitee\": null}")
                              (content-type "application/json")))]
      (is (= 400 (:status response)))))
  
  (testing "new invite route failed - invalid id"
    (let [response ((app) (-> (request :post "/api/users/new-invite") 
                              (body "{\"inviter\": \"a\", \"invitee\": \"2\"}")
                              (content-type "application/json")))]
      (is (= 400 (:status response)))))
  
  (testing "new invite route failed - missing param :inviter"
    (let [response ((app) (-> (request :post "/api/users/new-invite") 
                              (body "{\"invitee\": 2}")
                              (content-type "application/json")))]
      (is (= 400 (:status response)))))
  
  (testing "new invite route failed - missing param :invitee"
    (let [response ((app) (-> (request :post "/api/users/new-invite") 
                              (body "{\"inviter\": 1}")
                              (content-type "application/json")))]
      (is (= 400 (:status response)))))
  
  (testing "new invite route failed - missing params"
    (let [response ((app) (-> (request :post "/api/users/new-invite") 
                              (body "{}")
                              (content-type "application/json")))]
      (is (= 400 (:status response))))))

(deftest test-general-app-routes  
  (testing "main route"
    (let [response ((app) (request :get "/"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response ((app) (request :get "/invalid"))]
      (is (= 404 (:status response))))))

(deftest test-invites-workflow-sample
  (testing "
    Example invites sequence:
    1 2
    1 3
    3 4
    2 4
    4 5
    4 6
    The score is:
    1 - 2.5 (2 because he invited 2 and 3 plus 0.5 as 3 invited 4)
    3 - 1 (1 as 3 invited 4 and 4 invited someone)
    2 - 0 (even as 2 invited 4, it doesn't count as 4 was invited before by 3)
    4 - 0 (invited 5 and 6, but 5 and 6 didn't invite anyone)
    5 - 0 (no further invites)
    6 - 0 (no further invites)"
    (let [response1 ((app) (-> (request :post "/api/users/new-invite") 
                               (body "{\"inviter\": 1, \"invitee\": 2}")
                               (content-type "application/json")))
          response2 ((app) (-> (request :post "/api/users/new-invite") 
                               (body "{\"inviter\": 1, \"invitee\": 3}")
                               (content-type "application/json")))
          response3 ((app) (-> (request :post "/api/users/new-invite") 
                               (body "{\"inviter\": 3, \"invitee\": 4}")
                               (content-type "application/json")))
          response4 ((app) (-> (request :post "/api/users/new-invite") 
                               (body "{\"inviter\": 2, \"invitee\": 4}")
                               (content-type "application/json")))
          response5 ((app) (-> (request :post "/api/users/new-invite") 
                               (body "{\"inviter\": 4, \"invitee\": 5}")
                               (content-type "application/json")))
          response6 ((app) (-> (request :post "/api/users/new-invite") 
                               (body "{\"inviter\": 4, \"invitee\": 6}")
                               (content-type "application/json")))]
      (is (= 200 (:status response1)))
      (is (= 200 (:status response2)))
      (is (= 200 (:status response3)))
      (is (= 200 (:status response4)))
      (is (= 200 (:status response5)))
      (is (= 200 (:status response6)))
      (is (= 2.5 (:points (db/get-user 1))))
      (is (= 0.0 (:points (db/get-user 2))))
      (is (= 1.0 (:points (db/get-user 3))))
      (is (= 0.0 (:points (db/get-user 4))))
      (is (= 0.0 (:points (db/get-user 5))))
      (is (= 0.0 (:points (db/get-user 6)))))))
      

