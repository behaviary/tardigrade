(ns frontend.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [cljsjs.react-bootstrap]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:progress "Science Quote"}))


;;___________
;; COMPONENTS

(defonce timer (atom (js/Date.)))

(defonce time-updater (js/setInterval
                       #(reset! timer (js/Date.)) 1000))


(defn date-display []
  (let [time-str (-> @timer .toDateString)]
    [:div
     [:p "Today: " [:span {:style {:color "red"}} time-str]]]))

(defn atom-textarea [value]
  [:textarea {:type "text"
              :class "col-xs-12 col-md-10"
              :value @value
              :placeholder "For science!"
              :style {:height "300px"}
              :on-change #(reset! value (-> % .-target .-value))}])

(defn upload-button []
  [:input {:type "file"
           :accepts "image/*"
           :class "col-xs-12 col-md-10 btn btn-default"}])

(defn submit-button []
  [:input {:type "submit"
           :class "btn btn-primary"}])

;; use when you need a react component
(def Button (reagent/adapt-react-class (aget js/ReactBootstrap "Button"))) ;; this works

(defn new-progress-form []
  (let [val (atom "")]
    (fn []
      [:form {:class "form-group"}
       [:p {:class "col-xs-12"}
        [date-display]]
       [:p {:class "col-xs-12"}
        [upload-button]]
       [:p {:class "col-xs-12"}
        [atom-textarea val]]
       [:p {:class "col-xs-12"}
        [submit-button]]])))

;;_______
;; VIEWS
(defn home-page []
  [:div [:h1 "Welcome to tardigrade"]
   [:div [:a {:href "/progress"} "View the progress page!"]]])

(defn summary-page []
  [:div [:h4 "No summaries yet..."]
    [:div [:a {:href "/new"} "Add progress to science!"]]])

(defn new-progress-page []
  [:div {:class "row"}
    [:div {:class "row"}
      [:h1 {:class "col-xs-12"} "Eureka! Add progress"]
      [:p {:class "col-xs-12"} "create notes and upload images as you please!"]]
    [:div {:class "row"} [new-progress-form]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; ROUTES

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/progress" []
  (session/put! :current-page #'summary-page))

(secretary/defroute "/new" []
  (session/put! :current-page #'new-progress-page))

;; ________________________
;; MOUNT AND INITIALIZE APP
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))

(init!)

;;__________________
;; FOR DEVELOPEMENT
(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;;(swap! app-state update-in [:__figwheel_counter] inc)
)
