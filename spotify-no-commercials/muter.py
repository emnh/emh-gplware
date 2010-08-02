#!/usr/bin/env python
# -*- coding: utf-8 -*-
# vim: ft=python ts=4 sw=4 sts=4 et fenc=utf-8
# Original author: "Eivind Magnus Hvidevold" <hvidevold@gmail.com>
# License: GNU GPLv3 at http://www.gnu.org/licenses/gpl.html

'''
'''

import os
import re
import shelve
import spotimeta
import subprocess
import sys
import time

amixerargs = '-c1'
mixer = 'Desktop Speaker,0'

#amixerargs = '-c0'
#mixer = 'PCM'

def usage():
    'print usage'
    print 'usage: %s [options]' % sys.argv[0]

class LookupFailure(Exception):
    pass

def unaccent(s):
    s = s.replace(u'ö', u'o')
    return s

def isCommercial(artist, track):
    # search works differently with -, but it can appear in track, maybe also
    # in artist
    terms = (u'"%s" "%s"' % (artist, track))
    #terms = terms.replace(u'-', u' ')
    #terms = terms.replace(u':', u' ')
    # TODO: use unaccent lib
    #terms = terms.replace(u' – ', ' ')
    done = False
    print terms
    while not done:
        try:
            search = spotimeta.search_track(terms)
            done = True
        except spotimeta.ServiceUnavailable, e:
            print "Service unavailable, muting and trying again in 30 seconds"
            raise LookupFailure(str(e))
            # TODO: fix. check current state
            #mute()
            time.sleep(30)
            #unmute()
        except spotimeta.ServerError, e:
            print "Server error, muting and trying again in 30 seconds"
            raise LookupFailure(str(e))
            #mute()
            time.sleep(30)
            #unmute()
    iscommercial = True
    for result in search['result']:
        print result
        tracks = set([result['name'].lower()])
        artists = set([result['artist']['name'].lower()] + [x['name'].lower() for x in result['artists']])
        cmpTracks = set([track.lower(), unaccent(track).lower()])
        cmpArtists = set([artist.lower(), unaccent(artist).lower()])
        print tracks, artists, cmpTracks, cmpArtists
        if tracks.intersection(cmpTracks) and \
            artists.intersection(cmpArtists):
            print tracks.intersection(cmpTracks), 'ARTIST', artists.intersection(cmpArtists)
            iscommercial = False
    return iscommercial

def parseTitle(title):
    artist, track = title.split(u' – ', 1)
    return artist, track

def getSpotifyWindow():
    get_title_cmd = 'xwininfo -root -children -all'.split(' ')
    data = subprocess.Popen(get_title_cmd, stdout=subprocess.PIPE).communicate()[0]
    winid = None
    for line in data.splitlines():
        if 'spotify.exe' in line and 'Wine' in line:
            line = line.strip()
            winid = line.split(' ')[0]
    if winid == None:
        print 'spotify not running'
    return winid

def getCurrentTitleWin(winid):
    get_title_cmd = ['xwininfo', '-id', winid]
    data = subprocess.Popen(get_title_cmd, stdout=subprocess.PIPE).communicate()[0]
    title = None
    match = re.search('Spotify - (.*)"', data)
    if match:
        title = match.group(1)
    return title

def getCurrentTitle():
    get_title_cmd = 'xwininfo -root -children -all'.split(' ')
    data = subprocess.Popen(get_title_cmd, stdout=subprocess.PIPE).communicate()[0]
    title = None
    for line in data.splitlines():
        if 'spotify.exe' in line:
            match = re.search('Spotify - ([^"]*)', line)
            if match:
                title = match.group(1)
            else:
                print "failed to get title, spotify paused?"
            break
    if title == None:
        #print 'spotify not running'
        title = u''
    return title

def mute():
    os.system('amixer -q %s set "%s" mute' % (amixerargs, mixer))

def unmute():
    os.system('amixer -q %s set "%s" unmute' % (amixerargs, mixer))

#def main_from_script():
#    'entry point'
#    if len(sys.argv) < 2:
#        usage()
#        sys.exit(1)

#    home = os.getenv('HOME')
#    cachefile = os.path.join(home, '.iscommercial.cache')
#    cache = shelve.open(cachefile)

#    utftitle = sys.argv[1]
#    title = utftitle.decode('utf-8')
#    if not utftitle in cache:
#        artist, track = parseTitle(title)
#        iscommercial = cache[utftitle] = isCommercial(artist, track)
#    else:
#        iscommercial = cache[utftitle]
#    cache.close()

#    sys.exit({True: 0, False: 1}[iscommercial])

def main():
    'entry point'
    if len(sys.argv) < 1:
        usage()
        sys.exit(1)

    home = os.getenv('HOME')
    cachefile = os.path.join(home, '.iscommercial.cache')
    cache = shelve.open(cachefile)

    override = True

    try:
        muted = False
        oldutftitle = ''
        winid = getSpotifyWindow()
        if not winid:
            print 'failed to find spotify window'
            sys.exit(1)
        while True:
            utftitle = getCurrentTitleWin(winid)
            if utftitle != None:
                title = utftitle.decode('utf-8')
                wascached = utftitle in cache
                if override:
                    wascached = False
                    override = False

                if utftitle != oldutftitle:
                    if wascached:
                        iscommercial = cache[utftitle]
                    else:
                        print 'Looking up', utftitle
                        artist, track = parseTitle(title)
                        try:
                            iscommercial = cache[utftitle] = isCommercial(artist, track)
                        except LookupFailure, e:
                            print 'Lookup failure, not muting', str(e)
                            iscommercial = False

                    print "Playing: %s commercial=%s cached=%s" % (utftitle,
                            str(iscommercial), str(wascached))
                    oldutftitle = utftitle

                if iscommercial:
                    if not muted:
                        print 'Muting'
                        mute()
                        muted = True
                else:
                    if muted:
                        print 'Unmuting'
                        unmute()
                        muted = False
            else:
                if oldutftitle != None:
                    print "failed to get title, spotify paused?"
                    oldutftitle = utftitle
            time.sleep(0.5)
    finally:
        cache.close()

if __name__ == '__main__':
    main()

