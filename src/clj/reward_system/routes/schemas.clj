(ns reward-system.routes.schemas
  (:require [schema.core :as s]))

(s/defschema User
  {:_id Long
   :inviter (s/maybe Long)   
   :points Double
   :invitees [Long]})

