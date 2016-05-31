# Design Document
###### Nadeche Studer
### Mars Weather Explorer
##### Classes overview
##### UI Sketshes
##### API's and plugins/ libarys
The app will use three different API's as data sources. These API's all return a JSON object which can be put in java model classes to work with the data and display it.
- The {MAAS} API will be used for all weather data. [marsweather.ingenology](http://marsweather.ingenology.com/)
- The NASA Mars Photos API will be used to get all rover photos. [Mars Photos](https://api.nasa.gov/api.html#MarsPhotos)
- The Twitter API will be used to collect the latest tweets from Curiosity. [Twitter API](https://dev.twitter.com/rest/public) & [Curiosity Twitter Account](https://twitter.com/marscuriosity)

To display the graphs in the app displaying the past weather data the libary [GRAPH VIEW](http://www.android-graphview.org/) will be used. This libary can handle realtime data, so it should be able to handle a flexible x axis. This way the user can tell how much of the past weather data he wants to view.

The twitter news feed is nice and simple to implement with the twitter API, but the news on the [NASA Curiosity site](http://mars.nasa.gov/msl/mission/mars-rover-curiosity-mission-updates/) has more interesting content. However there is no API to collect these news articles. To bring these stories in the app the site will have to be screped. [JSOUP](https://jsoup.org/) is an open java libary to do this, but it is unsure if it can be implemented within an android app. When all other features are implemented if there is enough time this can be an option to implement. However if there is not enough time the twitter feed will be just fine.