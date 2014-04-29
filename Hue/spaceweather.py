import urllib2
from phue import Bridge

def SetBridge():
    # find your bridge ip adress by visiting www.meethue.com/api/nupnp
    global b
    bridge_ip = 'xxx.xxx.xxx.xxx'
    b = Bridge(bridge_ip)

def GetHueCommand(ind=None, transitiontime=100, bri=255, hue=46920, sat=255, on=True):
    hue_list = [46920, 25500, 12750, 0]
    hue_list_ = ['blue', 'green', 'orange', 'red']
    if ind != None:
        hue = hue_list[ind]
    command = {'on':on, 'transitiontime':transitiontime, 'bri':bri, 'hue':hue, 'sat':sat}
    return command

def GetSpaceWeatherForecast():
    url = 'http://www2.nict.go.jp/aeri/swe/swx/swcenter/tok_forecast.txt'
    data = urllib2.urlopen(url)
    
    flare_forecast = {'quiet':0, 'eruptive':1, 'active':2, 'major':3}
    geomag_forecast = {'quiet':0, 'active':1, 'minor':2, 'major':3}
    proton_forecast = {'quiet':0, 'in progress':1, 'major':3}
    
    flare_ind = 0
    geomag_ind = 0
    proton_ind = 0
    forecast = []
    
    for line_num, line in enumerate(data):
        if line_num == 4:
            for key, value in flare_forecast.iteritems():
                if key in line.lower():
                    flare_ind = value

        if line_num == 5:
            for key, value in geomag_forecast.iteritems():
                if key in line.lower():
                    geomag_ind = value

        if line_num == 6:
            for key, value in proton_forecast.iteritems():
                if key in line.lower():
                    proton_ind = value
    
    forecast = [flare_ind, geomag_ind, proton_ind]
    return forecast

def main():
    # get space weather forecast
    forecast = GetSpaceWeatherForecast()
    
    # set light state
    SetBridge()
    for bulb in range(3):
        command = GetHueCommand(forecast[bulb])
        b.set_light(bulb+1, command)

if __name__ == "__main__":
    main()
