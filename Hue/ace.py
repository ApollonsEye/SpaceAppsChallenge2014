import time
import urllib2
import pandas as pd
import pygame.mixer
from phue import Bridge

def SetBridge():
    # find your bridge ip adress by visiting www.meethue.com/api/nupnp
    global b
    bridge_ip = 'xxx.xxx.xxx.xxx'
    b = Bridge(bridge_ip)

def SetGroup(group=1):
    b.delete_group(group)
    b.create_group('alert', [1, 2, 3])

def GetHueCommand(ind=None, transitiontime=100, bri=255, hue=46920, sat=255, on=True):
    hue_list = [46920, 25500, 12750, 0]
    hue_list_ = ['blue', 'green', 'orange', 'red']
    if ind != None:
        hue = hue_list[ind]
    command = {'on':on, 'transitiontime':transitiontime, 'bri':bri, 'hue':hue, 'sat':sat}
    return command

def ConvertValToHue(val, valmin, valmax):
    val = min(valmax, max(valmin,val))
    huemax = 46920
    hue = int(huemax-(val-valmin)*huemax/valmax)
    return hue

def Alert(repeat=5, group=1):
    fn = '/Users/xxxxx/Git/SpaceAppsChallenge2014/Hue/wav/alert.wav'
    pygame.mixer.init()
    pygame.mixer.music.load(fn)
    pygame.mixer.music.set_volume(1.0)
    command1 = GetHueCommand(transitiontime=1, bri=255, hue=0)
    command2 = GetHueCommand(transitiontime=10, bri=0, hue=0)
    for i in range(repeat):
        pygame.mixer.music.play(1)
        b.set_group(group, command1)
        time.sleep(0.2)
        b.set_group(group, command2)
        time.sleep(2.2)

def GetAceSwepam():
    url = 'http://www.swpc.noaa.gov/ftpdir/lists/ace/ace_swepam_1m.txt'
    data = urllib2.urlopen(url)
    names = ['Y','M','D','HHMM','Julian','Seconds','S','Density','Speed','Temperature']
    df = pd.read_csv(data, delim_whitespace=True, skiprows=18, names=names)
    
    row_num = -1
    while True:
        density = df.iloc[row_num,-3]
        speed = df.iloc[row_num,-2]        
        if df.iloc[row_num,-4] == 0:
            break
        row_num += -1
        
    return density, speed

def GetAceSis():
    url = 'http://www.swpc.noaa.gov/ftpdir/lists/ace/ace_sis_5m.txt'
    data = urllib2.urlopen(url)    
    names = ['Y','M','D','HHMM','Julian','Seconds','S','Density','Speed','Temperature']
    df = pd.read_csv(data, delim_whitespace=True, skiprows=18, names=names)
    
    row_num = -1
    while True:
        proton_10mev = df.iloc[row_num,-3]    
        if df.iloc[row_num,-4] == 0:
            break
        row_num += -1

    row_num = -1
    while True:
        proton_30mev = df.iloc[row_num,-1]        
        if df.iloc[row_num,-2] == 0:
            break
        row_num += -1

    return proton_10mev, proton_30mev

def main():    
    density, speed = GetAceSwepam()
    proton10mev, proton30mev = GetAceSis()
    
    density_lim = [0, 30]
    speed_lim = [300, 800]
    proton10mev_lim = [0, 3000]
    
    command_density = GetHueCommand(hue=ConvertValToHue(density, density_lim[0], density_lim[1]))
    command_speed = GetHueCommand(hue=ConvertValToHue(speed, density_lim[0], speed_lim[1]))    
    command_proton10mev = GetHueCommand(hue=ConvertValToHue(proton10mev, proton10mev_lim[0], proton10mev_lim[1]))

    SetBridge()
    SetGroup()
    
    if proton10mev >= proton10mev_lim[1]:
        Alert(repeat=5, group=1)
    
    b.set_light(1, command_density)
    b.set_light(2, command_speed)
    b.set_light(3, command_proton10mev)

if __name__ == "__main__":
    main()