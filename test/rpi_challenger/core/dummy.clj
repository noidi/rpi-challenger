(ns rpi-challenger.core.dummy
  (:require [rpi-challenger.core.strike :as s]))

(def challenge {:question "1 + 2", :answer "3"})

(def hit-strike (s/make-strike
                  {:body "3", :status {:code 200, :msg "OK"}, :error nil}
                  challenge))

(def miss-strike (s/make-strike
                   {:body "4", :status {:code 200, :msg "OK"}, :error nil}
                   challenge))

(def fail-strike (s/make-strike
                   {:body nil, :status {:code 404, :msg "Not Found"}, :error nil}
                   challenge))

(def error-strike (s/make-strike
                    {:body nil, :status nil, :error "Connection was closed"}
                    challenge))
