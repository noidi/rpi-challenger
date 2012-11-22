(ns rpi-challenger.views
  (:use net.cgrand.enlive-html)
  (:require [rpi-challenger.core.participant :as p]
            [rpi-challenger.core.strike :as s]
            [clj-time.core :as time])
  (:import [org.joda.time LocalDateTime]))

; layout

(deftemplate layout "public/layout.html"
  [{:keys [title main]}]
  [:title ] (content title)
  [:h2 ] (content title)
  [:#main ] (substitute main))


; participants list

(def *participants-row [:#participants [:tr (nth-of-type 2)]])
(def *participant-link [[:a (nth-of-type 1)]])
(def *participant-score [[:td (nth-of-type 2)]])

(defsnippet participants-row "public/tournament.html" *participants-row
  [participant]
  *participant-link (do->
                      (content (:name participant))
                      (set-attr :href (str "/participant-" (:id participant))))
  *participant-score (content (str (:score participant))))

(defsnippet tournament-overview "public/tournament.html" [:body :> any-node]
  [participants]
  *participants-row (content (map #(participants-row %) participants)))


; strikes list

(defn format-timestamp
  [{timestamp :timestamp}]
  (str (LocalDateTime. timestamp)))

(defn format-status-or-error
  [{{status :status, error :error} :response}]
  (if (nil? error)
    (str (:code status) " " (:msg status))
    (str error)))

(defn strikes-row-class
  [strike]
  (cond
    (s/error? strike) "error"
    (s/miss? strike) "miss"
    (s/hit? strike) "hit"))

(defsnippet strikes-row "public/strikes-list.html" [:table [:tr (nth-of-type 2)]]
  [strike]
  [:tr ] (set-attr :class (strikes-row-class strike))
  [[:td (nth-of-type 1)]] (content (format-timestamp strike))
  [[:td (nth-of-type 2)]] (content (:question (:challenge strike)))
  [[:td (nth-of-type 3)]] (content (:answer (:challenge strike)))
  [[:td (nth-of-type 4)]] (content (:body (:response strike)))
  [[:td (nth-of-type 5)]] (content (format-status-or-error strike)))

(defsnippet strikes-list "public/strikes-list.html" [:table ]
  [strikes]
  [[:tr (nth-of-type 4)]] (substitute)
  [[:tr (nth-of-type 3)]] (substitute)
  [[:tr (nth-of-type 2)]] (substitute (map #(strikes-row %) strikes)))


; detail page

(defsnippet participant-details "public/participant.html" [:body :> any-node]
  [participant]
  [:#name ] (content (:name participant))
  [:#url ] (content (:url participant))
  [:#score ] (content (str (:score participant)))
  [:#recent-failures ] (substitute (strikes-list (reverse (p/recent-failures participant))))
  [:#current-round ] (substitute (strikes-list (reverse (p/current-round participant)))))


; pages

(defn tournament-overview-page
  [participants]
  (layout {:title "Tournament Overview"
           :main (tournament-overview participants)}))

(defn participant-details-page
  [participant]
  (layout {:title "Participant Details"
           :main (participant-details participant)}))
