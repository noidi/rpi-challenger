(ns rpi-challenger.main
  (:use [clojure.tools.cli :only [cli]]
        ring.adapter.jetty
        ring.middleware.file
        ring.middleware.reload
        ring.middleware.stacktrace)
  (:require [rpi-challenger.app :as app]
            [rpi-challenger.routes :as routes])
  (:gen-class ))

(defn make-webapp []
  (let [app (app/make-app)]
    (app/start app)
    (->
      app
      (routes/make-routes)
      (wrap-reload)
      (wrap-stacktrace))))

(defn run [options]
  (run-jetty (make-webapp) options))

(defn start [options]
  (.start (Thread. (fn [] (run options)))))

(defn -main [& args]
  (let [[options args banner] (cli args
    ["--port" "Port for the HTTP server to listen to" :default 8080 :parse-fn #(Integer. %)]
    ["--help" "Show this help" :flag true])]
    (when (:help options)
      (println banner)
      (System/exit 0))
    (start options)))
