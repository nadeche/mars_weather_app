# Process book
#####  Nadeche Studer
### Mars Weather Explorer
# Day 2
Yesterday I found out there is already an app in the Google play store named Mars Weather App. So today I decided to give this project the name "Mars Weather Explorer". I think this also covers the concept of this app a bit better because it tries to emphasize a bit on the feeling of exploring a new place.

The news on the [NASA Curiosity site](http://mars.nasa.gov/msl/mission/mars-rover-curiosity-mission-updates/) has more interesting content than the twitter news feed. However there is no API to collect these news articles. To bring these stories in the app the site will have to be screped.There is an open java libary to do this, but I am unsure if it can be implemented within an android app. So when all other features are implemented if there is enough time I can try to implement this. However if there is not enough time the twitter feed will be just fine.
# Day 3
I spend a lot of time making the UML diagram. I chose to save the user settings in an instance of SharedPreferences. This seems like a simple way to store primitive data types. Working on the UI I decided to move the option to ask the user from till when they want to see weather data to the action bar. This seems more consequent design wise withe the rest op the app.
# Day 4
Today I spend moost of my time figuring out how to get the swipeviews working. I had a big bug trying to implement the material theme. I choose to have my app work from android version 21 specialy so I could use that, but everything was setup by android studio using AppCompat again. It is frustrating that Goolge wants to promote using material design, but they make it dam hard. So now again I am stuck with AppCompat, when I have time I will try to make it work with Theme.Material.
