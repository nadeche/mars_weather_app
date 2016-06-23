# Process report
###### Nadeche Studer
![screenshot weather data](doc/screenshot_weather_data_small.png)
Curiosity sends back weather data to earth nearly every Martian day and images of its environment every Martian day. Although NASA makes this data publicly available, you have to look for daily updates yourself. This app automatically collects the latest data and provides an interface throuth which the data is easaly browsable. This way you can feel like you are in direct contact with a distant explorer, experiencing a part of the excitement human explores must have had when they found an unexplored piece of the earth.
###### Technical Design
![uml diagram of project structure](doc/Mars_Weather_Explorer_UML.png)
##### Activity
###### WeatherDataActivity
This class contains the activity that starts when the app starts. It handles the display of the actionbar and the dots at the bottom indicating which fragment is currently in view. These dots are implemented by the *SlidingIntroScreen* library. When the app is no longer visible on screen the activity sends a broadcast message to the **MarsWeatherWidgetProvider** to update is current state so the changes in saved in the **SharedPreferencesManager** are implemented.
##### Adapter
###### SwipeViewsAdapter
This class acts as an adapter between the fragment container in the **WeatherDataActivity** layout and the actual fragments to be displayed. This class is called by the **WeatherDataActivity**.
##### Fragments
###### BaseFragmentSuper
This class acts as a base for all fragments in the app. It handles the progressDialog when the fragment calls the **FetchDataAsync** task. And has an abstract method *setJsonToView(ReturnDataRequestModel returnDataRequest)* which is called in the **FetchDataAsync** task in order to return the fetched data to the right fragment. The abstract method *onTemperatureUnitChanged()* is called when the user changes the preferred temperature unit. The **sharedPreferencesManager** calls this method to let all fragments know the unit is changed and the views need to change with it.
###### GraphDataFragment
This class contains a fragment displaying a graph showing the maximum and minimum temperature over time on Mars. From the action bar a calendar icon opens a dialog where the user can select the period of time in earth dates from which to view temperature data about. When the fragment is created data about the last two weeks are loaded and displayed as default. To display the graph the library *MPAndroidChart* is used. The weather data is provided by the *marsweather.ingenology* api. To get the data the **FetchDataAsync** is called.
This class is made visible to the user by the **SwipeViewsAdapter**.
###### WeatherDataFragment
This class contains a fragment displaying weather data from Mars and a photo made on Mars by Curiosity. The weather data is provided by the *marsweather.ingenology* api. The photos are provided by the *Mars Photos* api. The user can load weather data and photos from any Martian solar day since Curiosity's landing by the interaction through dialogs triggered by icons in the action bar.
This class is made visible to the user by the **SwipeViewsAdapter**.
##### AsyncTasks
###### DownloadPhotoAsync
This class contains an **AsyncTask** implementation in order to download a photo. On construction it needs the *Activity* and the *ImageView* where the photo will be displayed.  When called it takes a string containing the url where the photo can be found. In the background through the **PhotoManager** the photo gets downloaded and resized to fit the width of the screen. Finally the photo is displayed in the given imageView and the photo is saved to internal storage. During these operations a progress dialog is displayed to the user saying "Loading photo..."
This class is called by the **WeatherDataFragment**.
###### FetchDataAsync
This class runs in the background of the activity to get data from the APIs. When constructed it needs the fragment context from where the call is made to pass the data back later and handle the display of a progress dialog. When called it needs to be passed a **RequestModel** to tell what kind of data to get. Before it tries to get data from internet it checks if there is an internet connection available through the **InternetManager**. If not, the user gets notified in *onPostExecute* by Toast notification. While fetching data it displays a progress dialog to the user saying "Loading data...". The data is passed back to the calling fragment for further processing.
This class is called by the **WeatherDataFragment** and the **GraphDataFragment**.
##### Managers
###### WeatherDataManager
This class manages the weather data from how it is received in json form to a form that can be handled in the application and the widget. It can convert data for the app and for the widget.
This manager is used by the **WeatherDataFragment** and the **UpdateWidgetService**.
###### PhotoManager
This class provides methods to handle photo related operations. It handles the download, resize and save to internal storage operations.
This manager is used by the **DownloadPhotoAsync** and the **UpdateWidgetService**.
###### SharedPreferencesManager
This class is a singleton that updates the SharedPreferences whenever they change. It provides information to the app and to the widget about the user's preferences and some system parameters.
This manager is called by the **WeatherDataFragment**, the **UpdateWidgetService**, the **GraphDataFragment**, the **PhotoManager**, the **WatherDataActivity**.
###### InternetManager
This class handles internet related operations that occur in multiple points in the app and the widget. It can make a check if there is an internet connection available. It can download data and convert it to a json object.
This class is called by the **UpdateWidgetService** and the **FetchDataAsync**.
##### Models
###### HttpRequestModel
This model contains information about the type of data request to be done. With the overload of the constructor, the fields of this model are set in a way so the with each constructor the rest of the code can reconstruct what kind of reqest it is. It also contains the specific url. This is why this model is passed on during the request process.
This model is used by the **WeatherDataFragment**, the **GraphDataFragment**, the **FetchDataAsync**, the **UpdateWidgetService** and part of the **ReturnDataRequestModel**.
###### ReturnDataRequestModel
This model acts as a collector of information about a data request in progress. During the execution of the data request, the gathered data is collected in this model for later processing. This model is used to pass on all vital information from *doInBackground()* to *OnPostExecute()*. This model is used by **FetchDataAsync** and both **fragments**.
###### WeatherDataModel
This model contains all weather data fields used in the app and the widget. It is used by both **fragments** and the **UpdateWidgetService**.
##### Widget
###### MarsWeatherWidgetProvider
This class extends an *AppWidgetProvider* that calls the **UpdateWidgetService** when the widget receives an update call either from the app or on its own once every 24 hours as specified in the xml of *widget_info_mars_weather.xml*.
###### UpdateWidgetService
This service provides the widget with data. It collects data about the weather on Mars and loads a photo from memory or from internet. To collect data from interent is runs a separete thread. After the service has run the widget layout is updated and the intent to open the app with a tap on the widget is set.
To collect data it uses the **PhotoManager** and the **InternetManager**.
##### Custom view classs
###### VerticalTextView
This class is an implementation of a custom textView. It converts a textView to a vertical textView. This is only used to create a y axis title next to the graph display in the **GraphDataFragment**.





