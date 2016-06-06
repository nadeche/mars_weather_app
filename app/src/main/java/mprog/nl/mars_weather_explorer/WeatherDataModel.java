package mprog.nl.mars_weather_explorer;

/**
 * Created by Nadeche on 6-6-2016.
 */
public class WeatherDataModel {

    private String terrestrial_date;    // contains the earth date
    private long min_temp_C;            // contains the minimum temperature in celsius
    private long max_temp_C;            // contains the maximum temperature in celsius
    private long min_temp_F;            // contains the minimum temperature in Fahrenheit
    private long max_temp_F;            // contains the maximum temperature in Fahrenheit
    private String sunset;              // contains the earth date and time of the Martian sunset
    private String sunrise;             // contains the earth date and time of the Martian sunrise
    private Long wind_speed = 0L;       // contains the wind speed (scale unknown)
    private long pressure;              // contains the atmospheric pressure in Pa
    private String atmo_opacity;        // contains the weather status
    private long sol;                   // contains the martian solar day since Curiosity's landing (i.e., Curiosity's landing = 0)
    private String season;              // contains the martian season


    public String getAtmo_opacity() {
        return atmo_opacity;
    }

    public void setAtmo_opacity(String atmo_opacity) {
        this.atmo_opacity = atmo_opacity;
    }

    public long getMax_temp_C() {
        return max_temp_C;
    }

    public void setMax_temp_C(long max_temp_C) {
        this.max_temp_C = max_temp_C;
    }

    public long getMax_temp_F() {
        return max_temp_F;
    }

    public void setMax_temp_F(long max_temp_F) {
        this.max_temp_F = max_temp_F;
    }

    public long getMin_temp_C() {
        return min_temp_C;
    }

    public void setMin_temp_C(long min_temp_C) {
        this.min_temp_C = min_temp_C;
    }

    public long getMin_temp_F() {
        return min_temp_F;
    }

    public void setMin_temp_F(long min_temp_F) {
        this.min_temp_F = min_temp_F;
    }

    public long getPressure() {
        return pressure;
    }

    public void setPressure(long pressure) {
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
