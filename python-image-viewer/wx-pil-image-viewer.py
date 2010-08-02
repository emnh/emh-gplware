#!/usr/bin/env python
# -*- coding: utf-8 -*-
# vim: ft=python ts=4 sw=4 sts=4 et fenc=utf-8
# Original author: "Eivind Magnus Hvidevold" <hvidevold@gmail.com>
# License: GNU GPLv3 at http://www.gnu.org/licenses/gpl.html

'''
'''

import os
import sys
import re
import wxversion
wxversion.select('2.8')
import wx
import Image

class Resizer(object):

    def __init__(self, frame):
        self.frame = frame

    def rszZoomToOriginalSize(self):
        frame = self.frame

    def rszFitImageToWindow(self):
        frame = self.frame

    resize = rszZoomToOriginalSize

def pilToImage(pil):
    image = wx.EmptyImage(pil.size[0], pil.size[1])
    image.SetData(pil.convert('RGB').tostring())
    return image

class ImgViewer(wx.Frame):

    def __init__(self, imgflist):
        wx.Frame.__init__(self, None, -1, "Good/bad viewer")

        self.SetBackgroundColour('BLACK')
        #self.Bind(wx.EVT_KEY_DOWN, self.OnKeyDown)

        panel = wx.Panel(self, -1)
        panel.Bind(wx.EVT_CHAR, self.OnChar)
        panel.SetFocus()

        self.resizer = Resizer(self)

        self.panel = panel
        self.imgflist = imgflist
        self.curidx = 0
        self.wximg = None
        self.fullscreen = False

        self.bmpctrl = wx.StaticBitmap(self.panel, -1, pos=(0,0))

        self.LoadImage(imgflist[0])

        self.Bind(wx.EVT_SIZE, self.OnSize)

    def LoadImage(self, imgfname):
#        oldimg = self.wximg
#        wximg = wx.Image(imgfname, wx.BITMAP_TYPE_JPEG)
#        self.wximg = wximg
#        if oldimg:
#            oldimg.Destroy()

        self.pilimg = Image.open(imgfname)

        self.ScaleImage(*self.GetSize())

    def ScaleImage(self, w, h):
        #wximg = self.wximg.Size((w, h), (0, 0))
        #iw, ih = self.wximg.GetSize()
        iw, ih = self.pilimg.size

        ow, oh = w, h

        # preserve ratio
        nw = h * iw / ih
        nh = w * ih / iw
        nx, ny = 0, 0
        if nw <= w:
            nx = (w - nw) / 2
            w = nw
        else:
            assert nh <= h
            ny = (h - nh) / 2
            h = nh
        assert w / h == iw / ih, 'image ratio not preserved %d/%d %d/%d' % (w, h, iw, ih)

#        wximg = self.wximg.Scale(w, h, wx.IMAGE_QUALITY_NORMAL)
#        wximg = self.wximg.Scale(w, h, wx.IMAGE_QUALITY_HIGH)
        pilimg = self.pilimg.resize((w, h), Image.BILINEAR)
        wximg = pilToImage(pilimg)

        wxbmp = wximg.ConvertToBitmap()

        # center image
        self.panel.Move((nx, ny), 0)

        self.panel.SetSize(wxbmp.GetSize())
        #oldbmp = self.bmpctrl.GetBitmap()
        self.bmpctrl.SetBitmap(wxbmp)
        #oldbmp.Destroy()
        wximg.Destroy()


    def OnChar(self, evt):
        c = evt.GetKeyCode()

        # quit
        if c == ord('q') or c == wx.WXK_ESCAPE:
            self.Close()

        # toggle full screen
        elif c == ord('f'):
            if self.fullscreen:
#                self.SetRect(self.oldrect)
                self.fullscreen = False
                self.ShowFullScreen(self.fullscreen)
            else:
#                self.oldrect = self.GetRect()
#                self.SetRect(wx.Rect(0, 0, *wx.DisplaySize()), 0)
                self.fullscreen = True
                self.ShowFullScreen(self.fullscreen)

        # prev image
        elif c == wx.WXK_PRIOR: # page up
            if self.curidx > 0:
                self.curidx -= 1
                imgfname = self.imgflist[self.curidx]
                self.LoadImage(imgfname)

        # next image
        elif c == wx.WXK_NEXT: # page down
            if self.curidx + 1 < len(self.imgflist):
                self.curidx += 1
                imgfname = self.imgflist[self.curidx]
                self.LoadImage(imgfname)

    def OnSize(self, evt):
        w, h = evt.GetSize()
        self.ScaleImage(w, h)

def win():
    a = wx.PySimpleApp()
    imgdir = sys.argv[1]
    flist = os.listdir(imgdir)
    flist = [os.path.join(imgdir, x) for x in flist if x.lower().endswith('jpg')]
    f = ImgViewer(flist)
    f.Show(True)
    a.MainLoop()

def usage():
    'print usage'
    print 'usage: %s [options]' % sys.argv[0]

def main():
    'entry point'
    if len(sys.argv) < 1:
        usage()
        sys.exit(1)
    win()

if __name__ == '__main__':
    main()

