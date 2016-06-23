# Process report
###### Nadeche Studer
![screenshot weather data](doc/screenshot_weather_data_small.png)
Curiosity sends back weather data to earth nearly every Martian day and images of its environment every Martian day. Although NASA makes this data publicly available, you have to look for daily updates yourself. This app automatically collects the latest data and provides an interface throuth which the data is easaly browsable. This way you can feel like you are in direct contact with a distant explorer, experiencing a part of the excitement human explores must have had when they found an unexplored piece of the earth.
#### Technical Design
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
#### Challenges
###### The implementation of the graph
To use the library I choose was not that difficult. But with the functionality of loading a graph with the data took some time before I had it working. I had some trouble working with the date pickers in the dialog where the user can choose the range of time to see data about. First I tried to get it working in a normal dialog, but this didn't work. From my process log: "Working with the date pickers is not so straight forward. It is implemented by using a dialog fragment, so I need to get two dialog fragments in a dialog. " This tuned out not to be possible so in the end I had to implement an alert dialog with a custom layout, so I could use the xml to put two date pickers in one dialog. Although this was also possible in a normal dialog the layout was very ugly because both date pickers have a little bit too much height to fit in a dialog. So to make use of a scrollViewLayout in a way that looked nice I had to implement an alert dialog.
After this was all figured out I still had to collect data from multiple pages and figure out how to get them in the graph in the most efficient way. My process log shows it took me a total of five days to get all this working. Some days later I realized though that the graph wasn't displaying the data correctly because it did not account for the fact that the data coming back did not include data about every day. The library I chose to use cannot handle this in a simple way, so I had to modify my methods so now they seem quite complicated. The solution I chose to implement was also suggested by the creator of the library in an answer to a question on github about this.
###### The implementation of the widget
The implementation of the widget was a challenge because I had to implement something that I had no knowledge about beforehand. To get a dummy widget running from my app was not the difficult part,  for that I found some good tutorials. But making an internet request from the widget was a lot of research on how to implement this. I learned about Services and the creation of separate threads to run code on. I learned that the WidgetProvider can only run for 5 seconds before it gets timed out so this is why you need a separate service to make data requests. I learned how to make the execution of the code wait until a thread is finished. This all took me in total three days, one of which I did very little coding but mostly research.
######  Working with different kind of contexts
Throughout the entire project this has been a challenge. I had no idea when I started that using a tab layout with swipe views would mean I had to deal with this problem. Before I had some basic knowledge about how to pass the context around. Now I have not only a much deeper understanding of contexts and fragments but also where they are used for and why they are needed. Working with this problem throughout the project was frustrating however because finding a solution for a problem mend that more than once the solution was not as I envisioned it which than took a lot of research to get it working.
###### Resizing the downloaded photo
Although I had calculated that this would be a problem I had to deal with in this project, the timing sucked.  When downloading the photo in the app there seemed no problem in the size, however loading the photo in the widget caused a problem. It turned out that the widget cannot process images bigger than one and a half times the screen size. This meant that I had to implement a resize functionality which was not calculated in my planning. Implementing it turned out to be much harder than I thought. There is much information available about this subject but also a lot of different ways to resize an image programmatically. I tried several ways using the BitmapFactory with BitmapOptions, using a library (Glide, I also looked at Picasso) in the end I used the methods that are part of the Bitmap class in combination with a matrix object. This object was also new for me.
The challenge was especially in getting the image resized with the width of the screen as a maximum but conserving the aspect ratio. Most ways need a width and height specified. Than to get this system working from the context of the app and the widget was some figuring out. I total this implementation cost me a day which does not seem like much but I had planned to do much more in this day. This affected in how far I have come implementing my application till now.
#### Changes
I didn't implement all features discussed in my design document simply because there was no time left. During the third week I made the decision that implementing a widget was more challenging and a bigger asset to my app than implementing a newsfeed. In my opinion a widget is an important part of a weather app, where a newsfeed is not really. The newsfeed feature was already a bit off topic to begin with. For me it seems more valuable for the user to receive automatic updates from this app on the users home screen. This is also why there is no extra graph: no time and not a big asset.
I changed the UI for the temperature setting because in use by myself it felt a bit unnecessary to deploy an entire dialog for this settings change.
The design of my application code is significantly different than what I envisioned before. The structure it has now grew during coding and refactoring. Which I think is better than holding on to a plan based on just an idea. In practice things turn out to be different than you thought. Given more time I would have liked to have ordered my code files in to separate directories so there is a clear structure in the type of classes used and which belongs to which.
There are no major changes in the code itself that I would like to make. I still have a lot of inspiration for little UI changes though. Mostly in the layout but also a few in the UI itself. For example given more time I would have liked to change all dialogs to alert dialog interfaces to give the app a more unified feel in design. I still have inspiration to ad animations in a few places. And work on the relation between the photo in the WeatherDataFragment and the weather data itself, by making the data able to slide from under the photo up over the photo when the user tabs on the data.