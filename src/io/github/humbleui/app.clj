(ns io.github.humbleui.app
  (:import
    [io.github.humbleui.jwm App Platform Screen]))

(def platform
  (condp = Platform/CURRENT
    Platform/WINDOWS :windows
    Platform/X11     :x11
    Platform/MACOS   :macos))

(defn start [^Runnable cb]
  (App/start cb))

(defn terminate []
  (App/terminate))

(defmacro doui-async [& forms]
  `(let [p# (promise)]
     (App/runOnUIThread #(deliver p# (try ~@forms (catch Throwable t# t#))))
     p#))

(defmacro doui [& forms]
  `(let [res# (deref (doui-async ~@forms))]
     (if (instance? Throwable res#)
       (throw res#)
       res#)))

(defn- screen->clj [^Screen screen]
  {:id        (.getId screen)
   :primary?  (.isPrimary screen)
   :bounds    (.getBounds screen)
   :work-area (.getWorkArea screen)
   :scale     (.getScale screen)})

(defn primary-screen []
  (screen->clj (App/getPrimaryScreen)))

(defn screens []
  (mapv screen->clj (App/getScreens)))

(defn open-symbols-palette []
  (App/openSymbolsPalette))
