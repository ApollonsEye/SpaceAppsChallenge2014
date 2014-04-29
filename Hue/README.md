#Space Weather Alert by Philips Hue

Python scripts to change colors of LED bulbs and to issue alert depending on space weather data aveilable online

##Requirements

- [Philips Hue](http://meethue.com/)
- [Python 2.7](https://www.python.org/)
- [Phue](https://github.com/studioimaginaire/phue)
- [Pandas](http://pandas.pydata.org/)
- [Pygame](http://www.pygame.org/news.html)
- Time-based Job Scheduler(ex.cron)

##Scripts

- ace.py: Set colors of lights and issue alert based on Real-time ACE Satellite Data by NOAA
 - Proton density
 - Bulk speed
 - Integral Proton Flux (>10Mev)
- spaceweather.py: Set colors of lights based on space weather forecast by NICT
 - Solar flares
 - Geomagnetic activity
 - Solar protons
- cron_examples.txt: Cron job examples

##Data Sourses

- [Space Weather Forecast by NICT](http://www2.nict.go.jp/aeri/swe/swx/swcenter/tok_forecast.txt)
- Real-time ACE Satellite Data by NOAA
 - [1-minute averaged Real-time Bulk Parameters of the Solar Wind Plasma](http://www.swpc.noaa.gov/ftpdir/lists/ace/ace_swepam_1m.txt)
 - [5-minute averaged Real-time Integral Flux of High-energy Solar Protons](http://www.swpc.noaa.gov/ftpdir/lists/ace/ace_sis_5m.txt)
