package mprog.nl.mars_weather_explorer;

/**
 * WeatherDataModel.java
 *
 * Created by Nadeche Studer
 *
 * This class contains all weather data fields used in the app and the widget.
 */
public class WeatherDataModel {

    private String terrestrial_date;    // the earth date
    private double min_temp_C;          // the minimum temperature in celsius
    private double max_temp_C;          // the maximum temperature in celsius
    private double min_temp_F = 0;      // the minimum temperature in Fahrenheit
    private double max_temp_F = 0;      // the maximum temperature in Fahrenheit
    private String sunset;              // the earth date and time of the Martian sunset
    private String sunrise;             // the earth date and time of the Martian sunrise
    private Long wind_speed = 0L;       // the wind speed Km/h
    private double pressure;            // the atmospheric pressure in Pa
    private String atmo_opacity;        // the weather status
    private long sol;                   // the martian solar day since Curiosity's landing (i.e., Curiosity's landing = 0)
    private String season;              // the martian season

    public String getAtmo_opacity() {
        return atmo_opacity;
    }

    public void setAtmo_opacity(String atmo_opacity) {
        this.atmo_opacity = atmo_opacity;
    }

    public double getMax_temp_C() {
        return max_temp_C;
    }

    public void setMax_temp_C(double max_temp_C) {
        this.max_temp_C = max_temp_C;
    }

    public double getMax_temp_F() {
        return max_temp_F;
    }

    public void setMax_temp_F(double max_temp_F) {
        this.max_temp_F = max_temp_F;
    }

    public double getMin_temp_C() {
        return min_temp_C;
    }

    public void setMin_temp_C(double min_temp_C) {
        this.min_temp_C = min_temp_C;
    }

    public double getMin_temp_F() {
        return min_temp_F;
    }

    public void setMin_temp_F(double min_temp_F) {
        this.min_temp_F = min_temp_F;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public long getSol() {
        return sol;
    }

    public void setSol(long sol) {
        this.sol = sol;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getTerrestrial_date() {
        return terrestrial_date;
    }

    public void setTerrestrial_date(String terrestrial_date) {
        this.terrestrial_date = terrestrial_date;
    }

    public Long getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(Long wind_speed) {
        this.wind_speed = wind_speed;
    }
}
