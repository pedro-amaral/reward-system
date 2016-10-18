(ns reward-system.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [reward-system.routes.schemas :refer :all]
            [reward-system.routes.core :as rs]
            [reward-system.db.core :as db]
            [ring.swagger.upload :refer [wrap-multipart-params TempFileUpload]]))

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Reward System"
                           :description "Manages user invites and correlated reward points"}}}}
  
  (context "/api" []
    :tags ["users"]

    (GET "/users" []
      :return       [User]
      :summary      "Returns all users information."
      (ok (db/get-users)))
    
    (GET "/users/:id" []
      :return       User
      :path-params  [id :- Long]
      :summary      "Returns a user information."
      (let [user (db/get-user id)]
        (if user
          (ok user)
          (not-found "User not found."))))
    
    (POST "/users/new-invite" []
      :body-params  [inviter :- Long, invitee :- Long]
      :summary      "Saves a new invite information. It will create the non-existing 
                    users automatically."
      (ok (rs/save-new-invite! inviter invitee)))
    
    (POST "/users/invites-file" []
      :multipart-params  [file :- TempFileUpload]
      :middleware        [wrap-multipart-params]
      :summary           "Handles invites file upload."
      :return            [User]
      (ok (rs/save-invites-from-file! (get file :tempfile))))))

