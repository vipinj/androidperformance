from django.http import HttpResponse, HttpResponseRedirect
import urllib2, urllib, json
from django.shortcuts import render_to_response
from backend.summary.models import *

import random
from datetime import datetime, timedelta
from time import time,mktime,strftime
import hashlib
import ast
import error_message_helper


def device(dev,m_deviceid):
    
    count=0
    d = Device(deviceid = m_deviceid,phonenumber=dev['phoneNumber'])
    
    try:
        try:
            d.phonetype = dev['phoneType']
        except:
            pass
        try:         
                        
            d.softwareversion = dev['softwareVersion']
        except:
            pass
        try:
            d.phonemodel = dev['phoneModel']
        except:
            pass
        try:
            d.androidversion = dev['androidVersion']
        except:
            pass
        try:
            d.phonebrand = dev['phoneBrand']
        except:
            pass
        try:
            d.devicedesign = dev['deviceDesign']
        except:
            pass
        try:
            d.manufacturer = dev['manufacturer']
        except:
            pass
        try:
            d.productname = dev['productName']
        except:
            pass
        try:
            d.radioversion = dev['radioVersion']
        except:
            pass
        try:
            d.boardname = dev['boardName']
        except:
            pass
        
        d.save()
	
        print "Device inserted"
    except Exception as inst:
        print type(inst)     # the exception instance
        print inst    
        print error_message_helper.insert_entry_fail("device",inst) 
       
    return d


def network(dev,m):
    
    
    n = Network()
    try:
        n.measurementid = m.measurementid
    except:
        pass
    try:
        n.networkcountry = dev["networkCountry"]
    except:
        pass
    try:
        n.networkname = dev["networkName"]
    except:
        pass
    try:                
        n.networktype = dev["networkType"]
    except:
        pass
    try:    
        n.connectiontype = dev["connectionType"]
    except:
        pass
    try:    
        n.mobilenetworkinfo = parse(dev["mobileNetworkInfo"])
    except:
        pass
     
    try:
        result = Cell.objects.filter(cellid=parse(dev['cellId']))[0]
        
    except:
        try:
            result = Cell()
            try:
                result.cellid = parse(dev['cellId'])
            except:
                pass
            print dev
            try:
                result.celllac = parse(dev["cellLac"])
            except:
                pass
            try:
                result.celltype= parse(dev["cellType"])
            except:
                pass
            try:
                result.longitude= parseFloat(dev["basestationLong"],-99)
            except:
                result.longitude= -99
            try:
                result.latitude= parseFloat(dev["basestationLat"],-99)
            except:
                result.latitude= -99
            try:
                result.networkid= parseInt(dev["networkid"],-1)
            except:
                result.networkid= -1
            
            try:
                result.systemid= parseInt(dev["systemid"],-1)
            except:
                result.systemid= -1
            
            result.save()
            print "Cell inserted"
        except Exception as inst:
            print error_message_helper.insert_entry_fail("cell",inst)
            pass
        
    try:        
        n.cellid = result
    except:
        pass
    
    try:     
        n.datastate = dev["dataState"]
    except:
        pass
    try:    
        n.dataactivity = dev["dataActivity"]
    except:
        pass
    try:    
        n.signalstrength = dev["signalStrength"]
    except:
        pass
    
    n.save()
    print "Network inserted"
       
    return n

def sim(dev):
    
    count=0
    s = Sim()
    try:
        s.serialnumber = dev["serialNumber"]
    except:
        pass
    try: 
        s.state = dev["state"]
    except:
        pass
    try: 
        s.operatorcode = dev["operatorCode"]
    except:
        pass
    try:        
        s.operatorname = dev["operatorName"]
    except:
        pass
    try:
        s.networkcountry = dev["networkCountry"]
    except:
        pass
    
    
    s.save()
    print "Sim inserted"
       
    return s

def gps(dev,m):
    
    g = Gps() 
    try:
        g.measurementid = m       
        g.latitude = dev['latitude']
        g.longitude = dev['longitude']    
        g.altitude = dev['altitude']
        g.save()
        print "GPS inserted"
    except:
        pass
       
    return g

def battery(dev,m):
    
    g = Battery()
    g.measurementid = m.measurementid
    
    try:        
        g.ispresent = dev['isPresent']
    except:
        pass
    try:
        g.technology = dev['technology']
    except:
        pass
    try:
        g.plugged = dev['plugged']
    except:
        pass
    try:
        g.scale = dev['scale']
    except:
        pass
    try:
        g.health = dev['health']
    except:
        pass
    try:
        g.voltage = dev['voltage']
    except:
        pass
    try:
        g.level = dev['level']
    except:
        pass
    try:
        g.temperature = dev['temperature']
    except:
        pass
    try:
        g.status = dev['status']
    except:
        pass
    
    g.save()
    print "Battery inserted"
       
    return g


