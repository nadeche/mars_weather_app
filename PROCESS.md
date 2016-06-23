# Process book

#####  Nadeche Studer

### Mars Weather Explorer

## Day 2| 31-05-2016

Yesterday I found out there is already an app in the Google play store named Mars Weather App. So today I decided to give this project the name "Mars Weather Explorer". I think this also covers the concept of this app a bit better because it tries to emphasize a bit on the feeling of exploring a new place.



The news on the [NASA Curiosity site](http://mars.nasa.gov/msl/mission/mars-rover-curiosity-mission-updates/) has more interesting content than the twitter news feed. However there is no API to collect these news articles. To bring these stories in the app the site will have to be scraped. There is an open java library to do this, but I am unsure if it can be implemented within an android app. So when all other features are implemented if there is enough time I can try to implement this. However if there is not enough time the twitter feed will be just fine.

## Day 3| 01-06-2016

I spend a lot of time making the UML diagram. I chose to save the user settings in an instance of SharedPreferences. This seems like a simple way to store primitive data types. Working on the UI I decided to move the option to ask the user from till when they want to see weather data to the action bar. This seems more consequent design wise with the rest of the app.

## Day 4| 02-06-2016

Today I spend most of my time figuring out how to get the swipeviews working. I had a big bug trying to implement the material theme. I choose to have my app work from android version 21 especially so I could use that, but everything was setup by android studio using AppCompat again. It is frustrating that Google wants to promote using material design, but they make it dam hard. So now again I am stuck with AppCompat, when I have time I will try to make it work with Theme.Material.

## Day 5| 03-06-2016

Today I worked on the layout. I still have a few bugs in the layout of the weather data screen. I still have to search how to display all data on top of the image, right now it doesn't look very nice and it doesn't fit in screen.

## Day 6| 04-06-2016

Today I figured out the bugs with the layout. The imageView had to be set to adjustToImageViewBounds: true. And because of the coordinator layout I have to set all views within a linear layout to prevent the layout extending behind the navigation bar. I am thinking about how to work with the action bar and different icons on the different screens. Because not all options are available with every screen.

## Day 7| 06-06-2016

I am thinking about changes to the UI. Particularly the setting for temperature unit. To change it to maybe a FAB or an icon in the action bar that works as a switch. So tab means change the other unit and change the icon. It seems a bit redundant to have a separate dialog for such a simple single setting.

## Day 8| 07-06-2016

Today I have been struggling with the context of the fragments and the context of the activity. The problem I had was the call to the AcyncTask. Yesterday I organized that AcyncTask works with the fragment context. Today I tried to implement the search for a particular date, but this was called through the actionbar in the activity. In the end I fixed it by moving the specific fragment related icons to the fragment itself. This also means that I fixed the change of the actionbar icons when changing fragment.

I stated working on the function to download a rover photo. I decided to just pick the first photo that comes back, since the photo's don't differ much. Now I have to decide how the setup the dialog to make the download request. It needs to contain a list of camera's and a number picker for the solar date. Not sure if I'm going to use a list with a radio button or a onItemClickListener that automatically brings the number from the number picker.

## Day 9| 08-06-2016

Today I decided to implement a spinner dropdown to let the user choose a camera. This seems like the most user-friendly solution because the dialog doesn't get so messy. I still have to think about how to position the downloaded photos. Some are smaller in with than the screen and some are so big the photo extends behind the weather data. What gives the user a nice view of the photo without compromising the view of the weather data or an empty screen because to photo is so small?

## Day 10| 09-06-2016

Today I decided to switch the library I use to make a graph from Graph View to [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart). After consideration this library provides a graph more in the layout I have in mind. Also the rerender function it offers seems to work quite efficient, which I need in order to let the user choose from when till when they want to see data from. This library also works for IOS so if I want to continue with this project and extent it to IOS there should be no problem.

## Day 11| 10-06-2016

Today I worked on implementing the graph by making a dialog with two datepickers to make a request from. Working with the datepickers is not so straight forward. It is implemented by using a dialog fragment, so I need to get two dialog fragments in a dialog. I don't want a dialog with two date pickers to appear on top of the dialog when a field is clicked. So how to implement this?

## Day 12| 11-06-2016

Today I tried to implement the date pickers through xml instead of a fragment. This brings its own set of problems, but seems to work better. The xml doesn't render in Android Studio but works when run in an emulator or a device. But two date pickers above one another doesn't fit the dialog screen so I needed to set the whole thing in a scrollView. But now the buttons at the bottom are not nice in view when the dialog pops up. So the best thing is to put it in a alert dialog with fixed buttons at the bottom. Today I finally got that custom layout for the alert dialog with two date pickers nice and centered in the alert dialog.

## Day 13| 12-06-2016

Today I spend my time at making a request to the API to work from the alert date picker dialog. I made an extra HttpRequestmodel constructor and a separate method in the AsyncTask to handle multiple pages of data coming back. I implemented the setJsonToView method in the graphDataFragment to extract the right data from the Json coming back. To set this data to the graph I decided for now to make ArrayLists and .add() data to that to use later in setting the data to the graph. I had planned to put all data in a weatherDataModel and make a list of that, but looking at the time constraints I don't think I have time to implement multiple graphs for different data sets. So to save all data seems a waist of memory. If I have time I can always change that.

## Day 14| 13-06-2016

Today I got the graph to work as I was going for. To get the graph to update correctly I needed to declare a few fields more general. I styled the graph more detailed so the data is visible better. I got some feedback today in the hour of code about refactoring some of my code in the fragments. There were some options given, I am thinking about, but I'm not sure if they are going to work for me. The amount of code in the fragments is a lot right now because they have to handle the icons actions from the action bar and the return of the Async requests. Since the icons are the start and the return Json is the end of the Async it works context wise. But it would be nice if the processing of Json could be handled in a separate class. But how to get the data from that class back to the fragment?

Also I am thinking about changing all dialogs to AlertDialogs in the app. This seems like a more coherent way of displaying dialogs. Something maybe for the last week?

## Day 15| 14-06-2016

Today I fixed some bugs from last week. I need to think about how the user changes the used temperature unit. Now it is part of the activity but I can't update the view from there because it is part of the fragment. It is redundant to have a settings dialog for only one setting. Maybe a menu item in the actionbar that acts as a switch would be better.

The total planning of completing the project is a bit sensitive. I still need to implement new things, the news feed and the widget. The choice depends on how complicated these things are to implement.

In order to handle the downloaded roverphoto I decided to save the last downloaded photo internally on device so it would also be available for the implementation of the widget.

## Day 16| 15-06-2016

Today I researched how to implement a widget. I have a working dummy widget that can open the app with the press of a button when clicked. But how to get actual information in the widget is another thing. After today I think I have an idea of how to design the code structure. The update function from the app needs to call a extended service class otherwise the update method gets timed out when collecting data(after 5 sec). The service needs to create an instance of a model class that contains the data fields to display in the widget. The model class needs to fill its data fields from within by connecting to internet. Because of context related issues I can't use the async that already exists. And because the widget UI is not directly connected with the service, I have to wait setting the vies unlit all internet calls are finished. The service can that set the vies with the data from the model class.

The only thing that is not clear now is if I should declare the service class within the widget class or in a separate class. I found different tutorials that do one or the other. Android Developers is not very informative in this case.

The other thing I still need to figure out is how to get a picture. I can't access my SharedPreferences because I don't have the Activity to pass from my widget. The internal file path to where the photo is saved needs to come from somewhere else, but where and how?

## Day 17| 16-06-2016

Today I got my widget working with data from an internet connection. The project structure is pretty much how I envisioned it yesterday. I modified my shared preferences so it can be accessed from the widget, this means that the problem I had with displaying the photo are now gone. To convert the received json to weather date I created a weather data manager. This works very nice and I think this is also how I am going to refactor my code partially next week. Now the manager only works for the widget only.

## Day 18| 17-06-2016

Today I added to the widget that when you choose a different photo in the app the widget gets updated. I figured out how to sent a special broadcast from an intent in the app to the widget when the widget is no longer visible on screen(onStop()). The idea was that the widget reloads the photo in the background according to the camera set in sharedPreferences. However I overlooked the fact that when the user Changes the preferred temperature unit the widget also needs to reload. The widget doesn't hold an entire weatherDataModel in memory so when the unit preference changes the entire data needs to be reloaded. So in the end is was more efficient to call update() of the appWidgetProvider to reload the entire data displayed in the widget either from memory or from internet connection. Than to make separate broadcasts and reload the data in different times. The broadcast happened anyway when the app calls onStop(). So in the end of the day I could throw away most of the code I wrote today. But I learned a new thing today.

On the presentations of today I noticed that I am not as far behind as I thought, other people are much more behind than me. This calms me a bit. Although I cleared the entire weekend again to work on this project. Maybe an extra feature in my app I can build when I have to little time to implement a news feed is to be able to save a loaded photo to your gallery for personal use.

## Day 19| 18-06-2016

Today I have been busy with the freaking resizing of my photos. Why do they have to make it so complicated??? First I tried to resize with the use of BitmapFactory and BitmapFactory.Options according to the example on android.developers. This did not work, weird algorithm. Then I tried to use a library (Glide) this did not work either because it needed the output width and height and I only have the Width on the device screen. Since the photo's I load are all different sizes and ratios. What I want is to fit big photos to the width of the screen device. Now I have a solution finally after a day of struggling using a createBirmap function on the loaded bitmap itself. This needs a matrix object which I initialize by giving it the ratio by which it needs to downscale the photo. This ratio I calculate by dividing the device screen width by the photo width. Finally now it works. And it works for the widget as well when it downloads a photo it can also use the same method to rescale the image by use of a manager class. This class I can use for refactoring in the next week.

## Day 20| 19-06-2016

Today I had to choose between bowing up photos that are smaller than the screen size which makes them very pixelly or displaying them at the right size which makes them very small. I decided that either way the user will probably load a different photo so blowing up the photo regardless of size seems like the simpler solution program wise.

I added a library to my project to implement a dot navigation at the bottom of the screen. To write it myself seemed a bit redundant although I did research how to make this feature. The library I use now I choose because it seemed simple to implement at the time and after fixing a few bugs it actually is. The library is called [SlidingIntroScreen](https://github.com/MatthewTamlin/SlidingIntroScreen) and is a project that has still recent updates on github. This means for me that the project has good compatibility for applications running on the latest android versions.

## Day 21| 20-06-2016

This morning I helped one of my fellow students with a problem in code. This took longer than expected so I didn't have as much time to work today as I would have liked. But my fellow student was very happy.

In the afternoon I have been very busy with getting the sunset and rise converted to the device time zone and in a format that fits in the UI as it is. In the end I settled for displaying the day, moth and the hour and minute. The year is not relevant for the user and neither is the time zone the user is at the moment. In this way the data fits in the UI. I choose to display the month in letters because than it can't be confused with the day in some cases because not everyone expects the day to come first.

Then I spend time on changing the UI for the used temperature scale. To just have one switchable setting under settings seemed a bit redundant. Now there is a menu item that changes to the opposite of what is selected. Just pressing on that menu item changes the saved preference. When the menu is reopened the menu item title changes to the opposite. The only problem I have now is how to update the fragments where the data is displayed when the preference is changed. Since this menu item resides in the activity and not in one of the fragments. Now the date changes only when the date is reloaded.
## Day 22| 21-06-2016
Today I have been busy with just one bug. This was very frustrating because I am very stressed out by the coming deadline of this project. Somewhere the fragments lose the reference to the activity but it is not clear why. In the end late in the evening I solved it by making all fragment a singleton so the PagerAdapter gets the same instance of the fragment and the connection to the activity is no longer lost.
## Day 23| 22-06-2016
Today was big refactor and comment day. In the beginning it was going far too slow and I had to hurry since I lost an entire day yesterday. After consulting with one of the TA's I had a better plan of how to make my comments. This was not enough though, I have worked on it till 2 o'clock in the morning, but I had to finish this so I have tomorrow for the final report and final code related changes.
## Day 24| 23-06-2016
Today I am very tired but it is the last day so I have to push through. I found a bug, caused by refactoring yesterday, which I fixed. And made a clean sweep check through all xml files. And made very minor changes to the layout for small enhancements. The rest of the time I spend on writing the report.
Overall this was a stressful month in which I learned a lot.