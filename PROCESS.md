# Process book
#####  Nadeche Studer
### Mars Weather Explorer
## Day 2| 31-05-2016
Yesterday I found out there is already an app in the Google play store named Mars Weather App. So today I decided to give this project the name "Mars Weather Explorer". I think this also covers the concept of this app a bit better because it tries to emphasize a bit on the feeling of exploring a new place.

The news on the [NASA Curiosity site](http://mars.nasa.gov/msl/mission/mars-rover-curiosity-mission-updates/) has more interesting content than the twitter news feed. However there is no API to collect these news articles. To bring these stories in the app the site will have to be screped.There is an open java libary to do this, but I am unsure if it can be implemented within an android app. So when all other features are implemented if there is enough time I can try to implement this. However if there is not enough time the twitter feed will be just fine.
## Day 3| 01-06-2016
I spend a lot of time making the UML diagram. I chose to save the user settings in an instance of SharedPreferences. This seems like a simple way to store primitive data types. Working on the UI I decided to move the option to ask the user from till when they want to see weather data to the action bar. This seems more consequent design wise withe the rest op the app.
## Day 4| 02-06-2016
Today I spend moost of my time figuring out how to get the swipeviews working. I had a big bug trying to implement the material theme. I choose to have my app work from android version 21 specialy so I could use that, but everything was setup by android studio using AppCompat again. It is frustrating that Goolge wants to promote using material design, but they make it dam hard. So now again I am stuck with AppCompat, when I have time I will try to make it work with Theme.Material.
## Day 5| 03-06-2016
Today I worked on the layout. I still have a feew bugs in the layout of the weather data screen. I still have to search how to dysplay all data on top of the image, right now it doesn't look very nice and it doesn't fit in sceen.
## Day 6| 04-06-2016
Today I figured out the bugs with the layout. The imageView had to be set to adjustToImageViewBounds: true. And bauces of the coordinator layout I have to set all views within a linear layout to prevent the layout extending behind the navigation bar. I am thinking about how to work with teh action bar and different icons on the different screens. Because not all options are avalible with every screen.
## Day 7| 06-06-2016
I am thinking about changes to the UI. Particularly the setting for temperature unit. To change it to maybe a FAB or an icon in the action bar that works as a sitch. So tab means change the the other unit and change the icon. It seems a bit redundent to have a seperate dialog for such a simpe single setting.
## Day 8| 07-06-2016
Today I have been strugeling with the context of the fragments and the context of the activity. The problem I had was the call to the AcyncTask. Yesterday I organised that AcyncTask works with the fragment context. Today I tryed to implement the search for a perticular date, but this was called through the actionbar in the activity. In the end I fixed it by moving the specific fragment related icons to the fragment itself. This also means that I fixed the change of the actionbar icons when changing fragmet.
I stated woring on the function to download a rover photo. I decided to just pick the first photo that comes back, since the photo's don't differ much. Now I have to decide how the setup the dialog to make the download request. It needs to contain a list of camera's and a number picker for the solar date. Not sure if Im'going to use a list with a radio button or a onItemClickListener that automaticly brings the number from the number picker.
## Day 9| 08-06-2016
Today I decided to implement a spinner dropdown to let the user choose a camera. This seens like the most userfrendly solution because the dialog doesn't get so messy. I still have to think about how to position the downloaded photos. Some are smaller in with than the screen and some are so big the photo extends behind the weather data. What gives the user a nice view of the photo without compromizing the view of the weather data or an empty screen because to photo is so smal?
## Day 10| 09-06-2016
Today I decided to switch the libary I use to make a graph from Graph View to [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart). After concideration this liberay provides a graph more in the layout I have in mind. Also the rerender function it offers seems to work quite efficient, which I need in order to let the user choose from when till when they want to see data from. This library also works for IOS so if I want to continue with this project and extent it to IOS there should be no problem.
## Day 11| 10-06-2016
Today I worked on implementing the graph by making a dialog with two datepickers to make a request from. Working with the datepickers is not so straight forward. It is implemented by using a dialog fragment, so I need to get two dialog fragments in a dialog. I don't want a dialog with two date pickers to apear ontop of the dialog when a field is clicked. So how to implement this?
## Day 12| 11-06-2016
Today I tried to implement the date pickers throuhg xml instead of a fragment. This brings it's own set of problems, but seems to work better. The xml doesn't render in Android Studio but works when run in an emulator or a device. But two date pickers above one an other doesn't fit the dialog screen so I needed to set the whole thing in a scrollView. But now the buttons a the bottom are not nice in view when the dialog pops up. So the best thing is to put it in a alert dialog with fixed buttons at the bottom. Today I finally got that custom layout for the aleart dialog with two date pickers nice and centered in the alert dialog.
## Day 13| 12-06-2016
Today I spend my time at making a request to the API to work from the alert date picker dialog. I made an extra HttpRequestmodel constructor and a seperate method in the AsyncTask to handle multiple pages of data coming back. I implemented the setJsonToView method in the graphDataFragment to extract the right data from the Json coming back. To set this data to the graph I decided for now to make ArrayLists and .add() data to that to use later in setting the data to the graph. I had planned to put all data in a weatherDataModel and make a list of that, but looking at the timeconstrains I don't think I have time to implement multiple graphs for different data sets. So to save all data seems a waist of memory. If I have time I can always change that.
## Day 14| 13-06-2016
Today I got the graph to work as I was going for. To get the graph to update correctly I needed to declare a fiew fields more general. I styled the graph more detailed so the data is visable better. I got some feedback today in the hour of code about refactoring some of my code in the fragments. There were some options given, I am thinking about, but I'm not sure if they are going to work for me. The amount of code in the fragments is a lot right now because they have to handle the icons actions from the action bar and the return of the Async requests. Since the icons are the start and the return Json is the end of the Async it works context wise. But it would be nice if the processing of Json could be handled in a seperate class. But how to get the data from that class back to the fragment?
Also I am thinking about changing all dialogs to AlertDialogs in the app. This seems like a more coherent way of displaying dialogs. Something mayby for the last week?
## Day 15| 14-06-2016
Today I fixed some bugs from last week. I need to think about how the user changes the used temperature unit. Now it is part of the activity but I can't update the view from there because it is part of the fragment. It is redundent to have a settings dialog for only one setting. Maybe a menue item in the actionbar that acts as a swich would be better.
The total planning of completing the project is a bit sensitive. I still need to implement new things, the news feed and the widget. The choise depends on how complicated these things are to inplement.
In order to handle the downloaded roverphoto I decided to save the last downloaded photo internaly on device so it would also be available for the implementation of the widget.
## Day 16| 15-06-2016
Today I researched how to implement a widget. I have a working dumy widget that can open the app with the press of a button when clicked. But how the get actual information in the widget is an other thing. After today I think I have an idea of how to design the code structure. The update function from the app needs to call a extended servic class otherwise the updat method gets timed out when collecting data(after 5 sec). The service needs to create an instance of a model class that contains the data fields to display in the widget. The model class needs to fill its data fields from within by connceting to internet. Because of context related issues I can't use the async that already exists. And because the widget ui is not directly connceted with the service, I have to wait setting the vies unlit all internet calls are fineshed. The service can that set the vies with the data from the model class.
The only thing that is not clear now is if I should declare the service class within the widget class or in a seperate class. I found different tutorials that do one or the other. Android Developers is not very informative in this case.
The other thing I still need to figure out is how to get a picture. I can't acces my SharedPreferences because I don't have the Activity to pass from my widget. The internal filepath to where the photo is saved needs to come from somewhere else, but where and how?
## Day 17| 16-06-2016
Today I got my widget working with data from an internet connection. The project structure is prittymuch how I invisioned it yesterday. I modified my shared preferences so it can be accesed from the widget, this means that the problem I had with displaing the photo are now gone. To convert the recieved json to weather date I created a weather data mananger. This works very nice and I think this is also how I am going to refactor my code partially next week. Now the manager only works for the widget only.