def link(dev):
    
    l = Link()
    try:
        l.count = dev['count']          
        l.message_size = dev['message_size']    
        l.duration = dev['time']    
        l.speed = dev['speedInBits']    
        l.port = dev['dstPort']
        l.ip_address = dev['dstIp']
        l.save()
        print "Link inserted"
    except:
        pass
    
    return l


def screen(arrdev,device):
    
    for dev in arrdev:
        s = Screen()
        try:
            s.time = dev['time']          
            s.localtime = dev['localtime']
            s.deviceid = device
            if dev['isOn'] == 1:
                s.turnedon = True
            else:
                s.turnedon = False
                
            s.save()
        except:
            pass
        
    print "Screen inserted"
    

def throughput(dev,m):
    
    t = Throughput()
    
    try:
        t.measurementid = m.measurementid
        t.uplinkid=link(dev['upLink'])
        t.downlinkid=link(dev['downLink'])
        t.save()
        print "Throughput inserted"
    except:
        pass
     
    
    return t


def usage(dev,m):
    
    u = Usage()
    u.measurementid=m.measurementid
    try:
        u.total_sent=dev['total_sent']
    except:
        pass
    try:
        u.total_recv=dev['total_recv']
    except:
        pass
    try:
        u.mobile_sent=dev['mobile_sent']
    except:
        pass
    try:
        u.mobile_recv=dev['mobile_recv']
    except:
        pass

    u.save()
    
    for app in dev['applications']:
        
        try:
            result = Application.objects.filter(package=app['packageName'][:50])[0]
        except:
            result = Application(package=app['packageName'][:50],name=app['name'][:20])
            result.save()
        
        appUse = ApplicationUse()
        
        try:
            appUse.package = result
        except:
            pass
        try:
            appUse.total_sent=app['total_sent']
        except:
            pass
        try:
            if app['isRunning'] == 1:
                appUse.isrunning=True
            else:
                appUse.isrunning=False
        except:
            pass
        try:
            appUse.total_recv=app['total_recv']
        except:
            pass
        try:
            appUse.measurementid=m
        except:
            pass

        appUse.save()
    
    print "Usage inserted"
        
    
    return u
    

def pings(pings,measurement):
    
    for p in pings:
        ping = Ping()
       
        
        ping.measurementid = measurement
        
        try:
            ping.scrip = p['src_ip']
        except:
            pass
        try:        
            ping.dstip = p['dst_ip']
        except:
            pass
        try:
            ping.time = p['time']
        except:
            pass
        
        measure = p['measure']
        
        
        try:           
            ping.avg = measure['average']
        except:
            pass
        try:
            ping.stdev = measure['stddev']
        except:
            pass
        try:
            ping.min = measure['min']
        except:
            pass
        try:
            ping.max = measure['max']
        except:
            pass
        
        print "saved?"
        ping.save()
        print "saved"
        

def wifi(dev,m):
    
    w = Wifi()
    w.measurementid = m.measurementid
    w.ipaddress = dev['ipAddress']
    w.detailedinfo = dev['detailedInfo']
    w.rssi = dev['rssi']
    w.signalstrength = dev['strength']
    w.speed = dev['speed']
    w.units = dev['units']
  
    for spot in dev['wifiNeighbors']:
       
        try:
            result = WifiHotspot.objects.filter(macaddress=spot['macAddress'])[0]
        except:
            result = WifiHotspot()
            result.macaddress = spot['macAddress']
            result.ssid = spot['ssid']
            result.frequency = spot['frequency']
            result.capability = spot['capability'][:20]
            result.save()
        
        if spot['isConnected'] == 'true':            
            w.connection = result            
            w.save()
            
    
    for spot in dev['wifiNeighbors']:
        result = WifiHotspot.objects.filter(macaddress=spot['macAddress'])[0]
        
        wn = WifiNeighbor()     
        wn.macaddress = result
        wn.measurementid = m
        if spot['isPreferred'] == 'true':
            wn.ispreferred = 1
        else:
            wn.ispreferred = 0
        wn.signallevel = spot['signalLevel']
        wn.save()
        
    print "Wifi inserted"
        
    w.save()
    return w

def parse(object):
   
    if object==None:
        return ''
    else:
        return object
    
def parseInt(object,backup):
    
    try:
        a=int(object)
        return object
    except:
        return backup
    
def parseFloat(object,backup):
    object = parse(object)
    try:
        a=float(object)
        return object
    except:
        return backup
        